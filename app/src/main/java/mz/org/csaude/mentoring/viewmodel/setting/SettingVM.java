package mz.org.csaude.mentoring.viewmodel.setting;

import static mz.org.csaude.mentoring.util.Constants.PREF_METADATA_SYNC_STATUS;
import static mz.org.csaude.mentoring.util.Constants.PREF_METADATA_SYNC_TIME;
import static mz.org.csaude.mentoring.util.Constants.PREF_SESSION_TIMEOUT;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;

import java.util.Locale;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.listner.rest.ServerStatusListener;
import mz.org.csaude.mentoring.util.Constants;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.SyncType;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.workSchedule.executor.WorkerScheduleExecutor;

public class SettingVM extends BaseViewModel implements ServerStatusListener {

    private final WorkerScheduleExecutor workerScheduleExecutor;
    private final SharedPreferences encryptedSharedPreferences;

    public MutableLiveData<Boolean> isAutoSyncEnabled = new MutableLiveData<>();
    public MutableLiveData<String> syncInterval = new MutableLiveData<>();
    public MutableLiveData<String> autoLogoutTime = new MutableLiveData<>();
    public MutableLiveData<String> selectedLanguage = new MutableLiveData<>();
    public MutableLiveData<Boolean> isBiometricEnabled = new MutableLiveData<>();


    private static final int MIN_SYNC_INTERVAL_HOURS = 1;    // Minimum allowed sync interval in hours
    private static final int MAX_SYNC_INTERVAL_HOURS = 24;   // Maximum allowed sync interval in hours (1 day)
    private static final int MIN_LOGOUT_TIME = 1;            // Minimum allowed auto logout time in minutes
    private static final int MAX_LOGOUT_TIME = 60;           // Maximum allowed auto logout time in minutes
    private SyncType syncType;

    public SettingVM(@NonNull Application application) {
        super(application);

        MentoringApplication app = (MentoringApplication) application;
        encryptedSharedPreferences = app.getEncryptedSharedPreferences();
        workerScheduleExecutor = WorkerScheduleExecutor.getInstance(application);

        // Load saved settings
        isAutoSyncEnabled.setValue(encryptedSharedPreferences.getBoolean(PREF_METADATA_SYNC_STATUS, true));
        syncInterval.setValue(encryptedSharedPreferences.getString(PREF_METADATA_SYNC_TIME, "2"));
        autoLogoutTime.setValue(encryptedSharedPreferences.getString(PREF_SESSION_TIMEOUT, "5"));

        // Observe changes and save them
        isAutoSyncEnabled.observeForever(this::onAutoSyncChanged);
        // Removed validations from observers as validations are now handled via buttons
        syncInterval.observeForever(this::onSyncIntervalChanged);
        autoLogoutTime.observeForever(this::onAutoLogoutTimeChanged);

        isBiometricEnabled.setValue(encryptedSharedPreferences.getBoolean(Constants.PREF_BIOMETRIC_ENABLED, false));

        isBiometricEnabled.observeForever(enabled ->
                encryptedSharedPreferences.edit().putBoolean(Constants.PREF_BIOMETRIC_ENABLED, enabled).apply()
        );

    }

    private void onAutoSyncChanged(Boolean isEnabled) {
        encryptedSharedPreferences.edit().putBoolean(PREF_METADATA_SYNC_STATUS, isEnabled).apply();
        handleAutoSyncChange(isEnabled);
    }

    public void onBiometricToggleChanged(boolean isEnabled) {
        isBiometricEnabled.setValue(isEnabled);
    }

    private void onSyncIntervalChanged(String intervalStr) {
        encryptedSharedPreferences.edit().putString(PREF_METADATA_SYNC_TIME, intervalStr).apply();
        // No validation here; it will be handled via the Validate button
    }

    private void onAutoLogoutTimeChanged(String logoutTimeStr) {
        encryptedSharedPreferences.edit().putString(PREF_SESSION_TIMEOUT, logoutTimeStr).apply();
        // No validation here; it will be handled via the Validate button
    }

