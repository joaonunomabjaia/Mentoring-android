package mz.org.csaude.mentoring.dto.mentorship;



import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.mentorship.Door;



public class DoorDTO extends BaseEntityDTO {
    private String code;
    private String description;
    public DoorDTO(Door door) {
        super(door);
        this.setCode(door.getCode());
        this.setDescription(door.getDescription());
    }
    public DoorDTO() {
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
    public Door getDoor() {
        Door door = new Door();
        door.setUuid(this.getUuid());
        door.setCreatedAt(this.getCreatedAt());
        door.setUpdatedAt(this.getUpdatedAt());
        door.setLifeCycleStatus(this.getLifeCycleStatus());
        door.setCode(this.getCode());
        door.setDescription(this.getDescription());
        return door;
    }
}
