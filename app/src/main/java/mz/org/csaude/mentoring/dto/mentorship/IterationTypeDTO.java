package mz.org.csaude.mentoring.dto.mentorship;




import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.mentorship.IterationType;



public class IterationTypeDTO extends BaseEntityDTO {
    private String code;
    private String description;
    public IterationTypeDTO(IterationType iterationType) {
        super(iterationType);
        this.setCode(iterationType.getCode());
        this.setDescription(iterationType.getDescription());
    }

    public IterationTypeDTO() {
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
    public IterationType getIterationType() {
        IterationType iterationType = new IterationType();
        iterationType.setUuid(this.getUuid());
        iterationType.setCreatedAt(this.getCreatedAt());
        iterationType.setUpdatedAt(this.getUpdatedAt());
        iterationType.setLifeCycleStatus(this.getLifeCycleStatus());
        iterationType.setCode(this.getCode()); // Set code from DTO to the IterationType
        iterationType.setDescription(this.getDescription()); // Set description from DTO to the IterationType
        return iterationType;
    }

}
