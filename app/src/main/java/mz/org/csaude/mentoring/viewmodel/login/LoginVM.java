package mz.org.csaude.mentoring.viewmodel.login;

import android.app.Application;
import android.app.Dialog;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.Bindable;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.listner.rest.ServerStatusListener;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.user.UserService;
import mz.org.csaude.mentoring.service.user.UserSyncService;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.home.MainActivity;
import mz.org.csaude.mentoring.workSchedule.executor.WorkerScheduleExecutor;
import mz.org.csaude.mentoring.workSchedule.rest.UserRestService;

public class LoginVM extends BaseViewModel implements RestResponseListener<User>, ServerStatusListener {

    private final UserService userService;
    private final User user;

    private boolean remeberMe;

    private boolean authenticating;
    private static final int INACTIVE_USER_CHECK = 1;
    private int serverOperation;

    private UserSyncService userSyncService;
    private AlertDialog checkDlg;


    public LoginVM(@NonNull Application application) {
        super(application);
        userService = getApplication().getUserService();
        this.user= new User();
        if (Utilities.stringHasValue(getApplication().getLastUser())) {
            user.setUserName(getApplication().getLastUser());
            setRemeberMe(true);
        }
        this.userSyncService = new UserRestService(application, this.user);
    }

    @Override
    public void preInit() {
        getApplication().initSessionManager();
    }

    @Bindable
    public String getUserName() {
        return this.user.getUserName();
    }
    public void setUserName(String userName) {
        this.user.setUserName(userName);
        notifyPropertyChanged(BR.userName);
    }

    @Bindable
    public String getUserPassword() {
        return this.user.getPassword();
    }
    public void setUserPassword(String password) {
        this.user.setPassword(password);
        notifyPropertyChanged(BR.userPassword);
    }

    public void doLogin() {
        getExecutorService().execute(()-> {
            setAuthenticating(true);
            if (AppHasUser()) {
                runOnMainThread(() -> {
                    try {
                        doLocalLogin();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                getApplication().isServerOnline(this);
            }
            getApplication().saveDefaultSyncSettings();
            getApplication().saveDefaultLastSyncDate(DateUtilities.getCurrentDate());
        });
    }

    private void doLocalLogin() throws SQLException {
        AtomicReference<User> logedUser = new AtomicReference<>();
        getExecutorService().execute(()-> {
            try {
                logedUser.set(userService.login(this.user));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            runOnMainThread(()->{
                if (logedUser.get() != null) {
                    if (!logedUser.get().isActivated()) {
                        String inactiveMessage = getRelatedActivity().getString(R.string.user_inactive_cheking_on_server);
                        checkDlg = Utilities.displayAlertDialog(getRelatedActivity(), inactiveMessage);
                        checkDlg.show();

                        this.serverOperation = INACTIVE_USER_CHECK;
                        getApplication().isServerOnline(this);
                        return;
                    }
                    getApplication().setAuthenticatedUser(logedUser.get(), remeberMe);
                    goHome();
                } else {
                    String invalidMessage = getRelatedActivity().getString(R.string.invalid_user_or_password);
                    Utilities.displayAlertDialog(getRelatedActivity(), invalidMessage).show();
                }

                setAuthenticating(false);
            });
        });
    }

    private boolean AppHasUser() {
        try {
            return Utilities.listHasElements(userService.getAll());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doOnRestSucessResponse(User user) {
        getRelatedActivity().runOnUiThread(() -> {
            try {
                if (getApplication().isInitialSetupComplete()) {
                    getApplication().init();
                    goHome();
                } else {
                    OneTimeWorkRequest request = WorkerScheduleExecutor.getInstance(getApplication()).runPostLoginSync();

                    WorkerScheduleExecutor.getInstance(getApplication()).getWorkManager().getWorkInfoByIdLiveData(request.getId()).observe(getRelatedActivity(), workInfo -> {
                        if (workInfo != null) {
                            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                                try {
                                    getApplication().init();
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }

                                OneTimeWorkRequest downloadMentorData = WorkerScheduleExecutor.getInstance(getApplication()).downloadMentorData();
                                WorkerScheduleExecutor.getInstance(getApplication()).getWorkManager().getWorkInfoByIdLiveData(downloadMentorData.getId()).observe(getRelatedActivity(), info -> {
                                    if (info.getState() == WorkInfo.State.SUCCEEDED) {
                                        getApplication().setInitialSetUpComplete();
                                        getApplication().saveDefaultLastSyncDate(DateUtilities.getCurrentDate());
                                        goHome();
                                    }
                                });
                            }
                        }
                    });
                }
            } catch (SQLException e) {
                Log.e("LoginVM", "doOnRestSucessResponse: ", e);
            }
        });
    }


    private void goHome() {
        try {
            getApplication().init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> params = new HashMap<>();
        getRelatedActivity().nextActivityFinishingCurrent(MainActivity.class, params);
    }

    @Bindable
    public boolean isAuthenticating() {
        return this.authenticating;
    }

    public void setAuthenticating(boolean authenticating) {
        this.authenticating = authenticating;
        notifyPropertyChanged(BR.authenticating);
    }

    @Bindable
    public boolean isRemeberMe() {
        return remeberMe;
    }

    public void setRemeberMe(boolean remeberMe) {
        this.remeberMe = remeberMe;
        notifyPropertyChanged(BR.remeberMe);
    }

    public void changeRemeberMeStatus() {
        setRemeberMe(!isRemeberMe());
    }

    @Override
    public void onServerStatusChecked(boolean isOnline) {
        if (isOnline) {
            if (serverOperation == INACTIVE_USER_CHECK) {
                OneTimeWorkRequest downloadMentorData = WorkerScheduleExecutor.getInstance(getApplication()).syncUserFromServer();
                WorkerScheduleExecutor.getInstance(getApplication()).getWorkManager().getWorkInfoByIdLiveData(downloadMentorData.getId()).observe(getRelatedActivity(), info -> {
                    if (info.getState() == WorkInfo.State.SUCCEEDED) {
                        getExecutorService().execute(()->{
                            try {
                                User u =userService.login(this.user);
                                if (!u.isActivated()) {
                                    checkDlg.dismiss();
                                    String inactiveMessage = getRelatedActivity().getString(R.string.user_inactive);
                                    Utilities.displayAlertDialog(getRelatedActivity(), inactiveMessage).show();
                                } else {
                                    getApplication().setAuthenticatedUser(u, remeberMe);
                                    goHome();
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else {
                        String inactiveMessage = getRelatedActivity().getString(R.string.user_inactive_cheking_error);
                        Utilities.displayAlertDialog(getRelatedActivity(), inactiveMessage).show();
                    }
                });
            } else {
                userSyncService.doOnlineLogin(this, remeberMe);
            }
        }else {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.server_unavailable)).show();
        }
    }

    @Override
    public void doOnRestErrorResponse(String errormsg) {
        runOnMainThread(() -> {
            if (Utilities.stringHasValue(errormsg)) {
                Utilities.displayAlertDialog(getRelatedActivity(), errormsg).show();
            } else {
                String invalidMessage = getRelatedActivity().getString(R.string.invalid_user_or_password);
                Utilities.displayAlertDialog(getRelatedActivity(), invalidMessage).show();
            }
            setAuthenticating(false);
        });
    }
}
