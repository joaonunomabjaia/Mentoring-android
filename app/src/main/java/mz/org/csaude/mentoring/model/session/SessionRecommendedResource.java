package mz.org.csaude.mentoring.model.session;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.Date;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.session.SessionRecommendedResourceDTO;
import mz.org.csaude.mentoring.model.resourceea.Node;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;

@Entity(
        tableName = SessionRecommendedResource.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Session.class, parentColumns = "id", childColumns = "session_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Tutored.class, parentColumns = "id", childColumns = "tutored_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Tutor.class, parentColumns = "id", childColumns = "tutor_id", onDelete = ForeignKey.CASCADE)
        }
)
public class SessionRecommendedResource extends BaseModel {

    public static final String TABLE_NAME = "session_recommended_resource";
    public static final String COLUMN_SESSION_ID = "session_id";
    public static final String COLUMN_TUTORED_ID = "tutored_id";
    public static final String COLUMN_TUTOR_ID = "tutor_id";
    public static final String COLUMN_RESOURCE_LINK = "resource_link";
    public static final String COLUMN_RESOURCE_NAME = "resource_name";
    public static final String COLUMN_DATE_RECOMMENDED = "date_recommended";

    @ColumnInfo(name = COLUMN_SESSION_ID)
    private Integer sessionId;

    @Ignore
    @Relation(parentColumn = COLUMN_SESSION_ID, entityColumn = "id")
    private Session session;

    @ColumnInfo(name = COLUMN_TUTORED_ID)
    private Integer tutoredId;

    @Ignore
    @Relation(parentColumn = COLUMN_TUTORED_ID, entityColumn = "id")
    private Tutored tutored;

    @ColumnInfo(name = COLUMN_TUTOR_ID)
    private Integer tutorId;

    @Ignore
    @Relation(parentColumn = COLUMN_TUTOR_ID, entityColumn = "id")
    private Tutor tutor;

    @ColumnInfo(name = COLUMN_RESOURCE_LINK)
    private String resourceLink;

    @ColumnInfo(name = COLUMN_RESOURCE_NAME)
    private String resourceName;

    @ColumnInfo(name = COLUMN_DATE_RECOMMENDED)
    private Date dateRecommended;

    // Default constructor is needed by Room
    public SessionRecommendedResource() {
    }

    @Ignore
    public SessionRecommendedResource(Session session, Node node) {
        this.session = session;
        this.sessionId = session.getId();

        this.tutor = session.getRonda().getActiveMentor();
        this.tutorId = this.tutor.getId();

        this.tutored = session.getTutored();
        this.tutoredId = this.tutored.getId();

        this.resourceLink = node.getName().replace(" ", "_");
        this.resourceName = node.getName().replace(" ", "_");
        this.dateRecommended = DateUtilities.getCurrentDate();

        this.setSyncStatus(SyncSatus.PENDING);
        this.setCreatedAt(DateUtilities.getCurrentDate());
        this.setUuid(Utilities.getNewUUID().toString());
    }

    @Ignore
    public SessionRecommendedResource(SessionRecommendedResourceDTO dto, Session session, Tutored tutored, Tutor tutor) {
        super(dto);
        this.session = session;
        this.sessionId = session.getId();

        this.tutored = tutored;
        this.tutoredId = tutored.getId();

        this.tutor = tutor;
        this.tutorId = tutor.getId();

        this.resourceLink = dto.getResourceLink();
        this.resourceName = dto.getResourceName();
        this.dateRecommended = dto.getDateRecommended();
    }

    // Getters and Setters
    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
        this.sessionId = session.getId();
    }

    public Integer getTutoredId() {
        return tutoredId;
    }

    public void setTutoredId(Integer tutoredId) {
        this.tutoredId = tutoredId;
    }

    public Tutored getTutored() {
        return tutored;
    }

    public void setTutored(Tutored tutored) {
        this.tutored = tutored;
        this.tutoredId = tutored.getId();
    }

    public Integer getTutorId() {
        return tutorId;
    }

    public void setTutorId(Integer tutorId) {
        this.tutorId = tutorId;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
        this.tutorId = tutor.getId();
    }

    public String getResourceLink() {
        return resourceLink;
    }

    public void setResourceLink(String resourceLink) {
        this.resourceLink = resourceLink;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Date getDateRecommended() {
        return dateRecommended;
    }

    public void setDateRecommended(Date dateRecommended) {
        this.dateRecommended = dateRecommended;
    }
}
