package mz.org.csaude.mentoring.viewmodel.session;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionSummary;
import mz.org.csaude.mentoring.util.PDFGenerator;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.session.SessionEAResourceActivity;
import mz.org.csaude.mentoring.view.session.SessionSummaryActivity;

public class SessionSummaryVM extends BaseViewModel {

    private Session session;

    private List<SessionSummary> sessionSummaryList;

    public SessionSummaryVM(@NonNull Application application) {
        super(application);
    }

    @Override
    public void preInit() {

    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void print() {
        getRelatedActivity().checkStoragePermission();
    }

    @Override
    public SessionSummaryActivity getRelatedActivity() {
        return (SessionSummaryActivity) super.getRelatedActivity();
    }

    public void downloadFile() {
        boolean print = PDFGenerator.createPDF(getRelatedActivity(), this.sessionSummaryList, this.session.getTutored());
        if (print) {
            Utilities.displayAlertDialog(getRelatedActivity(), "Resumo impresso com sucesso, encontre o documento no diretório de downloads.").show();
        } else {
            Utilities.displayAlertDialog(getRelatedActivity(), "Não foi possível imprimir o documento.").show();
        }
    }

    public List<SessionSummary> getSessionSummaryList() {
        return sessionSummaryList;
    }

    public void generateSessionSummary() {
        sessionSummaryList = getApplication().getSessionService().generateSessionSummary(session);
        if (Utilities.listHasElements(sessionSummaryList)) {
            runOnMainThread(()->getRelatedActivity().displaySearchResults());
            if (session.getRonda().isRondaZero()) {
                determineAndUpdateMenteeScore();
            }
        }
    }

    private void determineAndUpdateMenteeScore() {
        try {
            int yesCount = 0;
            int noCount = 0;
            for (SessionSummary sessionSummary : sessionSummaryList){
                yesCount = yesCount + sessionSummary.getSimCount();
                noCount = noCount + sessionSummary.getNaoCount();
            }
            double score = (double) yesCount / (yesCount + noCount) *100;
            session.getTutored().setZeroEvaluationScore(score);
            getApplication().getTutoredService().update(session.getTutored());
        } catch (SQLException e) {
            Log.e("SessionSummaryVM", "Exception: " + e.getMessage());
        }
    }
}
