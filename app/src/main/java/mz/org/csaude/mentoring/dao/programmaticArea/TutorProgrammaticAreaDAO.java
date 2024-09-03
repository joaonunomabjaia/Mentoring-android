package mz.org.csaude.mentoring.dao.programmaticArea;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Upsert;

import java.util.List;

import mz.org.csaude.mentoring.model.programmaticArea.TutorProgrammaticArea;

@Dao
public interface TutorProgrammaticAreaDAO {

    @Insert
    long insert(TutorProgrammaticArea tutorProgrammaticArea);

    @Update
    void update(TutorProgrammaticArea tutorProgrammaticArea);

    @Delete
    int delete(TutorProgrammaticArea tutorProgrammaticArea);

    @Query("SELECT * FROM tutor_programmatic_area WHERE tutor_id = :tutorId")
    List<TutorProgrammaticArea> getAllOfTutor(int tutorId);

    @Query("SELECT * FROM tutor_programmatic_area WHERE id = :id")
    TutorProgrammaticArea getById(int id);

    @Query("SELECT * FROM tutor_programmatic_area WHERE uuid = :uuid LIMIT 1")
    TutorProgrammaticArea getByUuid(String uuid);

    @Query("SELECT * FROM tutor_programmatic_area")
    List<TutorProgrammaticArea> queryForAll();

    @Query("SELECT * FROM tutor_programmatic_area WHERE id = :id")
    TutorProgrammaticArea queryForId(int id);

    @Query("SELECT * FROM tutor_programmatic_area WHERE uuid = :uuid LIMIT 1")
    TutorProgrammaticArea queryForUuid(String uuid);

}
