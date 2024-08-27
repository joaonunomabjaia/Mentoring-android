package mz.org.csaude.mentoring.dao.professionalCategoryDAO;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import mz.org.csaude.mentoring.model.professionalCategory.ProfessionalCategory;

@Dao
public interface ProfessionalCategoryDAO {

    // Insert a ProfessionalCategory into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ProfessionalCategory professionalCategory);

    // Update an existing ProfessionalCategory in the database
    @Update
    void update(ProfessionalCategory professionalCategory);

    // Delete a ProfessionalCategory from the database
    @Delete
    int delete(ProfessionalCategory professionalCategory);

    // Get a ProfessionalCategory by its UUID
    @Query("SELECT * FROM professional_category WHERE uuid = :uuid LIMIT 1")
    ProfessionalCategory getByUuid(String uuid);

    // Check if a ProfessionalCategory exists by UUID
    @Query("SELECT EXISTS(SELECT 1 FROM professional_category WHERE uuid = :uuid LIMIT 1)")
    boolean checkExists(String uuid);

    // Delete a ProfessionalCategory by UUID
    @Query("DELETE FROM professional_category WHERE uuid = :uuid")
    void deleteByUuid(String uuid);

    // Get all ProfessionalCategories
    @Query("SELECT * FROM professional_category")
    List<ProfessionalCategory> getAll();

    // Delete all ProfessionalCategories
    @Query("DELETE FROM professional_category")
    void deleteAll();

    // Get all ProfessionalCategories
    @Query("SELECT * FROM professional_category")
    List<ProfessionalCategory> queryForAll();

    // Get a ProfessionalCategory by its ID
    @Query("SELECT * FROM professional_category WHERE id = :id")
    ProfessionalCategory queryForId(int id);

    // Get a ProfessionalCategory by its name
    @Query("SELECT * FROM professional_category WHERE description = :name")
    ProfessionalCategory queryForName(String name);
}
