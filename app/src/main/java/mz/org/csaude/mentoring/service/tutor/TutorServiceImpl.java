package mz.org.csaude.mentoring.service.tutor;

import android.app.Application;

import androidx.room.Transaction;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.tutor.TutorDAO;
import mz.org.csaude.mentoring.dto.tutor.TutorDTO;
import mz.org.csaude.mentoring.model.employee.Employee;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.service.employee.EmployeeService;
import mz.org.csaude.mentoring.service.partner.PartnerService;
import mz.org.csaude.mentoring.service.user.UserService;

public class TutorServiceImpl extends BaseServiceImpl<Tutor> implements TutorService {

    TutorDAO tutorDAO;

    PartnerService partnerService;

    UserService userService;

    private EmployeeService employeeService;


    public TutorServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.tutorDAO = getDataBaseHelper().getTutorDAO();
        this.partnerService = getApplication().getPartnerService();
        this.userService = getApplication().getUserService();
        this.employeeService = getApplication().getEmployeeService();
    }

    @Override
    public Tutor save(Tutor record) throws SQLException {
        record.setId((int) this.tutorDAO.insert(record));
        return record;
    }

    @Override
    public Tutor update(Tutor record) throws SQLException {
        tutorDAO.update(record);
        return record;
    }

    @Override
    public int delete(Tutor record) throws SQLException {
        return this.tutorDAO.delete(record.getId());
    }

    @Override
    public List<Tutor> getAll() throws SQLException {
        return this.tutorDAO.queryForAll();
    }

    @Override
    public Tutor getById(int id) throws SQLException {
        return this.tutorDAO.queryForId(id);
    }

    @Override
    public Tutor getByuuid(String uuid) throws SQLException {
        return this.tutorDAO.getByUuid(uuid);
    }

    @Override
    @Transaction
    public void saveOrUpdateTutors(List<TutorDTO> tutorDTOS) throws SQLException {

        for(TutorDTO tutorDTO : tutorDTOS){
            boolean doesTutorExiste = this.tutorDAO.checkTutorExistance(tutorDTO.getUuid());
            Tutor tutor = tutorDTO.getTutor();
            Employee employee = tutorDTO.getEmployeeDTO().getEmployee();
            tutor.setEmployee(employee);
            this.employeeService.saveOrUpdateEmployee(employee);

            if(!doesTutorExiste){
                this.save(tutor);
            } else {
                Tutor t = this.tutorDAO.getByUuid(tutorDTO.getUuid());
                tutor.setId(t.getId());
                this.update(tutor);
            }
        }

    }

    @Override
    public Tutor saveOrUpdate(Tutor tutor) throws SQLException {
        tutor.setEmployee(getApplication().getEmployeeService().getByuuid(tutor.getEmployee().getUuid()));
        Tutor t = this.tutorDAO.getByUuid(tutor.getUuid());
        if(t != null){
            tutor.setId(t.getId());
            this.tutorDAO.update(tutor);
            return tutor;
        } else {
            this.tutorDAO.insert(tutor);
            tutor.setId(this.tutorDAO.getByUuid(tutor.getUuid()).getId());
            return tutor;
        }
    }

    @Override
    public Tutor getByEmployee(Employee employee) throws SQLException {
        Tutor tutor = this.tutorDAO.getByEmployee(employee.getId());
        if(tutor != null){
            tutor.setEmployee(employee);
            return tutor;
        } else {
            return null;
        }
    }
}
