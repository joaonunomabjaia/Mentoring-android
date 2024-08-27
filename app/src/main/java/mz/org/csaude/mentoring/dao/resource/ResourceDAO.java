package mz.org.csaude.mentoring.dao.resource;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import mz.org.csaude.mentoring.model.resourceea.Resource;

@Dao
public interface ResourceDAO {

    @Insert
    void insert(Resource resource);

    @Update
    void update(Resource resource);

    @Delete
    int delete(Resource resource);

    @Query("SELECT * FROM resources WHERE uuid = :uuid LIMIT 1")
    Resource getByUuid(String uuid);

    @Query("SELECT * FROM resources WHERE uuid = :uuid LIMIT 1")
    boolean checkResourceExistance(String uuid);

    @Query("SELECT * FROM resources")
    List<Resource> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createOrUpdate(Resource resource);

    @Query("SELECT * FROM resources")
    List<Resource> queryForAll();

    @Query("SELECT * FROM resources WHERE id = :id")
    Resource queryForId(int id);


}
