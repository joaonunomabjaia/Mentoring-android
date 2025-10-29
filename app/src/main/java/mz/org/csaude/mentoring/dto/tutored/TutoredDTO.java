package mz.org.csaude.mentoring.dto.tutored;

import com.google.gson.annotations.SerializedName;

import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.employee.EmployeeDTO;
import mz.org.csaude.mentoring.model.tutored.FlowHistory;
import mz.org.csaude.mentoring.model.tutored.Tutored;

/**
 * @author Jose Julai Ritsure
 */
public class TutoredDTO extends BaseEntityDTO {

    private EmployeeDTO employeeDTO;

    private boolean zeroEvaluationDone;

    private double zeroEvaluationScore;

    // NEW: JSON-backed object (stored as TEXT in DB via TypeConverter)
    @SerializedName("flowHistoryMenteeAuxDTO")
    private FlowHistory flowHistoryMenteeAuxDTO;

    public TutoredDTO() {
    }

    public TutoredDTO(Tutored tutored) {
        super(tutored);
        setZeroEvaluationScore(tutored.getZeroEvaluationScore());
        setZeroEvaluationDone(tutored.isZeroEvaluationDone());
        this.setEmployeeDTO(new EmployeeDTO(tutored.getEmployee()));
        this.setFlowHistoryMenteeAuxDTO(tutored.getFlowHistory()); // <-- map from entity
    }

    public EmployeeDTO getEmployeeDTO() {
        return employeeDTO;
    }

    public void setEmployeeDTO(EmployeeDTO employeeDTO) {
        this.employeeDTO = employeeDTO;
    }

    public boolean isZeroEvaluationDone() {
        return zeroEvaluationDone;
    }

    public void setZeroEvaluationDone(boolean zeroEvaluationDone) {
        this.zeroEvaluationDone = zeroEvaluationDone;
    }

    public double getZeroEvaluationScore() {
        return zeroEvaluationScore;
    }

    public void setZeroEvaluationScore(double zeroEvaluationScore) {
        this.zeroEvaluationScore = zeroEvaluationScore;
    }

    // NEW getters/setters
    public FlowHistory getFlowHistoryMenteeAuxDTO() {
        return flowHistoryMenteeAuxDTO;
    }

    public void setFlowHistoryMenteeAuxDTO(FlowHistory flowHistoryMenteeAuxDTO) {
        this.flowHistoryMenteeAuxDTO = flowHistoryMenteeAuxDTO;
    }

    public Tutored getMentee() {
        Tutored tutored = new Tutored();
        tutored.setUuid(this.getUuid());
        tutored.setZeroEvaluationDone(this.isZeroEvaluationDone());
        tutored.setZeroEvaluationScore(this.getZeroEvaluationScore());
        tutored.setCreatedAt(this.getCreatedAt());
        tutored.setUpdatedAt(this.getUpdatedAt());
        tutored.setLifeCycleStatus(this.getLifeCycleStatus());
        tutored.setCreatedByUuid(this.getCreatedByuuid());
        tutored.setUpdatedByUuid(this.getUpdatedByuuid());

        if (this.getEmployeeDTO() != null) {
            tutored.setEmployee(this.getEmployeeDTO().getEmployee());
        }

        // <-- map to entity
        tutored.setFlowHistory(this.getFlowHistoryMenteeAuxDTO());

        return tutored;
    }
}
