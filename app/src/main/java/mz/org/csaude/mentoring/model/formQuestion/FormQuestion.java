package mz.org.csaude.mentoring.model.formQuestion;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.form.FormQuestionDTO;
import mz.org.csaude.mentoring.model.answer.Answer;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.question.Question;
import mz.org.csaude.mentoring.model.responseType.ResponseType;

@Entity(tableName = FormQuestion.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Form.class,
                        parentColumns = "id",
                        childColumns = FormQuestion.COLUMN_FORM,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Question.class,
                        parentColumns = "id",
                        childColumns = FormQuestion.COLUMN_QUESTION,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = EvaluationType.class,
                        parentColumns = "id",
                        childColumns = FormQuestion.COLUMN_EVALUATION_TYPE,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = ResponseType.class,
                        parentColumns = "id",
                        childColumns = FormQuestion.COLUMN_RESPONSE_TYPE,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {FormQuestion.COLUMN_FORM}),
                @Index(value = {FormQuestion.COLUMN_QUESTION}),
                @Index(value = {FormQuestion.COLUMN_EVALUATION_TYPE}),
                @Index(value = {FormQuestion.COLUMN_RESPONSE_TYPE})
        })
public class FormQuestion extends BaseModel {

    public static final String TABLE_NAME = "form_question";
    public static final String COLUMN_FORM = "form_id";
    public static final String COLUMN_QUESTION = "question_id";
    public static final String COLUMN_EVALUATION_TYPE = "evaluation_type_id";
    public static final String COLUMN_RESPONSE_TYPE = "response_type_id";
    public static final String COLUMN_MANDATORY = "mandatory";
    public static final String COLUMN_SEQUENCE = "sequence";
    public static final String COLUMN_APPLICABLE = "applicable";

    @ColumnInfo(name = COLUMN_FORM)
    private int formId;

    @Ignore
    @Relation(parentColumn = COLUMN_FORM, entityColumn = "id")
    private Form form;

    @ColumnInfo(name = COLUMN_QUESTION)
    private int questionId;

    @Ignore
    @Relation(parentColumn = COLUMN_QUESTION, entityColumn = "id")
    private Question question;

    @ColumnInfo(name = COLUMN_EVALUATION_TYPE)
    private int evaluationTypeId;

    @Ignore
    @Relation(parentColumn = COLUMN_EVALUATION_TYPE, entityColumn = "id")
    private EvaluationType evaluationType;

    @ColumnInfo(name = COLUMN_RESPONSE_TYPE)
    private int responseTypeId;

    @Ignore
    @Relation(parentColumn = COLUMN_RESPONSE_TYPE, entityColumn = "id")
    private ResponseType responseType;

    @ColumnInfo(name = COLUMN_MANDATORY)
    private boolean mandatory;

    @ColumnInfo(name = COLUMN_SEQUENCE)
    private Integer sequence;

    @ColumnInfo(name = COLUMN_APPLICABLE)
    private Boolean applicable;

    @Ignore
    private Answer answer;

    public FormQuestion() {
    }

    @Ignore
    public FormQuestion(FormQuestionDTO formQuestionDTO) {
        super(formQuestionDTO);
        this.setSequence(formQuestionDTO.getSequence());
        if (formQuestionDTO.getQuestion() != null) this.setQuestion(new Question(formQuestionDTO.getQuestion()));
        if (formQuestionDTO.getEvaluationType() != null) this.setEvaluationType(new EvaluationType(formQuestionDTO.getEvaluationType()));
        if (formQuestionDTO.getResponseType() != null) this.setResponseType(new ResponseType(formQuestionDTO.getResponseType()));
        if (formQuestionDTO.getForm() != null) this.setForm(new Form(formQuestionDTO.getForm()));
    }

    public Form getForm() {
        return form;
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

    public void setForm(Form form) {
        this.form = form;
        this.formId = form.getId();
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

    public int getEvaluationTypeId() {
        return evaluationTypeId;
    }

    public void setEvaluationTypeId(int evaluationTypeId) {
        this.evaluationTypeId = evaluationTypeId;
    }

    public int getResponseTypeId() {
        return responseTypeId;
    }

    public void setResponseTypeId(int responseTypeId) {
        this.responseTypeId = responseTypeId;
    }
}
