package mz.org.csaude.mentoring.dto.ronda;



import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.rondatype.RondaType;


public class RondaTypeDTO extends BaseEntityDTO {
    private String code;
    private String description;
    public RondaTypeDTO(RondaType rondaType) {
        super(rondaType);
        this.setCode(rondaType.getCode());
        this.setDescription(rondaType.getDescription());
    }
    public RondaTypeDTO() {
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
    public RondaType getRondaType() {
        RondaType rondaType = new RondaType();
        rondaType.setUuid(this.getUuid());
        rondaType.setCode(this.getCode());
        rondaType.setDescription(this.getDescription());
        rondaType.setLifeCycleStatus(this.getLifeCycleStatus());
        return rondaType;
    }
}
