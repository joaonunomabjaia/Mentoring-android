package mz.org.csaude.mentoring.model.user;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.user.UserDTO;
import mz.org.csaude.mentoring.model.employee.Employee;
import mz.org.csaude.mentoring.util.LifeCycleStatus;
import mz.org.csaude.mentoring.util.Utilities;

@Entity(
        tableName = "user",
        foreignKeys = @ForeignKey(
                entity = Employee.class,
                parentColumns = "id",
                childColumns = "employee_id",
                onDelete = ForeignKey.CASCADE
        )
)
public class User extends BaseModel {

    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_SALT = "salt";
    public static final String COLUMN_ADMIN = "admin";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_EMPLOYEE = "employee_id";

    @NonNull
    @ColumnInfo(name = COLUMN_USER_NAME)
    private String userName;

    @NonNull
    @ColumnInfo(name = COLUMN_PASSWORD)
    private String password;

    @NonNull
    @ColumnInfo(name = COLUMN_SALT)
    private String salt;

    @ColumnInfo(name = COLUMN_ADMIN)
    private boolean admin;

    @ColumnInfo(name = COLUMN_TYPE)
    private String type;

    @NonNull
    @ColumnInfo(name = COLUMN_EMPLOYEE)
    private Integer employeeId;

    @Ignore
    private Employee employee;

    public User() {
    }
    @Ignore
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    @Ignore
    public User(UserDTO userDTO) {
        this.userName = userDTO.getUsername();
        this.password = userDTO.getPassword();
        this.salt = userDTO.getSalt();
        this.setUuid(userDTO.getUuid());
        if (userDTO.getEmployeeDTO() != null) {
            this.employee = new Employee(userDTO.getEmployeeDTO());
            this.employeeId = this.employee.getId();
        }
        this.setCreatedAt(userDTO.getCreatedAt());
        this.setUpdatedAt(userDTO.getUpdatedAt());
    }

    // Getters and Setters

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    // Custom Methods

    public String getFullName() {
        return this.employee.getFullName();
    }

    private String validateToLogin() {
        if (!Utilities.stringHasValue(this.userName)) return "O campo Utilizador deve ser preenchido.";
        if (!Utilities.stringHasValue(this.password)) return "O campo Senha deve ser preenchido.";
        if (this.userName.length() <= 3) return "O nome do utilizador deve ter o mínimo de quatro caracteres.";
        if (this.password.length() <= 3) return "A senha deve ter o mínimo de quatro caracteres.";
        return "";
    }

    public String isValid() {
        return validateToLogin();
    }

    public boolean isActivated() {
        return this.getLifeCycleStatus().equals(LifeCycleStatus.ACTIVE);
    }
}
