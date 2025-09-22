package mz.org.csaude.mentoring.model.location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.location.CabinetDTO;

@Entity(tableName = Cabinet.TABLE_NAME,
        indices = {
                @Index(value = {Cabinet.COLUMN_NAME}, unique = true)
        })
public class Cabinet extends BaseModel {

    public static final String TABLE_NAME = "cabinet";
    public static final String COLUMN_NAME = "name";
    public static final String COMMUNITY_CABINET_UUID = "e5f5ffee-8f90-4c92-9ad8-5280f62b7f33";

    @NonNull
    @ColumnInfo(name = COLUMN_NAME)
    private String name;

    public Cabinet() {
    }

    public Cabinet(String name) {
        this.name = name;
    }

    public Cabinet(CabinetDTO dto) {
        super(dto);
        this.setName(dto.getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return name;
    }

    public boolean isCommunityCabinet() {
        return getUuid().equals(COMMUNITY_CABINET_UUID);
    }
}
