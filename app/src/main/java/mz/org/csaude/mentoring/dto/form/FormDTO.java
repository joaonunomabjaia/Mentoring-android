package mz.org.csaude.mentoring.dto.form;




import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.partner.PartnerDTO;
import mz.org.csaude.mentoring.dto.programmaticArea.ProgrammaticAreaDTO;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.model.programmaticArea.ProgrammaticArea;




public class FormDTO extends BaseEntityDTO {
    private String name;
    private String code;
    private String description;
    private int targetPatient;
    private int targetFile;
    private PartnerDTO partner;
    private ProgrammaticAreaDTO programmaticArea;
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
            this.setProgrammaticArea(new ProgrammaticAreaDTO(form.getProgrammaticArea()));
        }
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

    public ProgrammaticAreaDTO getProgrammaticArea() {
        return programmaticArea;
    }

    public void setProgrammaticArea(ProgrammaticAreaDTO programmaticArea) {
        this.programmaticArea = programmaticArea;
    }

    public Form getForm() {
        Form form = new Form();
        form.setUuid(this.getUuid());
        form.setCreatedAt(this.getCreatedAt());
        form.setUpdatedAt(this.getUpdatedAt());
        form.setLifeCycleStatus(this.getLifeCycleStatus());
        form.setDescription(this.getDescription());
        form.setName(this.getName());
        form.setCode(this.getCode());
        form.setTargetFile(this.getTargetFile());
        form.setTargetPatient(this.getTargetPatient());
        if(this.getPartner()!=null) form.setPartner(new Partner(this.getPartner()));
        if(this.getProgrammaticArea()!=null) form.setProgrammaticArea(new ProgrammaticArea(this.getProgrammaticArea()));
        return form;
    }
}
