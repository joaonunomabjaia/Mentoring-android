package mz.org.csaude.mentoring.dao.location;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Upsert;

import java.util.List;

import mz.org.csaude.mentoring.model.location.District;

@Dao
public interface DistrictDAO {

    @Insert
    long insert(District district);

    @Update
    void update(District district);

    @Delete
    int delete(District district);

    @Query("SELECT * FROM district WHERE uuid = :uuid LIMIT 1")
    boolean checkDistrictExistence(String uuid);

    @Query("SELECT * FROM district WHERE province_id = :provinceId")
    List<District> getByProvince(int provinceId);

    @Query("SELECT * FROM district WHERE province_id = :provinceId AND uuid IN (:districtUuids)")
    List<District> getByProvinceAndMentor(int provinceId, List<String> districtUuids);

    @Query("SELECT * FROM district WHERE uuid = :uuid LIMIT 1")
    District getByUuid(String uuid);

    @Query("SELECT * FROM district")
    List<District> queryForAll();

    @Query("SELECT * FROM district WHERE id = :id")
    District queryForId(int id);


}
