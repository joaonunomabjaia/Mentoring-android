package mz.org.csaude.mentoring.dao.question;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import mz.org.csaude.mentoring.model.question.Question;

@Dao
public interface QuestionDAO {

    @Insert
    void insert(Question question);

    @Update
    void update(Question question);

    @Delete
    void delete(Question question);

    @Query("SELECT * FROM question WHERE id = :id")
    Question getById(int id);

    @Query("SELECT * FROM question WHERE uuid = :uuid LIMIT 1")
    Question getByUuid(String uuid);

    @Query("SELECT * FROM question")
    List<Question> getAll();
}
