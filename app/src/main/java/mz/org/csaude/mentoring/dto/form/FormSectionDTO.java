package mz.org.csaude.mentoring.dto.form;

import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.section.SectionDTO;
import mz.org.csaude.mentoring.model.form.FormSection;
import mz.org.csaude.mentoring.model.formSectionQuestion.FormSectionQuestion;
import mz.org.csaude.mentoring.util.Utilities;

public class FormSectionDTO extends BaseEntityDTO {

    private String formUuid;

    private String sectionUuid;

    private Integer sequence;

    private List<FormSectionQuestionDTO> formSectionQuestionDTOS;

    public FormSectionDTO() {}

    public FormSectionDTO(FormSection formSection) {
        super(formSection);
        this.formUuid = formSection.getForm().getUuid();
        this.sectionUuid = formSection.getUuid();
        this.sequence = formSection.getSequence();
        if (Utilities.listHasElements(formSection.getFormSectionQuestions())) {
            this.formSectionQuestionDTOS = new ArrayList<>();
            for (FormSectionQuestion fsq : formSection.getFormSectionQuestions()) {
                this.formSectionQuestionDTOS.add(new FormSectionQuestionDTO(fsq));
            }
        }
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getSectionUuid() {
        return sectionUuid;
    }

    public void setSectionUuid(String sectionUuid) {
        this.sectionUuid = sectionUuid;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public List<FormSectionQuestionDTO> getFormSectionQuestionDTOS() {
        return formSectionQuestionDTOS;
    }

    public void setFormSectionQuestionDTOS(List<FormSectionQuestionDTO> formSectionQuestionDTOS) {
        this.formSectionQuestionDTOS = formSectionQuestionDTOS;
    }
}
