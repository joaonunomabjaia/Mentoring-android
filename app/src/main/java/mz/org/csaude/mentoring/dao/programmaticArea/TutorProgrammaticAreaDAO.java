package mz.org.csaude.mentoring.dao.programmaticArea;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import mz.org.csaude.mentoring.model.programmaticArea.TutorProgrammaticArea;
import mz.org.csaude.mentoring.model.tutor.Tutor;

@Dao
public interface TutorProgrammaticAreaDAO {

    @Insert
    void insert(TutorProgrammaticArea tutorProgrammaticArea);

    @Update
    void update(TutorProgrammaticArea tutorProgrammaticArea);

    @Delete
    void delete(TutorProgrammaticArea tutorProgrammaticArea);

    @Query("SELECT * FROM tutor_programmatic_area WHERE tutor_id = :tutorId")
    List<TutorProgrammaticArea> getAllOfTutor(int tutorId);

    @Query("SELECT * FROM tutor_programmatic_area WHERE id = :id")
    TutorProgrammaticArea getById(int id);

    @Query("SELECT * FROM tutor_programmatic_area WHERE uuid = :uuid LIMIT 1")
    TutorProgrammaticArea getByUuid(String uuid);
}
