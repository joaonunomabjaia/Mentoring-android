package mz.org.csaude.mentoring.model.mentorship;

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
import mz.org.csaude.mentoring.dto.answer.AnswerDTO;
import mz.org.csaude.mentoring.dto.mentorship.MentorshipDTO;
import mz.org.csaude.mentoring.model.answer.Answer;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.location.Cabinet;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutored.Tutored;

@Entity(tableName = Mentorship.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Tutor.class,
                        parentColumns = "id",
                        childColumns = Mentorship.COLUMN_TUTOR,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Tutored.class,
                        parentColumns = "id",
                        childColumns = Mentorship.COLUMN_TUTORED,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Form.class,
                        parentColumns = "id",
                        childColumns = Mentorship.COLUMN_FORM,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Session.class,
                        parentColumns = "id",
                        childColumns = Mentorship.COLUMN_SESSION,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Cabinet.class,
                        parentColumns = "id",
                        childColumns = Mentorship.COLUMN_CABINET,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = EvaluationType.class,
                        parentColumns = "id",
                        childColumns = Mentorship.COLUMN_ITERATION_TYPE,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Door.class,
                        parentColumns = "id",
                        childColumns = Mentorship.COLUMN_DOOR,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {Mentorship.COLUMN_TUTOR}),
                @Index(value = {Mentorship.COLUMN_TUTORED}),
                @Index(value = {Mentorship.COLUMN_FORM}),
                @Index(value = {Mentorship.COLUMN_SESSION}),
                @Index(value = {Mentorship.COLUMN_CABINET}),
                @Index(value = {Mentorship.COLUMN_ITERATION_TYPE}),
                @Index(value = {Mentorship.COLUMN_DOOR})
        })
public class Mentorship extends BaseModel {

    public static final String TABLE_NAME = "mentorship";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_PERFORMED_DATE = "performed_date";
    public static final String COLUMN_TUTOR = "tutor_id";
    public static final String COLUMN_DEMOSTRATION_DETAILS = "demonstration_details";
    public static final String COLUMN_TUTORED = "tutored_id";
    public static final String COLUMN_FORM = "form_id";
    public static final String COLUMN_SESSION = "session_id";
    public static final String COLUMN_DEMOSTRATION = "demonstration";
    public static final String COLUMN_CABINET = "cabinet_id";
    public static final String COLUMN_ITERATION_TYPE = "iteration_type_id";
    public static final String COLUMN_ITERATION_NUMBER = "iteration_number";
    public static final String COLUMN_DOOR = "door_id";

    @ColumnInfo(name = COLUMN_START_DATE)
    private Date startDate;

    @ColumnInfo(name = COLUMN_END_DATE)
    private Date endDate;

    @ColumnInfo(name = COLUMN_PERFORMED_DATE)
    private Date performedDate;

    @ColumnInfo(name = COLUMN_TUTOR)
    private int tutorId;

    @Ignore
    @Relation(parentColumn = COLUMN_TUTOR, entityColumn = "id")
    private Tutor tutor;

    @ColumnInfo(name = COLUMN_TUTORED)
    private int tutoredId;

    @Ignore
    @Relation(parentColumn = COLUMN_TUTORED, entityColumn = "id")
    private Tutored tutored;

    @ColumnInfo(name = COLUMN_FORM)
    private int formId;

    @Ignore
    @Relation(parentColumn = COLUMN_FORM, entityColumn = "id")
    private Form form;

    @ColumnInfo(name = COLUMN_SESSION)
    private int sessionId;

    @Ignore
    @Relation(parentColumn = COLUMN_SESSION, entityColumn = "id")
    private Session session;

    @ColumnInfo(name = COLUMN_CABINET)
    private int cabinetId;

    @Ignore
    @Relation(parentColumn = COLUMN_CABINET, entityColumn = "id")
    private Cabinet cabinet;

    @ColumnInfo(name = COLUMN_ITERATION_TYPE)
    private int evaluationTypeId;

    @Ignore
    @Relation(parentColumn = COLUMN_ITERATION_TYPE, entityColumn = "id")
    private EvaluationType evaluationType;

    @ColumnInfo(name = COLUMN_ITERATION_NUMBER)
    private Integer iterationNumber;

    @ColumnInfo(name = COLUMN_DOOR)
    private int doorId;

    @Ignore
    @Relation(parentColumn = COLUMN_DOOR, entityColumn = "id")
    private Door door;

    @ColumnInfo(name = COLUMN_DEMOSTRATION)
    private boolean demonstration;

    @ColumnInfo(name = COLUMN_DEMOSTRATION_DETAILS)
    private String demonstrationDetails;

    @Ignore
    private List<Answer> answers;

    public Mentorship() {
    }

