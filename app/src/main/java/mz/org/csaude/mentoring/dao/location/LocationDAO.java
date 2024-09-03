package mz.org.csaude.mentoring.dao.location;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.List;

import mz.org.csaude.mentoring.model.location.Location;

@Dao
public interface LocationDAO {

    @Query("SELECT * FROM location WHERE uuid = :uuid")
    List<Location> checkLocation(String uuid);

    @Query("SELECT * FROM location WHERE employee_id = :employeeId")
    List<Location> getAllOfEmployee(int employeeId);

    @Insert
    void insertLocation(Location location);

    @Insert
    void insertLocations(List<Location> locations);

    @Update
    int update(Location location);

    @Query("SELECT * FROM location")
    List<Location> queryForAll();

    @Query("SELECT * FROM location WHERE id = :id")
    Location queryForId(int id);

    @Query("SELECT * FROM location WHERE uuid = :uuid")
    Location getByUuid(String uuid);

    @Query("DELETE FROM location WHERE id = :id")
    int delete(int id);


}
