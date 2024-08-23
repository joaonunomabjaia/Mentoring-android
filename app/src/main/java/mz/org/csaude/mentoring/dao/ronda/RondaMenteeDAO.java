package mz.org.csaude.mentoring.dao.ronda;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import mz.org.csaude.mentoring.model.ronda.RondaMentee;

@Dao
public interface RondaMenteeDAO {

    @Insert
    void insert(RondaMentee rondaMentee);

    @Insert
    void insertAll(List<RondaMentee> rondaMentees);

    @Delete
    void delete(RondaMentee rondaMentee);

    @Query("DELETE FROM ronda_mentee WHERE ronda_id = :rondaId")
    void deleteByRonda(int rondaId);

    @Query("SELECT * FROM ronda_mentee WHERE ronda_id = :rondaId")
    List<RondaMentee> getAllOfRonda(int rondaId);
}
