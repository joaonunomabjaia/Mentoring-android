package mz.org.csaude.mentoring.base.service;

import android.app.Application;

import java.sql.SQLException;

import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.base.databasehelper.MentoringDataBaseHelper;
import mz.org.csaude.mentoring.base.databasehelper.MentoringDatabase;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.model.user.User;

public abstract class BaseServiceImpl<T extends BaseModel> implements BaseService<T>{

    protected MentoringDatabase dataBaseHelper;

    protected MentoringApplication application;
    public static MentoringApplication app;
    protected User currentUser;

    public BaseServiceImpl(Application application) {
        try {
            init(application);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void init(Application application) throws SQLException {
        this.dataBaseHelper = MentoringDatabase.getInstance(application);
        this.application= (MentoringApplication) application;
        BaseServiceImpl.app = (MentoringApplication) application;
    }

    public MentoringDataBaseHelper getDataBaseHelper() {
        return dataBaseHelper;
    }

    public MentoringApplication getApplication() {
        return application;
    }

    public static MentoringApplication getApp() {
        return app;
    }

    public User getCurrentUser() throws SQLException {
        return ((MentoringApplication) this.application).getAuthenticatedUser();
    }

    @Override
    public T getByuuid(String uuid) throws SQLException {
        return null;
    }

    public void close() {
        getDataBaseHelper().close();
    }
}
