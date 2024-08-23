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
    void insert(Ronda ronda);

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
    List<Ronda> getAllByHealthFacilityAndMentor(int healthFacilityId, int mentorId, int status);

    // Get all Ronda not synced
    @Query("SELECT * FROM ronda WHERE sync_status = :syncStatus")
    List<Ronda> getAllNotSynced(int syncStatus);

    // Get all Ronda by RondaType
    @Query("SELECT r.* FROM ronda r " +
            "JOIN ronda_type rt ON r.ronda_type_id = rt.id " +
            "WHERE rt.code = :rondaTypeCode " +
            "AND r.life_cycle_status = :status " +
            "ORDER BY r.id")
    List<Ronda> getAllByRondaType(String rondaTypeCode, int status);

    // Get all Ronda by Mentor
    @Query("SELECT r.* FROM ronda r " +
            "JOIN ronda_mentor rm ON r.id = rm.ronda_id " +
            "WHERE rm.mentor_id = :mentorId " +
            "AND r.life_cycle_status = :status " +
            "ORDER BY r.start_date")
    List<Ronda> getAllByMentor(int mentorId, int status);
}
