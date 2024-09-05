package mz.org.csaude.mentoring.dto.program;


import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.program.Program;

public class ProgramDTO extends BaseEntityDTO {
    private String description;
    private String name;
    public ProgramDTO() {
        super();
    }
    public ProgramDTO(Program program) {
        super(program);
        this.setDescription(program.getDescription());
        this.setName(program.getName());
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

    public Program getProgram() {
        Program program = new Program();
        program.setUuid(this.getUuid());
        program.setCreatedAt(this.getCreatedAt());
        program.setUpdatedAt(this.getUpdatedAt());
        program.setLifeCycleStatus(this.getLifeCycleStatus());
        program.setName(this.getName());
        program.setDescription(this.getDescription());
        return program;
    }
}
