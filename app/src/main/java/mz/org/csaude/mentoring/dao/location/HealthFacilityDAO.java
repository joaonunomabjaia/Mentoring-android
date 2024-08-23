package mz.org.csaude.mentoring.dao.location;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.dao.MentoringBaseDao;
import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.tutor.Tutor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.model.location.HealthFacility;

@Dao
public interface HealthFacilityDAO {

    @Insert
    void insert(HealthFacility healthFacility);

    @Update
    void update(HealthFacility healthFacility);

    @Delete
    void delete(HealthFacility healthFacility);

    @Query("SELECT EXISTS(SELECT 1 FROM health_facility WHERE uuid = :uuid LIMIT 1)")
    boolean checkHealthFacilityExistence(String uuid);

    @Query("SELECT * FROM health_facility WHERE district_id = :districtId")
    List<HealthFacility> getHealthFacilityByDistrict(int districtId);

    @Query("SELECT * FROM health_facility WHERE district_id = :districtId AND uuid IN (:uuids)")
    List<HealthFacility> getHealthFacilityByDistrictAndMentor(int districtId, List<String> uuids);
}
