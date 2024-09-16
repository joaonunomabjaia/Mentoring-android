package mz.org.csaude.mentoring.base.viewModel;

import android.app.Application;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.AndroidViewModel;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.base.fragment.GenericFragment;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.common.ApplicationStep;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.util.LoadingDialog;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.workSchedule.executor.WorkerScheduleExecutor;

public abstract class BaseViewModel extends AndroidViewModel implements Observable {


    private PropertyChangeRegistry callbacks;
    private BaseActivity relatedActivity;


    //protected AppSettingsService settingsService;


    LoadingDialog loadingDialog;


    private boolean viewListEditButton;
    private boolean viewListRemoveButton;

    protected Listble selectedListble;



    protected BaseModel relatedRecord;
    protected List<Listble> selectedListbles;

    private NotificationManagerCompat notificationManager;

    protected WorkerScheduleExecutor workerScheduleExecutor;
    protected int notificationId;


    protected GenericFragment relatedFragment;

    public BaseViewModel(@NonNull Application application) {
        super(application);

        callbacks = new PropertyChangeRegistry();

        notificationManager = NotificationManagerCompat.from(getApplication());
        this.notificationId = ThreadLocalRandom.current().nextInt();

    }

    public NotificationManagerCompat getNotificationManager() {
        return notificationManager;
    }

    public void issueNotification(String contentMsg, String channel, boolean progressStatus){
        //Utilities.issueNotification(getNotificationManager(), getApplication(),contentMsg, channel, progressStatus, this.notificationId);
    }

    public BaseActivity getRelatedActivity() {
        return relatedActivity;
    }

    public abstract void preInit();

    public void setRelatedActivity(BaseActivity relatedActivity) {
        this.relatedActivity = relatedActivity;
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    /**
     * Notifies observers that all properties of this instance have changed.
     */
    protected void notifyChange() {
        callbacks.notifyCallbacks(this, 0, null);
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    protected void notifyPropertyChanged(int fieldId) {
        callbacks.notifyCallbacks(this, fieldId, null);
    }

    @NonNull
    @Override
    public MentoringApplication getApplication() {
        return super.getApplication();
    }

    @Bindable
    public ApplicationStep getCurrentStep(){
        return getApplication().getApplicationStep();
    }

    public String getAppVersionNumber(){
        if (getRelatedActivity() == null) return null;
        return Utilities.parseDoubleToString(getRelatedActivity().getAppVersionNumber());
    }

    public String getAppVersionName(){
        if (getRelatedActivity() == null) return null;
        return getRelatedActivity().getAppVersionName();
    }

    public String getAppName() {
        return getApplication().getString(R.string.app_name);
    }
    public boolean isViewListEditButton() {
        return viewListEditButton;
    }

    public void setViewListEditButton(boolean viewListEditButton) {
        this.viewListEditButton = viewListEditButton;
    }

    public boolean isViewListRemoveButton() {
        return viewListRemoveButton;
    }

    public void setViewListRemoveButton(boolean viewListRemoveButton) {
        this.viewListRemoveButton = viewListRemoveButton;
    }

    public Listble getSelectedListble() {
        return selectedListble;
    }

    public void setSelectedListble(Listble selectedListble) {
        this.selectedListble = selectedListble;
    }

    public LoadingDialog getLoadingDialog() {
        return loadingDialog;
    }

    public void openCollapse(View view) {
        if (view.getParent()== null) {
            //relatedView.setVisibility(View.GONE);
        } else {
            //relatedView.setVisibility(View.VISIBLE);
        }
    }

    public void setSelectedRecord(Serializable relatedRecord) {
        this.selectedListble = (Listble) relatedRecord;
    }

    public BaseModel getRelatedRecord() {
        return relatedRecord;
    }

    public User getCurrentUser() {
        return getApplication().getAuthenticatedUser();
    }

    public Tutor getCurrentTutor() {
        return getApplication().getCurrMentor();
    }

    public GenericFragment getRelatedFragment() {
        return relatedFragment;
    }

    public void setRelatedFragment(GenericFragment relatedFragment) {
        this.relatedFragment = relatedFragment;
    }

    public ExecutorService getExecutorService() {
        return getApplication().getServiceExecutor();
    }

    protected void runOnMainThread(Runnable task) {
        getRelatedActivity().runOnUiThread(task);
    }

    public void dismissProgress(Dialog progress) {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    protected SharedPreferences getEncryptedSharedPreferences(){
        return getApplication().getEncryptedSharedPreferences();
    }
}
