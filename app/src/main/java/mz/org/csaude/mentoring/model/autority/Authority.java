package mz.org.csaude.mentoring.model.autority;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import mz.org.csaude.mentoring.base.model.BaseModel;

@Entity(tableName = Authority.TABLE_NAME)
public class Authority extends BaseModel {

    public static final String TABLE_NAME = "authority";
    public static final String COLUMN_MODULE = "module";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE = "code";

    @ColumnInfo(name = COLUMN_MODULE)
    private String module;

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
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
