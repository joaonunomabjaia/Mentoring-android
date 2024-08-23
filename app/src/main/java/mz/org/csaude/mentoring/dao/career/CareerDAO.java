package mz.org.csaude.mentoring.dao.career;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.career.Career;

@Dao
public interface CareerDAO {

    @Query("SELECT * FROM career WHERE uuid = :uuid LIMIT 1")
    Career findByUuid(String uuid);

    @Query("SELECT * FROM career WHERE career_type_id = :careerTypeId")
    List<Career> findByCareerType(int careerTypeId);

    @Query("SELECT COUNT(*) > 0 FROM career WHERE uuid = :uuid")
    boolean checkCareerExistance(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Career career);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Career> careers);

    @Update
    void update(Career career);

    @Update
    void updateAll(List<Career> careers);

    @Query("SELECT * FROM career WHERE uuid = :uuid LIMIT 1")
    Career getByUuid(String uuid);
}
