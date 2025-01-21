package mz.org.csaude.mentoring.model.form;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.List;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.form.FormSectionDTO;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.model.formSectionQuestion.FormSectionQuestion;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.util.Utilities;

@Entity(tableName = FormSection.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Section.class,
                        parentColumns = "id",
                        childColumns = FormSection.COLUMN_SECTION,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Form.class,
                        parentColumns = "id",
                        childColumns = FormSection.COLUMN_FORM,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {FormSection.COLUMN_SECTION}),
                @Index(value = {FormSection.COLUMN_FORM})
        })
public class FormSection extends BaseModel implements Listble {

    public static final String TABLE_NAME = "form_section";
    public static final String COLUMN_FORM = "form_id";
    public static final String COLUMN_SECTION = "section_id";
    public static final String COLUMN_SEQUENCE = "sequence";

    @NonNull
    @ColumnInfo(name = COLUMN_FORM)
    private Integer formId;

    @NonNull
    @ColumnInfo(name = COLUMN_SECTION)
    private Integer sectionId;

    @NonNull
    @ColumnInfo(name = COLUMN_SEQUENCE)
    private Integer sequence;

    @Ignore
    private Section section;

    @Ignore
    private Form form;

    @Ignore
    private String extraInfo;

    @Ignore
    private List<FormSectionQuestion> formSectionQuestions;

    public FormSection() {
    }

    @Ignore
    public FormSection(FormSectionDTO formSectionDTO) {
        super(formSectionDTO);
        this.setForm(new Form(formSectionDTO.getFormUuid()));
        this.setSection(new Section(formSectionDTO.getSectionUuid()));
        this.setSequence(formSectionDTO.getSequence());
    }

    @Ignore
    public FormSection(String uuid) {
        super(uuid);
    }

    @NonNull
    public Integer getFormId() {
        return formId;
    }

    @NonNull
    public Integer getSectionId() {
        return sectionId;
    }

    @NonNull
    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(@NonNull Integer sequence) {
        this.sequence = sequence;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
        this.sectionId = section.getId();
    }

    public void setFormId(@NonNull Integer formId) {
        this.formId = formId;
    }

    public void setSectionId(@NonNull Integer sectionId) {
        this.sectionId = sectionId;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
        this.formId = form.getId();
    }

    public List<FormSectionQuestion> getFormSectionQuestions() {
        return formSectionQuestions;
    }

    public void setFormSectionQuestions(List<FormSectionQuestion> formSectionQuestions) {
        this.formSectionQuestions = formSectionQuestions;
    }

    @Override
    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public String getDescription() {
        return "[Secção: "+this.getSequence()+"] - "+this.getSection().getDescription();
    }

    public boolean hasQuestionsOnCurrMentorship(Mentorship mentorship) {
        if (!Utilities.listHasElements(this.getFormSectionQuestions())) return false;
        for (FormSectionQuestion formSectionQuestion : this.getFormSectionQuestions()) {
            if (formSectionQuestion.getEvaluationType().equals(mentorship.getEvaluationType()) || formSectionQuestion.getEvaluationType().getCode().equals(EvaluationType.AMBOS)) return true;
        }
        return false;
    }
}
