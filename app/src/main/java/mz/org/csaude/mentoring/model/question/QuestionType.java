package mz.org.csaude.mentoring.model.question;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.question.QuestionTypeDTO;

@Entity(tableName = QuestionType.TABLE_NAME,
        indices = {
                @Index(value = {QuestionType.COLUMN_CODE}, unique = true)
        })
public class QuestionType extends BaseModel {

    public static final String TABLE_NAME = "question_type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE = "code";

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    public QuestionType() {
    }

    @Ignore
    public QuestionType(QuestionTypeDTO questionTypeDTO) {
        super(questionTypeDTO);
        this.setCode(questionTypeDTO.getCode());
        this.setDescription(questionTypeDTO.getDescription());
    }

    @Ignore
    public QuestionType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
