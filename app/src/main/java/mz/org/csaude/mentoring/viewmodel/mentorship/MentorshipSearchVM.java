package mz.org.csaude.mentoring.viewmodel.mentorship;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.searchparams.AbstractSearchParams;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.base.viewModel.SearchVM;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.ronda.RondaMentor;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionStatus;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.service.mentorship.MentorshipService;
import mz.org.csaude.mentoring.service.mentorship.MentorshipServiceImpl;
import mz.org.csaude.mentoring.service.session.SessionService;
import mz.org.csaude.mentoring.service.session.SessionServiceImpl;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.form.ListFormActivity;
import mz.org.csaude.mentoring.view.mentorship.CreateMentorshipActivity;
import mz.org.csaude.mentoring.view.mentorship.MentorshipActivity;

public class MentorshipSearchVM extends AbstractSearchMentorshipVM {

    private MentorshipService mentorshipService;
    private SessionService sessionService;
    private Mentorship mentorship;

    private List<Listble> mentorships;
    private List<Listble> sessions;



    public MentorshipSearchVM(@NonNull Application application) {
        super(application);
        this.mentorshipService = new MentorshipServiceImpl(application);
        this.sessionService = new SessionServiceImpl(application);
        this.mentorships = new ArrayList<>();
        this.sessions = new ArrayList<>();
    }

    @Override
    protected void doOnNoRecordFound() {

    }


    public String getMentorshipTitle() {
        return "Sessão: " + DateUtilities.formatToDDMMYYYY(this.session.getStartDate(), "/") + ", Avaliações de: " + this.session.getTutored().getEmployee().getFullName();
    }
    @Override
    public void preInit() {
    }

    @Bindable
    public SessionStatus getSessionStatus() {
        return this.session.getStatus();
    }

    public void createNewMentorship() {
        getExecutorService().execute(()->{
            if (isOnGoingMentorshuip()) {
                String message = getRelatedActivity().getString(R.string.error_ongoing_mentorship);
                runOnMainThread(()->{
                    Utilities.displayAlertDialog(getRelatedActivity(), message).show();
                });
            } else
            if (this.searchResults.size() < 4) {
                runOnMainThread(()->{
                    Map<String, Object> params = new HashMap<>();
                    params.put("session", session);
                    params.put("CURR_MENTORSHIP_STEP", MentorshipVM.CURR_MENTORSHIP_STEP_PERIOD_SELECTION);
                    getCurrentStep().changetocreate();
                    getRelatedActivity().nextActivity(CreateMentorshipActivity.class, params);
                });
            } else {
                String message = getRelatedActivity().getString(R.string.error_max_evaluations, this.session.getTutored().getEmployee().getFullName());
                runOnMainThread(()->{
                    Utilities.displayAlertDialog(getRelatedActivity(), message).show();
                });

            }
        });

    }

    private boolean isOnGoingMentorshuip() {
        try {
            List<Mentorship> mentorships = getApplication().getMentorshipService().getAllOfSession(this.session);
            for (Mentorship mentorship : mentorships) {
                if (mentorship.getEndDate() == null) return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Mentorship> doSearch(long offset, long limit) throws SQLException {
        if (this.ronda.isRondaZero()) {
            return getApplication().getMentorshipService().getAllOfRonda(this.ronda);
        } else {
            return getApplication().getMentorshipService().getAllOfSession(this.session);
        }
    }

    @Override
    public MentorshipActivity getRelatedActivity() {
        return (MentorshipActivity) super.getRelatedActivity();
    }

    @Override
    public void displaySearchResults() {
        getRelatedActivity().populateRecyclerView();
    }

    @Override
    public AbstractSearchParams<Mentorship> initSearchParams() {
        return null;
    }

    @Override
    public void edit(Mentorship mentorship) {
        // Perform the database operations from the parent class in a background thread
        getExecutorService().execute(() -> {
            // Call the parent edit method (which performs the database operations)
            super.edit(mentorship);

            // After the parent method is done, switch to the main thread for UI updates
            runOnMainThread(() -> {
                // Prepare the params for the next activity
                Map<String, Object> params = new HashMap<>();
                params.put("mentorship", mentorship);
                params.put("CURR_MENTORSHIP_STEP", MentorshipVM.CURR_MENTORSHIP_STEP_PERIOD_SELECTION);

                // Change the application step to edit and start the next activity
                getApplication().getApplicationStep().changeToEdit();
                getRelatedActivity().nextActivity(CreateMentorshipActivity.class, params);
            });
        });
    }

}
