package mz.org.csaude.mentoring.dao.question;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import mz.org.csaude.mentoring.model.question.Question;

@Dao
public interface QuestionDAO {

    @Insert(onConflict = OnConflictStrategy.FAIL)
    long insert(Question question);

    @Update
    void update(Question question);

    @Delete
    int delete(Question question);

    @Query("SELECT * FROM question WHERE id = :id")
    Question getById(int id);

    @Query("SELECT * FROM question WHERE uuid = :uuid LIMIT 1")
    Question getByUuid(String uuid);

    @Query("SELECT * FROM question")
    List<Question> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createOrUpdate(Question question);

    @Query("SELECT * FROM question WHERE id = :id LIMIT 1")
    Question queryForId(int id);

    @Query("SELECT * FROM question WHERE uuid = :uuid LIMIT 1")
    Question queryForUuid(String uuid);

    @Query("SELECT * FROM question")
    List<Question> queryForAll();

}
