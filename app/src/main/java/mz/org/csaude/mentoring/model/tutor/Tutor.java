package mz.org.csaude.mentoring.model.tutor;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.Objects;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.tutor.TutorDTO;
import mz.org.csaude.mentoring.model.employee.Employee;

@Entity(
        tableName = Tutor.TABLE_NAME,
        foreignKeys = @ForeignKey(
                entity = Employee.class,
                parentColumns = "id",
                childColumns = Tutor.COLUMN_EMPLOYEE,
                onDelete = ForeignKey.CASCADE
        )
)
public class Tutor extends BaseModel {

  public static final String TABLE_NAME = "tutor";
  public static final String COLUMN_EMPLOYEE = "employee_id";

  @NonNull
  @ColumnInfo(name = COLUMN_EMPLOYEE)
  private Integer employeeId;

  @Ignore // This field is ignored because it represents a relationship.
  @Relation(parentColumn = COLUMN_EMPLOYEE, entityColumn = "id")
  private Employee employee;

  public Tutor() {
  }

  public Tutor(String uuid) {
    setUuid(uuid);
  }

  @Ignore // This constructor should be ignored by Room.
  public Tutor(TutorDTO tutorDTO) {
    super(tutorDTO);
    if (tutorDTO.getEmployeeDTO() != null) {
      this.employee = new Employee(tutorDTO.getEmployeeDTO());
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

  @Override
  public String validade() {
    return this.employee.validade();
  }

  @Override
  public String toString() {
    return "Tutor{" +
            "employee=" + employee +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Tutor)) return false;
    if (!super.equals(o)) return false;
    Tutor tutor = (Tutor) o;
    return Objects.equals(employee, tutor.employee);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), employee);
  }
}
