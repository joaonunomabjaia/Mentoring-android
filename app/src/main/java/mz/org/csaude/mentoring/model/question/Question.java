package mz.org.csaude.mentoring.model.question;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.question.QuestionDTO;
import mz.org.csaude.mentoring.model.program.Program;

@Entity(tableName = Question.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Program.class,
                        parentColumns = "id",
                        childColumns = Question.COLUMN_PROGRAM,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {Question.COLUMN_CODE}),
                @Index(value = {Question.COLUMN_PROGRAM})
        })
public class Question extends BaseModel {

    public static final String TABLE_NAME = "question";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_TABLE_CODE = "table_code";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_PROGRAM = "program_id";

    @NonNull
    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    @NonNull
    @ColumnInfo(name = COLUMN_TABLE_CODE)
    private String tableCode;

    @NonNull
    @ColumnInfo(name = COLUMN_QUESTION)
    private String question;

    @NonNull
    @ColumnInfo(name = COLUMN_PROGRAM)
    private Integer programId;

    @Ignore
    private Program program;

    public Question() {
    }

    @Ignore
    public Question(QuestionDTO questionDTO) {
        super(questionDTO);
        this.setCode(questionDTO.getCode());
        this.setQuestion(questionDTO.getQuestion());
        this.setTableCode(questionDTO.getTableCode());
        this.setProgram(new Program(questionDTO.getProgramUuid()));
    }

    public Question(String uuid) {
        super(uuid);
    }

    public String getCode() {
        return code;
    }

    public String getQuestion() {
        return question;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @NonNull
    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(@NonNull String tableCode) {
        this.tableCode = tableCode;
    }

    @NonNull
    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(@NonNull Integer programId) {
        this.programId = programId;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
        if (program != null) this.programId = program.getId();
    }
}
