package mz.org.csaude.mentoring.dto.answer;

import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.form.FormDTO;
import mz.org.csaude.mentoring.dto.mentorship.MentorshipDTO;
import mz.org.csaude.mentoring.dto.question.QuestionDTO;
import mz.org.csaude.mentoring.model.answer.Answer;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.question.Question;

public class AnswerDTO extends BaseEntityDTO {

    private String value;

    private String formUuid;

    private String mentorshipUuid;

    private String questionUUid;

    private String formSectionQuestionUuid;

    public AnswerDTO() {

    }
    public AnswerDTO(Answer answer) {
        super(answer);
        this.setValue(answer.getValue());
        if (answer.getForm() != null) {
            this.setFormUuid(answer.getForm().getUuid());
        }
        if (answer.getMentorship() != null) {
            this.setMentorshipUuid(answer.getMentorship().getUuid());
        }
        if (answer.getQuestion() != null) {
            this.setQuestionUUid(answer.getQuestion().getUuid());
        }
        if (answer.getFormSectionQuestion() != null) {
            this.setFormSectionQuestionUuid(answer.getFormSectionQuestion().getUuid());
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getMentorshipUuid() {
        return mentorshipUuid;
    }

    public void setMentorshipUuid(String mentorshipUuid) {
        this.mentorshipUuid = mentorshipUuid;
    }

    public String getQuestionUUid() {
        return questionUUid;
    }

    public void setQuestionUUid(String questionUUid) {
        this.questionUUid = questionUUid;
    }

    public String getFormSectionQuestionUuid() {
        return formSectionQuestionUuid;
    }

    public void setFormSectionQuestionUuid(String formSectionQuestionUuid) {
        this.formSectionQuestionUuid = formSectionQuestionUuid;
    }
}
