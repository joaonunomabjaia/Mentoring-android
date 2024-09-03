package mz.org.csaude.mentoring.model.location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.location.ProvinceDTO;

@Entity(tableName = Province.COLUMN_TABLE_NAME,
        indices = {
                @Index(value = {Province.COLUMN_DESIGNATION}, unique = true)
        })
public class Province extends BaseModel implements Listble {

    public static final String COLUMN_TABLE_NAME = "province";
    public static final String COLUMN_DESIGNATION = "designation";

    @NonNull
    @ColumnInfo(name = COLUMN_DESIGNATION)
    private String designation;

    public String getDescription() {
        return designation;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getCode() {
        return null;
    }

    public void setDescription(String designation) {
        this.designation = designation;
    }

    public Province() {
    }

    @Ignore
    public Province(ProvinceDTO provinceDTO) {
        super(provinceDTO);
        this.setDescription(provinceDTO.getDesignation());
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
