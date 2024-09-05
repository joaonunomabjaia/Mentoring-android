package mz.org.csaude.mentoring.dao.ronda;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.List;

import mz.org.csaude.mentoring.model.ronda.RondaMentor;

@Dao
public interface RondaMentorDAO {

    @Insert
    long insert(RondaMentor rondaMentor);

    @Insert
    void insertAll(List<RondaMentor> rondaMentors);

    @Delete
    int delete(RondaMentor rondaMentor);

    @Query("SELECT * FROM ronda_mentor WHERE ronda_id = :rondaId")
    List<RondaMentor> getRondaMentors(int rondaId);

    @Query("DELETE FROM ronda_mentor WHERE ronda_id = :rondaId")
    void deleteByRonda(int rondaId);

    @Update
    void update(RondaMentor record);

    @Query("SELECT * FROM ronda_mentor WHERE uuid = :uuid LIMIT 1")
    RondaMentor getByUuid(String uuid);

    @Query("SELECT * FROM ronda_mentor")
    List<RondaMentor> getAll();

    @Query("SELECT * FROM ronda_mentor")
    List<RondaMentor> queryForAll();

    @Query("SELECT * FROM ronda_mentor WHERE id = :id LIMIT 1")
    RondaMentor queryForId(int id);
}
