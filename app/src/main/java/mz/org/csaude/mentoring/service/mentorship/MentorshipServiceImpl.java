package mz.org.csaude.mentoring.service.mentorship;

import android.app.Application;

import androidx.room.Transaction;

import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.answer.AnswerDAO;
import mz.org.csaude.mentoring.dao.mentorship.MentorshipDAO;
import mz.org.csaude.mentoring.dao.ronda.RondaDAO;
import mz.org.csaude.mentoring.dao.session.SessionDAO;
import mz.org.csaude.mentoring.dao.session.SessionStatusDAO;
import mz.org.csaude.mentoring.dao.tutored.TutoredDao;
import mz.org.csaude.mentoring.dto.mentorship.MentorshipDTO;
import mz.org.csaude.mentoring.dto.session.SessionDTO;
import mz.org.csaude.mentoring.dto.session.SessionStatusDTO;
import mz.org.csaude.mentoring.model.answer.Answer;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionStatus;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;

public class MentorshipServiceImpl extends BaseServiceImpl<Mentorship> implements MentorshipService {


    MentorshipDAO mentorshipDAO;
    SessionDAO sessionDAO;
    SessionStatusDAO sessionStatusDAO;

    TutoredDao tutoredDao;

    RondaDAO rondaDAO;
    AnswerDAO answerDAO;

