package mz.org.csaude.mentoring.model.career;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.career.CareerTypeDTO;

@Entity(tableName = CareerType.COLUMN_TABLE_NAME)
public class CareerType extends BaseModel implements Listble {

    public static final String COLUMN_TABLE_NAME = "career_type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE = "code";

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    public CareerType() {
        super();
    }

    public CareerType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public CareerType(CareerTypeDTO careerTypeDTO) {
        super(careerTypeDTO);
        this.setCode(careerTypeDTO.getCode());
        this.setDescription(careerTypeDTO.getDescription());
    }

    @Override
    public int getListPosition() {
        return 0;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int getDrawable() {
        return 0;
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
