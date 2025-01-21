package mz.org.csaude.mentoring.dao.section;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.form.Section;

@Dao
public interface SectionDAO {

    // Insert a single Section entity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Section section);

    // Insert a list of Section entities
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<Section> sections);

    // Update a Section entity
    @Update
    int update(Section section);

    // Delete a Section entity
    @Delete
    int delete(Section section);

    // Query to retrieve a Section by its id
    @Query("SELECT * FROM section WHERE id = :id")
    Section queryForId(int id);

    // Query to retrieve a Section by its uuid
    @Query("SELECT * FROM section WHERE uuid = :uuid LIMIT 1")
    Section getByUuid(String uuid);

    // Query to retrieve all Section entities
    @Query("SELECT * FROM section")
    List<Section> queryForAll();

}
