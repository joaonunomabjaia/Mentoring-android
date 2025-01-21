package mz.org.csaude.mentoring.dto.evaluationLocation;

import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.evaluationLocation.EvaluationLocation;

public class EvaluationLocationDTO extends BaseEntityDTO {

    private String code;
    private String description;

    public EvaluationLocationDTO(EvaluationLocation evaluationLocation) {
        super(evaluationLocation);
        this.setCode(evaluationLocation.getCode());
        this.setDescription(evaluationLocation.getDescription());
    }

    public EvaluationLocationDTO() {
        super();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
