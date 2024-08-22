package mz.org.csaude.mentoring.model.tutored;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

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

    @ColumnInfo(name = COLUMN_EMPLOYEE)
    private Integer employeeId;

    @Relation(
            parentColumn = COLUMN_EMPLOYEE,
            entityColumn = "id"
    )
    private Employee employee;

    @ColumnInfo(name = COLUMN_ZERO_EVALUATION_STATUS)
    private boolean zeroEvaluationDone;

    @ColumnInfo(name = COLUMN_ZERO_EVALUATION_SCORE)
    private Double zeroEvaluationScore;

    public Tutored() {
    }

    public Tutored(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Tutored(Employee employee) {
        this.employeeId = employee.getId();
        this.employee = employee;
    }

    public Tutored(TutoredDTO tutoredDTO) {
        super(tutoredDTO);
        this.zeroEvaluationDone = tutoredDTO.isZeroEvaluationDone();
        this.zeroEvaluationScore = tutoredDTO.getZeroEvaluationScore();
        if (tutoredDTO.getEmployeeDTO() != null) {
            this.employee = new Employee(tutoredDTO.getEmployeeDTO());
            this.employeeId = this.employee.getId();
        }
    }

    // Getters and Setters

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        if (employee != null) {
            this.employeeId = employee.getId();
        }
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

    @Override
    public String getDescription() {
        return this.employee.getFullName();
    }

    @Override
    public String toString() {
        return "Tutored{" +
                "employee=" + employee +
                ", zeroEvaluationDone=" + zeroEvaluationDone +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tutored)) return false;
        if (!super.equals(o)) return false;
        Tutored tutored = (Tutored) o;
        return Objects.equals(employee, tutored.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), employee);
    }
}
