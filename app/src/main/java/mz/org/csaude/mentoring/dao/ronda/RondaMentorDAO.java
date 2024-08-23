package mz.org.csaude.mentoring.dao.ronda;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import mz.org.csaude.mentoring.model.ronda.RondaMentor;

@Dao
public interface RondaMentorDAO {

    @Insert
    void insert(RondaMentor rondaMentor);

    @Insert
    void insertAll(List<RondaMentor> rondaMentors);

    @Delete
    void delete(RondaMentor rondaMentor);

    @Query("SELECT * FROM ronda_mentor WHERE ronda_id = :rondaId")
    List<RondaMentor> getRondaMentors(int rondaId);

    @Query("DELETE FROM ronda_mentor WHERE ronda_id = :rondaId")
    void deleteByRonda(int rondaId);
}
