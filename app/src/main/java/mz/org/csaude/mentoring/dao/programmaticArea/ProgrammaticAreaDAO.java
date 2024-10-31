package mz.org.csaude.mentoring.dao.programmaticArea;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Upsert;

import java.util.List;

import mz.org.csaude.mentoring.model.programmaticArea.ProgrammaticArea;

@Dao
public interface ProgrammaticAreaDAO {

    @Insert
    long insert(ProgrammaticArea programmaticArea);

    @Update
    void update(ProgrammaticArea programmaticArea);

    @Delete
    int delete(ProgrammaticArea programmaticArea);

    @Query("SELECT * FROM programmatic_area WHERE id = :id")
    ProgrammaticArea getById(int id);

    @Query("SELECT * FROM programmatic_area WHERE life_cycle_status = 'ACTIVE'")
    List<ProgrammaticArea> getAll();

    @Query("SELECT * FROM programmatic_area WHERE uuid = :uuid LIMIT 1")
    ProgrammaticArea getByUuid(String uuid);

    @Query("SELECT * FROM programmatic_area WHERE life_cycle_status = 'ACTIVE'")
    List<ProgrammaticArea> queryForAll();

    @Query("SELECT * FROM programmatic_area WHERE id = :id")
    ProgrammaticArea queryForId(int id);

    @Query("SELECT * FROM programmatic_area WHERE uuid = :uuid LIMIT 1")
    ProgrammaticArea queryForUuid(String uuid);

}
