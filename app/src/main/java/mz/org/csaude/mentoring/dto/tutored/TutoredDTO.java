package mz.org.csaude.mentoring.dto.tutored;






import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.employee.EmployeeDTO;
import mz.org.csaude.mentoring.model.tutored.Tutored;

/**
 * @author Jose Julai Ritsure
 */



public class TutoredDTO extends BaseEntityDTO {

    private EmployeeDTO employeeDTO;

    private boolean zeroEvaluationDone;

    private Double zeroEvaluationScore;

    public TutoredDTO() {
    }

    public TutoredDTO(Tutored tutored) {
        super(tutored);
        setZeroEvaluationScore(tutored.getZeroEvaluationScore());
        setZeroEvaluationDone(tutored.isZeroEvaluationDone());
        this.setEmployeeDTO(new EmployeeDTO(tutored.getEmployee()));
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

    public Double getZeroEvaluationScore() {
        return zeroEvaluationScore;
    }

    public void setZeroEvaluationScore(Double zeroEvaluationScore) {
        this.zeroEvaluationScore = zeroEvaluationScore;
    }

    public Tutored getMentee() {
        Tutored tutored = new Tutored();
        tutored.setUuid(this.getUuid());
        tutored.setZeroEvaluationDone(this.isZeroEvaluationDone());
        tutored.setZeroEvaluationScore(this.getZeroEvaluationScore());
        tutored.setCreatedAt(this.getCreatedAt());
        tutored.setUpdatedAt(this.getUpdatedAt());
        tutored.setLifeCycleStatus(this.getLifeCycleStatus());
        if(this.getEmployeeDTO()!=null) {
            tutored.setEmployee(this.getEmployeeDTO().getEmployee());
        }
        return tutored;
    }
}
