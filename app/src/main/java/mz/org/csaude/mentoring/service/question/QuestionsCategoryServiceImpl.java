package mz.org.csaude.mentoring.service.question;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.question.QuestionsCategoryDAO;
import mz.org.csaude.mentoring.dto.question.QuestionCategoryDTO;
import mz.org.csaude.mentoring.model.question.QuestionsCategory;

public class QuestionsCategoryServiceImpl extends BaseServiceImpl<QuestionsCategory>
implements QuestionsCategoryService{

    QuestionsCategoryDAO questionsCategoryDAO;


    public QuestionsCategoryServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.questionsCategoryDAO = getDataBaseHelper().getQuestionsCategoryDAO();
    }
    @Override
    public QuestionsCategory save(QuestionsCategory record) throws SQLException {
        record.setId((int) this.questionsCategoryDAO.insert(record));
        return record;
    }

    @Override
    public QuestionsCategory update(QuestionsCategory record) throws SQLException {
        this.questionsCategoryDAO.update(record);
        return record;
    }

    @Override
    public int delete(QuestionsCategory record) throws SQLException {
        return this.questionsCategoryDAO.delete(record);
    }

    @Override
    public List<QuestionsCategory> getAll() throws SQLException {
        return this.questionsCategoryDAO.queryForAll();
    }

    @Override
    public QuestionsCategory getById(int id) throws SQLException {
        return this.questionsCategoryDAO.queryForId(id);
    }

    @Override
    public void saveOrUpdateQuestionCategories(List<QuestionCategoryDTO> questionCategoryDTOS) throws SQLException {
        for (QuestionCategoryDTO questionCategoryDTO : questionCategoryDTOS) {
           this.saveOrUpdateQuestionCategory(questionCategoryDTO);
        }
    }

    @Override
    public QuestionsCategory saveOrUpdateQuestionCategory(QuestionCategoryDTO questionCategoryDTO) throws SQLException {
        return saveOrUpdateQuestionCategory(questionCategoryDTO.getQuestionCategory());
    }

    @Override
    public QuestionsCategory saveOrUpdateQuestionCategory(QuestionsCategory questionCategory) throws SQLException {
        QuestionsCategory qc = this.questionsCategoryDAO.getByUuid(questionCategory.getUuid());
        if(qc!=null) {
            questionCategory.setId(qc.getId());
            this.update(questionCategory);
        } else {
            this.save(questionCategory);
        }
        return questionCategory;
    }

    @Override
    public QuestionsCategory getByuuid(String uuid) throws SQLException {
        return questionsCategoryDAO.getByUuid(uuid);
    }
}
