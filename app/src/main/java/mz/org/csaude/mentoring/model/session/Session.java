package mz.org.csaude.mentoring.model.session;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.session.SessionDTO;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.Utilities;

@Entity(tableName = Session.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = SessionStatus.class, parentColumns = "id", childColumns = Session.COLUMN_STATUS, onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Ronda.class, parentColumns = "id", childColumns = Session.COLUMN_RONDA, onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Tutored.class, parentColumns = "id", childColumns = Session.COLUMN_MENTEE, onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Form.class, parentColumns = "id", childColumns = Session.COLUMN_FORM, onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {Session.COLUMN_RONDA}),
                @Index(value = {Session.COLUMN_MENTEE}),
                @Index(value = {Session.COLUMN_FORM})
        })
public class Session extends BaseModel {

    public static final String TABLE_NAME = "session";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_PERFORMED_DATE = "performed_date";
    public static final String COLUMN_STATUS = "session_status_id";
    public static final String COLUMN_RONDA = "ronda_id";
    public static final String COLUMN_MENTEE = "mentee_id";
    public static final String COLUMN_FORM = "form_id";
    public static final String COLUMN_STRONG_POINTS = "strong_points";
    public static final String COLUMN_WEAK_POINTS = "points_to_improve";
    public static final String COLUMN_WORK_PLAN = "work_plan";
    public static final String COLUMN_OBSERVATIONS = "observations";

    @NonNull
    @ColumnInfo(name = COLUMN_START_DATE)
    private Date startDate;

    @ColumnInfo(name = COLUMN_END_DATE)
    private Date endDate;

    @ColumnInfo(name = COLUMN_PERFORMED_DATE)
    private Date performedDate;

    @NonNull
    @ColumnInfo(name = COLUMN_STATUS)
    private Integer statusId;

    @Relation(parentColumn = COLUMN_STATUS, entityColumn = "id")
    @Ignore
    private SessionStatus status;

    @NonNull
    @ColumnInfo(name = COLUMN_RONDA)
    private Integer rondaId;

    @Relation(parentColumn = COLUMN_RONDA, entityColumn = "id")
    @Ignore
    private Ronda ronda;

    @NonNull
    @ColumnInfo(name = COLUMN_MENTEE)
    private Integer menteeId;

    @Relation(parentColumn = COLUMN_MENTEE, entityColumn = "id")
    @Ignore
    private Tutored tutored;

    @NonNull
    @ColumnInfo(name = COLUMN_FORM)
    private Integer formId;

    @Relation(parentColumn = COLUMN_FORM, entityColumn = "id")
    @Ignore
    private Form form;

    @Ignore
    private List<Mentorship> mentorships;

    @ColumnInfo(name = COLUMN_STRONG_POINTS)
    private String strongPoints;

    @ColumnInfo(name = COLUMN_WEAK_POINTS)
    private String pointsToImprove;

    @ColumnInfo(name = COLUMN_WORK_PLAN)
    private String workPlan;

    @ColumnInfo(name = COLUMN_OBSERVATIONS)
    private String observations;

    public Session() {
    }

    @Ignore
    public Session(SessionDTO sessionDTO) {
        super(sessionDTO);
        this.setStartDate(sessionDTO.getStartDate());
        this.setEndDate(sessionDTO.getEndDate());
        this.setPerformedDate(sessionDTO.getPerformedDate());
        this.setPointsToImprove(sessionDTO.getPointsToImprove());
        this.setStrongPoints(sessionDTO.getStrongPoints());
        this.setObservations(sessionDTO.getObservations());
        if (sessionDTO.getSessionStatus() != null) {
            this.setStatus(new SessionStatus(sessionDTO.getSessionStatus()));
            this.statusId = this.status.getId();
        }
        if (sessionDTO.getMentee() != null) {
            this.setTutored(new Tutored(sessionDTO.getMentee()));
            this.menteeId = this.tutored.getId();
        }
        if (sessionDTO.getRonda() != null) {
            this.setRonda(new Ronda(sessionDTO.getRonda()));
            this.rondaId = this.ronda.getId();
        }
        if (sessionDTO.getForm() != null) {
            this.setForm(new Form(sessionDTO.getForm()));
            this.formId = this.form.getId();
        }
        if (Utilities.listHasElements(sessionDTO.getMentorships())) {
            setMentorships(Utilities.parse(sessionDTO.getMentorships(), Mentorship.class));
        }
    }

    // Getters and Setters

    public Ronda getRonda() {
        return ronda;
    }

    public void setRonda(Ronda ronda) {
        this.ronda = ronda;
        this.rondaId = ronda.getId();
    }

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

    public Date getPerformedDate() {
        return performedDate;
    }

    public void setPerformedDate(Date performedDate) {
        this.performedDate = performedDate;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
        this.statusId = status.getId();
    }

    public List<Mentorship> getMentorships() {
        return mentorships;
    }

    public void addMentorship(Mentorship mentorship) {
        if (mentorships == null) {
            mentorships = new ArrayList<>();
        }
        if (!Utilities.listHasElements(mentorships)) {
            mentorships.add(mentorship);
        } else {
            for (Mentorship m : mentorships) {
                if (m.getUuid().equals(mentorship.getUuid())) {
                    mentorships.remove(m);
                    mentorships.add(mentorship);
                    return;
                }
            }
        }
    }

    public void setMentorships(List<Mentorship> mentorships) {
        this.mentorships = mentorships;
    }

    public Tutored getTutored() {
        return tutored;
    }

    public void setTutored(Tutored tutored) {
        this.tutored = tutored;
        this.menteeId = tutored.getId();
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
        this.formId = form.getId();
    }

    @JsonIgnore
    public boolean isCompleted() {
        return this.status != null && this.status.isCompleted();
    }

    public boolean canBeClosed() {
        if (this.isCompleted()) return true;

        if (this.getRonda().isRondaZero()) {
            return Utilities.listHasElements(mentorships);
        }

        if (!Utilities.listHasElements(mentorships)) {
            return false;
        }

        int completedPatient = 0;
        int completedFile = 0;

        for (Mentorship mentorship : mentorships) {
            if (mentorship.isCompleted()) {
                if (mentorship.isPatientEvaluation()) {
                    completedPatient++;
                } else if (mentorship.isFileEvaluation()) {
                    completedFile++;
                }
            }
        }

        return completedPatient == form.getTargetPatient() && completedFile == form.getTargetFile();
    }

    public void addMentorships(List<Mentorship> completedMentorships) {
        for (Mentorship mentorship : completedMentorships) {
            if (!this.mentorships.contains(mentorship)) {
                this.mentorships.add(mentorship);
            }
        }
    }

    public String getStrongPoints() {
        return strongPoints;
    }

    public void setStrongPoints(String strongPoints) {
        this.strongPoints = strongPoints;
    }

    public String getPointsToImprove() {
        return pointsToImprove;
    }

    public void setPointsToImprove(String pointsToImprove) {
        this.pointsToImprove = pointsToImprove;
    }

    public String getWorkPlan() {
        return workPlan;
    }

    public void setWorkPlan(String workPlan) {
        this.workPlan = workPlan;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Session session = (Session) o;
        return Objects.equals(ronda, session.ronda) &&
                Objects.equals(tutored, session.tutored) &&
                Objects.equals(form, session.form);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ronda, tutored, form);
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getRondaId() {
        return rondaId;
    }

    public void setRondaId(int rondaId) {
        this.rondaId = rondaId;
    }

    public int getMenteeId() {
        return menteeId;
    }

    public void setMenteeId(int menteeId) {
        this.menteeId = menteeId;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }
}
