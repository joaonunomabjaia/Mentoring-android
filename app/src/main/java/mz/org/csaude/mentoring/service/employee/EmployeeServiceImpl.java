package mz.org.csaude.mentoring.service.employee;

import android.app.Application;

import androidx.room.Transaction;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.employee.EmployeeDAO;
import mz.org.csaude.mentoring.dao.location.LocationDAO;
import mz.org.csaude.mentoring.dao.partner.PartnerDao;
import mz.org.csaude.mentoring.dao.professionalCategoryDAO.ProfessionalCategoryDAO;
import mz.org.csaude.mentoring.dto.employee.EmployeeDTO;
import mz.org.csaude.mentoring.model.employee.Employee;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.location.Location;
import mz.org.csaude.mentoring.util.Utilities;

public class EmployeeServiceImpl extends BaseServiceImpl<Employee> implements EmployeeService {

    private EmployeeDAO employeeDAO;

    private ProfessionalCategoryDAO professionalCategoryDAO;
    private LocationDAO locationDAO;

    private PartnerDao partnerDao;

    public EmployeeServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application ) throws SQLException {
        super.init(application );
        this.employeeDAO = getDataBaseHelper().getEmployeeDAO();
        professionalCategoryDAO = dataBaseHelper.getProfessionalCategoryDAO();
        locationDAO = dataBaseHelper.getLocationDAO();
        partnerDao = dataBaseHelper.getPartnerDao();
    }

    @Override
    public Employee save(Employee record) throws SQLException {
        record.setId((int) this.employeeDAO.insert(record));
        return record;
    }

    @Override
    public Employee update(Employee record) throws SQLException {
        this.employeeDAO.update(record);
        return record;
    }

    @Override
    public int delete(Employee record) throws SQLException {
        return this.employeeDAO.delete(record.getId());
    }

    @Override
    public List<Employee> getAll() throws SQLException {
        return this.employeeDAO.queryForAll();
    }

    @Override
    public Employee getById(int id) throws SQLException {
        Employee employee = this.employeeDAO.queryForId(id);
        employee.setProfessionalCategory(professionalCategoryDAO.queryForId(employee.getProfessionalCategoryId()));
        employee.setPartner(partnerDao.queryForId(employee.getPartnerId()));

        employee.setLocations(locationDAO.getAllOfEmployee(employee.getId()));
        return employee;
    }

    @Override
    public void saveOrUpdateEmployees(List<EmployeeDTO> employeeDTOS) throws SQLException {

        for (EmployeeDTO employeeDTO : employeeDTOS){

            this.saveOrUpdateEmployee(new Employee(employeeDTO));
        }
    }

    @Override
    public Employee saveOrUpdateEmployee(Employee e) throws SQLException {

        try {
            Employee employee = this.employeeDAO.getByUuid(e.getUuid());

            if(employee == null){
                e.setProfessionalCategory(professionalCategoryDAO.getByUuid(e.getProfessionalCategory().getUuid()));
                e.setPartner(partnerDao.getByUuid(e.getPartner().getUuid()));
                this.employeeDAO.insert(e);
                e.setId(this.employeeDAO.getByUuid(e.getUuid()).getId());
                saveLocationFromEmplyee(e.getLocations(), e);
                return e;
            } else {
                e.setId(employee.getId());
                e.setProfessionalCategory(professionalCategoryDAO.getByUuid(e.getProfessionalCategory().getUuid()));
                e.setPartner(partnerDao.getByUuid(e.getPartner().getUuid()));
                this.employeeDAO.update(e);
                e.setId(this.employeeDAO.getByUuid(e.getUuid()).getId());
                saveLocationFromEmplyee(e.getLocations(), e);
                return employee;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void saveLocationFromEmplyee(List<Location> locations, Employee employee) throws SQLException {

        List<Location> oldLocations = getApplication().getLocationService().getAllOfEmploee(employee);
        if (Utilities.listHasElements(oldLocations)) {
            for (Location location : oldLocations) {
                getApplication().getLocationService().delete(location);
            }
        }

        for (Location location : locations){
            if (location.getProvince() != null) location.setProvince(getDataBaseHelper().getProvinceDAO().getByUuid(location.getProvince().getUuid()));
            if (location.getDistrict() != null) location.setDistrict(getDataBaseHelper().getDistrictDAO().getByUuid(location.getDistrict().getUuid()));
            HealthFacility h =getDataBaseHelper().getHealthFacilityDAO().getByUuid(location.getHealthFacility().getUuid());
            if (h == null) {
                location.getHealthFacility().setDistrict(getDataBaseHelper().getDistrictDAO().getByUuid(location.getHealthFacility().getDistrict().getUuid()));
                location.getHealthFacility().setId((int) getDataBaseHelper().getHealthFacilityDAO().insert(location.getHealthFacility()));
            } else {
                location.getHealthFacility().setId(h.getId());
            }
            location.setEmployee(employee);
            location.setHealthFacility(location.getHealthFacility());
            getApplication().getLocationService().saveOrUpdate(location);
        }
    }

    @Override
    public Employee getByuuid(String uuid) throws SQLException {
        return employeeDAO.getByUuid(uuid);
    }
}
