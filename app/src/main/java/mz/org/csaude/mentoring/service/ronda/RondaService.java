package mz.org.csaude.mentoring.service.ronda;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.dto.ronda.RondaDTO;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.user.User;

public interface RondaService extends BaseService<Ronda> {
    Ronda savedOrUpdateRonda(Ronda ronda) throws SQLException;
    List<Ronda> getAllByHealthFacilityAndMentor(HealthFacility healthFacility, Tutor tutor, MentoringApplication mentoringApplication) throws SQLException;
    List<Ronda> getAllNotSynced() throws SQLException;
    List<Ronda> doSearch(long offset, long limit);

    int countRondas() throws SQLException;
    List<Ronda> getAllByRondaType(RondaType rondaType, User authenticatedUser) throws SQLException;
    void saveOrUpdateRondas(List<RondaDTO> rondaDTOS) throws SQLException;
    Ronda saveOrUpdateRonda(RondaDTO rondaDTO) throws SQLException;

    Ronda getFullyLoadedRonda(Ronda ronda)  throws SQLException;

    List<Ronda> getAllByMentor(Tutor tutor, MentoringApplication mentoringApplication) throws SQLException;

    void tryToCloseRonda(Ronda ronda, Date endDate);
}
