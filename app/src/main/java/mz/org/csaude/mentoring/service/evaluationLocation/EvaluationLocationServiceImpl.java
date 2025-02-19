package mz.org.csaude.mentoring.service.evaluationLocation;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.evaluationLocation.EvaluationLocationDAO;
import mz.org.csaude.mentoring.dto.evaluationLocation.EvaluationLocationDTO;
import mz.org.csaude.mentoring.model.evaluationLocation.EvaluationLocation;

public class EvaluationLocationServiceImpl extends BaseServiceImpl<EvaluationLocation> implements EvaluationLocationService {

    private EvaluationLocationDAO evaluationLocationDAO;

    public EvaluationLocationServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.evaluationLocationDAO = dataBaseHelper.getEvaluationLocationDAO();
    }

    @Override
    public EvaluationLocation save(EvaluationLocation record) throws SQLException {
        this.evaluationLocationDAO.insert(record);
        return record;
    }

    @Override
    public EvaluationLocation update(EvaluationLocation record) throws SQLException {
        this.evaluationLocationDAO.update(record);
        return record;
    }

    @Override
    public int delete(EvaluationLocation record) throws SQLException {
        return this.evaluationLocationDAO.delete(record.getId());
    }

    @Override
    public List<EvaluationLocation> getAll() throws SQLException {
        return this.evaluationLocationDAO.queryForAll();
    }

    @Override
    public EvaluationLocation getById(int id) throws SQLException {
        return this.evaluationLocationDAO.queryForId(id);
    }

    @Override
    public void saveOrUpdateEvaluationLocations(List<EvaluationLocationDTO> evaluationLocationDTOS) throws SQLException {
        for (EvaluationLocationDTO evaluationLocationDTO : evaluationLocationDTOS) {
            this.saveOrUpdateEvaluationLocation(evaluationLocationDTO);
        }
    }

    @Override
    public EvaluationLocation saveOrUpdateEvaluationLocation(EvaluationLocationDTO evaluationLocationDTO) throws SQLException {
        EvaluationLocation existing = this.evaluationLocationDAO.getByUuid(evaluationLocationDTO.getUuid());
        EvaluationLocation evaluationLocation = new EvaluationLocation(evaluationLocationDTO);
        if (existing != null) {
            evaluationLocation.setId(existing.getId());
            this.evaluationLocationDAO.update(evaluationLocation);
        } else {
            this.evaluationLocationDAO.insert(evaluationLocation);
            evaluationLocation.setId(this.evaluationLocationDAO.getByUuid(evaluationLocationDTO.getUuid()).getId());
        }
        return evaluationLocation;
    }

    @Override
    public EvaluationLocation getByCode(String code) throws SQLException {
        return evaluationLocationDAO.findEvaluationLocationByCode(code);
    }

    @Override
    public EvaluationLocation getByuuid(String uuid) throws SQLException {
        return evaluationLocationDAO.getByUuid(uuid);
    }
}
