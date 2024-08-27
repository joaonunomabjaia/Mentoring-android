package mz.org.csaude.mentoring.dao.location;

import java.util.List;

import mz.org.csaude.mentoring.model.location.HealthFacility;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

@Dao
public interface HealthFacilityDAO {

    @Insert
    void insert(HealthFacility healthFacility);

    @Update
    void update(HealthFacility healthFacility);

    @Delete
    int delete(HealthFacility healthFacility);

    @Query("SELECT EXISTS(SELECT 1 FROM health_facility WHERE uuid = :uuid LIMIT 1)")
    boolean checkHealthFacilityExistence(String uuid);

    @Query("SELECT * FROM health_facility WHERE district_id = :districtId")
    List<HealthFacility> getHealthFacilityByDistrict(int districtId);

    @Query("SELECT * FROM health_facility WHERE district_id = :districtId AND uuid IN (:uuids)")
    List<HealthFacility> getHealthFacilityByDistrictAndMentor(int districtId, List<String> uuids);

    @Query("SELECT * FROM health_facility WHERE uuid = :uuid LIMIT 1")
    HealthFacility getByUuid(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createOrUpdate(HealthFacility healthFacility);

    @Query("SELECT * FROM health_facility")
    List<HealthFacility> queryForAll();

    @Query("SELECT * FROM health_facility WHERE id = :id LIMIT 1")
    HealthFacility queryForId(int id);

    @Query("SELECT * FROM health_facility WHERE uuid = :uuid LIMIT 1")
    HealthFacility queryForUuid(String uuid);

}
