package mz.org.csaude.mentoring.service.form;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.model.evaluationLocation.EvaluationLocation;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.tutor.Tutor;

public interface FormService extends BaseService<Form> {
    List<Form> getAllOfTutor(Tutor tutor) throws SQLException;
    void savedOrUpdateForms(List<Form> forms) throws SQLException;
    Form savedOrUpdateForm(Form form) throws SQLException;
    List<Form> getAllNotSynced() throws SQLException;
    List<Form> getAllSynced(Application application) throws SQLException;
    Form getFullByIdForEvaluation(int id, String evaluationType, EvaluationLocation evaluationLocation) throws SQLException;

    boolean hasQuestionsForSelectedLocation(Form form, EvaluationLocation evaluationLocation);
}
