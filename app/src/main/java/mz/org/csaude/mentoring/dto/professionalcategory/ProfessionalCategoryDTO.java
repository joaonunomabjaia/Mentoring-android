package mz.org.csaude.mentoring.dto.professionalcategory;

import java.io.Serializable;



import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.professionalCategory.ProfessionalCategory;



public class ProfessionalCategoryDTO extends BaseEntityDTO {

    private String code;
    private String description;

    public ProfessionalCategoryDTO(ProfessionalCategory professionalCategory){
        super(professionalCategory);
        this.setCode(professionalCategory.getCode());
        this.setDescription(professionalCategory.getDescription());
    }
    public ProfessionalCategoryDTO() {
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
    public ProfessionalCategory getProfessionalCategory() {
        ProfessionalCategory professionalCategory = new ProfessionalCategory();
        professionalCategory.setUpdatedAt(this.getUpdatedAt());
        professionalCategory.setCode(this.getCode());
        professionalCategory.setUuid(this.getUuid());
        professionalCategory.setDescription(this.getDescription());
        professionalCategory.setCreatedAt(this.getCreatedAt());
        professionalCategory.setLifeCycleStatus(this.getLifeCycleStatus());
        return professionalCategory;
    }

}
