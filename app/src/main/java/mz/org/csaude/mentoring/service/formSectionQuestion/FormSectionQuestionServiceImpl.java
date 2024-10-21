package mz.org.csaude.mentoring.service.formSectionQuestion;

import android.app.Application;

import androidx.room.Transaction;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.evaluation.EvaluationTypeDAO;
import mz.org.csaude.mentoring.dao.form.FormDAO;
import mz.org.csaude.mentoring.dao.formSectionQuestion.FormSectionQuestionDAO;
import mz.org.csaude.mentoring.dao.question.QuestionDAO;
import mz.org.csaude.mentoring.dao.question.QuestionsCategoryDAO;
import mz.org.csaude.mentoring.dao.responseType.ResponseTypeDAO;
import mz.org.csaude.mentoring.model.form.FormSection;
import mz.org.csaude.mentoring.model.formSectionQuestion.FormSectionQuestion;
import mz.org.csaude.mentoring.util.LifeCycleStatus;

public class FormSectionQuestionServiceImpl extends BaseServiceImpl<FormSectionQuestion> implements FormSectionQuestionService {

    FormSectionQuestionDAO formSectionQuestionDAO;
    QuestionDAO questionDAO;
    FormDAO formDAO;
    ResponseTypeDAO responseTypeDAO;
    EvaluationTypeDAO evaluationTypeDAO;
    public FormSectionQuestionServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.formSectionQuestionDAO = getDataBaseHelper().getFormSectionQuestionDAO();
        this.questionDAO = getDataBaseHelper().getQuestionDAO();
        this.formDAO = getDataBaseHelper().getFormDAO();
        this.responseTypeDAO = getDataBaseHelper().getResponseTypeDAO();
        this.evaluationTypeDAO = getDataBaseHelper().getEvaluationTypeDAO();
    }
    @Override
    public FormSectionQuestion save(FormSectionQuestion record) throws SQLException {
        record.setId((int) this.formSectionQuestionDAO.insert(record));
        return record;
    }

    @Override
    public FormSectionQuestion update(FormSectionQuestion record) throws SQLException {
        this.formSectionQuestionDAO.update(record);
        return record;
    }

    @Override
    public int delete(FormSectionQuestion record) throws SQLException {
        return this.formSectionQuestionDAO.delete(record);
    }

    @Override
    public List<FormSectionQuestion> getAll() throws SQLException {
        return this.formSectionQuestionDAO.queryForAll();
    }

    @Override
    public FormSectionQuestion getById(int id) throws SQLException {
        return this.formSectionQuestionDAO.queryForId(id);
    }

    @Override
    public FormSectionQuestion getByuuid(String uuid) throws SQLException {
        return this.formSectionQuestionDAO.getByUuid(uuid);
    }


    @Override
    @Transaction
    public void saveOrUpdate(List<FormSectionQuestion> formSectionQuestions) throws SQLException {
        for (FormSectionQuestion fQuestion : formSectionQuestions) {

            fQuestion.setQuestion(this.questionDAO.getByUuid(fQuestion.getQuestion().getUuid()));

            FormSectionQuestion existingFormSectionQuestion = this.formSectionQuestionDAO.getByUuid(fQuestion.getUuid());
            if (existingFormSectionQuestion != null) {
                fQuestion.setId(existingFormSectionQuestion.getId());
                this.formSectionQuestionDAO.update(fQuestion);
            } else {
                this.formSectionQuestionDAO.insert(fQuestion);
            }
        }
    }



    @Override
    public List<FormSectionQuestion> getAllOfFormSection(FormSection formSection, String evaluationType) throws SQLException {
        List<FormSectionQuestion> formSectionQuestions = formSectionQuestionDAO.getAllOfFormSection(formSection.getId(), evaluationType, String.valueOf(LifeCycleStatus.ACTIVE));
        for (FormSectionQuestion formSectionQuestion : formSectionQuestions) {
            formSectionQuestion.setQuestion(questionDAO.queryForId(formSectionQuestion.getQuestionId()));
            formSectionQuestion.getQuestion().setProgram(getApplication().getProgramService().getById(formSectionQuestion.getQuestion().getProgramId()));
            formSectionQuestion.setFormSection(getApplication().getFormSectionService().getById(formSectionQuestion.getFormSectionId()));
            formSectionQuestion.setResponseType(responseTypeDAO.queryForId(formSectionQuestion.getResponseTypeId()));
            formSectionQuestion.setEvaluationType(evaluationTypeDAO.queryForId(formSectionQuestion.getEvaluationTypeId()));
        }
        return formSectionQuestions;
    }
}
