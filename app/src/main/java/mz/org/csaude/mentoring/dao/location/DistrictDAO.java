package mz.org.csaude.mentoring.dao.location;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.model.location.Province;

@Dao
public interface DistrictDAO {

    @Insert
    void insert(District district);

    @Update
    void update(District district);

    @Delete
    void delete(District district);

    @Query("SELECT * FROM district WHERE uuid = :uuid LIMIT 1")
    boolean checkDistrictExistence(String uuid);

    @Query("SELECT * FROM district WHERE province_id = :provinceId")
    List<District> getByProvince(int provinceId);

    @Query("SELECT * FROM district WHERE province_id = :provinceId AND uuid IN (:districtUuids)")
    List<District> getByProvinceAndMentor(int provinceId, List<String> districtUuids);
}
