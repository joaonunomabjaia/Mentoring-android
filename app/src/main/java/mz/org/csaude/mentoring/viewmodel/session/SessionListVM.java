package mz.org.csaude.mentoring.viewmodel.session;

import android.app.Application;
import android.app.Dialog;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.adapter.recyclerview.session.SessionAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.searchparams.AbstractSearchParams;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.base.viewModel.SearchVM;
import mz.org.csaude.mentoring.listner.dialog.IDialogListener;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.setting.Setting;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.Constants;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.mentorship.MentorshipActivity;
import mz.org.csaude.mentoring.view.ronda.RondaActivity;
import mz.org.csaude.mentoring.view.session.SessionActivity;
import mz.org.csaude.mentoring.view.session.SessionClosureActivity;
import mz.org.csaude.mentoring.view.session.SessionListActivity;
import mz.org.csaude.mentoring.view.session.SessionSummaryActivity;

public class SessionListVM extends SearchVM<Session>  implements IDialogListener {

    private Ronda currRonda;

    private Tutored selectedMentee;

    private Session selectedSession;

    private RondaMentee currRondaMentee;



    public SessionListVM(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void doOnNoRecordFound() {
        getRelatedActivity().populateSessions();
    }

    public Ronda getCurrRonda() {
        return currRonda;
    }

    public void setCurrRonda(Ronda currRonda) {
        this.currRonda = currRonda;
    }

    @Bindable
    public Listble getSelectedMentee() {
        return selectedMentee;
    }


    public void setSelectedMentee(Listble selectedMentee) {
        Dialog progress = Utilities.showLoadingDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.processando));

        getExecutorService().execute(() -> {
            this.selectedMentee = (Tutored) selectedMentee;
            try {
                currRondaMentee = getApplication().getRondaMenteeService().getByMentee(this.selectedMentee,this.currRonda);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Setting setting = getApplication().getSetting(Constants.MUX_DAYS_ON_RONDA_WITHOUT_SESSION);
            if (setting != null && setting.getEnabled()) {
                int daysThreshold = setting.getSettingValueAsInt(); // safest

                Date thresholdDate = DateUtilities.addDays(currRondaMentee.getStartDate(), daysThreshold);
                Date now = DateUtilities.getCurrentDate();

                if (now.compareTo(thresholdDate) > 0) {
                    int mentorshipCount = getApplication().getMentorshipService().countMentorshipsOnLastDays((Tutored) selectedMentee, this.currRonda);

                    if (mentorshipCount == 0) {
                        getApplication().getRondaMenteeService().closeRondaMentee(this.currRonda, (Tutored) selectedMentee);
                        runOnMainThread(() -> {
                            String message = getRelatedActivity().getString(
                                    R.string.ronda_mentee_closed,
                                    daysThreshold,
                                    ((Tutored) selectedMentee).getEmployee().getFullName()
                            );

                            Utilities.displayAlertDialog(getRelatedActivity(), message).show();
                        });
                    }
                }
            }

            initSearch();

            getRelatedActivity().runOnUiThread(() -> {
                notifyPropertyChanged(BR.selectedMentee);
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            });
        });
    }


    @Override
    public void preInit() {

    }

    public void createSession() {

        if (currRondaMentee.getEndDate() != null) {
            Setting setting = getApplication().getSetting(Constants.MUX_DAYS_ON_RONDA_WITHOUT_SESSION);
            String message = getRelatedActivity().getString(
                    R.string.ronda_mentee_closed,
                    setting.getSettingValueAsInt(),
                    selectedMentee.getEmployee().getFullName()
            );
            Utilities.displayAlertDialog(getRelatedActivity(), message).show();
        } else
        if (this.searchResults.size() < 4) {
            for (Session session : this.searchResults) {
                if (!session.isCompleted()) {
                    String message = getRelatedActivity().getString(R.string.cannot_create_new_session_with_onpen_session);
                    Utilities.displayAlertDialog(getRelatedActivity(), message).show();
                    return;
                }
            }
            Map<String, Object> params = new HashMap<>();
            params.put("ronda", this.currRonda);
            params.put("mentee", this.selectedMentee);
            getRelatedActivity().nextActivity(SessionActivity.class, params);
        } else {
            String message = getRelatedActivity().getString(R.string.cannot_create_more_than_four_sessions, this.selectedMentee.getEmployee().getFullName());
            Utilities.displayAlertDialog(getRelatedActivity(), message).show();
        }
    }

