package mz.org.csaude.mentoring.viewmodel.mentorship;

import android.app.Application;

import androidx.annotation.NonNull;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.searchparams.AbstractSearchParams;
import mz.org.csaude.mentoring.base.viewModel.SearchVM;
import mz.org.csaude.mentoring.listner.dialog.IDialogListener;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.util.Utilities;

public abstract class AbstractSearchMentorshipVM extends SearchVM<Mentorship> implements IDialogListener {

    protected Mentorship selectedMentorship;

    protected Session session;

    protected Ronda ronda;
    public AbstractSearchMentorshipVM(@NonNull Application application) {
        super(application);
    }

    public void edit(Mentorship mentorship) {
            try {
                // Fetch session, Ronda sessions, and mentorship answers in the background
                mentorship.setSession(getApplication().getSessionService().getById(mentorship.getSessionId()));
                mentorship.getSession().setRonda(getApplication().getRondaService().getById(mentorship.getSession().getRondaId()));
                mentorship.getSession().getRonda().addSession(getApplication().getSessionService().getAllOfRonda(mentorship.getSession().getRonda()));
                mentorship.setAnswers(getApplication().getAnswerService().getAllOfMentorship(mentorship));
                mentorship.setCabinet(getApplication().getCabinetService().getById(mentorship.getCabinetId()));
                mentorship.setDoor(getApplication().getDoorService().getById(mentorship.getDoorId()));
                this.selectedMentorship = mentorship;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }


    public void delete(Mentorship mentorship) {
        this.selectedMentorship = mentorship;
        if (mentorship.isCompleted()) {
            Utilities.displayAlertDialog(getRelatedActivity(), "Não pode apagar uma avaliação finalizada").show();
        } else {
            Utilities.displayConfirmationDialog(getRelatedActivity(), "Deseja apagar a avaliação?", "SIM", "NÃO", this).show();
        }
    }

    @Override
    public void doOnConfirmed() {
        try {
            getApplication().getMentorshipService().delete(this.selectedMentorship);
            initSearch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doOnDeny() {

    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Ronda getRonda() {
        return ronda;
    }

    public void setRonda(Ronda ronda) {
        this.ronda = ronda;
    }
}
