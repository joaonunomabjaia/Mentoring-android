package mz.org.csaude.mentoring.dao.formSectionQuestion;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.formSectionQuestion.FormSectionQuestion;

@Dao
public interface FormSectionQuestionDAO {

    // Insert a single FormSectionQuestion. On conflict, IGNORE (can be changed to REPLACE or ABORT).
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(FormSectionQuestion formSectionQuestion);

    // Insert multiple FormSectionQuestion entities at once.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insert(List<FormSectionQuestion> formSectionQuestions);

    // Update a FormSectionQuestion record.
    @Update
    void update(FormSectionQuestion formSectionQuestion);

    // Delete a single FormSectionQuestion record.
    @Delete
    int delete(FormSectionQuestion formSectionQuestion);

    // Delete all FormSectionQuestion records related to a specific formSectionId.
    @Query("DELETE FROM form_section_question WHERE form_section_id = :formSectionId")
    void deleteByFormSectionId(int formSectionId);

    @Query("SELECT * FROM form_section_question WHERE form_section_id = :formSectionId AND life_cycle_status = :lifeCycleStatus " +
            "AND evaluation_type_id IN (SELECT id FROM evaluation_type WHERE code = :evaluationType OR code = 'Ambos') " +
            "AND evaluation_location_id IN (SELECT id FROM evaluation_location WHERE id = :evaluationLocationId OR code = 'BOTH') " +
            "ORDER BY sequence ASC")
    List<FormSectionQuestion> getAllOfFormSection(int formSectionId, String evaluationType, String lifeCycleStatus, int evaluationLocationId);

    // Retrieve a FormSectionQuestion by its uuid.
    @Query("SELECT * FROM form_section_question WHERE uuid = :uuid LIMIT 1")
    FormSectionQuestion getByUuid(String uuid);

    // Retrieve a FormSectionQuestion by its id.
    @Query("SELECT * FROM form_section_question WHERE id = :id")
    FormSectionQuestion queryForId(int id);

    // Retrieve all FormSectionQuestion records.
    @Query("SELECT * FROM form_section_question")
    List<FormSectionQuestion> queryForAll();
}
