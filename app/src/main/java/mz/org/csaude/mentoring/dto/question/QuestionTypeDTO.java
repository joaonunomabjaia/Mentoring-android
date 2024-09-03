package mz.org.csaude.mentoring.dto.question;



import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.question.QuestionType;


public class QuestionTypeDTO extends BaseEntityDTO {
    private String code;
    private String description;
    public QuestionTypeDTO(QuestionType questionType) {
        super(questionType);
        this.setCode(questionType.getCode());
        this.setDescription(questionType.getDescription());
    }
    public QuestionTypeDTO() {
        super();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
