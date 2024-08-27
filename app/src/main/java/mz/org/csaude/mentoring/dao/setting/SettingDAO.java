package mz.org.csaude.mentoring.dao.setting;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.setting.Setting;

@Dao
public interface SettingDAO {

    @Insert
    void insert(Setting setting);

    @Update
    void update(Setting setting);

    @Query("SELECT * FROM setting WHERE uuid = :uuid LIMIT 1")
    Setting getByUuid(String uuid);

    @Query("SELECT COUNT(*) > 0 FROM setting WHERE uuid = :uuid")
    boolean checkSettingExistence(String uuid);

    @Query("SELECT * FROM setting")
    List<Setting> getAllSettings();

    @Delete
    int delete(Setting record);

    @Query("SELECT * FROM setting WHERE id = :id")
    Setting queryForId(int id);

    @Query("SELECT * FROM setting")
    List<Setting> queryForAll();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createOrUpdate(Setting setting);
}
