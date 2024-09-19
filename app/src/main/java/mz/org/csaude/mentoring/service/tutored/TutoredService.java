package mz.org.csaude.mentoring.service.tutored;

import androidx.paging.PagingData;

import java.sql.SQLException;
import java.util.List;

import kotlinx.coroutines.flow.Flow;
import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.tutored.Tutored;

public interface TutoredService extends BaseService<Tutored> {

    void savedOrUpdateTutoreds(List<Tutored> tutoredS) throws SQLException;

    Tutored savedOrUpdateTutored(Tutored tutored) throws SQLException;

    List<Tutored> getAllOfRonda(Ronda currRonda) throws SQLException;

    List<Tutored> getAllOfRondaForZeroEvaluation(Ronda currRonda) throws SQLException;

    List<Tutored> getAllOfHealthFacility(HealthFacility healthFacility) throws SQLException;

    List<Tutored> getAllNotSynced() throws SQLException;

    public List<Tutored> getAllForMentoringRound(HealthFacility healthFacility, boolean zeroEvaluation) throws SQLException;

    List<Tutored> getAllOfRondaForNewRonda(HealthFacility healthFacility) throws SQLException;

    List<Tutored> getAllPagenated(long offset, long limit);

}
