package mz.org.csaude.mentoring.dao.mentorship;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.mentorship.Door;

@Dao
public interface DoorDAO {

    @Insert
    long insertDoor(Door door);

    @Insert
    void insertDoors(List<Door> doors);

    @Update
    void updateDoor(Door door);

    @Query("SELECT * FROM door WHERE id = :id")
    Door getDoorById(int id);

    @Query("SELECT * FROM door")
    List<Door> getAllDoors();

    @Query("DELETE FROM door WHERE id = :id")
    void deleteDoorById(int id);

    @Query("SELECT * FROM door WHERE uuid = :uuid LIMIT 1")
    Door getByUuid(String uuid);

    @Query("SELECT * FROM door WHERE uuid = :uuid LIMIT 1")
    boolean checkDoorExistance(String uuid);

    @Query("DELETE FROM door WHERE id = :id")
    int delete(int id);

    @Query("SELECT * FROM door")
    List<Door> queryForAll();

    @Query("SELECT * FROM door WHERE id = :id LIMIT 1")
    Door queryForId(int id);


}
