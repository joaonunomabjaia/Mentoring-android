package mz.org.csaude.mentoring.model.question;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.question.QuestionCategoryDTO;

@Entity(tableName = QuestionsCategory.TABLE_NAME,
        indices = {
                @Index(value = {QuestionsCategory.COLUMN_CATEGORY}, unique = true)
        })
public class QuestionsCategory extends BaseModel {

    public static final String TABLE_NAME = "question_category";
    public static final String COLUMN_CATEGORY = "category";

    @ColumnInfo(name = COLUMN_CATEGORY)
    private String category;

    public QuestionsCategory() {
    }

    @Ignore
    public QuestionsCategory(String category) {
        this.category = category;
    }

    @Ignore
    public QuestionsCategory(QuestionCategoryDTO questionCategoryDTO) {
        super(questionCategoryDTO);
        this.setCategory(questionCategoryDTO.getCategory());
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getDescription() {
        return this.category;
    }
}
