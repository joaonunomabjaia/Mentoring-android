package mz.org.csaude.mentoring.viewmodel.setting;

import static mz.org.csaude.mentoring.util.Constants.PREF_METADATA_SYNC_STATUS;
import static mz.org.csaude.mentoring.util.Constants.PREF_METADATA_SYNC_TIME;
import static mz.org.csaude.mentoring.util.Constants.PREF_SESSION_TIMEOUT;
import static mz.org.csaude.mentoring.util.Constants.PREF_SESSION_TIMEOUT_MINUTES;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.sync.SyncStatusAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.listner.rest.ServerStatusListener;
import mz.org.csaude.mentoring.model.sync.SyncStatus;
import mz.org.csaude.mentoring.util.Constants;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.SyncType;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.workSchedule.TaggedWorkRequest;
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
        autoLogoutTime.setValue(encryptedSharedPreferences.getString(PREF_SESSION_TIMEOUT, String.valueOf(PREF_SESSION_TIMEOUT_MINUTES)));

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
            if (getRelatedActivity() != null) {
                BaseActivity baseActivity = getRelatedActivity();
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

    public void doSync() {
        LayoutInflater inflater = LayoutInflater.from(getRelatedActivity());
        View dialogView = inflater.inflate(R.layout.dialog_sync_status, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.sync_status_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getRelatedActivity()));

        Button closeButton = dialogView.findViewById(R.id.btn_close_dialog);
        Button retryButton = dialogView.findViewById(R.id.btn_retry_sync);
        retryButton.setVisibility(View.GONE);
        closeButton.setVisibility(View.GONE);

        List<TaggedWorkRequest> requests = (syncType == SyncType.METADATA)
                ? workerScheduleExecutor.syncNowMeteData()
                : workerScheduleExecutor.syncNowData();

        List<SyncStatus> syncStatuses = new ArrayList<>();
        for (TaggedWorkRequest twr : requests) {
            syncStatuses.add(new SyncStatus(twr.getTag(), WorkInfo.State.ENQUEUED));
        }

        SyncStatusAdapter adapter = new SyncStatusAdapter(recyclerView, syncStatuses, getRelatedActivity());
        recyclerView.setAdapter(adapter);

        AlertDialog statusDialog = new AlertDialog.Builder(getRelatedActivity())
                .setTitle(R.string.syncing_data_please_wait)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        statusDialog.show();

        closeButton.setOnClickListener(v -> statusDialog.dismiss());

        retryButton.setOnClickListener(v -> {
            statusDialog.dismiss();
            doSync(); // Retry the whole sync process
        });

        Handler mainHandler = new Handler(Looper.getMainLooper());
        List<UUID> completedRequests = new ArrayList<>();
        AtomicBoolean alreadyHandled = new AtomicBoolean(false);

        mainHandler.post(() -> {
            for (int i = 0; i < requests.size(); i++) {
                final int index = i;
                TaggedWorkRequest twr = requests.get(i);
                UUID id = twr.getRequest().getId();

                workerScheduleExecutor.getWorkManager().getWorkInfoByIdLiveData(id)
                        .observe(getRelatedActivity(), info -> {
                            if (info != null) {
                                syncStatuses.get(index).setState(info.getState());
                                adapter.notifyItemChanged(index);

                                if (info.getState().isFinished() && !completedRequests.contains(id)) {
                                    completedRequests.add(id);

                                    if (completedRequests.size() == requests.size() && !alreadyHandled.get()) {
                                        alreadyHandled.set(true);

                                        boolean allSucceeded = true;
                                        for (UUID uid : completedRequests) {
                                            try {
                                                WorkInfo wInfo = workerScheduleExecutor.getWorkManager().getWorkInfoById(uid).get();
                                                if (wInfo == null || wInfo.getState() != WorkInfo.State.SUCCEEDED) {
                                                    allSucceeded = false;
                                                    break;
                                                }
                                            } catch (Exception e) {
                                                allSucceeded = false;
                                                break;
                                            }
                                        }

                                        if (allSucceeded) {
                                            getApplication().saveDefaultLastSyncDate(DateUtilities.getCurrentDate());
                                            statusDialog.setTitle("Sincronização concluída com sucesso!");
                                            closeButton.setVisibility(View.VISIBLE);
                                        } else {
                                            retryButton.setVisibility(View.VISIBLE);
                                            statusDialog.setTitle("Sincronização concluída com falhas!");
                                        }
                                    }

                                    workerScheduleExecutor.getWorkManager()
                                            .getWorkInfoByIdLiveData(id)
                                            .removeObservers(getRelatedActivity());
                                }
                            }
                        });
            }
        });
    }





    @Override
    public void onServerStatusChecked(boolean isOnline, boolean isSlow) {
        if (isOnline) {
            if (isSlow) {
                // Show warning: Server is slow
                showSlowConnectionWarning(getRelatedActivity());
            }
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
