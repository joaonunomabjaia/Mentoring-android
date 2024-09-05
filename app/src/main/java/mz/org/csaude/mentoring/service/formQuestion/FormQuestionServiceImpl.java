package mz.org.csaude.mentoring.service.formQuestion;

import android.app.Application;

import androidx.room.Transaction;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.evaluation.EvaluationTypeDAO;
import mz.org.csaude.mentoring.dao.form.FormDAO;
import mz.org.csaude.mentoring.dao.formQuestion.FormQuestionDAO;
import mz.org.csaude.mentoring.dao.question.QuestionDAO;
import mz.org.csaude.mentoring.dao.question.QuestionsCategoryDAO;
import mz.org.csaude.mentoring.dao.responseType.ResponseTypeDAO;
import mz.org.csaude.mentoring.dto.form.FormQuestionDTO;
import mz.org.csaude.mentoring.dto.question.QuestionCategoryDTO;
import mz.org.csaude.mentoring.dto.question.QuestionDTO;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.formQuestion.FormQuestion;
import mz.org.csaude.mentoring.model.question.Question;
import mz.org.csaude.mentoring.model.question.QuestionsCategory;
import mz.org.csaude.mentoring.model.responseType.ResponseType;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.util.LifeCycleStatus;

public class FormQuestionServiceImpl extends BaseServiceImpl<FormQuestion> implements FormQuestionService {

    FormQuestionDAO formQuestionDAO;
    QuestionDAO questionDAO;
    QuestionsCategoryDAO questionsCategoryDAO;
    FormDAO formDAO;
    ResponseTypeDAO responseTypeDAO;
    EvaluationTypeDAO evaluationTypeDAO;
    public FormQuestionServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.formQuestionDAO = getDataBaseHelper().getFormQuestionDAO();
        this.questionDAO = getDataBaseHelper().getQuestionDAO();
        this.questionsCategoryDAO = getDataBaseHelper().getQuestionsCategoryDAO();
        this.formDAO = getDataBaseHelper().getFormDAO();
        this.responseTypeDAO = getDataBaseHelper().getResponseTypeDAO();
        this.evaluationTypeDAO = getDataBaseHelper().getEvaluationTypeDAO();
    }
    @Override
    public FormQuestion save(FormQuestion record) throws SQLException {
        record.setId((int) this.formQuestionDAO.insert(record));
        return record;
    }

    @Override
    public FormQuestion update(FormQuestion record) throws SQLException {
        this.formQuestionDAO.update(record);
        return record;
    }

    @Override
    public int delete(FormQuestion record) throws SQLException {
        return this.formQuestionDAO.delete(record);
    }

    @Override
    public List<FormQuestion> getAll() throws SQLException {
        return this.formQuestionDAO.queryForAll();
    }

    @Override
    public FormQuestion getById(int id) throws SQLException {
        return this.formQuestionDAO.queryForId(id);
    }

    @Override
    public FormQuestion getByuuid(String uuid) throws SQLException {
        return this.formQuestionDAO.getByUuid(uuid);
    }


    @Override
    @Transaction
    public void saveOrUpdate(List<FormQuestion> formQuestions) throws SQLException {
        for (FormQuestion fQuestion : formQuestions) {

            fQuestion.setQuestion(this.questionDAO.getByUuid(fQuestion.getQuestion().getUuid()));

            FormQuestion existingFormQuestion = this.formQuestionDAO.getByUuid(fQuestion.getUuid());
            if (existingFormQuestion != null) {
                fQuestion.setId(existingFormQuestion.getId());
                this.formQuestionDAO.update(fQuestion);
            } else {
                this.formQuestionDAO.insert(fQuestion);
            }
        }
    }



    @Override
    public List<FormQuestion> getAllOfForm(Form form, String evaluationType) {
        List<FormQuestion> formQuestions = formQuestionDAO.getAllOfForm(form.getId(), evaluationType, String.valueOf(LifeCycleStatus.ACTIVE));
        for (FormQuestion formQuestion : formQuestions) {
            formQuestion.setQuestion(questionDAO.queryForId(formQuestion.getQuestionId()));
            formQuestion.getQuestion().setQuestionsCategory(questionsCategoryDAO.queryForId(formQuestion.getQuestion().getQuestionCategoryId()));
            formQuestion.setForm(formDAO.queryForId(formQuestion.getFormId()));
            formQuestion.setResponseType(responseTypeDAO.queryForId(formQuestion.getResponseTypeId()));
            formQuestion.setEvaluationType(evaluationTypeDAO.queryForId(formQuestion.getEvaluationTypeId()));
        }
        return formQuestions;
    }
}
