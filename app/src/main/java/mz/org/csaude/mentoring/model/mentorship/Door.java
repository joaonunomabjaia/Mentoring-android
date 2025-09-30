package mz.org.csaude.mentoring.model.mentorship;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.mentorship.DoorDTO;

@Entity(tableName = Door.TABLE_NAME,
        indices = {
                @Index(value = {Door.COLUMN_CODE}, unique = true)
        })
public class Door extends BaseModel {

    public static final String TABLE_NAME = "door";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE = "code";
    public static final String COMMUNITY_DOOR= "COMMUNITY";

    @NonNull
    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @NonNull
    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    public Door() {
    }

    @Ignore
    public Door(DoorDTO doorDTO) {
        super(doorDTO);
        this.setDescription(doorDTO.getDescription());
        this.setCode(doorDTO.getCode());
    }

    @Ignore
    public Door(String description, String code) {
        this.description = description;
        this.code = code;
    }

    @Override
    public String getDescription() {
        return this.description;
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

    public boolean isCommunityDoor() {
        return COMMUNITY_DOOR.equals(this.code);
    }
}
