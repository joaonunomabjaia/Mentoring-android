package mz.org.csaude.mentoring.dao.mentorship;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.mentorship.IterationType;

@Dao
public interface IterationTypeDAO {

    @Insert
    void insertIterationType(IterationType iterationType);

    @Insert
    void insertIterationTypes(List<IterationType> iterationTypes);

    @Update
    void updateIterationType(IterationType iterationType);

    @Query("SELECT * FROM iteration_type WHERE code = :code LIMIT 1")
    IterationType getByCode(String code);

    @Query("SELECT * FROM iteration_type")
    List<IterationType> getAllIterationTypes();

    @Query("DELETE FROM iteration_type WHERE id = :id")
    void deleteIterationTypeById(int id);

    @Query("SELECT * FROM iteration_type WHERE uuid = :uuid LIMIT 1")
    IterationType getByUuid(String uuid);

    @Query("SELECT * FROM iteration_type WHERE id = :id LIMIT 1")
    IterationType queryForId(int id);

    @Query("SELECT * FROM iteration_type")
    List<IterationType> queryForAll();

    @Query("DELETE FROM iteration_type WHERE id = :id")
    int delete(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createOrUpdate(IterationType iterationType);
}
