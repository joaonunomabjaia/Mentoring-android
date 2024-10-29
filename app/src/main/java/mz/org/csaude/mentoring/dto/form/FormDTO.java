package mz.org.csaude.mentoring.dto.form;




import java.util.List;

import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.partner.PartnerDTO;
import mz.org.csaude.mentoring.dto.programmaticArea.ProgrammaticAreaDTO;
import mz.org.csaude.mentoring.model.form.Form;


public class FormDTO extends BaseEntityDTO {
    private String name;
    private String code;
    private String description;
    private int targetPatient;
    private int targetFile;
    private PartnerDTO partner;
    private ProgrammaticAreaDTO programmaticAreaDTO;
    private List<FormSectionDTO> formSections;


    public FormDTO(Form form) {
        super(form);
        this.setCode(form.getCode());
        this.setDescription(form.getDescription());
        this.setTargetFile(form.getTargetFile());
        this.setTargetPatient(form.getTargetPatient());
        if(form.getPartner()!=null) {
            this.setPartner(new PartnerDTO(form.getPartner()));
        }
        if(form.getProgrammaticArea()!=null) {
            this.setProgrammaticAreaDTO(new ProgrammaticAreaDTO(form.getProgrammaticArea()));
        }
        this.setName(form.getName());

    }
    public FormDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getTargetPatient() {
        return targetPatient;
    }

    public void setTargetPatient(int targetPatient) {
        this.targetPatient = targetPatient;
    }

    public int getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(int targetFile) {
        this.targetFile = targetFile;
    }

    public PartnerDTO getPartner() {
        return partner;
    }

    public void setPartner(PartnerDTO partner) {
        this.partner = partner;
    }

    public ProgrammaticAreaDTO getProgrammaticAreaDTO() {
        return programmaticAreaDTO;
    }

    public void setProgrammaticAreaDTO(ProgrammaticAreaDTO programmaticAreaDTO) {
        this.programmaticAreaDTO = programmaticAreaDTO;
    }

    public List<FormSectionDTO> getFormSections() {
        return formSections;
    }

    public void setFormSections(List<FormSectionDTO> formSections) {
        this.formSections = formSections;
    }
}
