package mz.org.csaude.mentoring.dao.evaluation;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;

@Dao
public interface EvaluationTypeDAO {

    @Query("SELECT * FROM evaluation_type WHERE code = :code LIMIT 1")
    EvaluationType getByCode(String code);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EvaluationType evaluationType);

    @Update
    void update(EvaluationType evaluationType);
}
