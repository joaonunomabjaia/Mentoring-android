package mz.org.csaude.mentoring.service.question;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.question.QuestionTypeDAO;
import mz.org.csaude.mentoring.model.question.QuestionType;

public class QuestionTypeServiceImpl extends BaseServiceImpl<QuestionType> implements QuestionTypeService{

    QuestionTypeDAO questionTypeDAO;

    public QuestionTypeServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.questionTypeDAO = getDataBaseHelper().getQuestionTypeDAO();
    }

    @Override
    public QuestionType save(QuestionType record) throws SQLException {
        this.questionTypeDAO.insert(record);
        return record;
    }

    @Override
    public QuestionType update(QuestionType record) throws SQLException {
        this.questionTypeDAO.update(record);
        return record;
    }

    @Override
    public int delete(QuestionType record) throws SQLException {
        return this.questionTypeDAO.delete(record);
    }

    @Override
    public List<QuestionType> getAll() throws SQLException {
        return this.questionTypeDAO.queryForAll();
    }

    @Override
    public QuestionType getById(int id) throws SQLException {
        return this.questionTypeDAO.queryForId(id);
    }

    @Override
    public QuestionType getByuuid(String uuid) throws SQLException {
        return questionTypeDAO.getByUuid(uuid);
    }
}
