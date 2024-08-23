package mz.org.csaude.mentoring.model.session;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.tutored.Tutored;

@Entity(tableName = "session_report",
        foreignKeys = {
                @ForeignKey(entity = Session.class, parentColumns = "id", childColumns = "session_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Tutored.class, parentColumns = "id", childColumns = "tutored_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Form.class, parentColumns = "id", childColumns = "form_id", onDelete = ForeignKey.CASCADE)
        })
public class SessionReport extends BaseModel {

    @ColumnInfo(name = "session_id", index = true)
    private Integer sessionId;

    @ColumnInfo(name = "tutored_id", index = true)
    private Integer tutoredId;

    @ColumnInfo(name = "form_id", index = true)
    private Integer formId;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "yes_points")
    private int yesPoints;

    @ColumnInfo(name = "no_points")
    private int noPoints;

    @ColumnInfo(name = "score")
    private double score;

    // Default constructor is needed by Room
    public SessionReport() {
    }

    // Getters and Setters
    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getTutoredId() {
        return tutoredId;
    }

    public void setTutoredId(Integer tutoredId) {
        this.tutoredId = tutoredId;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getYesPoints() {
        return yesPoints;
    }

    public void setYesPoints(int yesPoints) {
        this.yesPoints = yesPoints;
    }

    public int getNoPoints() {
        return noPoints;
    }

    public void setNoPoints(int noPoints) {
        this.noPoints = noPoints;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
