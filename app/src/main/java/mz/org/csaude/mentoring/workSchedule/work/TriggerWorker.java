package mz.org.csaude.mentoring.workSchedule.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;

public class TriggerWorker extends BaseWorker {

    public TriggerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // This worker simply triggers the next chain
        return Result.success();
    }

    @Override
    protected void doOnStart() {

    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List recs) {

    }
}