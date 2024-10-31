package mz.org.csaude.mentoring.dao.program;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Upsert;

import java.util.List;

import mz.org.csaude.mentoring.model.program.Program;

@Dao
public interface ProgramDAO {

    @Insert
    long insert(Program program);

    @Update
    void update(Program program);

    @Delete
    int delete(Program program);

    @Query("SELECT * FROM program WHERE id = :id")
    Program getById(int id);

    @Query("SELECT * FROM program WHERE life_cycle_status = 'ACTIVE'")
    List<Program> getAll();

    @Query("SELECT * FROM program WHERE uuid = :uuid LIMIT 1")
    Program getByUuid(String uuid);

    @Query("SELECT * FROM program WHERE life_cycle_status = 'ACTIVE'")
    List<Program> queryForAll();

    @Query("SELECT * FROM program WHERE id = :id LIMIT 1")
    Program queryForId(int id);

    @Query("SELECT * FROM program WHERE uuid = :uuid LIMIT 1")
    Program queryForUuid(String uuid);

    @Query("SELECT * FROM program WHERE name = :name LIMIT 1")
    Program queryForName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createOrUpdate(Program program);
}
