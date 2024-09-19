package mz.org.csaude.mentoring.dto.programmaticArea;




import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.program.ProgramDTO;
import mz.org.csaude.mentoring.model.program.Program;
import mz.org.csaude.mentoring.model.programmaticArea.ProgrammaticArea;



public class ProgrammaticAreaDTO extends BaseEntityDTO {
    private String code;
    private String description;
    private String name;
    private ProgramDTO program;
    public ProgrammaticAreaDTO(ProgrammaticArea programmaticArea) {
        super(programmaticArea);
        this.setCode(programmaticArea.getCode());
        this.setDescription(programmaticArea.getDescription());
        this.setName(programmaticArea.getName());
        if(programmaticArea.getProgram()!=null) {
            this.setProgram(new ProgramDTO(programmaticArea.getProgram()));
        }
    }

    public ProgrammaticAreaDTO() {
        super();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProgramDTO getProgram() {
        return program;
    }

    public void setProgram(ProgramDTO program) {
        this.program = program;
    }

    public ProgrammaticArea getProgrammaticArea() {
        ProgrammaticArea programmaticArea = new ProgrammaticArea();
        programmaticArea.setUuid(this.getUuid());
        programmaticArea.setCreatedAt(this.getCreatedAt());
        programmaticArea.setUpdatedAt(this.getUpdatedAt());
        programmaticArea.setLifeCycleStatus(this.getLifeCycleStatus());
        programmaticArea.setCode(this.getCode());
        programmaticArea.setDescription(this.getDescription());
        programmaticArea.setName(this.getName());
        if(this.getProgram()!=null) {
            programmaticArea.setProgram(new Program(this.getProgram()));
        }
        return programmaticArea;
    }
}
