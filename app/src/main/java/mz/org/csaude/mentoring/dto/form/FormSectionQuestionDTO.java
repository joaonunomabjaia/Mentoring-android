package mz.org.csaude.mentoring.dto.form;



import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.evaluationType.EvaluationTypeDTO;
import mz.org.csaude.mentoring.dto.program.ProgramDTO;
import mz.org.csaude.mentoring.dto.question.QuestionDTO;
import mz.org.csaude.mentoring.dto.responseType.ResponseTypeDTO;
import mz.org.csaude.mentoring.dto.section.SectionDTO;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.form.Section;
import mz.org.csaude.mentoring.model.formSectionQuestion.FormSectionQuestion;
import mz.org.csaude.mentoring.model.question.Question;
import mz.org.csaude.mentoring.model.responseType.ResponseType;



public class FormSectionQuestionDTO extends BaseEntityDTO {
    private Integer sequence;
    private String questionUuid;
    private String evaluationTypeUuid;
    private String responseTypeUuid;
    private String formSectionUuid;
    private QuestionDTO questionDTO;

    public FormSectionQuestionDTO() {

    }
    public FormSectionQuestionDTO(FormSectionQuestion formSectionQuestion) {
        super(formSectionQuestion);
        this.setSequence(formSectionQuestion.getSequence());
        this.setQuestionUuid(formSectionQuestion.getQuestion().getUuid());
        this.setEvaluationTypeUuid(formSectionQuestion.getEvaluationType().getUuid());
        this.setResponseTypeUuid(formSectionQuestion.getResponseType().getUuid());
        this.setFormSectionUuid(formSectionQuestion.getFormSection().getUuid());
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getQuestionUuid() {
        return questionUuid;
    }

    public void setQuestionUuid(String questionUuid) {
        this.questionUuid = questionUuid;
    }

    public String getEvaluationTypeUuid() {
        return evaluationTypeUuid;
    }

    public void setEvaluationTypeUuid(String evaluationTypeUuid) {
        this.evaluationTypeUuid = evaluationTypeUuid;
    }

    public String getResponseTypeUuid() {
        return responseTypeUuid;
    }

    public void setResponseTypeUuid(String responseTypeUuid) {
        this.responseTypeUuid = responseTypeUuid;
    }

    public String getFormSectionUuid() {
        return formSectionUuid;
    }

    public void setFormSectionUuid(String formSectionUuid) {
        this.formSectionUuid = formSectionUuid;
    }

    public QuestionDTO getQuestionDTO() {
        return questionDTO;
    }

    public void setQuestionDTO(QuestionDTO questionDTO) {
        this.questionDTO = questionDTO;
    }
}
