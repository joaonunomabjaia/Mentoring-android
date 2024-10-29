package mz.org.csaude.mentoring.service.fromSection;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.form.FormSection;

public interface FormSectionService extends BaseService<FormSection> {

    List<FormSection> getAllOfFormWithQuestions(Form form, String evaluationType) throws SQLException;
}
