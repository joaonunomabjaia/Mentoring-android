package mz.org.csaude.mentoring.base.service;

import android.app.Application;

import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;

import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.metadata.SyncDataService;
import mz.org.csaude.mentoring.workSchedule.executor.ExecutorThreadProvider;
import retrofit2.Retrofit;

public class BaseRestService {

    public static final String REQUEST_SUCESS = "REQUEST_SUCESS";
    public static final String REQUEST_ERROR = "REQUEST_ERROR";
    public static final String REQUEST_NO_DATA = "REQUEST_NO_DATA";

    protected MentoringApplication application;

    protected User currentUser;
    protected static SyncDataService syncDataService;

    protected Gson gson;

    public BaseRestService(Application application, User currentUser) {
        init((MentoringApplication) application,currentUser);
    }

    public BaseRestService(Application application) {
        init((MentoringApplication) application,null);
    }


    public MentoringApplication getApplication() {
        return application;
    }

    private void init(MentoringApplication application, User currentUser){


        this.currentUser = currentUser;
        this.application = application;

        this.gson = new Gson();


        syncDataService = getRetrofit().create(SyncDataService.class);

    }
    protected Retrofit getRetrofit() {
        return getApplication().getRetrofit();
    }

    public ExecutorService getServiceExecutor() {
        return this.application.getServiceExecutor();
    }


    public SyncDataService getSyncDataService() {
        return syncDataService;
    }
}
