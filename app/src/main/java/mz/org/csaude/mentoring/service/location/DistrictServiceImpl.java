package mz.org.csaude.mentoring.service.location;

import android.app.Application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.location.DistrictDAO;
import mz.org.csaude.mentoring.dto.location.DistrictDTO;
import mz.org.csaude.mentoring.dto.location.ProvinceDTO;
import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.model.location.Location;
import mz.org.csaude.mentoring.model.location.Province;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.user.User;

public class DistrictServiceImpl extends BaseServiceImpl<District> implements DistrictService {

     DistrictDAO districtDAO;

     ProvinceServiceImpl provinceService;

    public DistrictServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.districtDAO = getDataBaseHelper().getDistrictDAO();
        this.provinceService = new ProvinceServiceImpl(application);
    }

    @Override
    public District save(District record) throws SQLException {
        this.districtDAO.insert(record);
        return record;
    }

    @Override
    public District update(District record) throws SQLException {
        this.districtDAO.update(record);
        return record;
    }

    @Override
    public int delete(District record) throws SQLException {
        return this.districtDAO.delete(record);
    }

    @Override
    public List<District> getAll() throws SQLException {
        return this.districtDAO.queryForAll();
    }

    @Override
    public District getById(int id) throws SQLException {
        return this.districtDAO.queryForId(id);
    }

    @Override
    public District getByuuid(String uuid) throws SQLException {
        return this.districtDAO.getByUuid(uuid);
    }


    @Override
    public void savedOrUpdateDistricts(List<DistrictDTO> districtDTOs) throws SQLException {

        for(DistrictDTO district : districtDTOs){
            this.savedOrUpdateDistrict(new District(district));
        }

    }

    @Override
    public District savedOrUpdateDistrict(District district) throws SQLException {

        District districts = this.districtDAO.getByUuid(district.getUuid());
        if(districts == null){

            Province province = this.provinceService.savedOrUpdateProvince(new ProvinceDTO(district.getProvince()));
            district.setProvince(province);
            this.districtDAO.createOrUpdate(district);
            return district;
        }
        return districts;
    }

    @Override
    public List<District> getByProvince(Province selectedProvince) throws SQLException{
        return this.districtDAO.getByProvince(selectedProvince.getId());
    }

    @Override
    public List<District> getByProvinceAndMentor(Province province, Tutor mentor) throws SQLException {
        // Collect district UUIDs from the mentor's locations using streams
        List<String> districtUuids = mentor.getEmployee().getLocations().stream()
                .map(location -> location.getDistrict().getUuid())
                .distinct() // Ensure uniqueness of district UUIDs
                .collect(Collectors.toList());

        // Return the list of districts that match the province and district UUIDs
        return districtDAO.getByProvinceAndMentor(province.getId(), districtUuids);
    }

}
