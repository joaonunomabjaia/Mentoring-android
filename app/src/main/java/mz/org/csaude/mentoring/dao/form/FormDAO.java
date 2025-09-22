package mz.org.csaude.mentoring.dao.form;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

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
            "WHERE tpa.tutor_id = :tutorId and f.life_cycle_status = 'ACTIVE'")
    List<Form> getAllOfTutor(int tutorId);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    long insertForm(Form form);

    @Update
    void updateForm(Form form);

    @Query("SELECT * FROM form WHERE uuid = :uuid LIMIT 1")
    Form getByUuid(String uuid);

    @Query("DELETE FROM form WHERE id = :id")
    int delete(int id);

    @Query("SELECT * FROM form WHERE id = :id LIMIT 1")
    Form queryForId(int id);

    @Query("SELECT * FROM form")
    List<Form> queryForAll();

    @Query("UPDATE form SET sync_status = :syncStatus WHERE id = :id")
    void updateSyncStatus(int id, String syncStatus);


    @Query("SELECT COUNT(*) > 0 FROM form_section_question fsq " +
            "JOIN form_section fs ON fsq.form_section_id=fs.id WHERE fs.form_id = :formId AND fsq.evaluation_location_id = :evaluationLocationId")
    boolean hasQuestionsForSelectedLocation(Integer formId, Integer evaluationLocationId);
}
