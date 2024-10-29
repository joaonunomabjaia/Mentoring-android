package mz.org.csaude.mentoring.dto.section;

import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.form.Section;

public class SectionDTO extends BaseEntityDTO {
    private String description;

    public SectionDTO() {}

    public SectionDTO(Section section) {
        super(section);
        this.setDescription(section.getDescription());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
