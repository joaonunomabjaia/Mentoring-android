package mz.org.csaude.mentoring.service.ronda;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.rondatype.RondaTypeDAO;
import mz.org.csaude.mentoring.dto.ronda.RondaTypeDTO;
import mz.org.csaude.mentoring.model.rondatype.RondaType;

public class RondaTypeServiceImpl extends BaseServiceImpl<RondaType> implements RondaTypeService {
    RondaTypeDAO rondaTypeDAO;
    public RondaTypeServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.rondaTypeDAO = getDataBaseHelper().getRondaTypeDAO();
    }

    @Override
    public RondaType save(RondaType record) throws SQLException {
        record.setId((int) this.rondaTypeDAO.insert(record));
        return record;
    }

    @Override
    public RondaType update(RondaType record) throws SQLException {
        this.rondaTypeDAO.update(record);
        return record;
    }

    @Override
    public int delete(RondaType record) throws SQLException {
        return 0;
    }

    @Override
    public List<RondaType> getAll() throws SQLException {
        return this.rondaTypeDAO.queryForAll();
    }

    @Override
    public RondaType getById(int id) throws SQLException {
        return this.rondaTypeDAO.queryForId(id);
    }

    @Override
    public RondaType getByuuid(String uuid) throws SQLException {
        return this.rondaTypeDAO.getByUuid(uuid);
    }

    @Override
    public void saveOrUpdateRondaTypes(List<RondaTypeDTO> rondaTypeDTOS) throws SQLException {
        for (RondaTypeDTO dto: rondaTypeDTOS) {
           this.saveOrUpdateRondaType(dto.getRondaType());
        }
    }

    @Override
    public RondaType saveOrUpdateRondaType(RondaType rondaType) throws SQLException {
        RondaType rt = this.rondaTypeDAO.getByUuid(rondaType.getUuid());
        if(rt!=null) {
            rondaType.setId(rt.getId());
            this.update(rondaType);
        } else {
            this.save(rondaType);
        }
        return rondaType;
    }

    @Override
    public RondaType getRondaTypeByCode(String code) throws SQLException {
        return rondaTypeDAO.getRondaTypeByCode(code);
    }

    @Override
    public List<RondaType> doSearch(long offset, long limit) {
        return null;
    }
}
