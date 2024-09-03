package mz.org.csaude.mentoring.base.service;

import android.app.Application;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.base.databasehelper.MentoringDatabase;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.workSchedule.executor.ExecutorThreadProvider;

public abstract class BaseServiceImpl<T extends BaseModel> implements BaseService<T>{

    protected MentoringDatabase dataBaseHelper;

    protected MentoringApplication application;
    //public static MentoringApplication app;

    protected ExecutorThreadProvider executorThreadProvider;

    public BaseServiceImpl(Application application) {
        try {
            init(application);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void init(Application application) throws SQLException {
        this.application= (MentoringApplication) application;
        this.executorThreadProvider = ExecutorThreadProvider.getInstance();
        this.dataBaseHelper = MentoringDatabase.getInstance(application, ((MentoringApplication) application).getEncryptedPassphrase());

        //BaseServiceImpl.app = (MentoringApplication) application;
    }

    protected ExecutorService getExecutorService() {
        return executorThreadProvider.getExecutorService();
    }

    public MentoringDatabase getDataBaseHelper() {
        return dataBaseHelper;
    }

    public MentoringApplication getApplication() {
        return application;
    }


    public User getCurrentUser() throws SQLException {
        return this.application.getAuthenticatedUser();
    }

    /*@Override
    public T getByuuid(String uuid) throws SQLException {
        return null;
    }*/

    public void close() {
        getDataBaseHelper().close();
    }
}
