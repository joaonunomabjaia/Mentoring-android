package mz.org.csaude.mentoring.service.ronda;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentor;

public interface RondaMentorService extends BaseService<RondaMentor> {
    RondaMentor savedOrUpdateRondaMentor(RondaMentor rondaMentor) throws SQLException;

    List<RondaMentor> getRondaMentors(Ronda ronda) throws SQLException;

    void closeAllActiveOnRonda(Ronda ronda);
}
