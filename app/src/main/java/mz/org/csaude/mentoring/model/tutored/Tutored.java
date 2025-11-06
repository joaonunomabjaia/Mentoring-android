package mz.org.csaude.mentoring.model.tutored;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.tutored.TutoredDTO;
import mz.org.csaude.mentoring.model.employee.Employee;

@Entity(tableName = Tutored.COLUMN_TABLE_NAME,
        foreignKeys = @ForeignKey(
                entity = Employee.class,
                parentColumns = "id",
                childColumns = Tutored.COLUMN_EMPLOYEE,
                onDelete = ForeignKey.CASCADE
        ))
public class Tutored extends BaseModel {

    public static final String COLUMN_TABLE_NAME = "tutored";
    public static final String COLUMN_EMPLOYEE = "employee_id";
    public static final String COLUMN_ZERO_EVALUATION_STATUS = "zero_evaluation_status";
    public static final String COLUMN_ZERO_EVALUATION_SCORE = "zero_evaluation_score";
    public static final String COLUMN_FLOW_HISTORY = "flow_history"; // JSON array

    @NonNull
    @ColumnInfo(name = COLUMN_EMPLOYEE)
    private Integer employeeId;

    @Ignore
    private Employee employee;

    @ColumnInfo(name = COLUMN_ZERO_EVALUATION_STATUS)
    private boolean zeroEvaluationDone;

    @ColumnInfo(name = COLUMN_ZERO_EVALUATION_SCORE)
    private double zeroEvaluationScore;

    // >>> changed from FlowHistory to List<FlowHistory>
    @ColumnInfo(name = COLUMN_FLOW_HISTORY)
    private List<FlowHistory> flowHistory; // stored as JSON array via TypeConverter

    public Tutored() {}

    public Tutored(Integer employeeId) {
        this.employeeId = employeeId;
    }

    @Ignore
    public Tutored(Employee employee) {
        this.employeeId = employee.getId();
        this.employee = employee;
    }

    @Ignore
    public Tutored(TutoredDTO tutoredDTO) {
        super(tutoredDTO);
        this.zeroEvaluationDone = tutoredDTO.isZeroEvaluationDone();
        this.zeroEvaluationScore = tutoredDTO.getZeroEvaluationScore();

        if (tutoredDTO.getEmployeeDTO() != null) {
            this.employee = new Employee(tutoredDTO.getEmployeeDTO());
            this.employeeId = this.employee.getId();
        }

        // Map DTO → List<FlowHistory>
        // Prefer a list on the DTO; if only a single aux exists, wrap it.
        if (tutoredDTO.getFlowHistoryMenteeAuxDTOList() != null &&
                !tutoredDTO.getFlowHistoryMenteeAuxDTOList().isEmpty()) {

            this.flowHistory = new ArrayList<>();
            for (var fh : tutoredDTO.getFlowHistoryMenteeAuxDTOList()) {
                this.flowHistory.add(new FlowHistory(
                        fh.getEstagio(),
                        fh.getEstado(),
                        fh.getClassificacao()
                ));
            }
        } else if (tutoredDTO.getFlowHistoryMenteeAuxDTO() != null) {
            var fh = tutoredDTO.getFlowHistoryMenteeAuxDTO();
            this.flowHistory = new ArrayList<>();
            this.flowHistory.add(new FlowHistory(
                    fh.getEstagio(),
                    fh.getEstado(),
                    fh.getClassificacao()
            ));
        } else {
            this.flowHistory = null;
        }
    }

    @Override
    public String validade() {
        return employee.validade();
    }

    // Getters/Setters
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) {
        this.employee = employee;
        if (employee != null) this.employeeId = employee.getId();
    }

    public boolean isZeroEvaluationDone() { return zeroEvaluationDone; }
    public void setZeroEvaluationDone(boolean zeroEvaluationDone) { this.zeroEvaluationDone = zeroEvaluationDone; }

    public double getZeroEvaluationScore() { return zeroEvaluationScore; }
    public void setZeroEvaluationScore(double zeroEvaluationScore) { this.zeroEvaluationScore = zeroEvaluationScore; }

    public List<FlowHistory> getFlowHistory() { return flowHistory; }
    public void setFlowHistory(List<FlowHistory> flowHistory) { this.flowHistory = flowHistory; }

    @Override
    public String getDescription() {
        return this.employee != null ? this.employee.getFullName() : "No Employee";
    }

    @Override
    public String toString() {
        return this.getEmployee() != null ? this.getEmployee().getFullName() : super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tutored)) return false;
        if (!super.equals(o)) return false;
        Tutored tutored = (Tutored) o;
        return Objects.equals(employeeId, tutored.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), employeeId);
    }
}
