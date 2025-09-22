package mz.org.csaude.mentoring.service.ronda;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.ronda.RondaMenteeDAO;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.DateUtilities;

public class RondaMenteeServiceImpl extends BaseServiceImpl<RondaMentee> implements RondaMenteeService {
    private RondaMenteeDAO rondaMenteeDAO;
    public RondaMenteeServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.rondaMenteeDAO = getDataBaseHelper().getRondaMenteeDAO();
    }

    @Override
    public RondaMentee save(RondaMentee record) throws SQLException {
        record.setId((int) this.rondaMenteeDAO.insert(record));
        return record;
    }

    @Override
    public RondaMentee update(RondaMentee record) throws SQLException {
        this.rondaMenteeDAO.update(record);
        return record;
    }

    @Override
    public int delete(RondaMentee record) throws SQLException {
        return this.rondaMenteeDAO.delete(record);
    }

    @Override
    public List<RondaMentee> getAll() throws SQLException {
        return this.rondaMenteeDAO.queryForAll();
    }

    @Override
    public RondaMentee getById(int id) throws SQLException {
        RondaMentee rondaMentee = this.rondaMenteeDAO.queryForId(id);
        if(rondaMentee!=null) {
            rondaMentee.setTutored(getApplication().getTutoredService().getById(rondaMentee.getMenteeId()));
        }
        return rondaMentee;
    }

    @Override
    public RondaMentee getByMentee(Tutored tutored, Ronda ronda) throws SQLException {
        RondaMentee rondaMentee = this.rondaMenteeDAO.getByMenteeId(tutored.getId(), ronda.getId());
        if(rondaMentee!=null) {
            rondaMentee.setTutored(tutored);
        }
        return rondaMentee;
    }

    @Override
    public RondaMentee savedOrUpdateRondaMentee(RondaMentee rondaMentee) throws SQLException {
        RondaMentee rm = this.rondaMenteeDAO.getByUuid(rondaMentee.getUuid());
        if(rm!=null) {
            rondaMentee.setId(rm.getId());
            rondaMenteeDAO.update(rondaMentee);
        } else {
            this.rondaMenteeDAO.insert(rondaMentee);
            rondaMentee.setId(this.rondaMenteeDAO.getByUuid(rondaMentee.getUuid()).getId());
        }
        return rondaMentee;
    }

    @Override
    public List<RondaMentee> getAllOfRonda(Ronda ronda) throws SQLException {
        List<RondaMentee> rondaMentees = rondaMenteeDAO.getAllOfRonda(ronda.getId());
        for (RondaMentee rondaMentee : rondaMentees) {
            rondaMentee.setTutored(getApplication().getTutoredService().getById(rondaMentee.getMenteeId()));
        }
        return rondaMentees;
    }

    @Override
    public void closeAllActiveOnRonda(Ronda ronda) {
        rondaMenteeDAO.closeAllActiveOnRonda(ronda.getId(), ronda.getEndDate());
    }

    @Override
    public void closeRondaMentee(Ronda currRonda, Tutored selectedMentee) {
        rondaMenteeDAO.closeOneActiveOnRonda(currRonda.getId(), DateUtilities.getCurrentDate(), selectedMentee.getId());
    }

    @Override
    public RondaMentee getByuuid(String uuid) throws SQLException {
        RondaMentee rondaMentee = rondaMenteeDAO.getByUuid(uuid);
        if(rondaMentee!=null) {
            rondaMentee.setTutored(getApplication().getTutoredService().getById(rondaMentee.getMenteeId()));
        }
        return rondaMentee;
    }
}
