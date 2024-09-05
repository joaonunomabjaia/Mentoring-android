package mz.org.csaude.mentoring.service.location;

import android.app.Application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.location.ProvinceDAO;
import mz.org.csaude.mentoring.dto.location.ProvinceDTO;
import mz.org.csaude.mentoring.model.location.Location;
import mz.org.csaude.mentoring.model.location.Province;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.user.User;

public class ProvinceServiceImpl extends BaseServiceImpl<Province> implements ProvinceService {

    ProvinceDAO provinceDAO;


    public ProvinceServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application ) throws SQLException {
        super.init(application );
        this.provinceDAO = getDataBaseHelper().getProvinceDAO();
    }

    @Override
    public Province save(Province record) throws SQLException {
        this.provinceDAO.insertProvince(record);
        return record;
    }

    @Override
    public Province update(Province record) throws SQLException {
        this.provinceDAO.update(record);
        return record;
    }

    @Override
    public int delete(Province record) throws SQLException {
        return this.provinceDAO.delete(record.getId());
    }

    @Override
    public List<Province> getAll() throws SQLException {
        return this.provinceDAO.queryForAll();
    }

    @Override
    public Province getById(int id) throws SQLException {
        return this.provinceDAO.queryForId(id);
    }

    @Override
    public List<Province> getAllOfTutor(Tutor tutor) throws SQLException {
        List<String> provinceUuids = new ArrayList<>();
        for (Location location : tutor.getEmployee().getLocations()) {
            provinceUuids.add(location.getProvince().getUuid());
        }
        return provinceDAO.getAllOfTutor(provinceUuids);
    }

    @Override
    public void savedOrUpdateProvince(List<ProvinceDTO> provinceDTOs) throws SQLException {
        for (ProvinceDTO provinceDTO : provinceDTOs) {
           this.savedOrUpdateProvince(provinceDTO);
        }
    }

    @Override
    public Province savedOrUpdateProvince(ProvinceDTO provinceDTO) throws SQLException {
        Province provinces = this.provinceDAO.getByUuid(provinceDTO.getUuid());
        if (provinces == null){
            Province province = new Province(provinceDTO);
            this.provinceDAO.insertProvince(province);
            return province;
        }
        return provinces;
    }

    @Override
    public Province getByuuid(String uuid) throws SQLException {
        return this.provinceDAO.getByUuid(uuid);
    }
}
