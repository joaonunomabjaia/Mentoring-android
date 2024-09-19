package mz.org.csaude.mentoring.service.location;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.dto.location.ProvinceDTO;
import mz.org.csaude.mentoring.model.location.Province;
import mz.org.csaude.mentoring.model.tutor.Tutor;

public interface ProvinceService extends BaseService<Province> {
    List<Province> getAllOfTutor(Tutor tutor) throws SQLException;

    void savedOrUpdateProvince(List<ProvinceDTO> provinceDTOs) throws SQLException;

    Province savedOrUpdateProvince(ProvinceDTO provinceDTO) throws SQLException;
}
