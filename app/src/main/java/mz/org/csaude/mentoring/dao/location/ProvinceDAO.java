package mz.org.csaude.mentoring.dao.location;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

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

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insertProvinces(List<Province> provinces);

    @Query("SELECT * FROM province WHERE uuid = :uuid LIMIT 1")
    Province getByUuid(String uuid);

    @Update
    int update(Province record);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createOrUpdate(Province province);

    @Query("SELECT * FROM province")
    List<Province> queryForAll();

    @Query("SELECT * FROM province WHERE id = :id LIMIT 1")
    Province queryForId(int id);


    @Query("DELETE FROM province WHERE id = :id")
    int delete(int id);


}
