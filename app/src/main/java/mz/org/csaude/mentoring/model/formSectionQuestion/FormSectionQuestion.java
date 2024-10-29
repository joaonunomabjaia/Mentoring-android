package mz.org.csaude.mentoring.model.formSectionQuestion;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.form.FormSectionQuestionDTO;
import mz.org.csaude.mentoring.model.answer.Answer;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.form.FormSection;
import mz.org.csaude.mentoring.model.form.Section;
import mz.org.csaude.mentoring.model.question.Question;
import mz.org.csaude.mentoring.model.responseType.ResponseType;

@Entity(tableName = FormSectionQuestion.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = FormSection.class,
                        parentColumns = "id",
                        childColumns = FormSectionQuestion.COLUMN_FORM_SECTION,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Question.class,
                        parentColumns = "id",
                        childColumns = FormSectionQuestion.COLUMN_QUESTION,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = EvaluationType.class,
                        parentColumns = "id",
                        childColumns = FormSectionQuestion.COLUMN_EVALUATION_TYPE,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = ResponseType.class,
                        parentColumns = "id",
                        childColumns = FormSectionQuestion.COLUMN_RESPONSE_TYPE,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {FormSectionQuestion.COLUMN_FORM_SECTION}),
                @Index(value = {FormSectionQuestion.COLUMN_QUESTION}),
                @Index(value = {FormSectionQuestion.COLUMN_EVALUATION_TYPE}),
                @Index(value = {FormSectionQuestion.COLUMN_RESPONSE_TYPE})
        })
public class FormSectionQuestion extends BaseModel {

    public static final String TABLE_NAME = "form_section_question";
    public static final String COLUMN_FORM_SECTION = "form_section_id";
    public static final String COLUMN_QUESTION = "question_id";
    public static final String COLUMN_EVALUATION_TYPE = "evaluation_type_id";
    public static final String COLUMN_RESPONSE_TYPE = "response_type_id";
    public static final String COLUMN_MANDATORY = "mandatory";
    public static final String COLUMN_SEQUENCE = "sequence";
    public static final String COLUMN_APPLICABLE = "applicable";

    @NonNull
    @ColumnInfo(name = COLUMN_FORM_SECTION)
    private Integer formSectionId;

    @Ignore
    private FormSection formSection;

    @NonNull
    @ColumnInfo(name = COLUMN_QUESTION)
    private Integer questionId;

    @Ignore
    @Relation(parentColumn = COLUMN_QUESTION, entityColumn = "id")
    private Question question;

    @NonNull
    @ColumnInfo(name = COLUMN_EVALUATION_TYPE)
    private Integer evaluationTypeId;

    @Ignore
    @Relation(parentColumn = COLUMN_EVALUATION_TYPE, entityColumn = "id")
    private EvaluationType evaluationType;

    @NonNull
    @ColumnInfo(name = COLUMN_RESPONSE_TYPE)
    private Integer responseTypeId;

    @Ignore
    @Relation(parentColumn = COLUMN_RESPONSE_TYPE, entityColumn = "id")
    private ResponseType responseType;

    @NonNull
    @ColumnInfo(name = COLUMN_MANDATORY)
    private boolean mandatory;


    @ColumnInfo(name = COLUMN_SEQUENCE)
    private Integer sequence;


    @ColumnInfo(name = COLUMN_APPLICABLE)
    private Boolean applicable;

    @Ignore
    private Answer answer;

    public FormSectionQuestion() {
    }

    @Ignore
    public FormSectionQuestion(FormSectionQuestionDTO formSectionQuestionDTO) {
        super(formSectionQuestionDTO);
        this.setSequence(formSectionQuestionDTO.getSequence());
        this.setFormSection(new FormSection(formSectionQuestionDTO.getFormSectionUuid()));
        this.setQuestion(new Question(formSectionQuestionDTO.getQuestionDTO()));
        this.setEvaluationType(new EvaluationType(formSectionQuestionDTO.getEvaluationTypeUuid()));
        this.setResponseType(new ResponseType(formSectionQuestionDTO.getResponseTypeUuid()));
    }

    public FormSectionQuestion(String formSectionQuestionUuid) {
        super(formSectionQuestionUuid);
    }

    @NonNull
    public Integer getFormSectionId() {
        return formSectionId;
    }

    public void setFormSectionId(@NonNull Integer formSectionId) {
        this.formSectionId = formSectionId;
    }

    public FormSection getFormSection() {
        return formSection;
    }

    public void setFormSection(FormSection formSection) {
        this.formSection = formSection;
        this.formSectionId = formSection.getId();
    }

    public Question getQuestion() {
        return question;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public Integer getSequence() {
        return sequence;
    }

    public Boolean getApplicable() {
        return applicable;
    }

    public void setQuestion(Question question) {
        this.question = question;
        this.questionId = question.getId();
    }

    public EvaluationType getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(EvaluationType evaluationType) {
        this.evaluationType = evaluationType;
        this.evaluationTypeId = evaluationType.getId();
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
        this.responseTypeId = responseType.getId();
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public void setApplicable(Boolean applicable) {
        this.applicable = applicable;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getEvaluationTypeId() {
        return evaluationTypeId;
    }

    public void setEvaluationTypeId(Integer evaluationTypeId) {
        this.evaluationTypeId = evaluationTypeId;
    }


    public void setResponseTypeId(@NonNull Integer responseTypeId) {
        this.responseTypeId = responseTypeId;
    }

    public Integer getResponseTypeId() {
        return responseTypeId;
    }

}
