package mz.org.csaude.mentoring.service.tutored;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.tutored.TutoredDao;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.service.employee.EmployeeService;
import mz.org.csaude.mentoring.service.employee.EmployeeServiceImpl;
import mz.org.csaude.mentoring.util.LifeCycleStatus;
import mz.org.csaude.mentoring.util.SyncSatus;

public class TutoredServiceImpl extends BaseServiceImpl<Tutored> implements TutoredService{

    TutoredDao tutoredDao;


    EmployeeService employeeService;

    public TutoredServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.tutoredDao = getDataBaseHelper().getTutoredDao();
        this.employeeService = new EmployeeServiceImpl(application);
    }

    public Tutored save(Tutored tutored) throws SQLException {
        this.tutoredDao.insert(tutored);
        return tutored;

    }

    @Override
    public Tutored update(Tutored record) throws SQLException {
        this.tutoredDao.update(record);
        return record;
    }

    @Override
    public int delete(Tutored record) throws SQLException {
        return this.tutoredDao.delete(record);
    }

    @Override
    public List<Tutored> getAll() throws SQLException {
        return this.tutoredDao.queryForAll();
    }

    @Override
    public Tutored getById(int id) throws SQLException {
        return this.tutoredDao.queryForId(id);
    }

    @Override
    public void savedOrUpdateTutoreds(List<Tutored> tutoreds) throws SQLException {
        for (Tutored tutored: tutoreds) {
            savedOrUpdateTutored(tutored);
        }
    }

    @Override
    public Tutored savedOrUpdateTutored(Tutored tutored) throws SQLException {

        Tutored t = this.tutoredDao.getByUuid(tutored.getUuid());
        if (t != null) {
            tutored.setId(t.getId());
        }
        getApplication().getEmployeeService().saveOrUpdateEmployee(tutored.getEmployee());
        this.tutoredDao.createOrUpdate(tutored);
        return tutored;
    }

    @Override
    public List<Tutored> getAllOfRonda(Ronda currRonda) throws SQLException {
        return  this.tutoredDao.getAllOfRonda(currRonda.getId());
    }

    @Override
    public List<Tutored> getAllOfRondaForZeroEvaluation(Ronda currRonda) throws SQLException {
        return  this.tutoredDao.getAllOfRondaForZeroEvaluation(currRonda.getId());
    }

    @Override
    public List<Tutored> getAllOfHealthFacility(HealthFacility healthFacility) throws SQLException {
        return this.tutoredDao.getAllOfHealthFacility(healthFacility.getId(), String.valueOf(LifeCycleStatus.ACTIVE));
    }


    @Override
    public List<Tutored> getAllNotSynced() throws SQLException {
        List<Tutored> tutoreds = this.tutoredDao.getAllNotSynced(String.valueOf(SyncSatus.PENDING));
        for (Tutored tutored : tutoreds) {
            tutored.getEmployee().setLocations(getApplication().getLocationService().getAllOfEmploee(tutored.getEmployee()));
        }
        return tutoreds;
    }

    @Override
    public List<Tutored> getAllForMentoringRound(HealthFacility healthFacility, boolean zeroEvaluation) throws SQLException {
        return this.tutoredDao.getAllForMentoringRound(healthFacility.getId(), String.valueOf(LifeCycleStatus.ACTIVE), zeroEvaluation);
    }

    @Override
    public List<Tutored> getAllOfRondaForNewRonda(HealthFacility healthFacility) throws SQLException {
        return this.tutoredDao.getAllOfHealthFacilityForNewRonda(healthFacility.getId(), String.valueOf(LifeCycleStatus.ACTIVE));
    }


}
