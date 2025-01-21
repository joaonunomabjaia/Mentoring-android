package mz.org.csaude.mentoring.service.session;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionRecommendedResource;
import mz.org.csaude.mentoring.model.session.SessionSummary;
import mz.org.csaude.mentoring.model.tutored.Tutored;

public interface SessionService extends BaseService<Session> {
    List<Session> getAllOfRondaAndMentee(Ronda currRonda, Tutored selectedMentee, long offset, long limit) throws SQLException;

    int countAllOfRondaAndMentee(Ronda currRonda, Tutored selectedMentee);

    List<Session> getAllOfRonda(Ronda ronda) throws SQLException;

    List<SessionSummary> generateSessionSummary(Session session, boolean includeFinalScore);

    void saveRecommendedResources(Session session, List<SessionRecommendedResource> recommendedResources) throws SQLException;
    void updateRecommendedResources(SessionRecommendedResource recommendedResources) throws SQLException;

    List<SessionRecommendedResource> getPendingRecommendedResources() throws SQLException;

    SessionRecommendedResource getRecommendedResourceByUuid(String uuid);

    List<Session> getAllOfRondaPending(Ronda ronda) throws SQLException;

    Session saveOrUpdate(Session session) throws SQLException;

    List<Session> getAllNotSynced() throws SQLException;
}
