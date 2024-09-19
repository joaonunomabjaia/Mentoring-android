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
import mz.org.csaude.mentoring.dto.location.LocationDTO;
import mz.org.csaude.mentoring.model.employee.Employee;

@Entity(tableName = Location.COLUMN_TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Employee.class,
                        parentColumns = "id",
                        childColumns = Location.COLUMN_EMPLOYEE,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Province.class,
                        parentColumns = "id",
                        childColumns = Location.COLUMN_PROVINCE,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = District.class,
                        parentColumns = "id",
                        childColumns = Location.COLUMN_DISTRICT,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = HealthFacility.class,
                        parentColumns = "id",
                        childColumns = Location.COLUMN_HEALTH_FACILITY,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {Location.COLUMN_EMPLOYEE}),
                @Index(value = {Location.COLUMN_PROVINCE}),
                @Index(value = {Location.COLUMN_DISTRICT}),
                @Index(value = {Location.COLUMN_HEALTH_FACILITY})
        })
public class Location extends BaseModel implements Listble {

    public static final String COLUMN_TABLE_NAME = "location";
    public static final String COLUMN_EMPLOYEE = "employee_id";
    public static final String COLUMN_PROVINCE = "province_id";
    public static final String COLUMN_DISTRICT = "district_id";
    public static final String COLUMN_HEALTH_FACILITY = "health_facility_id"; // Corrected typo from "health_hacility_id" to "health_facility_id"
    public static final String COLUMN_LOCATION_LEVEL = "location_level";

    @NonNull
    @ColumnInfo(name = COLUMN_EMPLOYEE)
    private Integer employeeId;

    @Ignore
    @Relation(parentColumn = COLUMN_EMPLOYEE, entityColumn = "id")
    private Employee employee;

    @NonNull
    @ColumnInfo(name = COLUMN_PROVINCE)
    private Integer provinceId;

    @Ignore
    @Relation(parentColumn = COLUMN_PROVINCE, entityColumn = "id")
    private Province province;

    @NonNull
    @ColumnInfo(name = COLUMN_DISTRICT)
    private Integer districtId;

    @Ignore
    @Relation(parentColumn = COLUMN_DISTRICT, entityColumn = "id")
    private District district;

    @NonNull
    @ColumnInfo(name = COLUMN_HEALTH_FACILITY)
    private Integer healthFacilityId;

    @Ignore
    @Relation(parentColumn = COLUMN_HEALTH_FACILITY, entityColumn = "id")
    private HealthFacility healthFacility;


    @ColumnInfo(name = COLUMN_LOCATION_LEVEL)
    private String locationLevel;

    public Location() {
    }

    @Ignore
    public Location(Province province, District district, HealthFacility healthFacility, String locationLevel) {
        this.province = province;
        this.provinceId = province.getId();
        this.district = district;
        this.districtId = district.getId();
        this.healthFacility = healthFacility;
        this.healthFacilityId = healthFacility.getId();
        this.locationLevel = locationLevel;
    }

    @Ignore
    public Location(LocationDTO locationDTO) {
        this.setUuid(locationDTO.getUuid());
        this.setLocationLevel(locationDTO.getLocationLevel());
        if (locationDTO.getProvinceDTO() != null) this.setProvince(new Province(locationDTO.getProvinceDTO()));
        if (locationDTO.getDistrictDTO() != null) this.setDistrict(new District(locationDTO.getDistrictDTO()));
        if (locationDTO.getHealthFacilityDTO() != null) this.setHealthFacility(new HealthFacility(locationDTO.getHealthFacilityDTO()));
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        this.employeeId = employee.getId();
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
        this.provinceId = province.getId();
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
        this.districtId = district.getId();
    }

    public HealthFacility getHealthFacility() {
        return healthFacility;
    }

    public void setHealthFacility(HealthFacility healthFacility) {
        this.healthFacility = healthFacility;
        this.healthFacilityId = healthFacility.getId();
    }

    public String getLocationLevel() {
        return locationLevel;
    }

    public void setLocationLevel(String locationLevel) {
        this.locationLevel = locationLevel;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getCode() {
        return null;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public Integer getHealthFacilityId() {
        return healthFacilityId;
    }

    public void setHealthFacilityId(Integer healthFacilityId) {
        this.healthFacilityId = healthFacilityId;
    }
}
