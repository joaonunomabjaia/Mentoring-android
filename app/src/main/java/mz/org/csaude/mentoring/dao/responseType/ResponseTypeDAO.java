package mz.org.csaude.mentoring.dao.responseType;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import mz.org.csaude.mentoring.model.responseType.ResponseType;

@Dao
public interface ResponseTypeDAO {

    @Insert
    void insert(ResponseType responseType);

    @Update
    void update(ResponseType responseType);

    @Delete
    int delete(ResponseType responseType);

    @Query("SELECT * FROM response_type WHERE id = :id LIMIT 1")
    ResponseType getById(int id);

    @Query("SELECT * FROM response_type WHERE uuid = :uuid LIMIT 1")
    ResponseType getByUuid(String uuid);

    @Query("SELECT * FROM response_type")
    List<ResponseType> getAll();

    @Query("SELECT * FROM response_type")
    List<ResponseType> queryForAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ResponseType createOrUpdate(ResponseType responseType);

    @Query("SELECT * FROM response_type WHERE id = :id LIMIT 1")
    ResponseType queryForId(int id);

}
