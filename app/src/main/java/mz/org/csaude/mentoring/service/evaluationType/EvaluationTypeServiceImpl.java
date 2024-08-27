package mz.org.csaude.mentoring.service.evaluationType;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.evaluation.EvaluationTypeDAO;
import mz.org.csaude.mentoring.dto.evaluationType.EvaluationTypeDTO;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;

public class EvaluationTypeServiceImpl extends BaseServiceImpl<EvaluationType> implements EvaluationTypeService {
    private EvaluationTypeDAO evaluationTypeDAO;
    public EvaluationTypeServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application ) throws SQLException {
        super.init(application );
        this.evaluationTypeDAO = dataBaseHelper.getEvaluationTypeDAO();
    }

    @Override
    public EvaluationType save(EvaluationType record) throws SQLException {
        this.save(record);
        return record;
    }

    @Override
    public EvaluationType update(EvaluationType record) throws SQLException {
        this.update(record);
        return record;
    }

    @Override
    public int delete(EvaluationType record) throws SQLException {
        return this.evaluationTypeDAO.delete(record.getId());
    }

    @Override
    public List<EvaluationType> getAll() throws SQLException {
        return this.evaluationTypeDAO.queryForAll();
    }

    @Override
    public EvaluationType getById(int id) throws SQLException {
        return this.evaluationTypeDAO.queryForId(id);
    }

    @Override
    public void saveOrUpdateEvaluationTypes(List<EvaluationTypeDTO> evaluationTypeDTOS) throws SQLException {
        for (EvaluationTypeDTO evaluationTypeDTO: evaluationTypeDTOS) {
            this.saveOrUpdateEvaluationType(evaluationTypeDTO);
        }
    }

    @Override
    public EvaluationType saveOrUpdateEvaluationType(EvaluationTypeDTO evaluationTypeDTO) throws SQLException {
        EvaluationType et = this.evaluationTypeDAO.getByUuid(evaluationTypeDTO.getUuid());
        EvaluationType evaluationType = evaluationTypeDTO.getEvaluationType();
        if(et!=null) {
            evaluationType.setId(et.getId());
        }
        this.evaluationTypeDAO.createOrUpdate(evaluationType);
        return evaluationType;
    }

    @Override
    public EvaluationType getByCode(String code) throws SQLException {
        return evaluationTypeDAO.getByCode(code);
    }

    @Override
    public EvaluationType getByuuid(String uuid) throws SQLException {
        return evaluationTypeDAO.getByUuid(uuid);
    }
}
