package mz.org.csaude.mentoring.dao.session;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.List;

import mz.org.csaude.mentoring.model.session.SessionStatus;

@Dao
public interface SessionStatusDAO {

    @Query("SELECT * FROM session_status WHERE code = :code LIMIT 1")
    SessionStatus getByCode(String code);

    @Query("SELECT * FROM session_status WHERE uuid = :uuid LIMIT 1")
    SessionStatus getByUuid(String uuid);

    @Insert
    long insert(SessionStatus sessionStatus);

    @Update
    int update(SessionStatus sessionStatus);

    @Query("DELETE FROM session_status WHERE id = :id")
    int deleteById(int id);

    @Query("SELECT * FROM session_status")
    List<SessionStatus> getAllSessionStatuses();

    @Query("SELECT * FROM session_status WHERE id = :id")
    SessionStatus getSessionStatusById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createOrUpdate(SessionStatus sessionStatus);

    @Query("DELETE FROM session_status")
    int deleteAll();

    @Query("DELETE FROM session_status WHERE id = :id")
    int delete(int id);


    @Query("SELECT * FROM session_status")
    List<SessionStatus> queryForAll();

    @Query("SELECT * FROM session_status WHERE id = :id")
    SessionStatus queryForId(int id);


}
