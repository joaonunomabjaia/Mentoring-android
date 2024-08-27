package mz.org.csaude.mentoring.dao.career;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.career.CareerType;

@Dao
public interface CareerTypeDAO {

    @Query("SELECT * FROM career_type WHERE code = :code LIMIT 1")
    CareerType findByCode(String code);

    @Query("SELECT COUNT(*) > 0 FROM career_type WHERE code = :code")
    boolean checkCareerTypeExistance(String code);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CareerType careerType);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CareerType> careerTypes);

    @Update
    void update(CareerType careerType);

    @Update
    void updateAll(List<CareerType> careerTypes);
}
