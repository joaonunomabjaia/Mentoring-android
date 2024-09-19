package mz.org.csaude.mentoring.dto.user;

import java.util.List;




import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.employee.EmployeeDTO;
import mz.org.csaude.mentoring.dto.role.UserRoleDTO;
import mz.org.csaude.mentoring.model.user.User;




public class UserDTO extends BaseEntityDTO {

    private String username;

    private String password;

    private String salt;

    private EmployeeDTO employeeDTO;

    private List<UserRoleDTO> userRoleDTOS;

    public UserDTO(User user) {
        super(user);
        this.setEmployeeDTO(new EmployeeDTO(user.getEmployee()));
        this.setUsername(user.getUserName());
        this.setPassword(user.getPassword());
        this.setSalt(user.getSalt());
    }

    public UserDTO() {
    }

    public List<UserRoleDTO> getUserRoleDTOS() {
        return userRoleDTOS;
    }

    public void setUserRoleDTOS(List<UserRoleDTO> userRoleDTOS) {
        this.userRoleDTOS = userRoleDTOS;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public EmployeeDTO getEmployeeDTO() {
        return employeeDTO;
    }

    public void setEmployeeDTO(EmployeeDTO employeeDTO) {
        this.employeeDTO = employeeDTO;
    }
}
