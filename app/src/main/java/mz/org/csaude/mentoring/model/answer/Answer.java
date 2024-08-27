package mz.org.csaude.mentoring.model.answer;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.answer.AnswerDTO;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.question.Question;

@Entity(tableName = Answer.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Form.class,
                        parentColumns = "id",
                        childColumns = "form_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Mentorship.class,
                        parentColumns = "id",
                        childColumns = "mentorship_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Question.class,
                        parentColumns = "id",
                        childColumns = "question_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {"form_id"}),
                @Index(value = {"mentorship_id"}),
                @Index(value = {"question_id"})
        }
)
public class Answer extends BaseModel {

    public static final String TABLE_NAME = "answer";

    public static final String COLUMN_FORM = "form_id";
    public static final String COLUMN_MENTORSHIP = "mentorship_id";
    public static final String COLUMN_QUESTION = "question_id";
    public static final String COLUMN_VALUE = "value";

    @ColumnInfo(name = COLUMN_FORM)
    private int formId;

    @ColumnInfo(name = COLUMN_MENTORSHIP)
    private int mentorshipId;

    @ColumnInfo(name = COLUMN_QUESTION)
    private int questionId;

    @ColumnInfo(name = COLUMN_VALUE)
    private String value;

    @Ignore
    @Relation(parentColumn = "form_id", entityColumn = "id")
    private Form form;

    @Ignore
    @Relation(parentColumn = "mentorship_id", entityColumn = "id")
    private Mentorship mentorship;

    @Ignore
    @Relation(parentColumn = "question_id", entityColumn = "id")
    private Question question;

    public Answer() {
    }

    public Answer(AnswerDTO answerDTO) {
        super(answerDTO);
        this.setValue(answerDTO.getValue());
        if (answerDTO.getForm() != null) {
            this.setForm(answerDTO.getForm().getForm());
        }
        if (answerDTO.getQuestion() != null) {
            this.setQuestion(answerDTO.getQuestion().getQuestionObj());
        }
        if (answerDTO.getMentorship() != null) {
            this.setMentorship(new Mentorship(answerDTO.getMentorship()));
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
        this.formId = form.getId();
    }

    public Mentorship getMentorship() {
        return mentorship;
    }

    public void setMentorship(Mentorship mentorship) {
        this.mentorship = mentorship;
        this.mentorshipId = mentorship.getId();
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
        this.questionId = question.getId();
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getMentorshipId() {
        return mentorshipId;
    }

    public void setMentorshipId(int mentorshipId) {
        this.mentorshipId = mentorshipId;
    }
}
