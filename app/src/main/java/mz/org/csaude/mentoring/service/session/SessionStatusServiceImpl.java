package mz.org.csaude.mentoring.service.session;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.session.SessionStatusDAO;
import mz.org.csaude.mentoring.dto.session.SessionStatusDTO;
import mz.org.csaude.mentoring.model.session.SessionStatus;

public class SessionStatusServiceImpl extends BaseServiceImpl<SessionStatus> implements SessionStatusService {

    SessionStatusDAO sessionStatusDAO;


    public SessionStatusServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.sessionStatusDAO = getDataBaseHelper().getSessionStatusDAO();
    }

    @Override
    public SessionStatus save(SessionStatus record) throws SQLException {
        this.sessionStatusDAO.insert(record);
        return record;
    }

    @Override
    public SessionStatus update(SessionStatus record) throws SQLException {
        this.sessionStatusDAO.update(record);
        return record;
    }

    @Override
    public int delete(SessionStatus record) throws SQLException {
        return this.sessionStatusDAO.delete(record.getId());
    }

    @Override
    public List<SessionStatus> getAll() throws SQLException {
        return this.sessionStatusDAO.queryForAll();
    }

    @Override
    public SessionStatus getById(int id) throws SQLException {
        return this.sessionStatusDAO.queryForId(id);
    }

    @Override
    public void saveOrUpdateSessionStatuses(List<SessionStatusDTO> sessionStatusDTOS) throws SQLException {
        for (SessionStatusDTO sessionStatusDTO: sessionStatusDTOS) {
            this.saveOrUpdateSessionStatus(sessionStatusDTO);
        }

    }

    @Override
    public SessionStatus saveOrUpdateSessionStatus(SessionStatusDTO sessionStatusDTO) throws SQLException {
        SessionStatus ss = this.sessionStatusDAO.getByUuid(sessionStatusDTO.getUuid());
        SessionStatus sessionStatus = sessionStatusDTO.getSessionStatus();
        if(ss!=null) {
            sessionStatus.setId(ss.getId());
            sessionStatusDAO.update(sessionStatus);
        } else {
            sessionStatus.setId((int) sessionStatusDAO.insert(sessionStatus));
        }
        return sessionStatus;
    }

    @Override
    public SessionStatus getByCode(String code) throws SQLException {
        return sessionStatusDAO.getByCode(code);
    }

    @Override
    public SessionStatus getByuuid(String uuid) throws SQLException {
        return sessionStatusDAO.getByUuid(uuid);
    }
}
