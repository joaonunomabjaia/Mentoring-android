package mz.org.csaude.mentoring.dao.form;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.programmaticArea.TutorProgrammaticArea;
import mz.org.csaude.mentoring.model.tutor.Tutor;

@Dao
public interface FormDAO {

    @Query("SELECT * FROM form WHERE sync_status = :syncStatus")
    List<Form> getAllBySyncStatus(String syncStatus);

    @Query("SELECT * FROM form WHERE sync_status = 'PENDING'")
    List<Form> getAllNotSynced();

    @Query("SELECT * FROM form WHERE sync_status = 'SENT'")
    List<Form> getAllSynced();

    @Query("SELECT f.* FROM form f " +
            "JOIN tutor_programmatic_area tpa ON f.programmatic_area_id = tpa.programmatic_area_id " +
            "WHERE tpa.tutor_id = :tutorId")
    List<Form> getAllOfTutor(int tutorId);

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    void insertForm(Form form);

    @Update
    void updateForm(Form form);

    @Query("SELECT * FROM form WHERE uuid = :uuid LIMIT 1")
    Form getByUuid(String uuid);
}