    public List<Listble> getRondaMentees() {
        try {
            return Utilities.parseList(getApplication().getTutoredService().getAllOfRonda(this.currRonda), Listble.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SessionListActivity getRelatedActivity() {
        return (SessionListActivity) super.getRelatedActivity();
    }

    @Override
    public List<Session> doSearch(long offset, long limit) throws SQLException {
        return getApplication().getSessionService().getAllOfRondaAndMentee(this.currRonda, this.selectedMentee, offset, limit);
    }

    @Override
    public void displaySearchResults() {
        getRelatedActivity().populateSessions();
    }

    @Override
    public AbstractSearchParams<Session> initSearchParams() {
        return null;
    }

    public void openSession(Session session) {
        Map<String, Object> params = new HashMap<>();
        params.put("session", session);
        getRelatedActivity().nextActivity(MentorshipActivity.class, params);
    }

    public void deleteSession(Session session) {
        this.selectedSession = session;
        Dialog progress = Utilities.showLoadingDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.processando));

        getExecutorService().execute(() -> {
            try {
                session.setMentorships(getApplication().getMentorshipService().getAllOfSession(session));

                getRelatedActivity().runOnUiThread(() -> {
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }

                    if (session.isCompleted()) {
                        String message = getRelatedActivity().getString(R.string.cannot_delete_completed_session);
                        Utilities.displayAlertDialog(getRelatedActivity(), message).show();
                        return;
                    } else if (Utilities.listHasElements(session.getMentorships())) {
                        String message = getRelatedActivity().getString(R.string.cannot_delete_with_evaluations);
                        Utilities.displayAlertDialog(getRelatedActivity(), message).show();
                        return;
                    }

                    String confirmationMessage = getRelatedActivity().getString(R.string.confirm_delete_session);
                    Utilities.displayConfirmationDialog(getRelatedActivity(), confirmationMessage, getRelatedActivity().getString(R.string.yes), getRelatedActivity().getString(R.string.no), this).show();
                });
            } catch (SQLException e) {
                Log.e("SessionListVM", "deleteSession: ", e);

                getRelatedActivity().runOnUiThread(() -> {
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                    String errorMessage = getRelatedActivity().getString(R.string.session_delete_error);
                    Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                });
            }
        });
    }



    public void editSession(Session session) {
        Dialog progress = Utilities.showLoadingDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.processando));

        getExecutorService().execute(() -> {
            try {
                session.setMentorships(getApplication().getMentorshipService().getAllOfSession(session));

                getRelatedActivity().runOnUiThread(() -> {
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }

                    if (session.isCompleted()) {
                        String message = getRelatedActivity().getString(R.string.cannot_edit_completed_session);
                        Utilities.displayAlertDialog(getRelatedActivity(), message).show();
                        return;
                    } else if (Utilities.listHasElements(session.getMentorships())) {
                        String message = getRelatedActivity().getString(R.string.cannot_edit_with_evaluations);
                        Utilities.displayAlertDialog(getRelatedActivity(), message).show();
                        return;
                    }

                    Map<String, Object> params = new HashMap<>();
                    params.put("session", session);
                    getApplication().getApplicationStep().changeToEdit();
                    getRelatedActivity().nextActivity(SessionActivity.class, params);
                });

            } catch (SQLException e) {
                Log.e("SessionListVM", "editSession: ", e);

                getRelatedActivity().runOnUiThread(() -> {
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                    String errorMessage = getRelatedActivity().getString(R.string.session_update_error);
                    Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                });
            }
        });
    }



    public void printSummary(Session session) {
        Map<String, Object> params = new HashMap<>();
        session.setMentorships(Collections.emptyList());
        params.put("session", session);
        if (!session.isCompleted()) {
            getRelatedActivity().nextActivity(SessionClosureActivity.class, params);
        } else {
            getRelatedActivity().nextActivity(SessionSummaryActivity.class, params);
        }
    }

    @Override
    public void doOnConfirmed() {
        Dialog progress = Utilities.showLoadingDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.processando));

        getExecutorService().execute(() -> {
            try {
                getApplication().getSessionService().delete(this.selectedSession);

                getRelatedActivity().runOnUiThread(() -> {
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                    initSearch();
                });

            } catch (SQLException e) {
                Log.e("SessionListVM", "doOnConfirmed: ", e);

                getRelatedActivity().runOnUiThread(() -> {
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                    String errorMessage = getRelatedActivity().getString(R.string.session_delete_error);
                    Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                });
            }
        });
    }

    public void showAISummary(Session session) {
        Map<String, Object> params = new HashMap<>();
        params.put("session", session);
        getRelatedActivity().nextActivity(SessionSummaryActivity.class, params);
    }


    @Override
    public void doOnDeny() {

    }
}
