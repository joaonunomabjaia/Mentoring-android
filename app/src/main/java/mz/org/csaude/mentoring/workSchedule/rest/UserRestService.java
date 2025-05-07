package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import mz.org.csaude.mentoring.base.auth.LoginRequest;
import mz.org.csaude.mentoring.base.auth.LoginResponse;
import mz.org.csaude.mentoring.base.auth.SessionManager;
import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.common.HttpStatus;
import mz.org.csaude.mentoring.common.MentoringAPIError;
import mz.org.csaude.mentoring.dto.role.UserRoleDTO;
import mz.org.csaude.mentoring.dto.tutored.TutoredDTO;
import mz.org.csaude.mentoring.dto.user.UserDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.metadata.SyncDataService;
import mz.org.csaude.mentoring.service.user.UserService;
import mz.org.csaude.mentoring.service.user.UserServiceImpl;
import mz.org.csaude.mentoring.service.user.UserSyncService;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.LifeCycleStatus;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.workSchedule.work.post.PATCHUserWorker;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRestService extends BaseRestService implements UserSyncService {


    private SessionManager sessionManager;

    public UserRestService(Application application, User currentUser) {
        super(application, currentUser);
    }


    public void doOnlineLogin(RestResponseListener listener, boolean rememberMe) {
        this.sessionManager = new SessionManager(getApplication());

        SyncDataService syncDataService = getRetrofit().create(SyncDataService.class);

        LoginRequest loginRequest = new LoginRequest(currentUser.getPassword(), currentUser.getUserName());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), gson.toJson(loginRequest));

        Call<LoginResponse> call = syncDataService.login(body);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    LoginResponse data = response.body();

                    // Move the database operations to a background thread
                    getServiceExecutor().execute(() -> {
                        try {
                            String error = validateUserData(data);

                            if (error != null) {
                                listener.doOnRestErrorResponse(error);
                                return;
                            }

                            // Save user information and handle session data in the background thread
                            saveUserAndSessionData(data, rememberMe);

                            // If the access token exists, save it
                            if (Utilities.stringHasValue(data.getAccess_token())) {
                                sessionManager.saveAuthToken(data.getUsername(), data.getAccess_token(), data.getRefresh_token(), data.getExpires_in());
                                sessionManager.setActiveUser(data.getUserUuid());

                                // Notify success on the main thread
                                getApplication().getAuthenticatedUser().setPassword(currentUser.getPassword());
                                listener.doOnRestSucessResponse(getApplication().getAuthenticatedUser());
                            }

                        } catch (SQLException e) {
                            // Handle database errors on the main thread
                            listener.doOnRestErrorResponse("Database error: " + e.getMessage());
                        }
                    });
                } else {
                    listener.doOnRestErrorResponse(response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("USER LOGIN --", t.getMessage(), t);
                listener.doOnRestErrorResponse("Login failed: " + t.getMessage());
            }
        });
    }

    private String validateUserData(LoginResponse data) {
        if (!Utilities.listHasElements(data.getUserDTO().getUserRoleDTOS())) {
            return "O utilizador não tem perfil associado";
        } else if (!isMentor(data.getUserDTO())) {
            return "O utilizador não tem perfil de mentor associado";
        } else if (data.getUserDTO().getLifeCycleStatus().equals(LifeCycleStatus.INACTIVE)) {
            return "O utilizador está inativo, contacte o administrador.";
        }
        return null;
    }

    private void saveUserAndSessionData(LoginResponse data, boolean rememberMe) throws SQLException {
        // Save user data to the database in the background thread
        getApplication().getUserService().savedOrUpdateUser(new User(data.getUserDTO()));

        // Retrieve the saved user and update additional information
        User user = getApplication().getUserService().getByuuid(data.getUserDTO().getUuid());
        user.setEmployee(getApplication().getEmployeeService().getById(user.getEmployeeId()));

        // Set the authenticated user with the rememberMe flag
        getApplication().setAuthenticatedUser(user, rememberMe);
    }


    private boolean isMentor(UserDTO userDTO) {
        for (UserRoleDTO userRoleDTO : userDTO.getUserRoleDTOS()) {
            if (userRoleDTO.getRoleDTO().getCode().equals("HEALTH_FACILITY_MENTOR")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void getByUuid(RestResponseListener<User> listener) {

        Call<UserDTO> call = syncDataService.getByuuid(getSessionManager().getActiveUser());

        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.code() == 200) {
                    UserDTO data = response.body();
                    getServiceExecutor().execute(()-> {
                        try {
                            User userOnServer = new User(data);
                            User userOnDB = getApplication().getUserService().getByuuid(userOnServer.getUuid());
                            if (userOnDB == null) {
                                userOnServer.setSyncStatus(SyncSatus.SENT);
                                getApplication().getUserService().savedOrUpdateUser(userOnServer);
                            } else
                            if (DateUtilities.isDateAfterIgnoringTime(userOnServer.getUpdatedAt(), userOnDB.getUpdatedAt())) {
                                userOnServer.setId(userOnDB.getId());
                                getApplication().getUserService().savedOrUpdateUser(userOnServer);
                            }
                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Collections.singletonList(userOnServer));
                        } catch (SQLException e) {
                            Log.e("USER FETCH --", e.getMessage(), e);
                            listener.doOnRestErrorResponse(e.getMessage());
                        }
                    });
                } else {
                    listener.doOnRestErrorResponse("Error on server updating user");
                }

            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.e("USER FETCH --", t.getMessage(), t);
                listener.doOnRestErrorResponse(t.getMessage());
            }
        });
    }

    public void pacthUser(RestResponseListener<User> listener) {
        if (!getSessionManager().isAnyUserConfigured()) {
            listener.doOnResponse(REQUEST_NO_DATA, null);
            return;
        }


        Call<Void> tutoredCall = null;
        try {
            tutoredCall = syncDataService.patchUsers(Utilities.parse(getApplication().getUserService().getAll(), UserDTO.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        tutoredCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Collections.emptyList());
                } else {
                    if (response.code() == HttpStatus.BAD_REQUEST) {
                        // Parse custom error response
                        try {
                            Gson gson = new Gson();
                            MentoringAPIError error = gson.fromJson(response.errorBody().string(), MentoringAPIError.class);
                            listener.doOnRestErrorResponse(error.getMessage());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        // Handle other error responses
                        listener.doOnRestErrorResponse(response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i("pacthUser --", t.getMessage(), t);
                listener.doOnRestErrorResponse(t.getMessage());
            }
        });

    }
    
}
