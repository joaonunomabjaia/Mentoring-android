package mz.org.csaude.mentoring.dao.formSection;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.form.FormSection;

@Dao
public interface FormSectionDAO {

    // Insert a single FormSection entity.
    // Replace in case of conflict (can be changed to IGNORE or ABORT as needed).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(FormSection formSection);

    // Insert multiple FormSection entities at once.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<FormSection> formSections);

    // Update an existing FormSection entity.
    @Update
    void update(FormSection formSection);

    // Delete a single FormSection entity.
    @Delete
    int delete(FormSection formSection);

    // Query to retrieve all FormSection records.
    @Query("SELECT * FROM " + FormSection.TABLE_NAME)
    List<FormSection> getAllFormSections();

    // Query to retrieve a specific FormSection by its id.
    @Query("SELECT * FROM " + FormSection.TABLE_NAME + " WHERE id = :id")
    FormSection getFormSectionById(long id);

    // Query to retrieve a specific FormSection by its uuid.
    @Query("SELECT * FROM " + FormSection.TABLE_NAME + " WHERE uuid = :uuid")
    FormSection getFormSectionByUuid(String uuid);

    // Query to retrieve FormSections based on formId.
    @Query("SELECT * FROM " + FormSection.TABLE_NAME + " WHERE form_id = :formId ORDER BY sequence ASC")
    List<FormSection> getFormSectionsByFormId(int formId);

    // Query to retrieve FormSections based on sectionId.
    @Query("SELECT * FROM " + FormSection.TABLE_NAME + " WHERE section_id = :sectionId")
    List<FormSection> getFormSectionsBySectionId(int sectionId);

    // Delete all FormSections for a specific formId (cascading deletion can handle most of this).
    @Query("DELETE FROM " + FormSection.TABLE_NAME + " WHERE form_id = :formId")
    void deleteFormSectionsByFormId(int formId);

    // Query to retrieve a FormSection by its id
    @Query("SELECT * FROM form_section WHERE id = :id")
    FormSection queryForId(int id);

    // Query to retrieve all FormSection entities
    @Query("SELECT * FROM form_section")
    List<FormSection> queryForAll();

    // Query to retrieve a FormSection by its uuid
    @Query("SELECT * FROM form_section WHERE uuid = :uuid LIMIT 1")
    FormSection getByUuid(String uuid);
}
