package mz.org.csaude.mentoring.service.formSectionQuestion;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.model.form.FormSection;
import mz.org.csaude.mentoring.model.formSectionQuestion.FormSectionQuestion;

public interface FormSectionQuestionService extends BaseService<FormSectionQuestion> {

    void saveOrUpdate(List<FormSectionQuestion> formSectionQuestionDTOS) throws SQLException;

    List<FormSectionQuestion> getAllOfFormSection(FormSection formSection, String evaluationType) throws SQLException;
}