    public MentorshipServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application){
        try {
            super.init(application);
            this.mentorshipDAO = getDataBaseHelper().getMentorshipDAO();
            this.sessionDAO = getDataBaseHelper().getSessionDAO();
            this.sessionStatusDAO = getDataBaseHelper().getSessionStatusDAO();
            this.answerDAO = getDataBaseHelper().getAnswerDAO();
            this.rondaDAO = getDataBaseHelper().getRondaDAO();
            this.tutoredDao = getDataBaseHelper().getTutoredDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transaction
    public Mentorship save(Mentorship record) {
        // Check if the Ronda associated with the Session is Ronda Zero
        if (record.getSession().getRonda().isRondaZero()) {
            // Set the SyncStatus of the Tutored entity to PENDING and update it
            record.getTutored().setSyncStatus(SyncSatus.PENDING);
            tutoredDao.update(record.getTutored());
        }

        // If the Ronda is completed, update the Ronda
        if(record.getSession().getRonda().isRondaCompleted()) {
            rondaDAO.update(record.getSession().getRonda());
        }

        // If the Ronda is Ronda Zero, insert or update the Session
        if (record.getSession().getRonda().isRondaZero()) {
            if (record.getSession().getId() == null) {
                record.getSession().setId((int) sessionDAO.insert(record.getSession()));
                record.setSessionId(record.getSession().getId());
            } else {
                sessionDAO.update(record.getSession());
            }
        }
        // If the Session is completed, update the Session
        else if (record.getSession().isCompleted()) {
            sessionDAO.update(record.getSession());
        }

        // Insert or update the Mentorship
        if (record.getId() == null) {
            record.setId((int) mentorshipDAO.insertMentorship(record));
        } else {
            mentorshipDAO.updateMentorship(record);
        }

        // Insert or update each Answer associated with the Mentorship
        for (Answer answer : record.getAnswers()) {
            if (answer.getId() == null) {
                answer.setMentorship(record);
                answer.setId((int) answerDAO.insert(answer));
            } else {
                answerDAO.update(answer);
            }
        }

        return record;
    }


    @Override
    public Mentorship update(Mentorship record) throws SQLException {
        mentorshipDAO.updateMentorship(record);
        return record;
    }

    @Override
    @Transaction
    public int delete(Mentorship record) throws SQLException {
        if (record.getSession().getRonda().isRondaZero()) {
            record.getTutored().setZeroEvaluationDone(false);
            tutoredDao.update(record.getTutored());
            sessionDAO.delete(record.getSession().getId());
        }
        return this.mentorshipDAO.delete(record.getId());
    }

    @Override
    public List<Mentorship> getAll() throws SQLException {
        return this.mentorshipDAO.queryForAll();
    }

    @Override
    public Mentorship getById(int id) throws SQLException {
        return this.mentorshipDAO.queryForId(id);
    }

    @Override
    public Mentorship getByuuid(String uuid) throws SQLException {
        return this.mentorshipDAO.getByUuid(uuid);
    }

    @Override
    public List<Mentorship> getMentorshipByTutor(String uuidTutor) throws SQLException {
        return this.mentorshipDAO.getMentorshipByTutor(uuidTutor);
    }

    @Override
    public void saveOrUpdateMentorships(List<MentorshipDTO> mentorshipDTOS) throws SQLException {
        for (MentorshipDTO mentorshipDTO: mentorshipDTOS) {
            this.saveOrUpdateMentorship(mentorshipDTO);
        }

    }

    @Override
    @Transaction
    public Mentorship saveOrUpdateMentorship(MentorshipDTO mentorshipDTO) throws SQLException {
        SessionDTO sessionDTO = mentorshipDTO.getSession();

        SessionStatusDTO sessionStatusDTO = sessionDTO.getSessionStatus();
        SessionStatus ss = this.sessionStatusDAO.getByUuid(sessionStatusDTO.getUuid());
        SessionStatus sessionStatus = sessionStatusDTO.getSessionStatus();
        if(ss!=null) {
            sessionStatus.setId(ss.getId());
        }
        this.sessionStatusDAO.createOrUpdate(sessionStatus);

        Session s = this.sessionDAO.getByUuid(sessionDTO.getUuid());
        Session session = sessionDTO.getSession();
        if(s!=null) {
            session.setId(s.getId());
            this.sessionDAO.update(session);
        } else {
            this.sessionDAO.insert(session);
            session.setId(this.sessionDAO.getByUuid(session.getUuid()).getId());
        }

        Mentorship m = this.mentorshipDAO.getByUuid(mentorshipDTO.getUuid());
        Mentorship mentorship = mentorshipDTO.getMentorship();
        if(m!=null) {
            mentorship.setId(m.getId());
            this.mentorshipDAO.updateMentorship(mentorship);
        } else {
            this.mentorshipDAO.insertMentorship(mentorship);
            mentorship.setId(this.mentorshipDAO.getByUuid(mentorship.getUuid()).getId());
        }

        return mentorship;
    }

    @Override
    public List<Mentorship> getAllNotSynced(Application application) throws SQLException {
        return this.mentorshipDAO.getAllNotSynced(String.valueOf(SyncSatus.PENDING));
    }

    @Override
    public List<Mentorship> getAllOfRonda(Ronda ronda) throws SQLException {
        List<Mentorship> mentorships = this.mentorshipDAO.getAllOfRonda(ronda.getId());
        if(!Utilities.listHasElements(mentorships)) return  null;

        for (Mentorship mentorship : mentorships) {
            mentorship.setSession(getApplication().getSessionService().getById(mentorship.getSessionId()));
            mentorship.getSession().setRonda(getApplication().getRondaService().getById(mentorship.getSession().getRondaId()));
            mentorship.getSession().getRonda().setRondaType(getApplication().getRondaTypeService().getById(mentorship.getSession().getRonda().getRondaTypeId()));
            mentorship.setEvaluationType(getApplication().getEvaluationTypeService().getById(mentorship.getEvaluationTypeId()));
            mentorship.setTutored(getApplication().getTutoredService().getById(mentorship.getTutorId()));
            mentorship.setCabinet(getApplication().getCabinetService().getById(mentorship.getCabinetId()));
            mentorship.setDoor(getApplication().getDoorService().getById(mentorship.getDoorId()));
            mentorship.setForm(getApplication().getFormService().getById(mentorship.getFormId()));
        }
        return mentorships;
    }

    @Override
    public List<Mentorship> getAllOfSession(Session session) throws SQLException {
        List<Mentorship> mentorships = mentorshipDAO.getAllOfSession(session.getId());
        for (Mentorship mentorship : mentorships) {
            mentorship.setEvaluationType(getApplication().getEvaluationTypeService().getById(mentorship.getEvaluationTypeId()));
            mentorship.setTutored(session.getTutored());
            mentorship.setCabinet(getApplication().getCabinetService().getById(mentorship.getCabinetId()));
            mentorship.setDoor(getApplication().getDoorService().getById(mentorship.getDoorId()));
            mentorship.setForm(getApplication().getFormService().getById(mentorship.getFormId()));
            mentorship.setSession(session);
        }
        return mentorships;
    }

    @Override
    public Mentorship saveOrUpdate(Mentorship mentorship) throws SQLException {
        Mentorship mm = this.mentorshipDAO.getByUuid(mentorship.getUuid());
        if(mm!=null) {
            mentorship.setId(mm.getId());
            this.mentorshipDAO.updateMentorship(mentorship);
        } else {
            this.mentorshipDAO.insertMentorship(mentorship);
            mentorship.setId(this.mentorshipDAO.getByUuid(mentorship.getUuid()).getId());
        }
        return mentorship;
    }

}
