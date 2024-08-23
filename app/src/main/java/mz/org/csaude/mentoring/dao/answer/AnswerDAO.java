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
    void insert(Answer answer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Answer> answers);

    @Update
    void update(Answer answer);

    @Update
    void updateAll(List<Answer> answers);
}
