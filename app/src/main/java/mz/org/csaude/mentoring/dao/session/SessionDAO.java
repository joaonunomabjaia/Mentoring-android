package mz.org.csaude.mentoring.dao.session;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.Date;
import java.util.List;

import mz.org.csaude.mentoring.model.session.Session;

@Dao
public interface SessionDAO {

    @Query("SELECT * FROM session WHERE ronda_id = :rondaId AND mentee_id = :menteeId")
    List<Session> queryForAllOfRondaAndMentee(Integer rondaId, Integer menteeId);

    @Query("SELECT * FROM session WHERE ronda_id = :rondaId")
    List<Session> queryForAllOfRonda(Integer rondaId);

    @Query("SELECT * FROM session WHERE ronda_id = :rondaId AND start_date = :startDate AND end_date IS NULL ORDER BY start_date ASC")
    List<Session> getAllOfRondaPending(Integer rondaId, Date startDate);

    @Query("SELECT s.* FROM session s " +
            "JOIN session_status ss ON s.session_status_id = ss.id " +
            "WHERE s.sync_status = :syncStatus " +
            "AND ss.code = 'COMPLETE'")
    List<Session> getAllNotSynced(String syncStatus);


    @Query("SELECT * FROM session WHERE uuid = :uuid LIMIT 1")
    Session getByUuid(String uuid);

    @Insert
    long insert(Session session);

    @Update
    void update(Session session);

    @Query("DELETE FROM session WHERE id = :id")
    int delete(Integer id);

    @Query("SELECT * FROM session")
    List<Session> queryForAll();

    @Query("SELECT * FROM session WHERE id = :id")
    Session queryForId(Integer id);

    @Query("SELECT count(*) FROM session WHERE ronda_id = :rondaId AND mentee_id = :menteeId")
    int countAllOfRondaAndMentee(Integer rondaId, Integer menteeId);
}
