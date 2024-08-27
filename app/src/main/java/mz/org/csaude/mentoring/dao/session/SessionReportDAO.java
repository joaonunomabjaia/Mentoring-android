package mz.org.csaude.mentoring.dao.session;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.session.SessionReport;

@Dao
public interface SessionReportDAO {

    @Query("SELECT * FROM session_report WHERE session_id = :sessionId")
    List<SessionReport> findBySessionId(int sessionId);

    @Query("SELECT * FROM session_report WHERE tutored_id = :tutoredId")
    List<SessionReport> findByTutoredId(int tutoredId);

    @Query("SELECT * FROM session_report WHERE category = :category")
    List<SessionReport> findByCategory(String category);

    @Query("SELECT * FROM session_report WHERE uuid = :uuid LIMIT 1")
    SessionReport getByUuid(String uuid);

    @Insert
    long insert(SessionReport sessionReport);

    @Update
    void update(SessionReport sessionReport);
}
