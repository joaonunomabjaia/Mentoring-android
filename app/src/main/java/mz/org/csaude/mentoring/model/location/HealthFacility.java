package mz.org.csaude.mentoring.model.location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.location.HealthFacilityDTO;

@Entity(tableName = HealthFacility.COLUMN_TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = District.class,
                        parentColumns = "id",
                        childColumns = HealthFacility.COLUMN_DISTRICT,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {HealthFacility.COLUMN_DISTRICT}),
                @Index(value = {HealthFacility.COLUMN_NAME}, unique = true)
        })
public class HealthFacility extends BaseModel implements Listble {

    public static final String COLUMN_TABLE_NAME = "health_facility";
    public static final String COLUMN_DISTRICT = "district_id";
    public static final String COLUMN_NAME = "name";

    @NonNull
    @ColumnInfo(name = COLUMN_DISTRICT)
    private Integer districtId;

    @Ignore
    @Relation(parentColumn = COLUMN_DISTRICT, entityColumn = "id")
    private District district;

    @NonNull
    @ColumnInfo(name = COLUMN_NAME)
    private String name;

    public HealthFacility() {
    }

    @Ignore
    public HealthFacility(HealthFacilityDTO healthFacilityDTO) {
        this.setUuid(healthFacilityDTO.getUuid());
        this.setDescription(healthFacilityDTO.getHealthFacility());
        if (healthFacilityDTO.getDistrictDTO() != null) {
            this.setDistrict(new District(healthFacilityDTO.getDistrictDTO()));
        }
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
        this.districtId = district.getId();
    }

    public String getDescription() {
        return name;
    }

    public void setDescription(String name) {
        this.name = name;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getCode() {
        return null;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