    public Mentorship(MentorshipDTO mentorshipDTO) {
        super(mentorshipDTO);
        this.setStartDate(mentorshipDTO.getStartDate());
        this.setEndDate(mentorshipDTO.getEndDate());
        this.setIterationNumber(mentorshipDTO.getIterationNumber());
        this.setDemonstration(mentorshipDTO.isDemonstration());
        this.setDemonstrationDetails(mentorshipDTO.getDemonstrationDetails());
        this.setPerformedDate(mentorshipDTO.getPerformedDate());

        if (mentorshipDTO.getMentor() != null) {
            this.setTutor(new Tutor(mentorshipDTO.getMentor()));
        }
        if (mentorshipDTO.getMentee() != null) {
            this.setTutored(new Tutored(mentorshipDTO.getMentee()));
        }
        if (mentorshipDTO.getSession() != null) {
            this.setSession(new Session(mentorshipDTO.getSession()));
        }
        if (mentorshipDTO.getForm() != null) {
            this.setForm(new Form(mentorshipDTO.getForm()));
        }
        if (mentorshipDTO.getCabinet() != null) {
            this.setCabinet(new Cabinet(mentorshipDTO.getCabinet()));
        }
        if (mentorshipDTO.getDoor() != null) {
            this.setDoor(new Door(mentorshipDTO.getDoor()));
        }
        if (mentorshipDTO.getEvaluationType() != null) {
            this.setEvaluationType(new EvaluationType(mentorshipDTO.getEvaluationType()));
        }
        if (mentorshipDTO.getAnswers() != null) {
            List<Answer> answerList = new ArrayList<>();
            for (AnswerDTO answerDTO : mentorshipDTO.getAnswers()) {
                answerList.add(new Answer(answerDTO));
            }
            this.setAnswers(answerList);
        }
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

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
        this.tutorId = tutor.getId();
    }

    public Tutored getTutored() {
        return tutored;
    }

    public void setTutored(Tutored tutored) {
        this.tutored = tutored;
        this.tutoredId = tutored.getId();
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
        this.formId = form.getId();
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
        this.sessionId = session.getId();
    }

    public Cabinet getCabinet() {
        return cabinet;
    }

    public void setCabinet(Cabinet cabinet) {
        this.cabinet = cabinet;
        this.cabinetId = cabinet.getId();
    }

    public EvaluationType getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(EvaluationType evaluationType) {
        this.evaluationType = evaluationType;
        this.evaluationTypeId = evaluationType.getId();
    }

    public Integer getIterationNumber() {
        return iterationNumber;
    }

    public void setIterationNumber(Integer iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    public Door getDoor() {
        return door;
    }

    public void setDoor(Door door) {
        this.door = door;
        this.doorId = door.getId();
    }

    public String getEvaluationTypeDescription() {
        return "Avaliação de " + evaluationType.getDescription();
    }

    @Override
    public String toString() {
        return "Mentorship{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", performedDate=" + performedDate +
                ", tutor=" + tutor +
                ", tutored=" + tutored +
                ", form=" + form +
                ", cabinet=" + cabinet +
                ", evaluationType=" + evaluationType +
                ", iterationNumber=" + iterationNumber +
                ", door=" + door +
                ", demonstration=" + demonstration +
                ", demonstrationDetails='" + demonstrationDetails +
                '}';
    }

    @JsonIgnore
    public boolean isCompleted() {
        return this.endDate != null;
    }

    @JsonIgnore
    public boolean isPatientEvaluation() {
        return this.evaluationType.isPatientEvaluation();
    }

    @JsonIgnore
    public boolean isFileEvaluation() {
        return this.evaluationType.isFichaEvaluation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mentorship)) return false;
        if (!super.equals(o)) return false;
        Mentorship that = (Mentorship) o;
        return Objects.equals(tutor, that.tutor) &&
                Objects.equals(tutored, that.tutored) &&
                Objects.equals(form, that.form) &&
                Objects.equals(session, that.session) &&
                Objects.equals(evaluationType, that.evaluationType) &&
                Objects.equals(iterationNumber, that.iterationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tutor, tutored, form, session, evaluationType, iterationNumber);
    }

    public boolean isDemonstration() {
        return demonstration;
    }

    public void setDemonstration(boolean demonstration) {
        this.demonstration = demonstration;
    }

    public String getDemonstrationDetails() {
        return demonstrationDetails;
    }

    public void setDemonstrationDetails(String demonstrationDetails) {
        this.demonstrationDetails = demonstrationDetails;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void addAnswer(Answer answer) {
        if (answers == null) answers = new ArrayList<>();
        answers.add(answer);
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public int getTutorId() {
        return tutorId;
    }

    public void setTutorId(int tutorId) {
        this.tutorId = tutorId;
    }

    public int getTutoredId() {
        return tutoredId;
    }

    public void setTutoredId(int tutoredId) {
        this.tutoredId = tutoredId;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(int cabinetId) {
        this.cabinetId = cabinetId;
    }

    public int getEvaluationTypeId() {
        return evaluationTypeId;
    }

    public void setEvaluationTypeId(int evaluationTypeId) {
        this.evaluationTypeId = evaluationTypeId;
    }

    public int getDoorId() {
        return doorId;
    }

    public void setDoorId(int doorId) {
        this.doorId = doorId;
    }
}
