package mz.org.csaude.mentoring.model.location;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.location.DistrictDTO;

@Entity(tableName = District.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Province.class,
                        parentColumns = "id",
                        childColumns = District.COLUMN_PROVINCE,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {District.COLUMN_PROVINCE}),
                @Index(value = {District.COLUMN_DISTRICT}, unique = true)
        })
public class District extends BaseModel {

    public static final String TABLE_NAME = "district";
    public static final String COLUMN_PROVINCE = "province_id";
    public static final String COLUMN_DISTRICT = "district";

    @ColumnInfo(name = COLUMN_PROVINCE)
    private int provinceId;

    @Ignore
    @Relation(parentColumn = COLUMN_PROVINCE, entityColumn = "id")
    private Province province;

    @ColumnInfo(name = COLUMN_DISTRICT)
    private String district;

    public District() {
    }

    public District(Province province, String district) {
        this.province = province;
        this.provinceId = province.getId();
        this.district = district;
    }

    public District(DistrictDTO districtDTO) {
        this.setUuid(districtDTO.getUuid());
        this.setDescription(districtDTO.getDescription());
        if (districtDTO.getProvinceDTO() != null) {
            this.setProvince(new Province(districtDTO.getProvinceDTO()));
        }
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
        this.provinceId = province.getId();
    }

    public String getDescription() {
        return district;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getCode() {
        return null;
    }

    public void setDescription(String district) {
        this.district = district;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
