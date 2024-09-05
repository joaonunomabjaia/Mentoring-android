package mz.org.csaude.mentoring.dto.question;



import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.question.QuestionsCategory;


public class QuestionCategoryDTO extends BaseEntityDTO {
    private String category;
    public QuestionCategoryDTO(QuestionsCategory questionsCategory) {
        super(questionsCategory);
        this.setCategory(questionsCategory.getCategory());
    }

    public QuestionCategoryDTO() {
        super();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public QuestionsCategory getQuestionCategory() {
        QuestionsCategory questionsCategory = new QuestionsCategory();
        questionsCategory.setUuid(this.getUuid());
        questionsCategory.setCreatedAt(this.getCreatedAt());
        questionsCategory.setUpdatedAt(this.getUpdatedAt());
        questionsCategory.setLifeCycleStatus(this.getLifeCycleStatus());
        questionsCategory.setCategory(this.getCategory());
        return questionsCategory;
    }
}
