package mz.org.csaude.mentoring.dao.ronda;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.Date;
import java.util.List;

import mz.org.csaude.mentoring.model.ronda.RondaMentee;

@Dao
public interface RondaMenteeDAO {

    @Insert
    long insert(RondaMentee rondaMentee);

    @Insert
    void insertAll(List<RondaMentee> rondaMentees);

    @Delete
    int delete(RondaMentee rondaMentee);

    @Query("DELETE FROM ronda_mentee WHERE ronda_id = :rondaId")
    void deleteByRonda(int rondaId);

    @Query("SELECT * FROM ronda_mentee WHERE ronda_id = :rondaId")
    List<RondaMentee> getAllOfRonda(int rondaId);

    @Update
    int update(RondaMentee record);

    @Query("SELECT * FROM ronda_mentee WHERE uuid = :uuid LIMIT 1")
    RondaMentee getByUuid(String uuid);

    @Query("SELECT * FROM ronda_mentee")
    List<RondaMentee> queryForAll();

    @Query("SELECT * FROM ronda_mentee WHERE id = :id LIMIT 1")
    RondaMentee queryForId(int id);

    @Query("UPDATE ronda_mentee SET end_date = :endDate WHERE ronda_id = :rondaId and end_date IS NULL")
    void closeAllActiveOnRonda(Integer rondaId, Date endDate);

    @Query("UPDATE ronda_mentee SET end_date = :endDate, sync_status = 'PENDING' WHERE ronda_id = :rondaId and mentee_id = :menteeId")
    void closeOneActiveOnRonda(Integer rondaId, Date endDate, Integer menteeId);

    @Query("SELECT * FROM ronda_mentee WHERE ronda_id = :rondaId and mentee_id = :tutoredId LIMIT 1")
    RondaMentee getByMenteeId(Integer tutoredId, Integer rondaId);
}
