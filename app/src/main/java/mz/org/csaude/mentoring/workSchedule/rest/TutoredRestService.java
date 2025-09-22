package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.base.service.SuccessResponse;
import mz.org.csaude.mentoring.common.HttpStatus;
import mz.org.csaude.mentoring.common.MentoringAPIError;
import mz.org.csaude.mentoring.dto.tutored.TutoredDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.location.Location;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.tutored.TutoredService;
import mz.org.csaude.mentoring.service.tutored.TutoredServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TutoredRestService extends BaseRestService {


    public TutoredRestService(Application application) {
        super(application);
    }

    public void restGetTutored(RestResponseListener<Tutored> listener, Long offset, Long limit){
        List<String> uuids = new ArrayList<>();
        Set<Location> locations = new HashSet<>();
        if (!getSessionManager().isAnyUserConfigured()){
            listener.doOnResponse(BaseRestService.REQUEST_NO_DATA, null);
            return;
        }
        if (getApplication().getAuthenticatedUser() == null) {
            try {
                List<User> users = getApplication().getUserService().getAll();
                for (User user : users) {
                    user.getEmployee().setLocations(getApplication().getLocationService().getAllOfEmploee(user.getEmployee()));
                    locations.addAll(user.getEmployee().getLocations());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            locations.addAll(getApplication().getAuthenticatedUser().getEmployee().getLocations());
        }

        for (Location location : locations) {
            uuids.add(location.getHealthFacility().getUuid());
        }
        Call<List<TutoredDTO>> tutoredCall = syncDataService.getTutoreds(uuids, offset, limit);

        tutoredCall.enqueue(new Callback<List<TutoredDTO>>() {
            @Override
            public void onResponse(Call<List<TutoredDTO>> call, Response<List<TutoredDTO>> response) {
                List<TutoredDTO> data = response.body();
                if (Utilities.listHasElements(data)) {
                    getServiceExecutor().execute(()-> {
                        try {
                            List<Tutored> tutoreds = Utilities.parse(data, Tutored.class);
                            for (Tutored tutored : tutoreds) {
                                tutored.setSyncStatus(SyncSatus.SENT);
                            }
                            getApplication().getTutoredService().savedOrUpdateTutoreds(tutoreds);
                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, tutoreds);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    listener.doOnResponse(BaseRestService.REQUEST_NO_DATA, null);
                }
            }

            @Override
            public void onFailure(Call<List<TutoredDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });
    }


    public void restPostTutored(RestResponseListener<Tutored> listener){

        List<Tutored> tutoreds = null;
        try {
            tutoreds = getApplication().getTutoredService().getAllNotSynced();
        if (Utilities.listHasElements(tutoreds)) {
            Call<List<TutoredDTO>> tutoredCall = syncDataService.postTutoreds(Utilities.parse(tutoreds, TutoredDTO.class));
            tutoredCall.enqueue(new Callback<List<TutoredDTO>>() {
                @Override
                public void onResponse(Call<List<TutoredDTO>> call, Response<List<TutoredDTO>> response) {
                    List<TutoredDTO> data = response.body();
                    if (response.code() == 200) {
                        getServiceExecutor().execute(()-> {
                            try {
                                List<Tutored> tutoreds = getApplication().getTutoredService().getAllNotSynced();
                                for (Tutored tutored : tutoreds) {
                                    tutored.setSyncStatus(SyncSatus.SENT);
                                    getApplication().getTutoredService().update(tutored);
                                }

                                listener.doOnResponse(BaseRestService.REQUEST_SUCESS, tutoreds);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else {
                        listener.doOnRestErrorResponse(response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<TutoredDTO>> call, Throwable t) {
                    Log.i("METADATA LOAD --", t.getMessage(), t);
                }
            });
        }else {
            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Collections.emptyList());
        }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void restPostTutored(Tutored tutored, RestResponseListener<Tutored> listener){


        Call<TutoredDTO> tutoredCall = syncDataService.postTutored(new TutoredDTO(tutored));
        tutoredCall.enqueue(new Callback<TutoredDTO>() {
            @Override
            public void onResponse(Call<TutoredDTO> call, Response<TutoredDTO> response) {
                TutoredDTO data = response.body();
                if (response.code() == 201) {
                    getServiceExecutor().execute(()-> {
                        try {
                            getApplication().getTutoredService().savedOrUpdateTutored(tutored);

                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Utilities.parseToList(tutored));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
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
            public void onFailure(Call<TutoredDTO> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
                listener.doOnRestErrorResponse(t.getMessage());
            }
        });

    }

    public void restGetTutoredByUuid(String uuid, RestResponseListener<Tutored> listener) {
        Call<ResponseBody> call = syncDataService.getTutoredByUuid(uuid);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Gson gson = new Gson();

                        Type type = new TypeToken<SuccessResponse<TutoredDTO>>() {}.getType();
                        SuccessResponse<TutoredDTO> successResponse = gson.fromJson(json, type);
                        TutoredDTO dto = successResponse.getData();

                        getServiceExecutor().execute(() -> {
                            try {
                                Tutored tutored = new Tutored(dto);
                                tutored.setSyncStatus(SyncSatus.SENT);
                                getApplication().getTutoredService().savedOrUpdateTutored(tutored);
                                listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Collections.singletonList(tutored));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });

                    } catch (IOException e) {
                        listener.doOnRestErrorResponse("Erro ao processar resposta: " + e.getMessage());
                    }
                } else {
                    listener.doOnRestErrorResponse("Erro ao buscar mentorando: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TUTORED_FETCH_ERROR", "Erro na chamada GET por UUID", t);
                listener.doOnRestErrorResponse(t.getMessage());
            }
        });
    }

    public void restUpdateTutored(Tutored tutored, RestResponseListener<Tutored> listener) {
        TutoredDTO dto = new TutoredDTO(tutored);
        Call<ResponseBody> call = syncDataService.updateTutored(dto);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Gson gson = new Gson();
                        Type type = new TypeToken<SuccessResponse<TutoredDTO>>() {}.getType();
                        SuccessResponse<TutoredDTO> success = gson.fromJson(json, type);

                        TutoredDTO updatedDTO = success.getData();
                        Tutored updated = new Tutored(updatedDTO);
                        updated.setSyncStatus(SyncSatus.SENT);

                        getServiceExecutor().execute(() -> {
                            try {
                                getApplication().getTutoredService().savedOrUpdateTutored(updated);
                                listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Collections.singletonList(updated));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } catch (IOException e) {
                        listener.doOnRestErrorResponse("Erro ao processar resposta: " + e.getMessage());
                    }
                } else {
                    listener.doOnRestErrorResponse("Erro na atualização: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.doOnRestErrorResponse("Erro de conexão: " + t.getMessage());
            }
        });
    }


}
