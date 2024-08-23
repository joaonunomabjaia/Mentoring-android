package mz.org.csaude.mentoring.dao.partner;

import androidx.room.Dao;
import androidx.room.Query;

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
}
