package mz.org.csaude.mentoring.dao.session;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.session.SessionRecommendedResource;

@Dao
public interface SessionRecommendedResourceDAO {

    @Transaction
    @Query("SELECT * FROM session_recommended_resource WHERE session_id = :sessionId")
    List<SessionRecommendedResource> findBySessionId(int sessionId);

    @Transaction
    @Query("SELECT * FROM session_recommended_resource WHERE tutor_id = :tutorId")
    List<SessionRecommendedResource> findByTutorId(int tutorId);

    @Transaction
    @Query("SELECT * FROM session_recommended_resource WHERE tutored_id = :tutoredId")
    List<SessionRecommendedResource> findByTutoredId(int tutoredId);

    @Query("SELECT DISTINCT srr.* FROM session_recommended_resource srr " +
            "JOIN session s ON srr.session_id = s.id " +
            "WHERE srr.sync_status = :syncStatus " +
            "AND s.sync_status = 'SENT'")
    List<SessionRecommendedResource> queryForAllPending(String syncStatus);

    @Transaction
    @Query("SELECT * FROM session_recommended_resource WHERE uuid = :uuid LIMIT 1")
    SessionRecommendedResource getByUuid(String uuid);

    @Insert
    long insert(SessionRecommendedResource sessionRecommendedResource);

    @Transaction
    @Update
    void update(SessionRecommendedResource sessionRecommendedResource);
}
