package mz.org.csaude.mentoring.service.fromSection;

import android.app.Application;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.formSection.FormSectionDAO;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.form.FormSection;
import mz.org.csaude.mentoring.service.fromSection.FormSectionService;

public class FormSectionServiceImpl extends BaseServiceImpl<FormSection> implements FormSectionService {

    private final FormSectionDAO formSectionDAO;

    public FormSectionServiceImpl(Application application){
        super(application);
        // Initialize the DAO from the database helper
        this.formSectionDAO = getDataBaseHelper().getFormSectionDAO();
    }

    @Override
    public FormSection save(FormSection record) throws SQLException {
        // Insert the FormSection record using the DAO and set the ID
        record.setId((int) this.formSectionDAO.insert(record));
        return record;
    }

    @Override
    public FormSection update(FormSection record) throws SQLException {
        // Update the FormSection record using the DAO
        this.formSectionDAO.update(record);
        return record;
    }

    @Override
    public int delete(FormSection record) throws SQLException {
        // Delete the FormSection record using the DAO
        return this.formSectionDAO.delete(record);
    }

    @Override
    public List<FormSection> getAll() throws SQLException {
        // Retrieve all FormSection records using the DAO
        List<FormSection> formSections = this.formSectionDAO.queryForAll();
        if (formSections != null && !formSections.isEmpty()) {
            return formSections;
        }
        return Collections.emptyList();
    }

    @Override
    public FormSection getById(int id) throws SQLException {
        // Retrieve a FormSection by its ID using the DAO
        return this.formSectionDAO.queryForId(id);
    }

    @Override
    public FormSection getByuuid(String uuid) throws SQLException {
        // Retrieve a FormSection by its UUID using the DAO
        return this.formSectionDAO.getByUuid(uuid);
    }


    @Override
    public List<FormSection> getAllOfFormWithQuestions(Form form, String evaluationType, int evaluationLocationId) throws SQLException {
        List<FormSection> formSections = this.formSectionDAO.getFormSectionsByFormId(form.getId());
        for (FormSection formSection : formSections) {
            formSection.setSection(getApplication().getSectionService().getById(formSection.getSectionId()));
            formSection.setFormSectionQuestions(getApplication().getFormSectionQuestionService().getAllOfFormSection(formSection, evaluationType, evaluationLocationId));
        }
        return formSections;
    }
}
