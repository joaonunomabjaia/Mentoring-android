package mz.org.csaude.mentoring.service.session;

import android.app.Application;

import androidx.room.Transaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.answer.AnswerDAO;
import mz.org.csaude.mentoring.dao.mentorship.MentorshipDAO;
import mz.org.csaude.mentoring.dao.session.SessionDAO;
import mz.org.csaude.mentoring.dao.session.SessionRecommendedResourceDAO;
import mz.org.csaude.mentoring.model.answer.Answer;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionRecommendedResource;
import mz.org.csaude.mentoring.model.session.SessionSummary;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.LifeCycleStatus;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;

public class SessionServiceImpl extends BaseServiceImpl<Session> implements SessionService{

    SessionDAO sessionDAO;
    MentorshipDAO mentorshipDAO;

    SessionRecommendedResourceDAO sessionRecommendedResourceDAO;

    AnswerDAO answerDAO;

    public SessionServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.sessionDAO = getDataBaseHelper().getSessionDAO();
        this.mentorshipDAO = getDataBaseHelper().getMentorshipDAO();
        this.sessionRecommendedResourceDAO = getDataBaseHelper().getSessionRecommendedResourceDAO();
        this.answerDAO = getDataBaseHelper().getAnswerDAO();
    }

    @Override
    @Transaction
    public Session save(Session record) throws SQLException {
            this.sessionDAO.insert(record);
            if (Utilities.listHasElements(record.getMentorships())) {
                for (Mentorship mentorship : record.getMentorships()) {
                    Form form = getApplication().getFormService().getByuuid(mentorship.getForm().getUuid());
                    mentorship.setForm(form);
                    mentorship.setCabinet(getApplication().getCabinetService().getByuuid(mentorship.getCabinet().getUuid()));
                    mentorship.setDoor(getApplication().getDoorService().getByuuid(mentorship.getDoor().getUuid()));
                    mentorship.setEvaluationType(getApplication().getEvaluationTypeService().getByuuid(mentorship.getEvaluationType().getUuid()));
                    mentorship.setTutor(getApplication().getTutorService().getByuuid(mentorship.getTutor().getUuid()));
                    mentorship.setTutored(getApplication().getTutoredService().getByuuid(mentorship.getTutored().getUuid()));
                    mentorship.setSession(record);
                    this.mentorshipDAO.insertMentorship(mentorship);
                    for (Answer answer : mentorship.getAnswers()) {
                        answer.setMentorship(mentorship);
                        answer.setForm(form);
                        answer.setQuestion(getApplication().getQuestionService().getByuuid(answer.getQuestion().getUuid()));
                        this.answerDAO.insert(answer);
                    }
                }
            }
        return record;
    }

    @Override
    public Session update(Session record) throws SQLException {
        this.sessionDAO.update(record);
        return record;
    }

    @Override
    public int delete(Session record) throws SQLException {
        return this.sessionDAO.delete(record.getId());
    }

    @Override
    public List<Session> getAll() throws SQLException {
        return this.sessionDAO.queryForAll();
    }

    @Override
    public Session getById(int id) throws SQLException {
        return this.sessionDAO.queryForId(id);
    }

    @Override
    public List<Session> getAllOfRondaAndMentee(Ronda currRonda, Tutored selectedMentee, long offset, long limit) throws SQLException {
        List<Session> sessions = this.sessionDAO.queryForAllOfRondaAndMentee(currRonda.getId(), selectedMentee.getId());
        for (Session session : sessions) {
            session.setMentorships(this.mentorshipDAO.getAllOfSession(session.getId()));
            session.setForm(getApplication().getFormService().getById(session.getFormId()));
            session.setTutored(getApplication().getTutoredService().getById(session.getMenteeId()));
            session.setRonda(getApplication().getRondaService().getById(session.getRondaId()));
            session.setStatus(getApplication().getSessionStatusService().getById(session.getStatusId()));
            for (Mentorship mentorship : session.getMentorships()) {
                mentorship.setEvaluationType(getApplication().getEvaluationTypeService().getById(mentorship.getEvaluationTypeId()));
                mentorship.setAnswers(this.getApplication().getAnswerService().getAllOfMentorship(mentorship));
            }
        }
        return sessions;
    }

    @Override
    public int countAllOfRondaAndMentee(Ronda currRonda, Tutored selectedMentee) {
        return this.sessionDAO.countAllOfRondaAndMentee(currRonda.getId(), selectedMentee.getId());
    }

    @Override
    public List<Session> getAllOfRonda(Ronda currRonda) throws SQLException {
        List<Session> sessions = this.sessionDAO.queryForAllOfRonda(currRonda.getId());
        for (Session session : sessions) {
            session.setMentorships(this.mentorshipDAO.getAllOfSession(session.getId()));
            session.setForm(getApplication().getFormService().getById(session.getFormId()));
            session.setTutored(getApplication().getTutoredService().getById(session.getMenteeId()));
            session.setRonda(getApplication().getRondaService().getById(session.getRondaId()));
            session.setStatus(getApplication().getSessionStatusService().getById(session.getStatusId()));

            for (Mentorship mentorship : session.getMentorships()) {
                mentorship.setEvaluationType(getApplication().getEvaluationTypeService().getById(mentorship.getEvaluationTypeId()));
                if (mentorship.isPatientEvaluation()) {
                    mentorship.setAnswers(this.getApplication().getAnswerService().getAllOfMentorship(mentorship));
                }
            }
        }
        return sessions;
    }

    @Override
    public List<SessionSummary> generateSessionSummary(Session session) {
        List<SessionSummary> summaries = new ArrayList<>();

        SessionSummary sessionSummary = new SessionSummary();
        sessionSummary.setTitle("Desempenho Final");
        try {
            session.setMentorships(getApplication().getMentorshipService().getAllOfSession(session));
            for (Mentorship mentorship : session.getMentorships()) {
                if (mentorship.isPatientEvaluation()) {
                    mentorship.setAnswers(getApplication().getAnswerService().getAllOfMentorship(mentorship));
                    determineFinalScore(sessionSummary, mentorship.getAnswers());
                    for (Answer answer : mentorship.getAnswers()) {
                        String cat = answer.getFormSectionQuestion().getFormSection().getSection().getDescription();
                        if (categoryAlreadyExists(cat, summaries)){
                            doCountInCategory(cat, summaries, answer);
                        } else {
                            summaries.add(initSessionSummary(answer));
                        }
                    }
                    break;
                }
            }
            summaries.add(sessionSummary);
            return summaries;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void determineFinalScore(SessionSummary sessionSummary, List<Answer> answers) {
        int simCount = 0;
        int naoCount = 0;

        // Loop through answers to count SIM and NÃO values
        for (Answer answer : answers) {
            String value = answer.getValue().trim().toUpperCase();
            if (value.equals("SIM")) {
                simCount++;
            } else if (value.equals("NAO")) {
                naoCount++;
            }
        }

        sessionSummary.setSimCount(simCount);
        sessionSummary.setNaoCount(naoCount);

       /* // Calculate Desempenho Final using the provided formula
        double finalPerformance = 0.0;
        if (simCount + naoCount > 0) {
            finalPerformance = ((double) simCount / (simCount + naoCount)) * 100;
        }

        // Set the final score in the session summary
        sessionSummary.setProgressPercentage(finalPerformance+"%");*/
    }


    @Override
    public void saveRecommendedResources(Session session, List<SessionRecommendedResource> recommendedResources) throws SQLException {
        for (SessionRecommendedResource recommendedResource : recommendedResources) {
            this.sessionRecommendedResourceDAO.insert(recommendedResource);
        }
    }

    @Override
    public void updateRecommendedResources(SessionRecommendedResource recommendedResources) throws SQLException {
        this.sessionRecommendedResourceDAO.update(recommendedResources);
    }

    @Override
    public List<SessionRecommendedResource> getPendingRecommendedResources() throws SQLException {
        List<SessionRecommendedResource> resources = this.sessionRecommendedResourceDAO.queryForAllPending(String.valueOf(SyncSatus.PENDING));
        for (SessionRecommendedResource resource : resources) {
            resource.setSession(getApplication().getSessionService().getById(resource.getSessionId()));
            resource.setTutored(getApplication().getTutoredService().getById(resource.getTutoredId()));
            resource.setTutor(getApplication().getTutorService().getById(resource.getTutorId()));
        }
        return resources;
    }

    @Override
    public SessionRecommendedResource getRecommendedResourceByUuid(String uuid) {
        return this.sessionRecommendedResourceDAO.getByUuid(uuid);
    }

    private void doCountInCategory(String cat, List<SessionSummary> summaries, Answer answer) {
        for (SessionSummary sessionSummary : summaries) {
            if (sessionSummary.getTitle().equals(cat)) {
                if (answer.getValue().equals("SIM")) {
                    sessionSummary.setSimCount(sessionSummary.getSimCount() + 1);
                } else if (answer.getValue().equals("NAO")) {
                    sessionSummary.setNaoCount(sessionSummary.getNaoCount() + 1);
                }
            }
        }
    }

    private boolean categoryAlreadyExists(String cat, List<SessionSummary> summaries) {
        for (SessionSummary sessionSummary : summaries) {
            if (sessionSummary.getTitle().equals(cat)) {
                return true;
            }
        }
        return false;
    }

    private SessionSummary initSessionSummary(Answer answer) {
        SessionSummary sessionSummary = new SessionSummary();
        String cat = answer.getFormSectionQuestion().getFormSection().getSection().getDescription();
        sessionSummary.setTitle(cat);

        if (answer.getValue().equals("SIM")) {
            sessionSummary.setSimCount(sessionSummary.getSimCount() + 1);
        } else if (answer.getValue().equals("NAO")) {
            sessionSummary.setNaoCount(sessionSummary.getNaoCount() + 1);
        }
        return sessionSummary;
    }

    @Override
    public List<Session> getAllOfRondaPending(Ronda ronda) throws SQLException {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 2);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate = cal.getTime();

        return this.sessionDAO.getAllOfRondaPending(ronda.getId(), startDate);
    }

    @Override
    public Session saveOrUpdate(Session session) throws SQLException {
        Session ss = this.sessionDAO.getByUuid(session.getUuid());
        if(ss!=null) {
            session.setId(ss.getId());
            sessionDAO.update(session);
        } else {
            sessionDAO.insert(session);
            session.setId(sessionDAO.getByUuid(session.getUuid()).getId());
        }
        return session;
    }

    @Override
    public List<Session> getAllNotSynced() throws SQLException {
        List<Session> sessions = this.sessionDAO.getAllNotSynced(String.valueOf(SyncSatus.PENDING));
        for (Session session : sessions) {
            session.setForm(getApplication().getFormService().getById(session.getFormId()));
            session.setTutored(getApplication().getTutoredService().getById(session.getMenteeId()));
            session.setRonda(getApplication().getRondaService().getById(session.getRondaId()));
            session.setStatus(getApplication().getSessionStatusService().getById(session.getStatusId()));
        }
        return sessions;
    }

    @Override
    public Session getByuuid(String uuid) throws SQLException {
        return sessionDAO.getByUuid(uuid);
    }
}
