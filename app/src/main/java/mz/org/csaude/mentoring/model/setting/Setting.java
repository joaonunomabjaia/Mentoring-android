package mz.org.csaude.mentoring.model.setting;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import mz.org.csaude.mentoring.base.model.BaseModel;

@Entity(tableName = Setting.TABLE_NAME)
public class Setting extends BaseModel {

    public static final String TABLE_NAME = "setting";

    public static final String COLUMN_DESIGNATION = "designation";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_ENABLED = "enabled";

    @ColumnInfo(name = COLUMN_DESIGNATION)
    private String designation;

    @ColumnInfo(name = COLUMN_VALUE)
    private String value;

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @ColumnInfo(name = COLUMN_TYPE)
    private String type;

    @ColumnInfo(name = COLUMN_ENABLED)
    private Boolean enabled;

    public Setting() {
    }

    public Setting(String designation, String value, String description, String type, Boolean enabled) {
        this.designation = designation;
        this.value = value;
        this.description = description;
        this.type = type;
        this.enabled = enabled;
    }

    // Getters and Setters
    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
