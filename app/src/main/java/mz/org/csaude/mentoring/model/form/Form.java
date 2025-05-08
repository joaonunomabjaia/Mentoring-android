package mz.org.csaude.mentoring.model.form;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.form.FormDTO;
import mz.org.csaude.mentoring.dto.form.FormSectionDTO;
import mz.org.csaude.mentoring.model.evaluationLocation.EvaluationLocation;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.model.programmaticArea.ProgrammaticArea;
import mz.org.csaude.mentoring.util.Utilities;

@Entity(tableName = Form.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = ProgrammaticArea.class,
                        parentColumns = "id",
                        childColumns = Form.COLUMN_PROGRAMMATIC_AREA,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Partner.class,
                        parentColumns = "id",
                        childColumns = Form.COLUMN_PARTNER,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = EvaluationLocation.class,
                        parentColumns = "id",
                        childColumns = Form.COLUMN_EVALUATION_LOCATION,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {Form.COLUMN_CODE}, unique = true),
                @Index(value = {Form.COLUMN_PROGRAMMATIC_AREA}),
                @Index(value = {Form.COLUMN_PARTNER}),
                @Index(value = {Mentorship.COLUMN_EVALUATION_LOCATION})
        })
public class Form extends BaseModel {

    public static final String TABLE_NAME = "form";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PROGRAMMATIC_AREA = "programmatic_area_id";
    public static final String COLUMN_TARGET_PATIENT = "target_patient";
    public static final String COLUMN_TARGET_FILE = "target_file";
    public static final String COLUMN_PARTNER = "partner_id";
    public static final String COLUMN_EVALUATION_LOCATION = "evaluation_location_id";

    @NonNull
    @ColumnInfo(name = COLUMN_NAME)
    private String name;

    @NonNull
    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    @NonNull
    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @NonNull
    @ColumnInfo(name = COLUMN_PROGRAMMATIC_AREA)
    private Integer programmaticAreaId;

    @Ignore
    @Relation(parentColumn = COLUMN_PROGRAMMATIC_AREA, entityColumn = "id")
    private ProgrammaticArea programmaticArea;

    @NonNull
    @ColumnInfo(name = COLUMN_TARGET_PATIENT)
    private Integer targetPatient;

    @NonNull
    @ColumnInfo(name = COLUMN_TARGET_FILE)
    private Integer targetFile;

    @NonNull
    @ColumnInfo(name = COLUMN_PARTNER)
    private Integer partnerId;

    @Ignore
    @Relation(parentColumn = COLUMN_PARTNER, entityColumn = "id")
    private Partner partner;

    @Ignore
    private List<FormSection> formSections;

    @NonNull
    @ColumnInfo(name = COLUMN_EVALUATION_LOCATION)
    private Integer evaluationLocationId;

    @Ignore
    @Relation(parentColumn = COLUMN_EVALUATION_LOCATION, entityColumn = "id")
    private EvaluationLocation evaluationLocation;

    public Form() {
    }

    @Ignore
    public Form(FormDTO formDTO) {
        super(formDTO);
        this.setCode(formDTO.getCode());
        this.setDescription(formDTO.getDescription());
        this.setName(formDTO.getName());
        this.setTargetFile(formDTO.getTargetFile());
        this.setTargetPatient(formDTO.getTargetPatient());
        this.setEvaluationLocation(new EvaluationLocation(formDTO.getEvaluationLocationUuid()));
        if (formDTO.getPartner() != null) {
            this.setPartner(new Partner(formDTO.getPartner()));
        }
        if (formDTO.getProgrammaticAreaDTO() != null) {
            this.setProgrammaticArea(new ProgrammaticArea(formDTO.getProgrammaticAreaDTO()));
        }
        if (Utilities.listHasElements(formDTO.getFormSections())){
            if (this.formSections == null) this.formSections = new ArrayList<>();
            for (FormSectionDTO formSection : formDTO.getFormSections()) {
                this.formSections.add(new FormSection(formSection));
            }
        }
    }

    public Form(String uuid) {
        super(uuid);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ProgrammaticArea getProgrammaticArea() {
        return programmaticArea;
    }

    public void setProgrammaticArea(ProgrammaticArea programmaticArea) {
        this.programmaticArea = programmaticArea;
        this.programmaticAreaId = programmaticArea.getId();
    }

    public Integer getTargetPatient() {
        return targetPatient;
    }

    public void setTargetPatient(Integer targetPatient) {
        this.targetPatient = targetPatient;
    }

    public Integer getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(Integer targetFile) {
        this.targetFile = targetFile;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
        this.partnerId = partner.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Form)) return false;
        if (!super.equals(o)) return false;
        Form form = (Form) o;
        return Objects.equals(code, form.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), code);
    }

    public Integer getProgrammaticAreaId() {
        return programmaticAreaId;
    }

    public void setProgrammaticAreaId(Integer programmaticAreaId) {
        this.programmaticAreaId = programmaticAreaId;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    public List<FormSection> getFormSections() {
        return formSections;
    }

    public void setFormSections(List<FormSection> formSections) {
        this.formSections = formSections;
    }

    @NonNull
    public Integer getEvaluationLocationId() {
        return evaluationLocationId;
    }

    public void setEvaluationLocationId(@NonNull Integer evaluationLocationId) {
        this.evaluationLocationId = evaluationLocationId;
    }

    public EvaluationLocation getEvaluationLocation() {
        return evaluationLocation;
    }

    public void setEvaluationLocation(EvaluationLocation evaluationLocation) {
        if (evaluationLocation != null) this.evaluationLocationId = evaluationLocation.getId();
        this.evaluationLocation = evaluationLocation;
    }
}
