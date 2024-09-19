package mz.org.csaude.mentoring.service.mentorship;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.dto.mentorship.MentorshipDTO;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.session.Session;

public interface MentorshipService extends BaseService<Mentorship> {
    public List<Mentorship> getMentorshipByTutor(String uuidTutor) throws SQLException;
    void saveOrUpdateMentorships(List<MentorshipDTO> mentorshipDTOS) throws SQLException;
    Mentorship saveOrUpdateMentorship(MentorshipDTO mentorshipDTO) throws SQLException;

    List<Mentorship> getAllNotSynced(Application application) throws SQLException;

    List<Mentorship> getAllOfRonda(Ronda ronda) throws SQLException;

    List<Mentorship> getAllOfSession(Session session) throws SQLException;

    Mentorship saveOrUpdate(Mentorship mentorship) throws SQLException;

}
