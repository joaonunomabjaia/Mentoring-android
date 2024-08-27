package mz.org.csaude.mentoring.dao.evaluation;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;

@Dao
public interface EvaluationTypeDAO {

    @Query("SELECT * FROM evaluation_type WHERE code = :code LIMIT 1")
    EvaluationType getByCode(String code);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EvaluationType evaluationType);

    @Update
    void update(EvaluationType evaluationType);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createOrUpdate(EvaluationType evaluationType);

    @Query("SELECT * FROM evaluation_type WHERE uuid = :uuid LIMIT 1")
    EvaluationType getByUuid(String uuid);

    @Query("DELETE FROM evaluation_type WHERE id = :id")
    int delete(int id);

    @Query("SELECT * FROM evaluation_type")
    List<EvaluationType> queryForAll();

    @Query("SELECT * FROM evaluation_type WHERE id = :id LIMIT 1")
    EvaluationType queryForId(int id);


}
