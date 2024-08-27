package mz.org.csaude.mentoring.dao.rondatype;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.rondatype.RondaType;

@Dao
public interface RondaTypeDAO {

    @Insert
    void insert(RondaType rondaType);

    @Update
    void update(RondaType rondaType);

    @Query("SELECT * FROM ronda_type WHERE code = :code LIMIT 1")
    RondaType getRondaTypeByCode(String code);

    @Query("SELECT * FROM ronda_type WHERE uuid = :uuid LIMIT 1")
    RondaType getByUuid(String uuid);

    @Query("SELECT EXISTS(SELECT 1 FROM ronda_type WHERE uuid = :uuid LIMIT 1)")
    boolean checkRondaTypeExistance(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createOrUpdate(RondaType rondaType);

    @Query("SELECT * FROM ronda_type")
    List<RondaType> queryForAll();

    @Query("SELECT * FROM ronda_type WHERE id = :id LIMIT 1")
    RondaType queryForId(int id);

    @Query("DELETE FROM ronda_type WHERE id = :id")
    void delete(int id);
}
