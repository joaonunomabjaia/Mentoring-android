package mz.org.csaude.mentoring.service.form;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.evaluationLocation.EvaluationLocationDAO;
import mz.org.csaude.mentoring.dao.form.FormDAO;
import mz.org.csaude.mentoring.dao.formSection.FormSectionDAO;
import mz.org.csaude.mentoring.dao.programmaticArea.TutorProgrammaticAreaDAO;
import mz.org.csaude.mentoring.dao.section.SectionDAO;
import mz.org.csaude.mentoring.model.evaluationLocation.EvaluationLocation;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.form.FormSection;
import mz.org.csaude.mentoring.model.tutor.Tutor;

public class FormServiceImpl extends BaseServiceImpl<Form> implements FormService{
    FormDAO formDAO;
    TutorProgrammaticAreaDAO tutorProgrammaticAreaDAO;
    FormSectionDAO formSectionDAO;
    SectionDAO sectionDAO;
    EvaluationLocationDAO evaluationLocationDAO;

    public FormServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) {
        try {
            super.init(application);
            this.formDAO = getDataBaseHelper().getFormDAO();
            this.tutorProgrammaticAreaDAO = getDataBaseHelper().getTutorProgrammaticAreaDAO();
            this.formSectionDAO = getDataBaseHelper().getFormSectionDAO();
            this.sectionDAO = getDataBaseHelper().getSectionDAO();
            this.evaluationLocationDAO = getDataBaseHelper().getEvaluationLocationDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Form save(Form record) throws SQLException {
        record.setId((int) this.formDAO.insertForm(record));
        return record;
    }

    @Override
    public Form update(Form record) throws SQLException {
        this.formDAO.updateForm(record);
        return record;
    }

    @Override
    public int delete(Form record) throws SQLException {
        return this.formDAO.delete(record.getId());
    }

    @Override
    public List<Form> getAll() throws SQLException {
        return this.formDAO.queryForAll();
    }

    @Override
    public Form getById(int id) throws SQLException {
        Form form = this.formDAO.queryForId(id);
        form.setEvaluationLocation(this.evaluationLocationDAO.queryForId(form.getEvaluationLocationId()));
        return form;
    }

    @Override
    public List<Form> getAllOfTutor(Tutor tutor) throws SQLException {
        return formDAO.getAllOfTutor(tutor.getId());
    }

    @Override
    public void savedOrUpdateForms(List<Form> forms) throws SQLException {
        for (Form form : forms) {
            this.savedOrUpdateForm(form);
        }
    }

    @Override
    public Form savedOrUpdateForm(Form form) throws SQLException {
        Form f = this.formDAO.getByUuid(form.getUuid());
        if(f!=null) {
            form.setId(f.getId());
            this.update(form);
            for (FormSection formSection : form.getFormSections()) {
                FormSection fsection = formSectionDAO.getByUuid(formSection.getUuid());
                formSection.setForm(form);
                formSection.setSection(sectionDAO.getByUuid(formSection.getSection().getUuid()));
                if(fsection!=null) {
                    formSection.setId(fsection.getId());
                    this.formSectionDAO.update(formSection);
                } else {
                    this.formSectionDAO.insert(formSection);
                }
            }
        } else {
            this.save(form);
            for (FormSection formSection : form.getFormSections()) {
                formSection.setForm(form);
                formSection.setSection(sectionDAO.getByUuid(formSection.getSection().getUuid()));
                this.formSectionDAO.insert(formSection);
            }
        }
        return form;
    }

    @Override
    public List<Form> getAllNotSynced() throws SQLException {
        return this.formDAO.getAllNotSynced();
    }

    @Override
    public List<Form> getAllSynced(Application application) throws SQLException {
        return this.formDAO.getAllSynced();
    }

    @Override
    public Form getByuuid(String uuid) throws SQLException {
        return formDAO.getByUuid(uuid);
    }

    public Form getFullByIdForEvaluation(int id, String evaluationType, EvaluationLocation evaluationLocation) throws SQLException {
        Form form = this.formDAO.queryForId(id);
        form.setEvaluationLocation(this.evaluationLocationDAO.queryForId(form.getEvaluationLocationId()));
        form.setFormSections(getApplication().getFormSectionService().getAllOfFormWithQuestions(form, evaluationType, evaluationLocation.getId()));
        return form;
    }
}
