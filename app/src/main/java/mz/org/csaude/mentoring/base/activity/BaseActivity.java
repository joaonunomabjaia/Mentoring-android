package mz.org.csaude.mentoring.base.activity;

import static mz.org.csaude.mentoring.util.Constants.PREF_SESSION_TIMEOUT;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.pm.PackageInfoCompat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.common.ApplicationStep;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.util.Constants;
import mz.org.csaude.mentoring.view.login.LoginActivity;

/**
 * Generic class that represents all application activities.
 */
public abstract class BaseActivity extends AppCompatActivity implements GenericActivity {

    /**
     * Activity-related ViewModel.
     */
    protected BaseViewModel relatedViewModel;

    /**
     * Application package info.
     */
    private PackageInfo pinfo;

    protected static final int REQUEST_WRITE_STORAGE = 112;
    private Integer positionRemoved;

    // Constants for auto logout
    private static final long WARNING_BEFORE_LOGOUT = 10000; // 10 seconds before logout
    private Handler autoLogoutHandler;
    private Runnable autoLogoutRunnable;
    private CountDownTimer countDownTimer;

    private AlertDialog alertDialog;

    private long autoLogoutDelay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.relatedViewModel = initViewModel();

        Intent intent = this.getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null && this.relatedViewModel != null) {
                if (bundle.getSerializable("relatedRecord") != null) {
                    this.relatedViewModel.setSelectedRecord(bundle.getSerializable("relatedRecord"));
                }
            }
        }

        if (this.relatedViewModel != null) {
            this.relatedViewModel.setRelatedActivity(this);
        }

        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        autoLogoutDelay = getAutoLogoutDelay();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("mz.org.csaude.mentoring.ACTION_LOGOUT");

        // Initialize the auto-logout handler and runnable
        autoLogoutHandler = new Handler();
        autoLogoutRunnable = this::showLogoutWarningDialog;

        SharedPreferences preferences = ((MentoringApplication) getApplication()).getEncryptedSharedPreferences();
        String selectedLanguageCode = preferences.getString(Constants.PREF_SELECTED_LANGUAGE, "en"); // Default to English

        setLocale(selectedLanguageCode);
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            config.setLocale(locale);
            getBaseContext().createConfigurationContext(config);
        } else {
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (this.relatedViewModel != null) {
            this.relatedViewModel.preInit();
        }
        if (isAutoLogoutEnabled()) {
            resetLogoutTimer(); // Start/reset the logout timer on activity resume
        }
    }

    @Override
    protected void onPause() {
        if (isAutoLogoutEnabled()) {
            stopLogoutTimer(); // Stop the logout timer when the activity is paused
        }
        super.onPause();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (isAutoLogoutEnabled()) {
            resetLogoutTimer(); // Reset the logout timer on user interaction
        }
    }

    @Override
    protected void onDestroy() {
        if (isAutoLogoutEnabled()) {
            stopLogoutTimer(); // Remove any callbacks to prevent leaks
        }
        super.onDestroy();
    }

    /**
     * Determines whether auto logout is enabled for this activity.
     * Subclasses can override this method to disable auto logout.
     *
     * @return true if auto logout is enabled, false otherwise
     */
    protected boolean isAutoLogoutEnabled() {
        return true; // Auto logout is enabled by default
    }

    public void updateAutoLogoutTime(int logoutTimeMinutes) {
        // Update the auto logout delay
        autoLogoutDelay = logoutTimeMinutes * 60 * 1000L; // Convert minutes to milliseconds

        // Reset the logout timer with the new delay
        resetLogoutTimer();
    }


    private void resetLogoutTimer() {
        stopLogoutTimer(); // Remove any pending callbacks
        autoLogoutDelay = getAutoLogoutDelay(); // Update the auto logout delay
        long warningTime = autoLogoutDelay - WARNING_BEFORE_LOGOUT; // Calculate when to show the warning dialog

        // Ensure warningTime is not negative
        if (warningTime < 0) {
            warningTime = 0;
        }

        autoLogoutHandler.postDelayed(autoLogoutRunnable, warningTime); // Start a new timer to show the warning dialog
    }

    private void stopLogoutTimer() {
        autoLogoutHandler.removeCallbacks(autoLogoutRunnable);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /**
     * Retrieves the auto logout delay from encrypted shared preferences.
     *
     * @return Auto logout delay in milliseconds
     */
    private long getAutoLogoutDelay() {
        MentoringApplication app = (MentoringApplication) getApplication();
        SharedPreferences encryptedSharedPreferences = app.getEncryptedSharedPreferences();
        String logoutTimeStr = encryptedSharedPreferences.getString(PREF_SESSION_TIMEOUT, "5");

        int logoutTimeMinutes;
        try {
            logoutTimeMinutes = Integer.parseInt(logoutTimeStr);
        } catch (NumberFormatException e) {
            logoutTimeMinutes = 5; // Default to 5 minutes if invalid
        }
        return logoutTimeMinutes * 60 * 1000L; // Convert minutes to milliseconds
    }

    private void showLogoutWarningDialog() {
        // Check if the activity is finishing or destroyed
        if (isFinishing() || isDestroyed()) {
            return; // Do not show the dialog if the activity is not in a valid state
        }

        // Initialize the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_title));

        // Set the initial message with the remaining time
        builder.setMessage(String.format(getString(R.string.dialog_message), 10));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.extend_session), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                resetLogoutTimer(); // Reset the timer when the user extends the session
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.logout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                logoutUser(); // Log out immediately
            }
        });

        alertDialog = builder.create();

        // Show the dialog on the UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && !isDestroyed()) {
                    alertDialog.show();
                    startCountDown(); // Start the countdown
                }
            }
        });
    }

    private void startCountDown() {
        final long totalTime = WARNING_BEFORE_LOGOUT; // Total time in milliseconds (10 seconds)
        final long countDownInterval = 1000; // Countdown interval (1 second)

        countDownTimer = new CountDownTimer(totalTime, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the dialog message with the remaining time
                long secondsRemaining = millisUntilFinished / 1000;
                String message = String.format(getString(R.string.dialog_message), secondsRemaining);
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.setMessage(message);
                }
            }

            @Override
            public void onFinish() {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                logoutUser(); // Log out when the time is up
            }
        }.start();
    }

    private void logoutUser() {
        // Perform logout action (clear session, navigate to login screen, etc.)
        // Navigate to LoginActivity
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * @return Application version number
     */
    public long getAppVersionNumber() {
        return PackageInfoCompat.getLongVersionCode(pinfo);
    }

    /**
     * @return Application version name
     */
    public String getAppVersionName() {
        return pinfo.versionName;
    }

    /**
     * Navigate from the current activity to a new one without finishing the current one.
     *
     * @param clazz Target activity class
     */
    public void nextActivity(Class clazz) {
        nextActivity(clazz, null, false);
    }

    /**
     * Navigate from the current activity to a new one, finishing the current one.
     *
     * @param clazz Target activity class
     */
    public void nextActivityFinishingCurrent(Class clazz) {
        nextActivity(clazz, null, true);
    }

    /**
     * Navigate from the current activity to a new one without finishing the current one, sending parameters.
     *
     * @param clazz  Target activity class
     * @param params Parameters to be sent
     */
    public void nextActivity(Class clazz, Map<String, Object> params) {
        nextActivity(clazz, params, false);
    }

    /**
     * Navigate from the current activity to a new one, finishing the current one, sending parameters.
     *
     * @param clazz  Target activity class
     * @param params Parameters to be sent
     */
    public void nextActivityFinishingCurrent(Class clazz, Map<String, Object> params) {
        nextActivity(clazz, params, true);
    }

    /**
     * Move from one activity to another.
     *
     * @param clazz                 Target activity class
     * @param params                Parameters to be sent
     * @param finishCurrentActivity Whether to finish the current activity
     */
    private void nextActivity(Class clazz, Map<String, Object> params, boolean finishCurrentActivity) {
        Intent intent = new Intent(getApplication(), clazz);
        Bundle bundle = new Bundle();

        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() instanceof Serializable) {
                    bundle.putSerializable(entry.getKey(), (Serializable) entry.getValue());
                }
            }
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if (finishCurrentActivity) finish();
    }

    public <T extends BaseActivity> void nextActivityWithGenericParams(Class<T> clazz) {
        Map<String, Object> params = new HashMap<>();
        nextActivity(clazz, params);
    }

    public <T extends BaseActivity> void nextActivityFinishingCurrentWithGenericParams(Class<T> clazz) {
        Map<String, Object> params = new HashMap<>();
        nextActivity(clazz, params);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate your menu here if needed
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * @return The related ViewModel
     */
    public BaseViewModel getRelatedViewModel() {
        return relatedViewModel;
    }

    /**
     * @return The application's current User
     */
    public User getCurrentUser() {
        return ((MentoringApplication) getApplication()).getAuthenticatedUser();
    }

    /**
     * Change the current ApplicationStep to STEP_INIT
     */
    protected void changeApplicationStepToInit() {
        getApplicationStep().changeToInit();
    }

    /**
     * Change the current ApplicationStep to STEP_LIST
     */
    protected void changeApplicationStepToList() {
        this.getApplicationStep().changeToList();
    }

    /**
     * Change the current ApplicationStep to STEP_DISPLAY
     */
    protected void changeApplicationStepToDisplay() {
        getApplicationStep().changeToDisplay();
    }

    /**
     * Change the current ApplicationStep to STEP_EDIT
     */
    protected void changeApplicationStepToEdit() {
        getApplicationStep().changeToEdit();
    }

    /**
     * Change the current ApplicationStep to STEP_SAVE
     */
    protected void changeApplicationStepToSave() {
        getApplicationStep().changeToSave();
    }

    /**
     * Change the current ApplicationStep to STEP_CREATE
     */
    protected void changeApplicationStepToCreate() {
        getApplicationStep().changetocreate();
    }

    protected void changeApplicationStepToDownload() {
        getApplicationStep().changetoDownload();
    }

    /**
     * @return The application's current step
     */
    public ApplicationStep getApplicationStep() {
        return ((MentoringApplication) getApplication()).getApplicationStep();
    }

    public boolean isViewListEditButton() {
        return getRelatedViewModel().isViewListEditButton();
    }

    public void setViewListEditButton(boolean viewListEditButton) {
        this.relatedViewModel.setViewListEditButton(viewListEditButton);
    }

    public boolean isViewListRemoveButton() {
        return getRelatedViewModel().isViewListRemoveButton();
    }

    public void setViewListRemoveButton(boolean viewListRemoveButton) {
        this.relatedViewModel.setViewListRemoveButton(viewListRemoveButton);
    }

    public Integer getPositionRemoved() {
        return positionRemoved;
    }

    public void setPositionRemoved(Integer positionRemoved) {
        this.positionRemoved = positionRemoved;
    }

    public void displaySearchResults() {
        // Implementation for displaying search results
    }
}
