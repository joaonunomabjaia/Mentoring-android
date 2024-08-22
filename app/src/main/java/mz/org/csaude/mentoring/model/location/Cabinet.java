package mz.org.csaude.mentoring.model.location;

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
}
