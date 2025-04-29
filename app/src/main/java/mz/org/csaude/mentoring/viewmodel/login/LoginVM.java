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
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.listner.rest.ServerStatusListener;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.user.UserService;
import mz.org.csaude.mentoring.service.user.UserSyncService;
import mz.org.csaude.mentoring.util.Constants;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.home.MainActivity;
import mz.org.csaude.mentoring.view.login.LoginActivity;
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

    private boolean biometricEnabled;

    public LoginVM(@NonNull Application application) {
        super(application);
        userService = getApplication().getUserService();
        this.user= new User();
        if (Utilities.stringHasValue(getApplication().getLastUser())) {
            user.setUserName(getApplication().getLastUser());
            setRemeberMe(true);
        }
        this.userSyncService = new UserRestService(application, this.user);
        loadBiometricSetting();
    }

    public boolean isBiometricEnabled() {
        return biometricEnabled;
    }

    private void loadBiometricSetting() {
        biometricEnabled = getApplication()
                .getEncryptedSharedPreferences()
                .getBoolean(Constants.PREF_BIOMETRIC_ENABLED, false);
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
        getExecutorService().execute(() -> {
            setAuthenticating(true);
            if (AppHasUser()) {
                try {
                    doLocalLogin();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                doOnlineLogin();
            }
            getApplication().saveDefaultSyncSettings();
            getApplication().saveDefaultLastSyncDate(DateUtilities.getCurrentDate());
        });
    }

    private void doOnlineLogin() {
        runOnMainThread(() -> setAuthenticating(true));

        getApplication().isServerOnline((isOnline, isSlow) -> {
            if (isOnline) {
                if (isSlow) {
                    showSlowConnectionWarning(getRelatedActivity());
                }
                userSyncService.doOnlineLogin(this, remeberMe);
            } else {
                runOnMainThread(() -> {
                    String serverUnavailableMessage = getRelatedActivity().getString(R.string.server_unavailable);
                    Utilities.displayAlertDialog(getRelatedActivity(), serverUnavailableMessage).show();
                    setAuthenticating(false);
                });
            }
        });
    }


    private void doLocalLogin() throws SQLException {
        AtomicReference<User> loggedUser = new AtomicReference<>();
        getExecutorService().execute(() -> {
            try {
                String pass = this.user.getPassword();
                loggedUser.set(userService.login(this.user));
                loggedUser.get().setPassword(pass);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            runOnMainThread(() -> {
                if (loggedUser.get() != null) {
                    if (!loggedUser.get().isActivated()) {
                        String inactiveMessage = getRelatedActivity().getString(R.string.user_inactive_cheking_on_server);
                        checkDlg = Utilities.displayAlertDialog(getRelatedActivity(), inactiveMessage);
                        checkDlg.show();

                        this.serverOperation = INACTIVE_USER_CHECK;
                        getApplication().isServerOnline(this);
                        return;
                    }
                    getApplication().setAuthenticatedUser(loggedUser.get(), remeberMe);
                    setAuthenticating(false);
                    goHome();
                } else {
                    // Fallback to online login if user is not found locally
                    setAuthenticating(true);
                    doOnlineLogin();
                }


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
        //getRelatedActivity().runOnUiThread(() -> {
            try {
                if (getSessionManager().isInitialSetupComplete(user.getUserName())) {
                    getApplication().init();
                    setAuthenticating(false);
                    goHome();
                } else {
                    OneTimeWorkRequest request = WorkerScheduleExecutor.getInstance(getApplication()).runPostLoginSync();

                    runOnMainThread(() -> {
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
                                            getSessionManager().setInitialSetUpComplete(user.getUserName());
                                            getApplication().saveDefaultLastSyncDate(DateUtilities.getCurrentDate());
                                            setAuthenticating(false);
                                            goHome();
                                        }
                                    });
                                } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                                    setAuthenticating(false);
                                    String errorMessage = getRelatedActivity().getString(R.string.error_downloading_mentor_data);
                                    Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                                }
                            }
                        });
                    });
                }
            } catch (SQLException e) {
                setAuthenticating(false);
                Log.e("LoginVM", "doOnRestSucessResponse: ", e);
            }
        //});
    }


    private void goHome() {
        try {
            getApplication().init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        setAuthenticating(false);
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
    public void onServerStatusChecked(boolean isOnline, boolean isSlow) {
        if (isOnline) {
            if (isSlow) {
                // Show warning: Server is slow
                showSlowConnectionWarning(getRelatedActivity());
            }
            if (serverOperation == INACTIVE_USER_CHECK) {
                OneTimeWorkRequest syncRequest = WorkerScheduleExecutor.getInstance(getApplication()).syncUserFromServer();
                WorkerScheduleExecutor.getInstance(getApplication()).getWorkManager()
                        .getWorkInfoByIdLiveData(syncRequest.getId())
                        .observe(getRelatedActivity(), info -> {
                            if (info.getState() == WorkInfo.State.SUCCEEDED) {
                                try {
                                    User user = userService.login(this.user);
                                    if (!user.isActivated()) {
                                        checkDlg.dismiss();
                                        String inactiveMessage = getRelatedActivity().getString(R.string.user_inactive);
                                        Utilities.displayAlertDialog(getRelatedActivity(), inactiveMessage).show();
                                    } else {
                                        getApplication().setAuthenticatedUser(user, remeberMe);
                                        goHome();
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                String errorMessage = getRelatedActivity().getString(R.string.user_inactive_cheking_error);
                                Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                            }
                        });
            } else {
                doOnlineLogin();
            }
        } else {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.server_unavailable)).show();
        }
    }

    @Override
    public LoginActivity getRelatedActivity() {
        return (LoginActivity) super.getRelatedActivity();
    }

    @Override
    public void doOnRestErrorResponse(String errormsg) {
        runOnMainThread(() -> {
            if (Utilities.stringHasValue(errormsg)) {
                Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.invalid_user_or_password)).show();
            } else {
                String invalidMessage = getRelatedActivity().getString(R.string.invalid_user_or_password);
                Utilities.displayAlertDialog(getRelatedActivity(), invalidMessage).show();
            }
            setAuthenticating(false);
        });
    }

    public void showBiometricPrompt() {
        getRelatedActivity().showBiometricPrompt();
    }
}
