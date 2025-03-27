package mz.org.csaude.mentoring.base.worker;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.base.searchparams.AbstractSearchParams;
import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.base.viewModel.SearchPaginator;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.util.Http;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.workSchedule.executor.ExecutorThreadProvider;

public abstract class BaseWorker<T extends BaseModel> extends Worker
        implements SearchPaginator<T>, RestResponseListener<T> {

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean hasFailed = new AtomicBoolean(false);

    public static final int RECORDS_PER_SEARCH = 200;
    public static final String WORK_STATUS_PERFORMING = "PERFORMING";
    public static final String WORK_STATUS_FINISHED = "FINISHED";
    public static final String WORK_STATUS_STARTING = "STARTING";

    protected int offset = 0;
    protected long newRecsQty;
    protected long updatedRecsQty;
    protected int notificationId;
    protected String workStatus;
    protected String requestType;

    protected ExecutorThreadProvider executorThreadProvider;
    protected Context context;

    public BaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.notificationId = ThreadLocalRandom.current().nextInt();
        this.executorThreadProvider = ExecutorThreadProvider.getInstance();
        this.requestType = getInputData().getString("requestType");
        this.workStatus = WORK_STATUS_STARTING;
    }

    protected MentoringApplication getApplication() {
        return (MentoringApplication) getApplicationContext();
    }

    @Override
    public Result doWork() {
        try {
            doOnStart();
            changeStatusToPerforming();
            fullLoadRecords();
            waitUntilFinished();

            if (hasFailed.get()) {
                return Result.failure();
            }
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            hasFailed.set(true);  // Mark as failed
            return Result.failure();
        }
    }

    private void waitUntilFinished() throws InterruptedException {
        while (workStatus.equals(WORK_STATUS_PERFORMING)) {
            Thread.sleep(3000);
        }
    }

    protected abstract void doOnStart();

    protected void issueNotification(String channel, String msg, boolean progressStatus) throws InterruptedException {
        Utilities.issueNotification(
                NotificationManagerCompat.from(getApplicationContext()),
                getApplicationContext(), msg, channel, progressStatus, this.notificationId
        );
    }

    @Override
    public List<T> doSearch(long offset, long limit) throws Exception {
        return null;  // Override in subclasses with actual logic
    }

    @Override
    public void displaySearchResults() {
        // Optional override
    }

    @Override
    public AbstractSearchParams<T> initSearchParams() {
        return null;  // Override in subclasses with actual logic
    }

    protected void fullLoadRecords() throws Exception {
        isRunning.set(true);
        executorThreadProvider.getExecutorService().execute(() -> {
            try {
                doOnlineSearch(offset, RECORDS_PER_SEARCH);
            } catch (Exception e) {
                e.printStackTrace();
                hasFailed.set(true);  // Mark as failed on exception
            } finally {
                isRunning.set(false);  // Ensure state cleanup
            }
        });
    }

    protected void doAfterSearch(String flag, List<T> recs) throws Exception {
        if ((Utilities.listHasElements(recs) || flag.equals(BaseRestService.REQUEST_SUCESS))
                && recs.size() < RECORDS_PER_SEARCH) {
            changeStatusToFinished();
            doOnFinish();
        } else if (Utilities.listHasElements(recs) || flag.equals(BaseRestService.REQUEST_SUCESS)) {
            newRecsQty += recs.size();
            offset += RECORDS_PER_SEARCH;
            doSave(recs);
            fullLoadRecords();  // Continue loading records
        } else {
            changeStatusToFinished();
            doOnFinish();
        }
    }

    protected abstract void doOnFinish();

    protected abstract void doSave(List<T> recs);

    @Override
    public void doOnRestSucessResponse(String flag) {
        // Optional override
    }

    @Override
    public void doOnRestErrorResponse(String errormsg) {
        hasFailed.set(true);  // Mark as failed
        changeStatusToFinished();
        doOnFinish();
    }

    @Override
    public void doOnRestSucessResponseObjects(String flag, List<T> objects) {
        // Optional override
    }

    @Override
    public void doOnResponse(String flag, List<T> objects) {
        try {
            doAfterSearch(flag, objects);
        } catch (Exception e) {
            e.printStackTrace();
            hasFailed.set(true);  // Mark as failed on exception
        }
    }

    @Override
    public void onResponse(String flag, HashMap<String, Object> result) {
        // Optional override
    }

    public void changeStatusToPerforming() {
        workStatus = WORK_STATUS_PERFORMING;
    }

    public void changeStatusToFinished() {
        workStatus = WORK_STATUS_FINISHED;
        isRunning.set(false);  // Ensure the worker is marked as finished
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public boolean isPOSTRequest() {
        return Utilities.stringHasValue(requestType)
                && requestType.equalsIgnoreCase(String.valueOf(Http.POST));
    }
}
