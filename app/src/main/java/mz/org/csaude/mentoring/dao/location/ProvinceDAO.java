package mz.org.csaude.mentoring.dao.location;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import java.util.List;

import mz.org.csaude.mentoring.model.location.Province;

@Dao
public interface ProvinceDAO {

    @Query("SELECT COUNT(*) > 0 FROM province WHERE uuid = :uuid")
    boolean checkProvinceExistance(String uuid);

    @Query("SELECT * FROM province WHERE uuid IN (:provinceUuids)")
    List<Province> getAllOfTutor(List<String> provinceUuids);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProvince(Province province);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProvinces(List<Province> provinces);
}
