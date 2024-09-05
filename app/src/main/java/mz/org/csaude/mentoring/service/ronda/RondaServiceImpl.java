package mz.org.csaude.mentoring.service.ronda;

import android.app.Application;

import androidx.room.Transaction;

import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.location.DistrictDAO;
import mz.org.csaude.mentoring.dao.location.HealthFacilityDAO;
import mz.org.csaude.mentoring.dao.ronda.RondaDAO;
import mz.org.csaude.mentoring.dao.ronda.RondaMenteeDAO;
import mz.org.csaude.mentoring.dao.ronda.RondaMentorDAO;
import mz.org.csaude.mentoring.dao.rondatype.RondaTypeDAO;
import mz.org.csaude.mentoring.dao.tutor.TutorDAO;
import mz.org.csaude.mentoring.dao.tutored.TutoredDao;
import mz.org.csaude.mentoring.dto.location.HealthFacilityDTO;
import mz.org.csaude.mentoring.dto.ronda.RondaDTO;
import mz.org.csaude.mentoring.dto.ronda.RondaMenteeDTO;
import mz.org.csaude.mentoring.dto.ronda.RondaMentorDTO;
import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.ronda.RondaMentor;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.LifeCycleStatus;
import mz.org.csaude.mentoring.util.SyncSatus;

public class RondaServiceImpl extends BaseServiceImpl<Ronda> implements RondaService {
    RondaDAO rondaDAO;
    HealthFacilityDAO healthFacilityDAO;
    RondaMentorDAO rondaMentorDAO;
    RondaMenteeDAO rondaMenteeDAO;
    DistrictDAO districtDAO;
    RondaTypeDAO rondaTypeDAO;
    TutorDAO tutorDAO;
    TutoredDao tutoredDao;

