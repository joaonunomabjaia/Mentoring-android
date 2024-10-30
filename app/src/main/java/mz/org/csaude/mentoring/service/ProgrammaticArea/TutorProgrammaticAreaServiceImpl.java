package mz.org.csaude.mentoring.service.ProgrammaticArea;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.programmaticArea.TutorProgrammaticAreaDAO;
import mz.org.csaude.mentoring.dao.tutor.TutorDAO;
import mz.org.csaude.mentoring.dto.programmaticArea.TutorProgrammaticAreaDTO;
import mz.org.csaude.mentoring.model.employee.Employee;
import mz.org.csaude.mentoring.model.programmaticArea.TutorProgrammaticArea;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.user.User;

public class TutorProgrammaticAreaServiceImpl extends BaseServiceImpl<TutorProgrammaticArea> implements TutorProgrammaticAreaService{

    TutorProgrammaticAreaDAO tutorProgrammaticAreaDAO;
    TutorDAO tutorDAO;

    public TutorProgrammaticAreaServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.tutorProgrammaticAreaDAO = getDataBaseHelper().getTutorProgrammaticAreaDAO();
        this.tutorDAO = getDataBaseHelper().getTutorDAO();
    }

    @Override
    public TutorProgrammaticArea save(TutorProgrammaticArea record) throws SQLException {
        this.tutorProgrammaticAreaDAO.insert(record);
        return record;
    }

    @Override
    public TutorProgrammaticArea update(TutorProgrammaticArea record) throws SQLException {
        this.tutorProgrammaticAreaDAO.update(record);
        return record;
    }

    @Override
    public int delete(TutorProgrammaticArea record) throws SQLException {
        return this.tutorProgrammaticAreaDAO.delete(record);
    }

    @Override
    public List<TutorProgrammaticArea> getAll() throws SQLException {
        return this.tutorProgrammaticAreaDAO.queryForAll();
    }

    @Override
    public TutorProgrammaticArea getById(int id) throws SQLException {
        return this.tutorProgrammaticAreaDAO.queryForId(id);
    }

    @Override
    public TutorProgrammaticArea getByuuid(String uuid) throws SQLException {
        return this.tutorProgrammaticAreaDAO.getByUuid(uuid);
    }

    @Override
    public void saveOrUpdateTutorProgrammaticAreas(List<TutorProgrammaticAreaDTO> tutorProgrammaticAreaDTOS) throws SQLException {
        for (TutorProgrammaticAreaDTO tutorProgrammaticAreaDTO: tutorProgrammaticAreaDTOS) {
            this.saveOrUpdateTutorProgrammaticArea(tutorProgrammaticAreaDTO);
        }
    }

    @Override
    public TutorProgrammaticArea saveOrUpdateTutorProgrammaticArea(TutorProgrammaticAreaDTO tutorProgrammaticAreaDTO) throws SQLException {
        TutorProgrammaticArea tpa = this.tutorProgrammaticAreaDAO.getByUuid(tutorProgrammaticAreaDTO.getUuid());
        TutorProgrammaticArea tutorProgrammaticArea = tutorProgrammaticAreaDTO.getTutorProgrammaticArea();
        tutorProgrammaticArea.setProgrammaticArea(getApplication().getProgrammaticAreaService().getByuuid(tutorProgrammaticArea.getProgrammaticArea().getUuid()));

        if (getApplication().getAuthenticatedUser() == null) {
            try {
                User user = getApplication().getUserService().getCurrentUser();
                getApplication().setAuthenticatedUser(user, false);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        Employee employee = getApplication().getAuthenticatedUser().getEmployee();
        Tutor tutor = tutorDAO.getByEmployee(employee.getId());
        tutorProgrammaticArea.setTutor(tutor);
        if(tpa!=null) {
            tutorProgrammaticArea.setId(tpa.getId());
            this.tutorProgrammaticAreaDAO.update(tutorProgrammaticArea);
        } else {
            tutorProgrammaticArea.setId((int) this.tutorProgrammaticAreaDAO.insert(tutorProgrammaticArea));
        }
        return tutorProgrammaticArea;
    }
}
