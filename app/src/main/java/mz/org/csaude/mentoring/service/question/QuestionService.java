package mz.org.csaude.mentoring.service.question;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.dto.question.QuestionDTO;
import mz.org.csaude.mentoring.model.question.Question;

public interface QuestionService extends BaseService<Question> {

    void saveOrUpdateQuestions(List<QuestionDTO> questionDTOS) throws SQLException;

    void saveOrUpdateQuestionList(List<Question> questions) throws SQLException;

    Question saveOrUpdateQuestion(QuestionDTO questionDTO) throws SQLException;
}
