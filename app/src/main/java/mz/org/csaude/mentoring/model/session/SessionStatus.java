package mz.org.csaude.mentoring.model.session;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import com.fasterxml.jackson.annotation.JsonIgnore;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.session.SessionStatusDTO;

@Entity(
        tableName = SessionStatus.TABLE_NAME,
        indices = {@Index(value = SessionStatus.COLUMN_CODE, unique = true)}
)
public class SessionStatus extends BaseModel {

    public static final String COMPLETE = "COMPLETE";
    public static final String INCOMPLETE = "INCOMPLETE";

    public static final String TABLE_NAME = "session_status";

    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE = "code";

    @NonNull
    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @NonNull
    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    public SessionStatus() {
    }

    @Ignore
    public SessionStatus(SessionStatusDTO sessionStatusDTO) {
        super(sessionStatusDTO);
        this.description = sessionStatusDTO.getDescription();
        this.code = sessionStatusDTO.getCode();
    }

    @Ignore
    public SessionStatus(String description, String code) {
        this.description = description;
        this.code = code;
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

    @Ignore
    @JsonIgnore
    public boolean isCompleted() {
        return this.code.equals(COMPLETE);
    }
}
