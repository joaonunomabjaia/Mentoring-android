package mz.org.csaude.mentoring.service.tutored;

import android.app.Application;

import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.room.Transaction;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import kotlinx.coroutines.flow.Flow;
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
        tutored.setId((int) this.tutoredDao.insert(tutored));
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
        List<Tutored> tutoreds = this.tutoredDao.queryForAll();
        for (Tutored tutored : tutoreds) {
            tutored.setEmployee(getApplication().getEmployeeService().getById(tutored.getEmployeeId()));
        }
        return tutoreds;
    }

    @Override
    public Tutored getById(int id) throws SQLException {
        Tutored tutored = this.tutoredDao.queryForId(id);
        tutored.setEmployee(getApplication().getEmployeeService().getById(tutored.getEmployeeId()));
        return tutored;
    }

    @Override
    public Tutored getByuuid(String uuid) throws SQLException {
        return this.tutoredDao.getByUuid(uuid);
    }

    @Override
    @Transaction
    public void savedOrUpdateTutoreds(List<Tutored> tutoreds) throws SQLException {
        for (Tutored tutored: tutoreds) {
            savedOrUpdateTutored(tutored);
        }
    }

    @Override
    public Tutored savedOrUpdateTutored(Tutored tutored) throws SQLException {

        Tutored t = this.tutoredDao.getByUuid(tutored.getUuid());
        tutored.setEmployee(getApplication().getEmployeeService().saveOrUpdateEmployee(tutored.getEmployee()));
        if (t != null) {
            tutored.setId(t.getId());
            this.update(tutored);
        } else {
            this.save(tutored);
        }

        return tutored;
    }

    @Override
    public List<Tutored> getAllOfRonda(Ronda currRonda) throws SQLException {
        List<Tutored> tutoreds =  this.tutoredDao.getAllOfRonda(currRonda.getId());
        for (Tutored tutored : tutoreds) {
            tutored.setEmployee(getApplication().getEmployeeService().getById(tutored.getEmployeeId()));
        }
        return tutoreds;
    }

    @Override
    public List<Tutored> getAllOfRondaForZeroEvaluation(Ronda currRonda) throws SQLException {
        List<Tutored> tutoreds =  this.tutoredDao.getAllOfRondaForZeroEvaluation(currRonda.getId());
        for (Tutored tutored : tutoreds) {
            tutored.setEmployee(getApplication().getEmployeeService().getById(tutored.getEmployeeId()));
        }
        return tutoreds;
    }

    @Override
    public List<Tutored> getAllOfHealthFacility(HealthFacility healthFacility) throws SQLException {
        List<Tutored> tutoreds = this.tutoredDao.getAllOfHealthFacility(healthFacility.getId(), String.valueOf(LifeCycleStatus.ACTIVE));
        for (Tutored tutored : tutoreds) {
            tutored.setEmployee(getApplication().getEmployeeService().getById(tutored.getEmployeeId()));
        }
        return tutoreds;
    }


    @Override
    public List<Tutored> getAllNotSynced() throws SQLException {
        List<Tutored> tutoreds = this.tutoredDao.getAllNotSynced(String.valueOf(SyncSatus.PENDING));
        for (Tutored tutored : tutoreds) {
            tutored.setEmployee(getApplication().getEmployeeService().getById(tutored.getEmployeeId()));
            tutored.getEmployee().setLocations(getApplication().getLocationService().getAllOfEmploee(tutored.getEmployee()));
        }
        return tutoreds;
    }

    @Override
    public List<Tutored> getAllForMentoringRound(HealthFacility healthFacility, boolean zeroEvaluation) throws SQLException {
        List<Tutored> tutoreds = this.tutoredDao.getAllForMentoringRound(healthFacility.getId(), String.valueOf(LifeCycleStatus.ACTIVE), zeroEvaluation);
        for (Tutored tutored : tutoreds) {
            tutored.setEmployee(getApplication().getEmployeeService().getById(tutored.getEmployeeId()));
        }
        return tutoreds;
    }

    @Override
    public List<Tutored> getAllOfRondaForNewRonda(HealthFacility healthFacility) throws SQLException {
        return this.tutoredDao.getAllOfHealthFacilityForNewRonda(healthFacility.getId(), String.valueOf(LifeCycleStatus.ACTIVE));
    }

    @Override
    public List<Tutored> getAllPagenated(long offset, long limit) {
        List<Tutored> tutoreds = this.tutoredDao.getTutoredsPaginated((int) limit, (int) offset);
        for (Tutored tutored : tutoreds) {
            try {
                tutored.setEmployee(getApplication().getEmployeeService().getById(tutored.getEmployeeId()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return tutoreds;
    }

}
