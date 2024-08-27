package mz.org.csaude.mentoring.model.rondatype;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.ronda.RondaTypeDTO;

@Entity(tableName = RondaType.TABLE_NAME)
public class RondaType extends BaseModel {

    public static final String TABLE_NAME = "ronda_type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE = "code";

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    public RondaType() {
    }

    @Ignore
    public RondaType(RondaTypeDTO rondaTypeDTO) {
        super(rondaTypeDTO);
        this.setDescription(rondaTypeDTO.getDescription());
        this.setCode(rondaTypeDTO.getCode());
    }

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
