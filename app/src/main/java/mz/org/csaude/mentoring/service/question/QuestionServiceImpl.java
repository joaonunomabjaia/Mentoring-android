package mz.org.csaude.mentoring.service.question;

import android.app.Application;

import androidx.room.Transaction;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.question.QuestionDAO;
import mz.org.csaude.mentoring.dto.question.QuestionDTO;
import mz.org.csaude.mentoring.model.question.Question;
import mz.org.csaude.mentoring.model.question.QuestionsCategory;

public class QuestionServiceImpl extends BaseServiceImpl<Question> implements QuestionService {

    QuestionDAO questionDAO;

    public QuestionServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.questionDAO = getDataBaseHelper().getQuestionDAO();
    }

    @Override
    public Question save(Question record) throws SQLException {
        record.setId((int) this.questionDAO.insert(record));
        return record;
    }

    @Override
    public Question update(Question record) throws SQLException {
        this.questionDAO.update(record);
        return record;
    }

    @Override
    public int delete(Question record) throws SQLException {
        return this.questionDAO.delete(record);
    }

    @Override
    public List<Question> getAll() throws SQLException {
        return this.questionDAO.queryForAll();
    }

    @Override
    public Question getById(int id) throws SQLException {
        return this.questionDAO.queryForId(id);
    }

    @Override
    public void saveOrUpdateQuestions(List<QuestionDTO> questionDTOS) throws SQLException {
        for (QuestionDTO questionDTO: questionDTOS) {
             this.saveOrUpdateQuestion(questionDTO);
        }
    }

    @Override
    @Transaction
    public void saveOrUpdateQuestionList(List<Question> questions) throws SQLException {
        for (Question question: questions) {
            QuestionsCategory qcOnDb = getApplication().getQuestionsCategoryService().getByuuid(question.getQuestionsCategory().getUuid());;
            if(qcOnDb!=null) {
                question.setQuestionsCategory(qcOnDb);
            } else {
                question.setQuestionsCategory(getApplication().getQuestionsCategoryService().saveOrUpdateQuestionCategory(question.getQuestionsCategory()));
            }

            Question qOnDb = this.questionDAO.getByUuid(question.getUuid());
            if(qOnDb!=null) {
                question.setId(qOnDb.getId());
                this.questionDAO.update(question);
            } else {
                question.setId((int) this.questionDAO.insert(question));
            }
        }
    }

    @Override
    public Question saveOrUpdateQuestion(QuestionDTO questionDTO) throws SQLException {
        Question q = this.questionDAO.getByUuid(questionDTO.getUuid());
        Question question = questionDTO.getQuestionObj();
        question.setQuestionsCategory(getApplication().getQuestionsCategoryService().getByuuid(questionDTO.getQuestionCategory().getUuid()));
        if(q!=null) {
            question.setId(q.getId());
            this.update(question);
        } else {
            this.save(question);
        }
        return question;
    }

    @Override
    public Question getByuuid(String uuid) throws SQLException {
        return questionDAO.getByUuid(uuid);
    }
}
