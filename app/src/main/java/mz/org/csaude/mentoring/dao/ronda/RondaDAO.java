package mz.org.csaude.mentoring.dao.ronda;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import mz.org.csaude.mentoring.model.ronda.Ronda;

@Dao
public interface RondaDAO {

    @Insert
    long insert(Ronda ronda);

    @Update
    void update(Ronda ronda);

    @Delete
    void delete(Ronda ronda);

    // Get all Ronda by HealthFacility and Mentor, joined with RondaMentor and RondaMentee
    @Query("SELECT r.* FROM ronda r " +
            "JOIN ronda_mentor rm ON r.id = rm.ronda_id " +
            "JOIN ronda_mentee rmt ON r.id = rmt.ronda_id " +
            "WHERE r.health_facility_id = :healthFacilityId " +
            "AND rm.mentor_id = :mentorId " +
            "AND r.life_cycle_status = :status " +
            "ORDER BY r.id")
    List<Ronda> getAllByHealthFacilityAndMentor(int healthFacilityId, int mentorId, String status);

    // Get all Ronda not synced
    @Query("SELECT * FROM ronda WHERE sync_status = :syncStatus AND life_cycle_status = 'ACTIVE'")
    List<Ronda> getAllNotSynced(String syncStatus);

    // Get all Ronda by RondaType
    @Query("SELECT r.* FROM ronda r " +
            "JOIN ronda_type rt ON r.ronda_type_id = rt.id " +
            "WHERE rt.code = :rondaTypeCode " +
            "AND r.life_cycle_status = :status " +
            "AND EXISTS (" +
            "  SELECT 1 FROM ronda_mentor rm " +
            "  WHERE rm.ronda_id = r.id AND rm.mentor_id = :mentorId " +
            ") " +
            "ORDER BY r.id")
    List<Ronda> getAllByRondaType(String rondaTypeCode, String status, int mentorId);

    // Get all Ronda by Mentor
    @Query("SELECT r.* FROM ronda r " +
            "JOIN ronda_mentor rm ON r.id = rm.ronda_id " +
            "WHERE rm.mentor_id = :mentorId " +
            "AND r.life_cycle_status = :status " +
            "ORDER BY r.start_date")
    List<Ronda> getAllByMentor(int mentorId, String status);

    @Query("SELECT * FROM ronda WHERE id = :id")
    Ronda queryForId(Integer id);

    @Query("SELECT * FROM ronda WHERE uuid = :uuid LIMIT 1")
    Ronda getByUuid(String uuid);

    @Query("SELECT * FROM ronda WHERE life_cycle_status = 'ACTIVE' ORDER BY start_date")
    List<Ronda> queryForAll();

    @Query("DELETE FROM ronda WHERE id = :id")
    void delete(Integer id);

    @Query("SELECT r.* FROM ronda r " +
            "JOIN ronda_type rt ON r.ronda_type_id = rt.id " +
            "WHERE rt.code = :code " +
            "AND r.life_cycle_status = :lifecycle " +
            "AND EXISTS ( " +
            "  SELECT 1 FROM ronda_mentor rm " +
            "  WHERE rm.ronda_id = r.id AND rm.mentor_id = :currMentorId " +
            ") " +
            "AND ( " +
            "  :query IS NULL OR TRIM(:query) = '' " +
            "  OR r.description LIKE '%' || :query || '%' COLLATE NOCASE " +
            ") " +
            "ORDER BY r.id")
    List<Ronda> search(String code, String query, String lifecycle, Integer currMentorId);

}
