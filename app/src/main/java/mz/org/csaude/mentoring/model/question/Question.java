package mz.org.csaude.mentoring.model.question;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.question.QuestionDTO;

@Entity(tableName = Question.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = QuestionsCategory.class,
                        parentColumns = "id",
                        childColumns = Question.COLUMN_QUESTION_CATEGORY,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {Question.COLUMN_CODE}),
                @Index(value = {Question.COLUMN_QUESTION_CATEGORY})
        })
public class Question extends BaseModel {

    public static final String TABLE_NAME = "question";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_QUESTION_CATEGORY = "question_category_id";

    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    @ColumnInfo(name = COLUMN_QUESTION)
    private String question;

    @ColumnInfo(name = COLUMN_QUESTION_CATEGORY)
    private int questionCategoryId;

    @Ignore
    @Relation(parentColumn = COLUMN_QUESTION_CATEGORY, entityColumn = "id")
    private QuestionsCategory questionsCategory;

    public Question() {
    }

    public Question(QuestionDTO questionDTO) {
        super(questionDTO);
        this.setCode(questionDTO.getCode());
        if (questionDTO.getQuestion() != null) this.setQuestion(questionDTO.getQuestion());
        if (questionDTO.getQuestionCategory() != null) {
            this.setQuestionsCategory(new QuestionsCategory(questionDTO.getQuestionCategory()));
            this.questionCategoryId = this.questionsCategory.getId();
        }
    }

    public String getCode() {
        return code;
    }

    public String getQuestion() {
        return question;
    }

    public QuestionsCategory getQuestionsCategory() {
        return questionsCategory;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setQuestionsCategory(QuestionsCategory questionsCategory) {
        this.questionsCategory = questionsCategory;
        this.questionCategoryId = questionsCategory.getId();
    }

    public int getQuestionCategoryId() {
        return questionCategoryId;
    }

    public void setQuestionCategoryId(int questionCategoryId) {
        this.questionCategoryId = questionCategoryId;
    }
}
