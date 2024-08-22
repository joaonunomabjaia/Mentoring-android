package mz.org.csaude.mentoring.model.mentorship;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.mentorship.IterationTypeDTO;

@Entity(tableName = IterationType.TABLE_NAME,
        indices = {
                @Index(value = {IterationType.COLUMN_CODE}, unique = true)
        })
public class IterationType extends BaseModel {

    public static final String TABLE_NAME = "iteration_type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE = "code";

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    public IterationType() {
    }

    public IterationType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public IterationType(IterationTypeDTO iterationTypeDTO) {
        super(iterationTypeDTO);
        this.setCode(iterationTypeDTO.getCode());
        this.setDescription(iterationTypeDTO.getDescription());
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
}