    private void handleAutoSyncChange(Boolean isEnabled) {
        if (isEnabled) {
            // Schedule periodic sync with the current interval
            String intervalStr = syncInterval.getValue();
            if (validateSyncInterval(intervalStr)) {
                int intervalHours = Integer.parseInt(intervalStr);
                //workerScheduleExecutor.schedulePeriodicSync(intervalHours * 60); // Convert hours to minutes
            }
        } else {
            // Cancel periodic sync
            workerScheduleExecutor.cancelPeriodicSync();
        }
    }

    /**
     * Method called when the Sync Interval Validate button is clicked.
     */
    public void onValidateSyncIntervalClicked() {
        String intervalStr = syncInterval.getValue();
        if (validateSyncInterval(intervalStr)) {
            int intervalHours = Integer.parseInt(intervalStr);
            encryptedSharedPreferences.edit().putString(PREF_METADATA_SYNC_TIME, intervalStr).apply();

            if (Boolean.TRUE.equals(isAutoSyncEnabled.getValue())) {
                workerScheduleExecutor.schedulePeriodicDataSync(); // Convert hours to minutes
                workerScheduleExecutor.schedulePeriodicMetaDataSync();
                Utilities.displayAlertDialog(
                        getRelatedActivity(),
                        getApplication().getString(R.string.auto_logout_time_validated_successfully)
                ).show();
            }
        }
    }

    /**
     * Method called when the Auto Logout Time Validate button is clicked.
     */
    public void onValidateAutoLogoutTimeClicked() {
        String logoutTimeStr = autoLogoutTime.getValue();
        if (validateAutoLogoutTime(logoutTimeStr)) {
            int logoutTimeMinutes = Integer.parseInt(logoutTimeStr);
            encryptedSharedPreferences.edit().putString(PREF_SESSION_TIMEOUT, logoutTimeStr).apply();

            // Notify the BaseActivity to update its timer
            if (getRelatedActivity() instanceof BaseActivity) {
                BaseActivity baseActivity = (BaseActivity) getRelatedActivity();
                baseActivity.updateAutoLogoutTime(logoutTimeMinutes);
            }

            Utilities.displayAlertDialog(
                    getRelatedActivity(),
                    getApplication().getString(R.string.auto_logout_time_validated_successfully)
            ).show();
        }
    }

    /**
     * Validates the Sync Interval input.
     *
     * @param intervalStr The input string representing sync interval in hours.
     * @return true if valid, false otherwise.
     */
    private boolean validateSyncInterval(String intervalStr) {
        if (Utilities.isValidNumber(intervalStr)) {
            int intervalHours = Integer.parseInt(intervalStr);
            if (intervalHours >= MIN_SYNC_INTERVAL_HOURS && intervalHours <= MAX_SYNC_INTERVAL_HOURS) {
                return true;
            } else {
                Utilities.displayAlertDialog(
                        getRelatedActivity(),
                        getApplication().getString(R.string.invalid_sync_interval_range_hours, MIN_SYNC_INTERVAL_HOURS, MAX_SYNC_INTERVAL_HOURS)
                ).show();
            }
        } else {
            Utilities.displayAlertDialog(
                    getRelatedActivity(),
                    getApplication().getString(R.string.invalid_sync_interval)
            ).show();
        }
        return false;
    }

    /**
     * Validates the Auto Logout Time input.
     *
     * @param logoutTimeStr The input string representing auto logout time in minutes.
     * @return true if valid, false otherwise.
     */
    private boolean validateAutoLogoutTime(String logoutTimeStr) {
        if (Utilities.isValidNumber(logoutTimeStr)) {
            int logoutTimeMinutes = Integer.parseInt(logoutTimeStr);
            if (logoutTimeMinutes >= MIN_LOGOUT_TIME && logoutTimeMinutes <= MAX_LOGOUT_TIME) {
                return true;
            } else {
                Utilities.displayAlertDialog(
                        getRelatedActivity(),
                        getApplication().getString(R.string.invalid_auto_logout_time_range, MIN_LOGOUT_TIME, MAX_LOGOUT_TIME)
                ).show();
            }
        } else {
            Utilities.displayAlertDialog(
                    getRelatedActivity(),
                    getApplication().getString(R.string.invalid_auto_logout_time)
            ).show();
        }
        return false;
    }

