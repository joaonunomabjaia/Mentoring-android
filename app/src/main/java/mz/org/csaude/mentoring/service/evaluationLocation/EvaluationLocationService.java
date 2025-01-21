package mz.org.csaude.mentoring.service.evaluationLocation;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.dto.evaluationLocation.EvaluationLocationDTO;
import mz.org.csaude.mentoring.model.evaluationLocation.EvaluationLocation;

public interface EvaluationLocationService extends BaseService<EvaluationLocation> {
    void saveOrUpdateEvaluationLocations(List<EvaluationLocationDTO> evaluationLocationDTOS) throws SQLException;
    EvaluationLocation saveOrUpdateEvaluationLocation(EvaluationLocationDTO evaluationLocationDTO) throws SQLException;

    EvaluationLocation getByCode(String code) throws SQLException;
}
