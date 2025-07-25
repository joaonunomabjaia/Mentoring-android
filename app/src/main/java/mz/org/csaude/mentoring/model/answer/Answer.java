package mz.org.csaude.mentoring.model.answer;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.answer.AnswerDTO;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.formSectionQuestion.FormSectionQuestion;
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
                @ForeignKey(entity = FormSectionQuestion.class,
                        parentColumns = "id",
                        childColumns = Answer.COLUMN_FORM_SECTION_QUESTION,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Question.class,
                        parentColumns = "id",
                        childColumns = "question_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {"form_id"}),
                @Index(value = {"mentorship_id"}),
                @Index(value = {Answer.COLUMN_FORM_SECTION_QUESTION}),
                @Index(value = {"question_id"})
        }
)
public class Answer extends BaseModel {

    public static final String TABLE_NAME = "answer";
    private final String YES_ANSWER = "SIM";
    private final String NO_ANSWER = "NAO";
    private final String NA_ANSWER = "NA";

    public static final String COLUMN_FORM = "form_id";
    public static final String COLUMN_MENTORSHIP = "mentorship_id";
    public static final String COLUMN_QUESTION = "question_id";
    public static final String COLUMN_FORM_SECTION_QUESTION = "form_section_question_id";
    public static final String COLUMN_VALUE = "value";

    @NonNull
    @ColumnInfo(name = COLUMN_FORM)
    private Integer formId;

    @NonNull
    @ColumnInfo(name = COLUMN_MENTORSHIP)
    private Integer mentorshipId;

    @NonNull
    @ColumnInfo(name = COLUMN_QUESTION)
    private Integer questionId;

    @NonNull
    @ColumnInfo(name = COLUMN_FORM_SECTION_QUESTION)
    private Integer formSectionQuestionId;

    @ColumnInfo(name = COLUMN_VALUE)
    private String value;

    @Ignore
    private FormSectionQuestion formSectionQuestion;

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
        this.setForm(new Form(answerDTO.getFormUuid()));
        this.setMentorship(new Mentorship(answerDTO.getMentorshipUuid()));
        this.setQuestion(new Question(answerDTO.getQuestionUUid()));
        this.setFormSectionQuestion(new FormSectionQuestion(answerDTO.getFormSectionQuestionUuid()));
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

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getMentorshipId() {
        return mentorshipId;
    }

    public void setMentorshipId(Integer mentorshipId) {
        this.mentorshipId = mentorshipId;
    }

    @NonNull
    public Integer getFormSectionQuestionId() {
        return formSectionQuestionId;
    }

    public void setFormSectionQuestionId(@NonNull Integer formSectionQuestionId) {
        this.formSectionQuestionId = formSectionQuestionId;
    }

    public FormSectionQuestion getFormSectionQuestion() {
        return formSectionQuestion;
    }

    public void setFormSectionQuestion(FormSectionQuestion formSectionQuestion) {
        this.formSectionQuestion = formSectionQuestion;
        this.formSectionQuestionId = formSectionQuestion.getId();
    }

    @JsonIgnore
    public boolean isYesAnswer() {
        return value.trim().toUpperCase().equals(YES_ANSWER);
    }

    @JsonIgnore
    public boolean isNoAnswer() {
        return value.trim().toUpperCase().equals(NO_ANSWER);
    }

    @JsonIgnore
    public boolean isNaAnswer() {
        return value.trim().toUpperCase().equals(NA_ANSWER);
    }

}
