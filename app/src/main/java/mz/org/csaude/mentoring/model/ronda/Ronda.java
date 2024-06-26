package mz.org.csaude.mentoring.model.ronda;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.List;

import lombok.Data;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dao.ronda.RondaDAOImpl;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.rondatype.RondaType;


@DatabaseTable(tableName = Ronda.TABLE_NAME, daoClass = RondaDAOImpl.class)
public class Ronda extends BaseModel {

    public static final String TABLE_NAME = "ronda";

    public static final String COLUMN_START_DATE = "start_date";

    public static final String COLUMN_END_DATE = "end_date";

    public static final String COLUMN_DESCRIPTION = "description";

    public static final String COLUMN_HEALTH_FACILITY = "health_facility_id";

    public static final String COLUMN_RONDA_TYPE = "ronda_type_id";

    @DatabaseField(columnName = COLUMN_START_DATE, canBeNull = false)
    private Date startDate;

    @DatabaseField(columnName = COLUMN_END_DATE, canBeNull = true)
    private Date endDate;


    @DatabaseField(columnName = COLUMN_HEALTH_FACILITY, canBeNull = false, foreign = true, foreignAutoRefresh = true )
    private HealthFacility healthFacility;

    @DatabaseField(columnName = COLUMN_RONDA_TYPE, canBeNull = false, foreign = true, foreignAutoRefresh = true )
    private RondaType rondaType;

    private List<RondaMentee> rondaMentees;

    private List<RondaMentor> rondaMentors;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public HealthFacility getHealthFacility() {
        return healthFacility;
    }

    public void setHealthFacility(HealthFacility healthFacility) {
        this.healthFacility = healthFacility;
    }

    public RondaType getRondaType() {
        return rondaType;
    }

    public void setRondaType(RondaType rondaType) {
        this.rondaType = rondaType;
    }

    public List<RondaMentee> getRondaMentees() {
        return rondaMentees;
    }

    public void setRondaMentees(List<RondaMentee> rondaMentees) {
        this.rondaMentees = rondaMentees;
    }

    public List<RondaMentor> getRondaMentors() {
        return rondaMentors;
    }

    public void setRondaMentors(List<RondaMentor> rondaMentors) {
        this.rondaMentors = rondaMentors;
    }
}
