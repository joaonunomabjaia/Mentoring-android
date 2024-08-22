package mz.org.csaude.mentoring.model.session;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;

import mz.org.csaude.mentoring.base.model.BaseModel;

@Entity(tableName = SessionStatus.TABLE_NAME)
public class SessionStatus extends BaseModel {

    public static final String COMPLETE = "COMPLETE";
    public static final String INCOMPLETE = "INCOMPLETE";

    public static final String TABLE_NAME = "session_status";

    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE = "code";

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    public SessionStatus() {
    }

    public SessionStatus(String description, String code) {
        this.description = description;
        this.code = code;
    }

    // Removed the constructor using SessionStatusDTO since Room entities typically don't use DTOs directly.
    // You would map a DTO to this entity in your repository or data layer.

    // Getters and Setters


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

    @JsonIgnore
    public boolean isCompleted() {
        return COMPLETE.equals(this.code);
    }
}
