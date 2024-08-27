package mz.org.csaude.mentoring.model.form;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

import java.util.Objects;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.form.FormDTO;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.model.programmaticArea.ProgrammaticArea;

@Entity(tableName = Form.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = ProgrammaticArea.class,
                        parentColumns = "id",
                        childColumns = Form.COLUMN_PROGRAMMATIC_AREA,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Partner.class,
                        parentColumns = "id",
                        childColumns = Form.COLUMN_PARTNER,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {Form.COLUMN_CODE}, unique = true),
                @Index(value = {Form.COLUMN_PROGRAMMATIC_AREA}),
                @Index(value = {Form.COLUMN_PARTNER})
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

    @ColumnInfo(name = COLUMN_NAME)
    private String name;

    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @ColumnInfo(name = COLUMN_PROGRAMMATIC_AREA)
    private int programmaticAreaId;

    @Ignore
    @Relation(parentColumn = COLUMN_PROGRAMMATIC_AREA, entityColumn = "id")
    private ProgrammaticArea programmaticArea;

    @ColumnInfo(name = COLUMN_TARGET_PATIENT)
    private int targetPatient;

    @ColumnInfo(name = COLUMN_TARGET_FILE)
    private int targetFile;

    @ColumnInfo(name = COLUMN_PARTNER)
    private int partnerId;

    @Ignore
    @Relation(parentColumn = COLUMN_PARTNER, entityColumn = "id")
    private Partner partner;

    public Form() {
    }

    public Form(FormDTO formDTO) {
        super(formDTO);
        this.setCode(formDTO.getCode());
        this.setDescription(formDTO.getDescription());
        this.setName(formDTO.getName());
        this.setTargetFile(formDTO.getTargetFile());
        this.setTargetPatient(formDTO.getTargetPatient());
        if (formDTO.getPartner() != null) {
            this.setPartner(new Partner(formDTO.getPartner()));
        }
        if (formDTO.getProgrammaticArea() != null) {
            this.setProgrammaticArea(new ProgrammaticArea(formDTO.getProgrammaticArea()));
        }
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

    public int getTargetPatient() {
        return targetPatient;
    }

    public void setTargetPatient(int targetPatient) {
        this.targetPatient = targetPatient;
    }

    public int getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(int targetFile) {
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

    public int getProgrammaticAreaId() {
        return programmaticAreaId;
    }

    public void setProgrammaticAreaId(int programmaticAreaId) {
        this.programmaticAreaId = programmaticAreaId;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }
}
