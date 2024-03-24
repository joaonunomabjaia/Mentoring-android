package mz.org.csaude.mentoring.model.location;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dao.location.DistrictDAOImpl;

@Data
@DatabaseTable(tableName = District.TABLE_NAME, daoClass = DistrictDAOImpl.class)
@EqualsAndHashCode(callSuper=false)
public class District extends BaseModel implements Listble {

    public static final String TABLE_NAME = "district";

    public static final String COLUMN_PROVINCE = "province_id";

    public static final String COLUMN_DISTRICT = "district";

    @DatabaseField(columnName = COLUMN_PROVINCE, canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Province province;

    @DatabaseField(columnName = COLUMN_DISTRICT, unique = true, canBeNull = false)
    private String district;

    public District() {
    }

    public District(Province province, String district) {
        this.province = province;
        this.district = district;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Override
    public String getDescription() {
        return this.district;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getCode() {
        return null;
    }
}
