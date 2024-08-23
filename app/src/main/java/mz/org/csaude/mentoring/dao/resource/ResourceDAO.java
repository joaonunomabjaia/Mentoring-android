package mz.org.csaude.mentoring.dao.resource;

import androidx.room.Dao;
import androidx.room.Insert;
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
    void delete(Resource resource);

    @Query("SELECT * FROM resources WHERE uuid = :uuid LIMIT 1")
    Resource getByUuid(String uuid);

    @Query("SELECT * FROM resources WHERE uuid = :uuid LIMIT 1")
    boolean checkResourceExistance(String uuid);

    @Query("SELECT * FROM resources")
    List<Resource> getAll();
}
