package mz.org.csaude.mentoring.dao.evaluationLocation;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.evaluationLocation.EvaluationLocation;

@Dao
public interface EvaluationLocationDAO {

    @Insert
    long insert(EvaluationLocation evaluationLocation);

    @Insert
    void insertAll(List<EvaluationLocation> evaluationLocations);

    @Update
    int update(EvaluationLocation evaluationLocation);

    @Query("SELECT * FROM evaluation_location WHERE id = :id")
    EvaluationLocation queryForId(long id);

    @Query("SELECT * FROM evaluation_location WHERE uuid = :uuid")
    EvaluationLocation getByUuid(String uuid);

    @Query("SELECT * FROM evaluation_location WHERE code = :code")
    EvaluationLocation findEvaluationLocationByCode(String code);

    @Query("SELECT * FROM evaluation_location")
    List<EvaluationLocation> queryForAll();

    @Query("DELETE FROM evaluation_location WHERE id = :id")
    int delete(long id);

    @Query("DELETE FROM evaluation_location")
    void deleteAll();
}
