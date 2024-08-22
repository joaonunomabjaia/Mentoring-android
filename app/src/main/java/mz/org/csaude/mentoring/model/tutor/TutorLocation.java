package mz.org.csaude.mentoring.model.tutor;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.tutor.Tutor;

@Entity(tableName = TutorLocation.COLUMN_TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Tutor.class,
                        parentColumns = "id",
                        childColumns = TutorLocation.COLUMN_TUTOR,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = HealthFacility.class,
                        parentColumns = "id",
                        childColumns = TutorLocation.COLUMN_HEALTHFACILITY,
                        onDelete = ForeignKey.CASCADE)
        })
public class TutorLocation extends BaseModel {

    public static final String COLUMN_TABLE_NAME = "tutor_location";
    public static final String COLUMN_TUTOR = "tutor_id";
    public static final String COLUMN_HEALTHFACILITY = "health_facility_id";

    @ColumnInfo(name = COLUMN_TUTOR)
    private Long tutorId;

    @ColumnInfo(name = COLUMN_HEALTHFACILITY)
    private Long healthFacilityId;

    @Relation(
            parentColumn = COLUMN_TUTOR,
            entityColumn = "id"
    )
    private Tutor tutor;

    @Relation(
            parentColumn = COLUMN_HEALTHFACILITY,
            entityColumn = "id"
    )
    private HealthFacility healthFacility;

    public TutorLocation() {
    }

    public TutorLocation(Long tutorId, Long healthFacilityId) {
        this.tutorId = tutorId;
        this.healthFacilityId = healthFacilityId;
    }

    // Getters and Setters

    public Long getTutorId() {
        return tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public Long getHealthFacilityId() {
        return healthFacilityId;
    }

    public void setHealthFacilityId(Long healthFacilityId) {
        this.healthFacilityId = healthFacilityId;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public HealthFacility getHealthFacility() {
        return healthFacility;
    }

    public void setHealthFacility(HealthFacility healthFacility) {
        this.healthFacility = healthFacility;
    }
}
