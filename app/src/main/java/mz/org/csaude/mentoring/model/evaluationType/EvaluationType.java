package mz.org.csaude.mentoring.model.evaluationType;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import com.fasterxml.jackson.annotation.JsonIgnore;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.evaluationType.EvaluationTypeDTO;

@Entity(tableName = EvaluationType.TABLE_NAME,
        indices = {
                @Index(value = {EvaluationType.COLUMN_CODE}, unique = true)
        })
public class EvaluationType extends BaseModel {

    public static final String TABLE_NAME = "evaluation_type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE = "code";

    public static final String CONSULTA = "Consulta";
    public static final String FICHA = "Ficha";

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    public EvaluationType() {
    }

    public EvaluationType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public EvaluationType(EvaluationTypeDTO evaluationType) {
        super(evaluationType);
        this.setCode(evaluationType.getCode());
        this.setDescription(evaluationType.getDescription());
    }

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

    @JsonIgnore
    public boolean isPatientEvaluation() {
        return this.code.equals(EvaluationType.CONSULTA);
    }

    @JsonIgnore
    public boolean isFichaEvaluation() {
        return this.code.equals(EvaluationType.FICHA);
    }
}
