package mz.org.csaude.mentoring.dao.tutored;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.List;

import mz.org.csaude.mentoring.model.tutored.Tutored;

@Dao
public interface TutoredDao {

    @Query("SELECT COUNT(*) > 0 FROM tutored WHERE uuid = :uuid")
    boolean checkTutoredExistance(final String uuid);

    @Query("SELECT t.* FROM tutored t " +
            "JOIN employee e ON t.employee_id = e.id " +
            "JOIN location l ON e.id = l.employee_id " +
            "WHERE l.health_facility_id = :healthFacilityId " +
            "AND l.life_cycle_status = :lifeCycleStatus " +
            "AND t.life_cycle_status = :lifeCycleStatus " +
            "ORDER BY e.id")
    List<Tutored> getAllOfHealthFacility(final int healthFacilityId, final String lifeCycleStatus);

    @Query("SELECT t.* FROM tutored t " +
            "JOIN employee e ON t.employee_id = e.id " +
            "JOIN location l ON e.id = l.employee_id " +
            "WHERE l.health_facility_id = :healthFacilityId " +
            "AND l.life_cycle_status = :lifeCycleStatus " +
            "AND t.life_cycle_status = :lifeCycleStatus " +
            "AND t.zero_evaluation_status = 1 " +
            "ORDER BY e.id")
    List<Tutored> getAllOfHealthFacilityForNewRonda(final int healthFacilityId, final String lifeCycleStatus);

    @Query("SELECT * FROM tutored WHERE sync_status = :syncStatus")
    List<Tutored> getAllNotSynced(final String syncStatus);

    @Query("SELECT t.* FROM tutored t " +
            "JOIN ronda_mentee rm ON t.id = rm.mentee_id " +
            "WHERE rm.ronda_id = :rondaId")
    List<Tutored> getAllOfRonda(final int rondaId);

    @Query("SELECT t.* FROM tutored t " +
            "JOIN ronda_mentee rm ON t.id = rm.mentee_id " +
            "WHERE rm.ronda_id = :rondaId " +
            "AND t.zero_evaluation_status = 0")
    List<Tutored> getAllOfRondaForZeroEvaluation(final int rondaId);

    @Query("SELECT t.* FROM tutored t " +
            "JOIN employee e ON t.employee_id = e.id " +
            "JOIN location l ON e.id = l.employee_id " +
            "WHERE l.health_facility_id = :healthFacilityId " +
            "AND l.life_cycle_status = :lifeCycleStatus " +
            "AND t.life_cycle_status = :lifeCycleStatus " +
            "AND t.zero_evaluation_status = :zeroEvaluation " +
            "AND (:zeroEvaluation = 0 OR t.zero_evaluation_score IS NOT NULL) " +
            "ORDER BY e.id")
    List<Tutored> getAllForMentoringRound(final int healthFacilityId, final String lifeCycleStatus, final boolean zeroEvaluation);

    // Insert, update, delete methods
    @Insert
    long insert(Tutored tutored);

    @Insert
    void insertAll(List<Tutored> tutoredList);

    @Update
    void update(Tutored tutored);

    @Update
    void updateAll(List<Tutored> tutoredList);

    @Query("SELECT * FROM tutored WHERE uuid = :uuid LIMIT 1")
    Tutored getByUuid(String uuid);

    @Delete
    int delete(Tutored record);

    @Query("SELECT * FROM tutored WHERE id = :id")
    Tutored queryForId(int id);

    @Query("SELECT * FROM tutored")
    List<Tutored> queryForAll();

}
