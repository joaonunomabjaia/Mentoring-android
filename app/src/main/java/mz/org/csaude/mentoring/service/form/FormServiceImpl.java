package mz.org.csaude.mentoring.service.form;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.form.FormDAO;
import mz.org.csaude.mentoring.dao.programmaticArea.TutorProgrammaticAreaDAO;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.tutor.Tutor;

public class FormServiceImpl extends BaseServiceImpl<Form> implements FormService{
    FormDAO formDAO;
    TutorProgrammaticAreaDAO tutorProgrammaticAreaDAO;
    public FormServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) {
        try {
            super.init(application);
            this.formDAO = getDataBaseHelper().getFormDAO();
            this.tutorProgrammaticAreaDAO = getDataBaseHelper().getTutorProgrammaticAreaDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Form save(Form record) throws SQLException {
        this.formDAO.insertForm(record);
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
        return this.formDAO.queryForId(id);
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
        }
        this.formDAO.createOrUpdate(form);
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
}
