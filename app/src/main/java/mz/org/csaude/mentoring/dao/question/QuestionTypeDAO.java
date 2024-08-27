package mz.org.csaude.mentoring.dao.question;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import mz.org.csaude.mentoring.model.question.QuestionType;

@Dao
public interface QuestionTypeDAO {

    @Insert
    long insert(QuestionType questionType);

    @Update
    void update(QuestionType questionType);

    @Delete
    int delete(QuestionType questionType);

    @Query("SELECT * FROM question_type WHERE id = :id")
    QuestionType getById(int id);

    @Query("SELECT * FROM question_type WHERE uuid = :uuid LIMIT 1")
    QuestionType getByUuid(String uuid);

    @Query("SELECT * FROM question_type")
    List<QuestionType> getAll();

    @Query("SELECT * FROM question_type")
    List<QuestionType> queryForAll();

    @Query("SELECT * FROM question_type WHERE id = :id")
    QuestionType queryForId(int id);


}
