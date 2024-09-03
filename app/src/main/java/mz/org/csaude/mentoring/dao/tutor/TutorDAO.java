package mz.org.csaude.mentoring.dao.tutor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.tutor.Tutor;

@Dao
public interface TutorDAO {

    @Insert
    long insert(Tutor tutor);

    @Update
    void update(Tutor tutor);

    @Query("SELECT * FROM tutor WHERE uuid = :uuid LIMIT 1")
    Tutor getByUuid(String uuid);

    @Query("SELECT * FROM tutor WHERE employee_id = :employeeId LIMIT 1")
    Tutor getByEmployee(int employeeId);

    @Query("SELECT COUNT(*) > 0 FROM tutor WHERE uuid = :uuid")
    boolean checkTutorExistance(String uuid);

    @Query("SELECT * FROM tutor")
    List<Tutor> queryForAll();

    @Query("SELECT * FROM tutor WHERE id = :id")
    Tutor queryForId(int id);

    @Query("DELETE FROM tutor WHERE id = :id")
    int delete(int id);


}
