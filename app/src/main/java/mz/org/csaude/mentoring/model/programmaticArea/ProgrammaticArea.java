package mz.org.csaude.mentoring.model.programmaticArea;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.programmaticArea.ProgrammaticAreaDTO;
import mz.org.csaude.mentoring.model.program.Program;

@Entity(tableName = ProgrammaticArea.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Program.class,
                        parentColumns = "id",
                        childColumns = ProgrammaticArea.COLUMN_PROGRAM,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {ProgrammaticArea.COLUMN_CODE}, unique = true),
                @Index(value = {ProgrammaticArea.COLUMN_PROGRAM})
        })
public class ProgrammaticArea extends BaseModel {

    public static final String TABLE_NAME = "programmatic_area";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PROGRAM = "program_id";

    @NonNull
    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @NonNull
    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    @NonNull
    @ColumnInfo(name = COLUMN_NAME)
    private String name;

    @NonNull
    @ColumnInfo(name = COLUMN_PROGRAM)
    private Integer programId;

    @Ignore
    private Program program;

    public ProgrammaticArea() {
    }

    @Ignore
    public ProgrammaticArea(ProgrammaticAreaDTO programmaticAreaDTO) {
        super(programmaticAreaDTO);
        this.setCode(programmaticAreaDTO.getCode());
        this.setDescription(programmaticAreaDTO.getDescription());
        this.setName(programmaticAreaDTO.getName());
        if (programmaticAreaDTO.getProgram() != null) {
            this.program = new Program(programmaticAreaDTO.getProgram());
            this.programId = this.program.getId();
        }
    }

    @Ignore
    public ProgrammaticArea(String description, String code, String name, Program program) {
        this.description = description;
        this.code = code;
        this.name = name;
        this.program = program;
        this.programId = program.getId();
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
        this.programId = program.getId();
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }
}
