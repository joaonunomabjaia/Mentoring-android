package mz.org.csaude.mentoring.model.career;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.career.CareerDTO;

@Entity(tableName = Career.TABLE_NAME,
        foreignKeys = @ForeignKey(entity = CareerType.class,
                parentColumns = "id",
                childColumns = "career_type_id",
                onDelete = ForeignKey.CASCADE),
        indices = @Index(value = {"career_type_id"})
)
public class Career extends BaseModel implements Listble {

    public static final String TABLE_NAME = "career";
    public static final String COLUMN_POSITION = "position";
    public static final String COLUMN_CAREER_TYPE = "career_type_id";

    @ColumnInfo(name = COLUMN_CAREER_TYPE)
    private int careerTypeId;

    @Ignore
    @Relation(parentColumn = "career_type_id", entityColumn = "id")
    private CareerType careerType;

    @ColumnInfo(name = COLUMN_POSITION)
    private String position;

    public Career() {
        super();
    }

    public Career(CareerDTO careerDTO) {
        this.setUuid(careerDTO.getUuid());
        this.setPosition(careerDTO.getPosition());
        if (careerDTO.getCareerTypeDTO() != null) {
            this.setCareerType(new CareerType(careerDTO.getCareerTypeDTO()));
        }
    }

    public CareerType getCareerType() {
        return careerType;
    }

    public void setCareerType(CareerType careerType) {
        this.careerType = careerType;
        this.careerTypeId = careerType.getId();
    }

    public String getPosition() {
        return this.position;
    }

    @Override
    public String getDescription() {
        return this.getPosition();
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getCode() {
        return null;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