    public void onSyncNowClicked() {
        this.syncType = SyncType.DATA;
        getApplication().isServerOnline(this);
    }

    public void onSyncMetadateNowClicked() {
        this.syncType = SyncType.METADATA;
        getApplication().isServerOnline(this);
    }

    @Override
    public void preInit() {
        // Implement if needed
    }

    private void doSync() {
        // Create a ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(getRelatedActivity());
        progressDialog.setMessage(getApplication().getString(R.string.syncing_data_please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        OneTimeWorkRequest request;
        if (syncType == SyncType.METADATA) {
            request = workerScheduleExecutor.syncNowMeteData();
        } else {
            request = workerScheduleExecutor.syncNowData();
        }
        // Schedule the sync work

        workerScheduleExecutor.getWorkManager().getWorkInfoByIdLiveData(request.getId()).observe(getRelatedActivity(), new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo info) {
                if (info != null) {
                    if (info.getState() == WorkInfo.State.SUCCEEDED) {
                        // Sync succeeded
                        getApplication().saveDefaultLastSyncDate(DateUtilities.getCurrentDate());
                        Utilities.displayAlertDialog(
                                getRelatedActivity(),
                                getApplication().getString(R.string.sync_completed_successfully)
                        ).show();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } else if (info.getState() == WorkInfo.State.FAILED) {
                        // Sync failed
                        Utilities.displayAlertDialog(
                                getRelatedActivity(),
                                getApplication().getString(R.string.sync_failed)
                        ).show();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onServerStatusChecked(boolean isOnline) {
        if (isOnline) {
            doSync();
        } else {
            Utilities.displayAlertDialog(
                    getRelatedActivity(),
                    getApplication().getString(R.string.server_unavailable)
            ).show();
        }
    }

    @Override
    protected void onCleared() {
        // Remove observers to prevent memory leaks
        isAutoSyncEnabled.removeObserver(this::onAutoSyncChanged);
        syncInterval.removeObserver(this::onSyncIntervalChanged);
        autoLogoutTime.removeObserver(this::onAutoLogoutTimeChanged);
        super.onCleared();
    }

    /**
     * Initialize the spinner and set up the listener for language selection.
     */
    public void initSelectedLanguage(Spinner languageSpinner) {
        // Set the selected language in the spinner
        String currentLanguageCode = encryptedSharedPreferences.getString(Constants.PREF_SELECTED_LANGUAGE, "en");
        int selectedPosition = getLanguagePosition(currentLanguageCode, languageSpinner);
        languageSpinner.setSelection(selectedPosition);

        // Set the listener to detect changes in language selection
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguageCode = getLanguageCode(position);
                if (!selectedLanguageCode.equals(selectedLanguage.getValue())) {
                    selectedLanguage.setValue(selectedLanguageCode);
                    saveSelectedLanguage(selectedLanguageCode);
                    setLocale(selectedLanguageCode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    /**
     * Save the selected language in SharedPreferences.
     */
    private void saveSelectedLanguage(String languageCode) {
        encryptedSharedPreferences.edit().putString(Constants.PREF_SELECTED_LANGUAGE, languageCode).apply();
    }

    /**
     * Get the language code based on the spinner position.
     */
    private String getLanguageCode(int position) {
        String[] languageCodes = getApplication().getResources().getStringArray(mz.org.csaude.mentoring.R.array.language_codes);
        return languageCodes[position];
    }

    /**
     * Get the spinner position based on the language code.
     */
    private int getLanguagePosition(String languageCode, Spinner languageSpinner) {
        String[] languageCodes = getApplication().getResources().getStringArray(mz.org.csaude.mentoring.R.array.language_codes);
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(languageCode)) {
                return i;
            }
        }
        return 0; // Default to English if not found
    }

    /**
     * Set the locale for the app.
     */
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            config.setLocale(locale);
            getApplication().getBaseContext().createConfigurationContext(config);
        } else {
            config.locale = locale;
            getApplication().getResources().updateConfiguration(config, getApplication().getResources().getDisplayMetrics());
        }

        // Refresh the current activity to apply the new language
        if (getRelatedActivity() != null) {
            getRelatedActivity().recreate();
        }
    }
}
