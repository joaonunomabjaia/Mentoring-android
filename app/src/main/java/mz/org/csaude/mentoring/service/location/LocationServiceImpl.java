package mz.org.csaude.mentoring.service.location;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.location.LocationDAO;
import mz.org.csaude.mentoring.dto.location.LocationDTO;
import mz.org.csaude.mentoring.dto.location.ProvinceDTO;
import mz.org.csaude.mentoring.model.employee.Employee;
import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.model.location.Location;
import mz.org.csaude.mentoring.model.location.Province;

public class LocationServiceImpl extends BaseServiceImpl<Location> implements LocationService {


    private LocationDAO locationDAO;

    public LocationServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
     this.locationDAO = getDataBaseHelper().getLocationDAO();
    }
    @Override
    public Location save(Location record) throws SQLException {
        this.locationDAO.insertLocation(record);
        return record;
    }

    @Override
    public Location update(Location record) throws SQLException {
        this.locationDAO.update(record);
        return record;
    }

    @Override
    public int delete(Location record) throws SQLException {
        return this.locationDAO.delete(record.getId());
    }

    @Override
    public List<Location> getAll() throws SQLException {
        return this.locationDAO.queryForAll();
    }

    @Override
    public Location getById(int id) throws SQLException {
        return this.locationDAO.queryForId(id);
    }

    @Override
    public Location getByuuid(String uuid) throws SQLException {
        return this.locationDAO.getByUuid(uuid);
    }

    @Override
    public void saveOrUpdates(List<LocationDTO> locationDTOS) throws SQLException {
        for (LocationDTO locationDTO : locationDTOS){
            this.saveOrUpdate(new Location(locationDTO));
        }
    }

    @Override
    public Location saveOrUpdate(Location location) throws SQLException {

        Location l = this.locationDAO.getByUuid(location.getUuid());

        if (l == null){
            this.locationDAO.insertLocation(location);
            return location;
        } else {
            location.setId(l.getId());
            this.locationDAO.update(location);
            return location;
        }
    }

    @Override
    public List<Location> getAllOfEmploee(Employee employee) throws SQLException {
        List<Location> locations = this.locationDAO.getAllOfEmployee(employee.getId());
        for (Location location : locations){
            location.setEmployee(employee);
            location.setProvince(getApplication().getProvinceService().getById(location.getProvinceId()));
            location.setDistrict(getApplication().getDistrictService().getById(location.getDistrictId()));
            location.setHealthFacility(getApplication().getHealthFacilityService().getById(location.getHealthFacilityId()));
        }
        return locations;
    }
}
