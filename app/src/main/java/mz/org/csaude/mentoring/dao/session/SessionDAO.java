package mz.org.csaude.mentoring.dao.session;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

    @Query("SELECT * FROM session WHERE sync_status = :syncStatus")
    List<Session> getAllNotSynced(String syncStatus);

    @Query("SELECT * FROM session WHERE uuid = :uuid LIMIT 1")
    Session getByUuid(String uuid);

    @Insert
    void insert(Session session);

    @Update
    void update(Session session);

    @Query("DELETE FROM session WHERE id = :id")
    void deleteById(Integer id);
}
