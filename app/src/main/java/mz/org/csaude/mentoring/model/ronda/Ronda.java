package mz.org.csaude.mentoring.model.ronda;

import com.fasterxml.jackson.annotation.JsonIgnore;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.ronda.RondaDTO;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.RondaStatus;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;

@Entity(tableName = Ronda.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = HealthFacility.class,
                        parentColumns = "id",
                        childColumns = Ronda.COLUMN_HEALTH_FACILITY,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = RondaType.class,
                        parentColumns = "id",
                        childColumns = Ronda.COLUMN_RONDA_TYPE,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {Ronda.COLUMN_HEALTH_FACILITY}),
                @Index(value = {Ronda.COLUMN_RONDA_TYPE})
        })
public class Ronda extends BaseModel implements Listble {

    public static final String TABLE_NAME = "ronda";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_HEALTH_FACILITY = "health_facility_id";
    public static final String COLUMN_RONDA_TYPE = "ronda_type_id";
    public static final String COLUMN_MENTOR_TYPE = "mentor_type";

    @NonNull
    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @NonNull
    @ColumnInfo(name = COLUMN_START_DATE)
    private Date startDate;

    @ColumnInfo(name = COLUMN_END_DATE)
    private Date endDate;

    @NonNull
    @ColumnInfo(name = COLUMN_HEALTH_FACILITY)
    private Integer healthFacilityId;

    @Relation(parentColumn = COLUMN_HEALTH_FACILITY, entityColumn = "id")
    @Ignore
    private HealthFacility healthFacility;

    @NonNull
    @ColumnInfo(name = COLUMN_RONDA_TYPE)
    private Integer rondaTypeId;

    @Relation(parentColumn = COLUMN_RONDA_TYPE, entityColumn = "id")
    @Ignore
    private RondaType rondaType;

    @NonNull
    @ColumnInfo(name = COLUMN_MENTOR_TYPE)
    private String mentorType;

    @Ignore
    @JsonIgnore
    private List<Session> sessions;

    @Ignore
    @JsonIgnore
    private List<RondaMentee> rondaMentees;

    @Ignore
    @JsonIgnore
    private List<RondaMentor> rondaMentors;

    public Ronda() {
    }

    @Ignore
    public Ronda(RondaDTO rondaDTO) {
        super(rondaDTO);
        this.setDescription(rondaDTO.getDescription());
        this.setStartDate(rondaDTO.getStartDate());
        this.setEndDate(rondaDTO.getEndDate());
        if (rondaDTO.getRondaType() != null) this.setRondaType(new RondaType(rondaDTO.getRondaType()));
        if (rondaDTO.getMentorType() != null) this.setMentorType(rondaDTO.getMentorType());
        if (rondaDTO.getHealthFacility() != null) {
            this.setHealthFacility(new HealthFacility(rondaDTO.getHealthFacility()));
        }
        if (Utilities.listHasElements(rondaDTO.getRondaMentors())) {
            List<RondaMentor> rondaMentors = rondaDTO.getRondaMentors().stream()
                    .map(RondaMentor::new)
                    .collect(Collectors.toList());
            this.setRondaMentors(rondaMentors);
        }

        if (Utilities.listHasElements(rondaDTO.getRondaMentees())) {
            List<RondaMentee> rondaMentees = rondaDTO.getRondaMentees().stream()
                    .map(RondaMentee::new)
                    .collect(Collectors.toList());
            this.setRondaMentees(rondaMentees);
        }
    }

    // Getters and Setters

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
        this.setHealthFacilityId(healthFacility.getId());
    }

    public RondaType getRondaType() {
        return rondaType;
    }

    public void setRondaType(RondaType rondaType) {
        this.rondaType = rondaType;
        this.setRondaTypeId(rondaType.getId());
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

    @Override
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getCode() {
        return null;
    }

    public String getRondaPeriod() {
        return this.getEndDate() == null ? DateUtilities.parseDateToDDMMYYYYString(this.getStartDate())
                : DateUtilities.parseDateToDDMMYYYYString(this.getStartDate()).concat(" - ").concat(DateUtilities.parseDateToDDMMYYYYString(this.getEndDate()));
    }

    public String getRondaExecutionStatus() {
        return !this.isRondaCompleted() ? RondaStatus.ON_GOING.toString() : RondaStatus.FINISHED.toString();
    }

    @JsonIgnore
    public boolean isClosed() {
        return this.getEndDate() != null;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    @JsonIgnore
    public boolean isRondaZero() {
        return this.rondaType.getCode().equals("SESSAO_ZERO");
    }

    public void addSession(Session session) {
        if (this.sessions == null) this.sessions = new ArrayList<>();
        if (!Utilities.listHasElements(this.sessions)) {
            this.sessions.add(session);
        } else {
            for (Session s : this.sessions) {
                if (s.getUuid().equals(session.getUuid())) {
                    this.sessions.remove(s);
                    this.sessions.add(session);
                    return;
                }
            }
        }
    }

    public void addSession(List<Session> sessions) {
        for (Session session : sessions) {
            this.addSession(session);
        }
    }

    public void removeSession(Session session) {
        if (this.sessions == null) return;
        this.sessions.remove(session);
    }

    public void tryToCloseRonda() {
        boolean allSessionsClosed = true;

        for (RondaMentee rondaMentee : rondaMentees) {
            if (!allMenteeSessionsClosed(rondaMentee.getTutored())) {
                allSessionsClosed = false;
                break;
            }
        }

        if (allSessionsClosed) {
            this.setEndDate(DateUtilities.getCurrentDate());
            this.setSyncStatus(SyncSatus.PENDING);
        }
    }

    private boolean allMenteeSessionsClosed(Tutored tutored) {
        if (!isRondaZero() && !menteeHasFourSessions(tutored)) return false;

        for (Session session : sessions) {
            if (session.getTutored().equals(tutored) && !session.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    private boolean menteeHasFourSessions(Tutored tutored) {
        return this.sessions.stream().filter(session -> session.getTutored().equals(tutored)).count() == 4;
    }

    public boolean isRondaCompleted() {
        return this.getEndDate() != null;
    }

    private boolean hasSessionClosed(Tutored tutored) {
        for (Session session : sessions) {
            if (session.getTutored().equals(tutored) && session.isCompleted()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Ronda ronda = (Ronda) o;
        return Objects.equals(startDate, ronda.startDate) &&
                Objects.equals(healthFacility, ronda.healthFacility) &&
                Objects.equals(rondaType, ronda.rondaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startDate, healthFacility, rondaType);
    }

    public Tutor getActiveMentor() {
        if (this.getRondaMentors() == null) return null;
        for (RondaMentor rondaMentor : rondaMentors) {
            if (rondaMentor.isActive()) {
                return rondaMentor.getTutor();
            }
        }
        return null;
    }

    public String getMentorType() {
        return mentorType;
    }

    public void setMentorType(String mentorType) {
        this.mentorType = mentorType;
    }

    public Integer getHealthFacilityId() {
        return healthFacilityId;
    }

    public void setHealthFacilityId(Integer healthFacilityId) {
        this.healthFacilityId = healthFacilityId;
    }

    public Integer getRondaTypeId() {
        return rondaTypeId;
    }

    public void setRondaTypeId(Integer rondaTypeId) {
        this.rondaTypeId = rondaTypeId;
    }
}
