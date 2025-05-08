package mz.org.csaude.mentoring.viewmodel;


import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.Bindable;
import androidx.work.WorkInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.listner.rest.ServerStatusListener;
import mz.org.csaude.mentoring.model.sync.SyncStatus;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.login.LoginActivity;
import mz.org.csaude.mentoring.view.splash.SplashActivity;
import mz.org.csaude.mentoring.workSchedule.TaggedWorkRequest;
import mz.org.csaude.mentoring.workSchedule.executor.ExecutorThreadProvider;
import mz.org.csaude.mentoring.workSchedule.executor.WorkerScheduleExecutor;

public class SplashVM extends BaseViewModel implements RestResponseListener, ServerStatusListener {


    public SplashVM(@NonNull Application application) {
        super(application);
        setProgressMsg("Iniciando a aplicação...");
    }
    private String progressMsg;

    @Override
    public void preInit() {

    }


    @Override
    public SplashActivity getRelatedActivity() {
        return (SplashActivity) super.getRelatedActivity();
    }


    public void initAppConfiguration() {
        if (getApplication().isInitialSetupComplete()) {
            scheduleSyncDataTasks();
            goToLogin();
        } else {
            getApplication().isServerOnline(this);
        }
    }

    public void goToLogin() {
        getRelatedActivity().nextActivityFinishingCurrent(LoginActivity.class);
    }

    @Bindable
    public String getProgressMsg() {
        return progressMsg;
    }

    public void setProgressMsg(String progressMsg) {
        this.progressMsg = progressMsg;
        notifyPropertyChanged(BR.progressMsg);
    }

    @Override
    public void onServerStatusChecked(boolean isOnline, boolean isSlow) {
        if (!isOnline) {
            Utilities.displayAlertDialog(
                    getRelatedActivity(),
                    getRelatedActivity().getString(R.string.server_unavailable)
            ).show();
            return;
        }

        if (isSlow) {
            showSlowConnectionWarning(getRelatedActivity());
        }

        List<TaggedWorkRequest> initialSyncTasks = WorkerScheduleExecutor.getInstance(getApplication()).runInitialSync();
        int total = initialSyncTasks.size();
        AtomicInteger completedCount = new AtomicInteger(0);
        AtomicBoolean alreadyHandled = new AtomicBoolean(false);
        List<UUID> completedIds = new ArrayList<>();

        for (TaggedWorkRequest twr : initialSyncTasks) {
            UUID id = twr.getRequest().getId();
            String tag = twr.getTag();

            WorkerScheduleExecutor.getInstance(getApplication())
                    .getWorkManager()
                    .getWorkInfoByIdLiveData(id)
                    .observe(getRelatedActivity(), info -> {
                        if (info == null) return;

                        if (info.getState() == WorkInfo.State.RUNNING) {
                            String name = new SyncStatus(tag, info.getState()).getDisplayName();
                            int progress = completedCount.get();
                            setProgressMsg("🔄 " + name + " (" + progress + "/" + total + ")");
                        }

                        if (info.getState().isFinished() && !completedIds.contains(id)) {
                            completedIds.add(id);
                            int finished = completedCount.incrementAndGet();

                            if (finished == total && !alreadyHandled.get()) {
                                alreadyHandled.set(true);

                                boolean allSucceeded = true;
                                List<String> failedSteps = new ArrayList<>();
                                for (TaggedWorkRequest task : initialSyncTasks) {
                                    try {
                                        WorkInfo result = WorkerScheduleExecutor.getInstance(getApplication())
                                                .getWorkManager()
                                                .getWorkInfoById(task.getRequest().getId())
                                                .get();
                                        if (result == null || result.getState() != WorkInfo.State.SUCCEEDED) {
                                            allSucceeded = false;
                                            failedSteps.add(new SyncStatus(task.getTag(), result != null ? result.getState() : null).getDisplayName());
                                        }
                                    } catch (Exception e) {
                                        allSucceeded = false;
                                        failedSteps.add(new SyncStatus(task.getTag(), null).getDisplayName());
                                    }
                                }

                                if (allSucceeded) {
                                    getApplication().setInitialSetUpComplete();
                                    goToLogin();
                                } else {
                                    StringBuilder msg = new StringBuilder("Falha ao sincronizar os seguintes passos:\n");
                                    for (String failed : failedSteps) {
                                        msg.append("• ").append(failed).append("\n");
                                    }

                                    new AlertDialog.Builder(getRelatedActivity())
                                            .setTitle("Erro na sincronização inicial")
                                            .setMessage(msg.toString())
                                            .setCancelable(false)
                                            .setPositiveButton("Tentar novamente", (dialog, which) -> {
                                                // Retry the entire sync process
                                                onServerStatusChecked(true, false);
                                            })
                                            .setNegativeButton("Cancelar", null)
                                            .show();
                                }
                            }

                            WorkerScheduleExecutor.getInstance(getApplication())
                                    .getWorkManager()
                                    .getWorkInfoByIdLiveData(id)
                                    .removeObservers(getRelatedActivity());
                        }
                    });

            // Timeout (2 min)
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                WorkerScheduleExecutor.getInstance(getApplication())
                        .getWorkManager()
                        .getWorkInfoById(id)
                        .addListener(() -> {
                            try {
                                WorkInfo info = WorkerScheduleExecutor.getInstance(getApplication())
                                        .getWorkManager()
                                        .getWorkInfoById(id).get();

                                if (info != null && !info.getState().isFinished()) {
                                    WorkerScheduleExecutor.getInstance(getApplication())
                                            .getWorkManager()
                                            .cancelWorkById(id);

                                    runOnMainThread(() -> {
                                        String name = new SyncStatus(tag, info.getState()).getDisplayName();
                                        setProgressMsg("⏱ Cancelado por tempo: " + name);
                                    });
                                }
                            } catch (Exception e) {
                                Log.e("SplashVM", "Erro ao aplicar timeout ao worker " + tag, e);
                            }
                        }, ExecutorThreadProvider.getInstance().getExecutorService());
            }, 2 * 60 * 1000); // 2 minutes
        }
    }


    void scheduleSyncDataTasks() {
        WorkerScheduleExecutor.getInstance(getApplication()).schedulePeriodicDataSync();
        WorkerScheduleExecutor.getInstance(getApplication()).schedulePeriodicMetaDataSync();
        getApplication().saveDefaultLastSyncDate(DateUtilities.getCurrentDate());
    }
}
