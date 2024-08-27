package mz.org.csaude.mentoring.model.program;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.program.ProgramDTO;

@Entity(tableName = Program.TABLE_NAME,
        indices = {
                @Index(value = {Program.COLUMN_NAME}, unique = true)
        })
public class Program extends BaseModel {

    public static final String TABLE_NAME = "program";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";

    @ColumnInfo(name = COLUMN_NAME)
    private String name;

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    public Program() {
        super();
    }

    @Ignore
    public Program(ProgramDTO programDTO) {
        super(programDTO);
        this.setDescription(programDTO.getDescription());
        this.setName(programDTO.getName());
    }

    @Ignore
    public Program(String description, String name) {
        this.description = description;
        this.name = name;
    }

    @Override
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
}
