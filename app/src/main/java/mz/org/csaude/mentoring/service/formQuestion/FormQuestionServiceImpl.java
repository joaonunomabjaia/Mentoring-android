package mz.org.csaude.mentoring.service.formQuestion;

import android.app.Application;

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
        this.formQuestionDAO.insert(record);
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
    public void saveOrUpdateFormQuestions(List<FormQuestionDTO> formQuestionDTOS) throws SQLException {
        for (FormQuestionDTO formQuestionDTO: formQuestionDTOS) {
            this.saveOrUpdateFormQuestion(formQuestionDTO);
        }
    }
    @Override
    public FormQuestion saveOrUpdateFormQuestion(FormQuestionDTO formQuestionDTO) throws SQLException {
        QuestionCategoryDTO questionCategoryDTO = formQuestionDTO.getQuestion().getQuestionCategory();
        QuestionsCategory qc = this.questionsCategoryDAO.getByUuid(questionCategoryDTO.getUuid());
        QuestionsCategory questionsCategory = questionCategoryDTO.getQuestionCategory();
        if(qc!=null) {
            questionsCategory.setId(qc.getId());
        }
        this.questionsCategoryDAO.createOrUpdate(questionsCategory);

        QuestionDTO questionDTO = formQuestionDTO.getQuestion();
        Question q = this.questionDAO.getByUuid(questionDTO.getUuid());
        Question question = questionDTO.getQuestionObj();
        if(q!=null) {
            question.setId(q.getId());
        }
        this.questionDAO.createOrUpdate(question);

        FormQuestion fq = this.formQuestionDAO.getByUuid(formQuestionDTO.getUuid());
        FormQuestion formQuestion = formQuestionDTO.getFormQuestion();
        if(fq!=null) {
            formQuestion.setId(fq.getId());
        }
        this.formQuestionDAO.createOrUpdate(formQuestion);
        return formQuestion;
    }

    @Override
    public void saveOrUpdate(List<FormQuestion> formQuestions) throws SQLException {
        for (FormQuestion fQuestion : formQuestions) {
            Question q = this.questionDAO.getByUuid(fQuestion.getQuestion().getUuid());
            if(q!=null) {
                fQuestion.getQuestion().setId(q.getId());
            }
            QuestionsCategory qc = getApplication().getQuestionsCategoryService().getByuuid(fQuestion.getQuestion().getQuestionsCategory().getUuid());
            if (qc != null) {
                fQuestion.getQuestion().getQuestionsCategory().setId(qc.getId());
            }
            Form f = this.formDAO.getByUuid(fQuestion.getForm().getUuid());
            if(f!=null) {
                fQuestion.getForm().setId(f.getId());
            }
            EvaluationType ev = this.evaluationTypeDAO.getByUuid(fQuestion.getEvaluationType().getUuid());
            if(ev!=null) {
                fQuestion.getEvaluationType().setId(ev.getId());
            }
            ResponseType rt = this.responseTypeDAO.getByUuid(fQuestion.getResponseType().getUuid());
            if(rt!=null) {
                fQuestion.getResponseType().setId(rt.getId());
            }
            getApplication().getQuestionsCategoryService().saveOrUpdateQuestionCategory(fQuestion.getQuestion().getQuestionsCategory());
            this.questionDAO.createOrUpdate(fQuestion.getQuestion());
            FormQuestion fq = this.formQuestionDAO.getByUuid(fQuestion.getUuid());
            if(fq!=null) {
                fQuestion.setId(fq.getId());
            }
            this.formQuestionDAO.createOrUpdate(fQuestion);
        }
    }

    @Override
    public List<FormQuestion> getAllOfForm(Form form, String evaluationType) {
        return formQuestionDAO.getAllOfForm(form.getId(), evaluationType, String.valueOf(LifeCycleStatus.ACTIVE));
    }
}
