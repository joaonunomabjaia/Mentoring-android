package mz.org.csaude.mentoring.dao.partner;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.partner.Partner;

@Dao
public interface PartnerDao {

    @Query("SELECT * FROM partner WHERE uuid = :uuid LIMIT 1")
    Partner getPartnerByUuid(String uuid);

    @Query("SELECT * FROM partner WHERE uuid = :uuid LIMIT 1")
    Partner getMISAU(String uuid);

    @Query("SELECT * FROM partner WHERE uuid != :misauUuid AND life_cycle_status = :activeStatus")
    List<Partner> getNotMISAU(String misauUuid, String activeStatus);

    @Query("SELECT * FROM partner WHERE uuid = :uuid LIMIT 1")
    Partner getByUuid(String uuid);

    @Insert
    void insert (Partner partner);

    @Insert
    void insertAll(List<Partner> partners);

    @Query("SELECT * FROM partner WHERE life_cycle_status = 'ACTIVE'")
    List<Partner> getAll();

    @Update
    void update(Partner partner);

    @Query("DELETE FROM partner WHERE id = :id")
    int delete(int id);

    @Query("DELETE FROM partner")
    void deleteAll();

    @Query("SELECT * FROM partner WHERE id = :id")
    Partner queryForId(int id);

    @Query("SELECT * FROM partner WHERE uuid = :uuid LIMIT 1")
    Partner queryForUuid(String uuid);

    @Query("SELECT * FROM partner WHERE name = :name LIMIT 1")
    Partner queryForName(String name);


}
