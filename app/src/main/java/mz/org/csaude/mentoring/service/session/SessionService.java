package mz.org.csaude.mentoring.service.session;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionRecommendedResource;
import mz.org.csaude.mentoring.model.session.SessionSummary;
import mz.org.csaude.mentoring.model.tutored.Tutored;

public interface SessionService extends BaseService<Session> {
    List<Session> getAllOfRondaAndMentee(Ronda currRonda, Tutored selectedMentee, long offset, long limit) throws SQLException;

    List<Session> getAllOfRonda(Ronda ronda) throws SQLException;

    List<SessionSummary> generateSessionSummary(Session session);

    void saveRecommendedResources(Session session, List<SessionRecommendedResource> recommendedResources) throws SQLException;
    void updateRecommendedResources(SessionRecommendedResource recommendedResources) throws SQLException;

    List<SessionRecommendedResource> getPendingRecommendedResources() throws SQLException;

    List<Session> getAllOfRondaPending(Ronda ronda) throws SQLException;

    Session saveOrUpdate(Session session) throws SQLException;

    List<Session> getAllNotSynced() throws SQLException;
}
