package mz.org.csaude.mentoring.dao.mentorship;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Transaction;
import androidx.room.Upsert;

import java.util.List;

import mz.org.csaude.mentoring.model.mentorship.Mentorship;

@Dao
public interface MentorshipDAO {

    @Insert
    long insertMentorship(Mentorship mentorship);

    @Insert
    void insertAll(List<Mentorship> mentorships);

    @Update
    void updateMentorship(Mentorship mentorship);

    @Query("SELECT * FROM mentorship WHERE tutor_id = (SELECT id FROM tutor WHERE uuid = :uuidTutor)")
    List<Mentorship> getMentorshipByTutor(String uuidTutor);

    @Query("SELECT * FROM mentorship WHERE sync_status = :syncStatus AND end_date IS NOT NULL")
    List<Mentorship> getAllNotSynced(String syncStatus);

    @Transaction
    @Query("SELECT * FROM mentorship WHERE session_id IN (SELECT id FROM session WHERE ronda_id = :rondaId)")
    List<Mentorship> getAllOfRonda(int rondaId);

    @Query("SELECT * FROM mentorship WHERE session_id = :sessionId")
    List<Mentorship> getAllOfSession(int sessionId);

    @Query("SELECT * FROM mentorship WHERE uuid = :uuid LIMIT 1")
    Mentorship getByUuid(String uuid);

    @Query("SELECT * FROM mentorship")
    List<Mentorship> queryForAll();

    @Query("SELECT * FROM mentorship WHERE id = :id")
    Mentorship queryForId(int id);

    @Query("DELETE FROM mentorship WHERE uuid = :uuid")
    void deleteMentorshipByUuid(String uuid);

    @Query("DELETE FROM mentorship")
    void deleteAll();


    @Query("DELETE FROM mentorship WHERE id = :id")
    int delete(int id);
}
