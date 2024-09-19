package mz.org.csaude.mentoring.base.worker;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.base.searchparams.AbstractSearchParams;
import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.base.viewModel.SearchPaginator;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.util.Http;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.workSchedule.executor.ExecutorThreadProvider;

public abstract class BaseWorker<T extends BaseModel> extends Worker implements SearchPaginator<T>, RestResponseListener<T> {


    public static final int RECORDS_PER_SEARCH = 200;
    protected int offset = 0;
    public static final String WORK_STATUS_PERFORMING = "PERFORMING";
    public static final String WORK_STATUS_FINISHED = "FINISHED";
    public static final String WORK_STATUS_STARTING = "STARTING";
    protected String workStatus;
    public static Application app;
    protected long newRecsQty;
    protected long updatedRecsQty;
    protected int notificationId;

    protected ExecutorThreadProvider executorThreadProvider;

    protected Context context;

    protected String requestType;

    public BaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        setWorkStatus(WORK_STATUS_STARTING);
        this.executorThreadProvider = ExecutorThreadProvider.getInstance();

        this.context = context;
        this.notificationId = ThreadLocalRandom.current().nextInt();

        requestType = getInputData().getString("requestType");
    }

    protected MentoringApplication getApplication() {
        return (MentoringApplication) getApplicationContext();
    }
    @Override
    public Result doWork() {
        try {
            doOnStart();

            changeStatusToPerforming();
            fullLoadRecords();        } catch (SQLException e) {
            e.printStackTrace();
            return Result.failure();
        }

        while (isRunning()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return Result.success();
    }

    protected abstract void doOnStart();

    protected void issueNotification(String channel, String msg, boolean progressStatus) throws InterruptedException {
        Utilities.issueNotification(NotificationManagerCompat.from(getApplicationContext()), getApplicationContext(), msg, channel, progressStatus, this.notificationId);
    }

    @Override
    public List<T> doSearch(long offset, long limit) throws SQLException {
        return null;
    }

    @Override
    public void displaySearchResults() {

    }

    @Override
    public AbstractSearchParams<T> initSearchParams() {
        return null;
    }

    protected void fullLoadRecords() throws SQLException {
        this.executorThreadProvider.getExecutorService().execute(() -> {
            try {
                doOnlineSearch(this.offset, RECORDS_PER_SEARCH);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    protected void doAfterSearch(String flag, List<T> recs) throws SQLException {
        if ((Utilities.listHasElements(recs) || flag.equals(BaseRestService.REQUEST_SUCESS)) && recs.size() < RECORDS_PER_SEARCH) {
            changeStatusToFinished();
            doOnFinish();
        } else if (Utilities.listHasElements(recs) || flag.equals(BaseRestService.REQUEST_SUCESS)) {
            this.newRecsQty = this.newRecsQty + recs.size();
            this.offset = this.offset + RECORDS_PER_SEARCH;
            doSave(recs);
            fullLoadRecords();
        } else {
            changeStatusToFinished();
            doOnFinish();
        }
    }

    protected boolean hasNewRescs () {
        return getNewRecsQty() > 0 || getUpdatedRecsQty() > 0;
    }

    protected abstract void doOnFinish();

    protected abstract void doSave(List<T> recs);

    @Override
    public void doOnRestSucessResponse(String flag) {

    }

    @Override
    public void doOnRestErrorResponse(String errormsg) {
        changeStatusToFinished();
        doOnFinish();
    }

    @Override
    public void doOnRestSucessResponseObjects(String flag, List<T> objects) {

    }

    @Override
    public void doOnResponse(String flag, List<T> objects) {
        try {
            doAfterSearch(flag, objects);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void onResponse(String flag, HashMap<String, Object> result) {

    }

    public long getUpdatedRecsQty() {
        return updatedRecsQty;
    }

    public long getNewRecsQty() {
        return newRecsQty;
    }

    public void setNewRecsQty(long newRecsQty) {
        this.newRecsQty = newRecsQty;
    }

    protected void changeStatusToPerforming () {
        this.setWorkStatus(WORK_STATUS_PERFORMING);
    }

    protected void changeStatusToFinished () {
        this.setWorkStatus(WORK_STATUS_FINISHED);
    }

    protected boolean isRunning () {
        return this.workStatus.equals(WORK_STATUS_PERFORMING);
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    protected boolean isPOSTRequest() {
        return Utilities.stringHasValue(requestType) && requestType.equalsIgnoreCase(String.valueOf(Http.POST));
    }
}
