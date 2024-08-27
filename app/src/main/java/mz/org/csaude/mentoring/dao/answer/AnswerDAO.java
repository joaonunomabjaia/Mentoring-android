package mz.org.csaude.mentoring.dao.answer;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.answer.Answer;

@Dao
public interface AnswerDAO {

    @Query("SELECT * FROM answer WHERE mentorship_id = :mentorshipId")
    List<Answer> queryForMentorship(int mentorshipId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Answer answer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Answer> answers);

    @Update
    void update(Answer answer);

    @Update
    void updateAll(List<Answer> answers);

    @Query("SELECT * FROM answer WHERE uuid = :uuid LIMIT 1")
    Answer getByUuid(String uuid);


    @Query("SELECT * FROM answer WHERE id = :id LIMIT 1")
    Answer queryForId(int id);

    @Query("DELETE FROM answer WHERE id = :id")
    int delete(int id);

    @Query("SELECT * FROM answer")
    List<Answer> queryForAll();
}
