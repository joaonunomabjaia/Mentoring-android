package mz.org.csaude.mentoring.model.evaluationLocation;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import com.fasterxml.jackson.annotation.JsonIgnore;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.evaluationLocation.EvaluationLocationDTO;

@Entity(tableName = EvaluationLocation.TABLE_NAME,
        indices = {
                @Index(value = {EvaluationLocation.COLUMN_CODE}, unique = true)
        })
public class EvaluationLocation extends BaseModel {

    public static final String TABLE_NAME = "evaluation_location";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE = "code";

    public static final String HEALTH_FACILITY = "HEALTH_FACILITY";
    public static final String COMMUNITY = "COMMUNITY";
    public static final String BOTH = "BOTH";

    @NonNull
    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @NonNull
    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    public EvaluationLocation() {
    }

    @Ignore
    public EvaluationLocation(String description, String code) {
        this.description = description;
        this.code = code;
    }

    @Ignore
    public EvaluationLocation(EvaluationLocationDTO evaluationLocationDTO) {
        super(evaluationLocationDTO);
        this.setCode(evaluationLocationDTO.getCode());
        this.setDescription(evaluationLocationDTO.getDescription());
    }

    @Ignore
    public EvaluationLocation(String uuid) {
        super(uuid);
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
    public boolean isHealthFacilityEvaluation() {
        return this.code.equals(EvaluationLocation.HEALTH_FACILITY);
    }

    @JsonIgnore
    public boolean isCommunityEvaluation() {
        return this.code.equals(EvaluationLocation.COMMUNITY);
    }

    public boolean isBoth() {
        return this.code.equals(EvaluationLocation.BOTH);
    }
}
