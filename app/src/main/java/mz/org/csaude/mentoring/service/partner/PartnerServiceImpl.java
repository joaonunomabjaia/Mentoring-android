package mz.org.csaude.mentoring.service.partner;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.partner.PartnerDao;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.util.LifeCycleStatus;

public class PartnerServiceImpl extends BaseServiceImpl<Partner> implements PartnerService {

    PartnerDao partnerDao;


    public PartnerServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException{
        super.init(application);
        this.partnerDao = getDataBaseHelper().getPartnerDao();
    }


    @Override
    public Partner save(Partner record) throws SQLException {
        this.partnerDao.insert(record);
        return record;
    }

    @Override
    public Partner update(Partner record) throws SQLException {
        this.partnerDao.update(record);
        return record;
    }

    @Override
    public int delete(Partner record) throws SQLException {
        return this.partnerDao.delete(record.getId());
    }

    @Override
    public List<Partner> getAll() throws SQLException {
        return this.partnerDao.getNotMISAU(Partner.MISAU_UUID, String.valueOf(LifeCycleStatus.ACTIVE));
    }

    @Override
    public Partner getById(int id) throws SQLException {
        return this.getById(id);
    }

    @Override
    public Partner savedOrUpdatePartner(Partner partner) throws SQLException {

        Partner p = this.partnerDao.getByUuid(partner.getUuid());

       if(p == null){
           this.partnerDao.insert(partner);
           return partner;
       } else {
           p.setName(partner.getName());
           p.setDescription(partner.getDescription());
           this.partnerDao.update(p);
           return p;
       }
    }

    @Override
    public void saveAll(List<Partner> partners) throws SQLException {
        for (Partner partner : partners) {
            savedOrUpdatePartner(partner);
        }
    }

    @Override
    public Partner getMISAU() throws SQLException {
        return this.partnerDao.getMISAU(Partner.MISAU_UUID);
    }

    @Override
    public Partner getByuuid(String uuid) throws SQLException {
        return partnerDao.getByUuid(uuid);
    }
}
