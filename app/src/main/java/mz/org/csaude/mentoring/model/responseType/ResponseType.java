package mz.org.csaude.mentoring.model.responseType;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.responseType.ResponseTypeDTO;

@Entity(tableName = ResponseType.TABLE_NAME,
        indices = {
                @Index(value = {ResponseType.COLUMN_CODE}, unique = true)
        })
public class ResponseType extends BaseModel {

    public static final String TABLE_NAME = "response_type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE = "code";

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    public ResponseType() {
    }

    @Ignore
    public ResponseType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    @Ignore
    public ResponseType(ResponseTypeDTO responseTypeDTO) {
        super(responseTypeDTO);
        this.setCode(responseTypeDTO.getCode());
        this.setDescription(responseTypeDTO.getDescription());
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
