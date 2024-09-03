package mz.org.csaude.mentoring.service.ronda;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.ronda.RondaMentorDAO;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentor;

public class RondaMentorServiceImpl extends BaseServiceImpl<RondaMentor> implements RondaMentorService {
    private RondaMentorDAO rondaMentorDAO;
    public RondaMentorServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.rondaMentorDAO = getDataBaseHelper().getRondaMentorDAO();
    }

    @Override
    public RondaMentor save(RondaMentor record) throws SQLException {
        record.setId((int) this.rondaMentorDAO.insert(record));
        return record;
    }

    @Override
    public RondaMentor update(RondaMentor record) throws SQLException {
        this.rondaMentorDAO.update(record);
        return record;
    }

    @Override
    public int delete(RondaMentor record) throws SQLException {
        return this.rondaMentorDAO.delete(record);
    }

    @Override
    public List<RondaMentor> getAll() throws SQLException {
        return this.rondaMentorDAO.queryForAll();
    }

    @Override
    public RondaMentor getById(int id) throws SQLException {
        return this.rondaMentorDAO.queryForId(id);
    }

    @Override
    public RondaMentor getByuuid(String uuid) throws SQLException {
        return this.rondaMentorDAO.getByUuid(uuid);
    }

    @Override
    public RondaMentor savedOrUpdateRondaMentor(RondaMentor rondaMentor) throws SQLException {
        RondaMentor rm = this.getByuuid(rondaMentor.getUuid());
        if(rm!=null) {
            rondaMentor.setId(rm.getId());
            this.update(rondaMentor);
        } else {
            this.save(rondaMentor);
        }
        return rondaMentor;
    }

    @Override
    public List<RondaMentor> getRondaMentors(Ronda ronda) throws SQLException {
        return this.rondaMentorDAO.getRondaMentors(ronda.getId());
    }
}
