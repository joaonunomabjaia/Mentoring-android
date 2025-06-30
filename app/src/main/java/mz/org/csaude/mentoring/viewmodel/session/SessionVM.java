package mz.org.csaude.mentoring.viewmodel.session;

import android.app.Application;
import android.app.Dialog;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionStatus;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.service.session.SessionService;
import mz.org.csaude.mentoring.service.session.SessionServiceImpl;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;

public class SessionVM extends BaseViewModel {

    private Session session;

    private List<Form> forms;
    public SessionVM(@NonNull Application application) {
        super(application);
        init();
    }

    public void init() {
        getExecutorService().execute(() -> {
            try {
                if (!getApplication().getApplicationStep().isApplicationStepEdit()) {
                    // Create a new session in the background
                    Session newSession = new Session();
                    newSession.setStatus(getApplication().getSessionStatusService().getByCode(SessionStatus.INCOMPLETE));
                    newSession.setStartDate(DateUtilities.getCurrentDate());
                    newSession.setSyncStatus(SyncSatus.PENDING);
                    newSession.setUuid(Utilities.getNewUUID().toString());
                    newSession.setCreatedAt(DateUtilities.getCurrentDate());

                    this.session = newSession;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void preInit() {

    }

    @Bindable
    public Date getStartDate() {
        return this.session.getStartDate();
    }

    public List<Form> getTutorForms() {
        try {
            this.forms = getApplication().getFormService().getAllOfTutor(getCurrentTutor());
            return forms;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectForm(int position) {
        for (Form form : this.forms) {
            if (form.isSelected()) form.setItemSelected(false);
        }
        Form form = this.forms.get(position);
        form.setItemSelected(true);
        session.setForm(form);
    }

    public Session getSession() {
        return session;
    }

    public void setStartDate(Date startDate) {
        this.session.setStartDate(startDate);
        notifyPropertyChanged(BR.startDate);
    }
    public void save() {
        // Show a progress dialog while saving
        Dialog progress = Utilities.showLoadingDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.processando));

        // Perform the retrieval of sessions in a background thread
        getExecutorService().execute(() -> {
            Ronda ronda = this.session.getRonda();
            List<Session> sessions;
            sessions = getApplication().getSessionService().getAllOfRondaAndMentee(ronda, this.session.getTutored());
            ronda.setSessions(sessions);

            // Validate session dates on the background thread
            if (Utilities.listHasElements(ronda.getSessions())) {
                // Find the session with the greatest endDate (ignoring nulls)
                Session latestSession = null;
                for (Session s : ronda.getSessions()) {
                    if (s.getEndDate() == null) continue; // Pula sessões sem data de fim

                    if (latestSession == null || latestSession.getEndDate() == null ||
                            s.getEndDate().after(latestSession.getEndDate())) {
                        latestSession = s;
                    }
                }


                // Validate that this.session's startDate is not before the latest session's endDate
                if (latestSession != null && latestSession.getEndDate() != null && DateUtilities.isDateBeforeIgnoringTime(this.session.getStartDate(), latestSession.getEndDate())) {
                    runOnMainThread(() -> {
                        progress.dismiss(); // Dismiss progress dialog before showing the error
                        String dateError = getRelatedActivity().getString(R.string.prev_session_start_date_error);
                        Utilities.displayAlertDialog(getRelatedActivity(), dateError).show();
                    });
                    return;
                }
            }

            // Additional validations on the main thread
            runOnMainThread(() -> {
                if (DateUtilities.isDateBeforeIgnoringTime(this.session.getStartDate(), this.session.getRonda().getStartDate())) {
                    progress.dismiss(); // Dismiss progress dialog before showing the error
                    String startDateError = getRelatedActivity().getString(R.string.session_start_date_error);
                    Utilities.displayAlertDialog(getRelatedActivity(), startDateError).show();
                    return;
                }
                if (DateUtilities.isDateAfterIgnoringTime(this.session.getStartDate(), DateUtilities.getCurrentDate())) {
                    progress.dismiss();
                    String futureDateError = getRelatedActivity().getString(R.string.session_start_date_future_error);
                    Utilities.displayAlertDialog(getRelatedActivity(), futureDateError).show();
                    return;
                }
                if (this.session.getForm() == null) {
                    progress.dismiss(); // Dismiss progress dialog before showing the error
                    String selectTableError = getRelatedActivity().getString(R.string.select_competence_table);
                    Utilities.displayAlertDialog(getRelatedActivity(), selectTableError).show();
                    return;
                }

                // Perform the save operation
                performSave(progress);
            });
        });
    }

    private void performSave(Dialog progress) {
        // Perform the save operation in a background thread
        getExecutorService().execute(() -> {
            try {
                // Perform the database operations in the background
                if (getApplication().getApplicationStep().isApplicationStepEdit()) {
                    getApplication().getSessionService().update(this.session);
                } else {
                    getApplication().getSessionService().save(this.session);
                }

                // After saving, dismiss progress dialog and finish the activity on the main thread
                runOnMainThread(() -> {
                    progress.dismiss(); // Dismiss the progress dialog
                    getCurrentStep().changeToList();
                    getRelatedActivity().finish(); // Finish the activity
                });

            } catch (SQLException e) {
                Log.e("SessionVM", "save: " + e.getMessage());

                // Handle any database error on the main thread
                runOnMainThread(() -> {
                    progress.dismiss(); // Dismiss the progress dialog on error
                    String saveError = getRelatedActivity().getString(R.string.session_save_error);
                    Utilities.displayAlertDialog(getRelatedActivity(), saveError).show();
                });
            }
        });
    }




    public void setSession(Session session) {
        this.session = session;
    }

    public void setCurrRonda(Ronda ronda) {
        this.session.setRonda(ronda);
    }

    public void setMentee(Tutored mentee) {
        this.session.setTutored(mentee);
    }

    public void setSelectedForm() {
        for (Form form : this.forms) {
            if (form.equals(this.session.getForm())) {
                form.setItemSelected(true);
                break;
            }
        }
    }
}