    public RondaServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.rondaDAO = getDataBaseHelper().getRondaDAO();
        this.healthFacilityDAO = getDataBaseHelper().getHealthFacilityDAO();
        this.rondaMenteeDAO = getDataBaseHelper().getRondaMenteeDAO();
        this.rondaMentorDAO = getDataBaseHelper().getRondaMentorDAO();
        this.districtDAO = getDataBaseHelper().getDistrictDAO();
        this.rondaTypeDAO = getDataBaseHelper().getRondaTypeDAO();
        this.tutorDAO = getDataBaseHelper().getTutorDAO();
        this.tutoredDao = getDataBaseHelper().getTutoredDao();
    }

    @Override
    @Transaction
    public Ronda savedOrUpdateRonda(Ronda ronda) throws SQLException {
            Ronda r = this.rondaDAO.getByUuid(ronda.getUuid());

            ronda.setHealthFacility(this.healthFacilityDAO.getByUuid(ronda.getHealthFacility().getUuid()));
            ronda.setRondaType(this.rondaTypeDAO.getByUuid(ronda.getRondaType().getUuid()));
            if(r!=null) {
                ronda.setId(r.getId());
                this.rondaDAO.update(ronda);
            } else {
                this.rondaDAO.insert(ronda);
                ronda.setId(this.rondaDAO.getByUuid(ronda.getUuid()).getId());
            }

            this.rondaMentorDAO.deleteByRonda(ronda.getId());
            this.rondaMenteeDAO.deleteByRonda(ronda.getId());

            for (RondaMentor rondaMentor: ronda.getRondaMentors()) {
                rondaMentor.setRonda(ronda);
                rondaMentor.setSyncStatus(ronda.getSyncStatus());
                rondaMentor.setStartDate(ronda.getStartDate());
                rondaMentor.setTutor(this.tutorDAO.getByUuid(rondaMentor.getTutor().getUuid()));

                this.rondaMentorDAO.insert(rondaMentor);
            }
            for (RondaMentee rondaMentee: ronda.getRondaMentees()) {
                rondaMentee.setRonda(ronda);
                rondaMentee.setSyncStatus(ronda.getSyncStatus());
                rondaMentee.setStartDate(ronda.getStartDate());
                rondaMentee.setTutored(this.tutoredDao.getByUuid(rondaMentee.getTutored().getUuid()));

                this.rondaMenteeDAO.insert(rondaMentee);
            }
        return ronda;
    }

    @Override
    public List<Ronda> getAllByHealthFacilityAndMentor(HealthFacility healthFacility, Tutor tutor, MentoringApplication mentoringApplication) throws SQLException {
        return this.rondaDAO.getAllByHealthFacilityAndMentor(healthFacility.getId(), tutor.getId(), String.valueOf(LifeCycleStatus.ACTIVE));
    }

    @Override
    public List<Ronda> getAllNotSynced() throws SQLException {
        return this.rondaDAO.getAllNotSynced(String.valueOf(SyncSatus.PENDING));
    }

    @Override
    public List<Ronda> doSearch(long offset, long limit) {
        return null;
    }

    @Override
    public int countRondas() throws SQLException {
        return this.rondaDAO.queryForAll().size();
    }

    @Override
    public List<Ronda> getAllByRondaType(RondaType rondaType) throws SQLException {
        List<Ronda> rondas = this.rondaDAO.getAllByRondaType(rondaType.getCode(), String.valueOf(LifeCycleStatus.ACTIVE));
        for (Ronda ronda: rondas) {
            ronda.setRondaMentors(this.rondaMentorDAO.getRondaMentors(ronda.getId()));
            ronda.setRondaMentees(this.rondaMenteeDAO.getAllOfRonda(ronda.getId()));
            ronda.setSessions(getApplication().getSessionService().getAllOfRonda(ronda));
            ronda.setRondaType(this.rondaTypeDAO.queryForId(ronda.getRondaTypeId()));
            ronda.setHealthFacility(this.healthFacilityDAO.queryForId(ronda.getHealthFacilityId()));
        }
        return rondas;
    }

    @Override
    public void saveOrUpdateRondas(List<RondaDTO> rondaDTOS) throws SQLException {
        for (RondaDTO rondaDTO: rondaDTOS) {
            this.saveOrUpdateRonda(rondaDTO);
        }
    }

    @Override
    public Ronda saveOrUpdateRonda(RondaDTO rondaDTO) throws SQLException {
        Ronda r = this.rondaDAO.getByUuid(rondaDTO.getUuid());
        Ronda ronda = rondaDTO.getRonda();

        ronda.setHealthFacility(this.healthFacilityDAO.getByUuid(rondaDTO.getHealthFacility().getUuid()));
        ronda.setRondaType(this.rondaTypeDAO.getByUuid(rondaDTO.getRondaType().getUuid()));

        if(r!=null) {
            ronda.setId(r.getId());
            this.rondaDAO.update(ronda);
        } else {
            this.rondaDAO.insert(ronda);
            ronda.setId(this.rondaDAO.getByUuid(ronda.getUuid()).getId());
        }

        saveRondaMentors(rondaDTO.getRondaMentors(), ronda);

        saveRondaMentees(rondaDTO.getRondaMentees(), ronda);

        return ronda;
    }

    @Override
    public Ronda getFullyLoadedRonda(Ronda ronda) throws SQLException {
        Ronda r = this.rondaDAO.getByUuid(ronda.getUuid());
        r.setRondaMentors(this.rondaMentorDAO.getRondaMentors(r.getId()));
        r.setRondaMentees(this.rondaMenteeDAO.getAllOfRonda(r.getId()));
        r.setSessions(getApplication().getSessionService().getAllOfRonda(r));
        return r;
    }

    private void saveRondaMentors(List<RondaMentorDTO> rondaMentorDTOS, Ronda ronda) throws SQLException {
        List<RondaMentor> rondaMentors = new ArrayList<>();
        for (RondaMentorDTO rondaMentorDTO: rondaMentorDTOS) {
            RondaMentor rm = this.rondaMentorDAO.getByUuid(rondaMentorDTO.getUuid());
            RondaMentor rondaMentor = rondaMentorDTO.getRondaMentor();

            rondaMentor.setRonda(ronda);
            rondaMentor.setTutor(this.tutorDAO.getByUuid(rondaMentorDTO.getMentor().getUuid()));
            if(rm!=null) {
                rondaMentor.setId(rm.getId());
                this.rondaMentorDAO.update(rondaMentor);
            } else {
                this.rondaMentorDAO.insert(rondaMentor);
                rondaMentor.setId(this.rondaMentorDAO.getByUuid(rondaMentor.getUuid()).getId());
            }
            rondaMentors.add(rondaMentor);
        }
        ronda.setRondaMentors(rondaMentors);
    }
    private void saveRondaMentees(List<RondaMenteeDTO> rondaMenteeDTOS, Ronda ronda) throws SQLException {
        List<RondaMentee> rondaMentees = new ArrayList<>();
        for (RondaMenteeDTO rondaMenteeDTO: rondaMenteeDTOS) {
            RondaMentee rm = this.rondaMenteeDAO.getByUuid(rondaMenteeDTO.getUuid());
            RondaMentee rondaMentee = rondaMenteeDTO.getRondaMentee();

            rondaMentee.setRonda(ronda);
            rondaMentee.setTutored(this.tutoredDao.getByUuid(rondaMenteeDTO.getMentee().getUuid()));

            if(rm!=null) {
                rondaMentee.setId(rm.getId());
                this.rondaMenteeDAO.update(rondaMentee);
            } else {
                this.rondaMenteeDAO.insert(rondaMentee);
                rondaMentee.setId(this.rondaMenteeDAO.getByUuid(rondaMentee.getUuid()).getId());
            }
            rondaMentees.add(rondaMentee);
        }
        ronda.setRondaMentees(rondaMentees);
    }

    @Override
    public Ronda save(Ronda record) throws SQLException {
        record.setId((int) this.rondaDAO.insert(record));
        return record;
    }

    @Override
    public Ronda update(Ronda record) throws SQLException {
        this.rondaDAO.update(record);
        return record;
    }

    @Override
    @Transaction
    public int delete(Ronda record) throws SQLException {
            this.rondaMentorDAO.deleteByRonda(record.getId());
            this.rondaMenteeDAO.deleteByRonda(record.getId());
            this.rondaDAO.delete(record);
        return record.getId();
    }

    @Override
    public List<Ronda> getAll() throws SQLException {
        return this.rondaDAO.queryForAll();
    }

    @Override
    public Ronda getById(int id) throws SQLException {
        Ronda ronda = this.rondaDAO.queryForId(id);
        ronda.setHealthFacility(this.healthFacilityDAO.queryForId(ronda.getHealthFacilityId()));
        ronda.setRondaType(this.rondaTypeDAO.queryForId(ronda.getRondaTypeId()));
        return ronda;
    }

    @Override
    public Ronda getByuuid(String uuid) throws SQLException {
        return this.rondaDAO.getByUuid(uuid);
    }

    @Override
    public List<Ronda> getAllByMentor(Tutor tutor, MentoringApplication mentoringApplication) throws SQLException {
        return this.rondaDAO.getAllByMentor(tutor.getId(), String.valueOf(LifeCycleStatus.ACTIVE));
    }

}
