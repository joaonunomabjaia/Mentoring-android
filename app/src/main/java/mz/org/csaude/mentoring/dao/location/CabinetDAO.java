package mz.org.csaude.mentoring.dao.location;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import mz.org.csaude.mentoring.model.location.Cabinet;

@Dao
public interface CabinetDAO {

    @Insert
    long insert(Cabinet cabinet);

    @Update
    void update(Cabinet cabinet);

    @Delete
    int delete(Cabinet cabinet);

    @Query("SELECT * FROM cabinet WHERE uuid = :uuid LIMIT 1")
    Cabinet getByUuid(String uuid);

    @Query("SELECT * FROM cabinet WHERE uuid = :uuid LIMIT 1")
    boolean checkCabinetExistance(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createOrUpdate(Cabinet cabinet);

    @Query("SELECT * FROM cabinet")
    List<Cabinet> queryForAll();

    @Query("SELECT * FROM cabinet WHERE id = :id LIMIT 1")
    Cabinet queryForId(int id);

}
