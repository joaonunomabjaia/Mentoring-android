package mz.org.csaude.mentoring.viewmodel.session;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Application;
import android.app.Dialog;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionStatus;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.session.SessionClosureActivity;
import mz.org.csaude.mentoring.view.session.SessionEAResourceActivity;

public class SessionClosureVM extends BaseViewModel {
    private Session session;

    private boolean initialDataVisible;
    private boolean fourthSession;

    public SessionClosureVM(@NonNull Application application) {
        super(application);
    }

    @Override
    public void preInit() {
        getExecutorService().execute(()->{
            this.fourthSession = getApplication().getSessionService().countAllOfRondaAndMentee(session.getRonda(), session.getTutored()) == 4;
        });
    }

    @Bindable
    public String getSessionStrongPoints() {
        return session.getStrongPoints();
    }

    public void setSessionStrongPoints(String strongPoints) {
        session.setStrongPoints(strongPoints);
        notifyPropertyChanged(BR.sessionStrongPoints);
    }

    @Bindable
    public String getPointsToImprove() {
        return session.getPointsToImprove();
    }

    public void setPointsToImprove(String strongPoints) {
        session.setPointsToImprove(strongPoints);
        notifyPropertyChanged(BR.pointsToImprove);
    }

    public void setObsevations(String strongPoints) {
        session.setObservations(strongPoints);
        notifyPropertyChanged(BR.obsevations);
    }

    @Bindable
    public String getObsevations() {
        return session.getObservations();
    }
    @Override
    public SessionClosureActivity getRelatedActivity() {
        return (SessionClosureActivity) super.getRelatedActivity();
    }

    @Bindable
    public String getWorkPlan() {
        return session.getWorkPlan();
    }

    public void setWorkPlan(String strongPoints) {
        session.setWorkPlan(strongPoints);
        notifyPropertyChanged(BR.workPlan);
    }

    @Bindable
    public boolean isInitialDataVisible() {
        return initialDataVisible;
    }

    public void setInitialDataVisible(boolean initialDataVisible) {
        this.initialDataVisible = initialDataVisible;
        this.notifyPropertyChanged(BR.initialDataVisible);
    }

    @Bindable
    public Date getNextSessionDate() {
        return this.session.getNextSessionDate();
    }

    public void setNextSessionDate(Date nextSessionDate) {
        this.session.setNextSessionDate(nextSessionDate);
        notifyPropertyChanged(BR.nextSessionDate);
    }

    public boolean isFourthSession() {
        return fourthSession;
    }

    @Bindable
    public Date getEndDate() {
        return this.session.getEndDate();
    }

    public void setEndtDate(Date startDate) {
        this.session.setEndDate(startDate);
        notifyPropertyChanged(BR.endDate);
    }


    public void nextStep() {
        Dialog progress = Utilities.showLoadingDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.processando));

        getExecutorService().execute(() -> {
            try {
                session.setStatus(getApplication().getSessionStatusService().getByCode(SessionStatus.COMPLETE));
                session.setSyncStatus(SyncSatus.PENDING);

                // Check if the end date is null
                if (session.getEndDate() == null) {
                    getRelatedActivity().runOnUiThread(() -> {
                        if (progress != null && progress.isShowing()) progress.dismiss();
                        String errorMessage = getRelatedActivity().getString(R.string.session_end_date_null);
                        Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                    });
                    return;
                }

                // Validation logic
                if (DateUtilities.isDateBeforeIgnoringTime(session.getEndDate(), session.getStartDate())) {
                    getRelatedActivity().runOnUiThread(() -> {
                        if (progress != null && progress.isShowing()) progress.dismiss();
                        String errorMessage = getRelatedActivity().getString(R.string.session_end_date_before_start);
                        Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                    });
                    return;
                }

                if (sessionCloseDateBeforeLastMentorship()) {
                    getRelatedActivity().runOnUiThread(() -> {
                        if (progress != null && progress.isShowing()) progress.dismiss();
                        String errorMessage = getRelatedActivity().getString(R.string.session_end_date_before_last_mentorship);
                        Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                    });
                    return;
                }

                if (session.getNextSessionDate() != null) {

                    if (DateUtilities.isDateBeforeIgnoringTime(session.getNextSessionDate(), session.getEndDate())) {
                        getRelatedActivity().runOnUiThread(() -> {
                            if (progress != null && progress.isShowing()) progress.dismiss();
                            String errorMessage = getRelatedActivity().getString(R.string.session_next_date_before_end);
                            Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                        });
                        return;
                    }
                }

                // Perform updates in the background
                getApplication().getSessionService().update(session);
                session.getRonda().setRondaMentors(getApplication().getRondaMentorService().getRondaMentors(session.getRonda()));
                getApplication().getRondaService().tryToCloseRonda(session.getRonda(), session.getEndDate());

                // UI transition must be done on the main thread
                getRelatedActivity().runOnUiThread(() -> {
                    if (progress != null && progress.isShowing()) progress.dismiss();
                    Map<String, Object> params = new HashMap<>();
                    session.setMentorships(Collections.emptyList());
                    session.getForm().setFormSections(Collections.emptyList());
                    session.getRonda().setSessions(Collections.emptyList());

                    params.put("session", session);
                    getRelatedActivity().nextActivity(SessionEAResourceActivity.class, params);
                });

            } catch (SQLException e) {
                Log.e(TAG, e.getMessage());

                // Handle error on the main thread
                getRelatedActivity().runOnUiThread(() -> {
                    if (progress != null && progress.isShowing()) progress.dismiss();
                    String errorMessage = getRelatedActivity().getString(R.string.session_update_error);
                    Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                });
            }
        });
    }



    private boolean sessionCloseDateBeforeLastMentorship() {
        for (Mentorship mentorship : session.getMentorships()) {
            if (DateUtilities.isDateBeforeIgnoringTime(session.getEndDate(), mentorship.getEndDate())) {
                return true;
            }
        }
        return false;
    }

    public void changeInitialDataViewStatus(View view){
        getRelatedActivity().changeFormSectionVisibility(view);
    }


    public void saveAndContinue(){
        //
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
