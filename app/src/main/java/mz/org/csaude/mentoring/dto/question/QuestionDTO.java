package mz.org.csaude.mentoring.dto.question;


import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.question.Question;
import mz.org.csaude.mentoring.model.question.QuestionsCategory;


public class QuestionDTO extends BaseEntityDTO {
    private String code;
    private String tableCode;
    private String question;
    private String programUuid;

    public QuestionDTO() {
        super();
    }

    public QuestionDTO(Question question) {
        super(question);
        this.setCode(question.getCode());
        this.setQuestion(question.getQuestion());
        this.setTableCode(question.getTableCode());
        this.setProgramUuid(question.getProgram().getUuid());

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public String getProgramUuid() {
        return programUuid;
    }

    public void setProgramUuid(String programUuid) {
        this.programUuid = programUuid;
    }
}
