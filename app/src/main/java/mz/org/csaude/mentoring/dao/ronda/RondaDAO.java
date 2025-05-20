package mz.org.csaude.mentoring.dao.ronda;

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
    @Query("SELECT * FROM ronda WHERE sync_status = :syncStatus")
    List<Ronda> getAllNotSynced(String syncStatus);

    // Get all Ronda by RondaType
    @Query("SELECT r.* FROM ronda r " +
            "JOIN ronda_type rt ON r.ronda_type_id = rt.id " +
            "JOIN ronda_mentor rm ON rm.ronda_id = r.id " +
            "WHERE rt.code = :rondaTypeCode " +
            "AND r.life_cycle_status = :status " +
            "AND rm.mentor_id = :mentorId AND rm.end_date IS NULL " +
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

    @Query("SELECT * FROM ronda")
    List<Ronda> queryForAll();

    @Query("DELETE FROM ronda WHERE id = :id")
    void delete(Integer id);
}
