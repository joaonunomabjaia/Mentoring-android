package mz.org.csaude.mentoring.service.responseType;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.responseType.ResponseTypeDAO;
import mz.org.csaude.mentoring.dto.responseType.ResponseTypeDTO;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.model.responseType.ResponseType;

public class ResponseTypeServiceImpl extends BaseServiceImpl<ResponseType> implements ResponseTypeService {

    private ResponseTypeDAO responseTypeDAO;

    public ResponseTypeServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application ) throws SQLException {
        super.init(application );
        this.responseTypeDAO = dataBaseHelper.getResponseTypeDAO();
    }

    @Override
    public ResponseType save(ResponseType record) throws SQLException {
        record.setId((int) this.responseTypeDAO.insert(record));
        return record;
    }

    @Override
    public ResponseType update(ResponseType record) throws SQLException {
        this.responseTypeDAO.update(record);
        return record;
    }

    @Override
    public int delete(ResponseType record) throws SQLException {
        return this.responseTypeDAO.delete(record);
    }

    @Override
    public List<ResponseType> getAll() throws SQLException {
        return this.responseTypeDAO.queryForAll();
    }

    @Override
    public ResponseType getById(int id) throws SQLException {
        return this.responseTypeDAO.queryForId(id);
    }

    @Override
    public void saveOrUpdateResponseTypes(List<ResponseTypeDTO> responseTypeDTOS) throws SQLException {
        for (ResponseTypeDTO responseTypeDTO : responseTypeDTOS) {
              this.saveOrUpdateResponseType(responseTypeDTO);
        }
    }

    @Override
    public ResponseType saveOrUpdateResponseType(ResponseTypeDTO responseTypeDTO) throws SQLException {
        ResponseType rt = this.responseTypeDAO.getByUuid(responseTypeDTO.getUuid());
        ResponseType responseType = responseTypeDTO.getResponseType();
        if(rt!=null) {
            responseType.setId(rt.getId());
            this.update(responseType);
        } else {
            this.save(responseType);
        }
        return responseType;
    }

    @Override
    public ResponseType getByuuid(String uuid) throws SQLException {
        return responseTypeDAO.getByUuid(uuid);
    }
}
