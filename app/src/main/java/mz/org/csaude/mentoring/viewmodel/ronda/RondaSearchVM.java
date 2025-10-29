package mz.org.csaude.mentoring.viewmodel.ronda;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.searchparams.AbstractSearchParams;
import mz.org.csaude.mentoring.base.viewModel.SearchVM;
import mz.org.csaude.mentoring.listner.dialog.IDialogListener;
import mz.org.csaude.mentoring.listner.rest.ServerStatusListener;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.ronda.RondaSummary;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.model.rondatype.RondaTypeCode;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionSummary;
import mz.org.csaude.mentoring.util.PDFGenerator;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.mentorship.ZeroMentorshipListActivity;
import mz.org.csaude.mentoring.view.ronda.CreateRondaActivity;
import mz.org.csaude.mentoring.view.ronda.RondaActivity;
import mz.org.csaude.mentoring.view.session.SessionListActivity;

public class RondaSearchVM extends SearchVM<Ronda> implements IDialogListener, ServerStatusListener {

    // Legacy entity (kept for intents/compat)
    private RondaType rondaType;

    private String title;
    private Ronda selectedRonda;
    private Dialog progress;

    // Enum-first flow for logic/UI
    private RondaTypeCode rondaTypeCode = RondaTypeCode.MENTORIA_INTERNA;

    public RondaSearchVM(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void doOnNoRecordFound() {
        getRelatedActivity().populateRecyclerView();
    }

    @Override
    public void preInit() { }

    // --- Create / Navigation ---
    public void createNewRonda() {
        Map<String, Object> params = new HashMap<>();

        // If caller didn't set the entity, resolve from enum now
        if (rondaType == null) {
            try {
                rondaType = getApplication().getRondaTypeService()
                        .getRondaTypeByCode(rondaTypeCode.code());
            } catch (SQLException ignored) { }
        }

        params.put("rondaType", rondaType);
        params.put("title", title);
        getApplication().getApplicationStep().changetocreate();
        getRelatedActivity().nextActivityFinishingCurrent(CreateRondaActivity.class, params);
    }

    // --- Title ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    // --- RondaType (entity + enum) ---
    public RondaType getRondaType() { return rondaType; }

    /** Called when another screen passes the entity in the Intent */
    public void setRondaType(RondaType entity) {
        this.rondaType = entity;
        this.rondaTypeCode = (entity == null)
                ? RondaTypeCode.MENTORIA_INTERNA
                : RondaTypeCode.fromCode(entity.getCode());
    }

    /** Called by bottom-nav changes */
    public void setRondaType(RondaTypeCode code) {
        this.rondaTypeCode = (code == null) ? RondaTypeCode.MENTORIA_INTERNA : code;
        // keep entity lazy; resolve when needed
        this.rondaType = null;
    }

    public RondaTypeCode getRondaTypeCode() { return rondaTypeCode; }

    // --- Search ---
    @Override
    public List<Ronda> doSearch(long offset, long limit) throws SQLException {
        RondaType typeEntity = getApplication().getRondaTypeService().getRondaTypeByCode(rondaTypeCode.code());
        this.rondaType = typeEntity;
        return getApplication().getRondaService().search(typeEntity, getQuery(), getApplication().getCurrMentor());
    }


    @Override
    public void displaySearchResults() {
        getRelatedActivity().populateRecyclerView();
    }

    @Override
    public RondaActivity getRelatedActivity() {
        return (RondaActivity) super.getRelatedActivity();
    }

    @Override
    public AbstractSearchParams<Ronda> initSearchParams() {
        return null;
    }

    public void goToMentoriships(Ronda ronda) {
        getApplication().getApplicationStep().changeToList();
        Map<String, Object> params = new HashMap<>();
        params.put("ronda", ronda);

        if (ronda.isRondaZero()) {
            Log.d("goToMentoriships", "Navigating to ZeroMentorshipListActivity with params: " + params);
            getRelatedActivity().nextActivity(ZeroMentorshipListActivity.class, params);
        } else {
            Log.d("goToMentoriships", "Navigating to SessionListActivity with params: " + params);
            ronda.setSessions(Collections.emptyList());
            getRelatedActivity().nextActivity(SessionListActivity.class, params);
        }
    }

    public void printRondaSummary(Ronda ronda) {
        this.selectedRonda = ronda;
        getRelatedActivity().checkStoragePermission();
    }

    @SuppressLint("StaticFieldLeak")
    public void printRondaReport() {
        new AsyncTask<Void, Void, Boolean>() {
            private Dialog progress;

            @Override
            protected void onPreExecute() {
                progress = Utilities.showLoadingDialog(getRelatedActivity(),
                        getRelatedActivity().getString(R.string.processando));
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    List<RondaSummary> rondaSummaryList = generateRondaSummaries(selectedRonda);
                    return PDFGenerator.createRondaSummary(getRelatedActivity(), rondaSummaryList);
                } catch (SQLException e) {
                    Log.e("printRondaReport", "Exception: ", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean printSuccessful) {
                dismissProgress(progress);
                showPrintResultMessage(printSuccessful, progress);
            }
        }.execute();
    }

    private List<RondaSummary> generateRondaSummaries(Ronda selectedRonda) throws SQLException {
        List<RondaSummary> rondaSummaryList = new ArrayList<>();
        Ronda ronda = getApplication().getRondaService().getFullyLoadedRonda(selectedRonda);

        for (RondaMentee mentee : ronda.getRondaMentees()) {
            RondaSummary rondaSummary = new RondaSummary();
            rondaSummary.setRonda(ronda);
            rondaSummary.setZeroEvaluation(mentee.getTutored().getZeroEvaluationScore());
            rondaSummary.setMentor(ronda.getActiveMentor().getEmployee().getFullName());
            rondaSummary.setMentee(mentee.getTutored().getEmployee().getFullName());
            rondaSummary.setNuit(mentee.getTutored().getEmployee().getNuit());

            List<Session> sessions = getSessionsForMentee(ronda, mentee);
            rondaSummary.setSummaryDetails(generateSessionSummaries(sessions));
            assignSessionScores(rondaSummary);

            rondaSummaryList.add(rondaSummary);
        }
        return rondaSummaryList;
    }

    private List<Session> getSessionsForMentee(Ronda ronda, RondaMentee mentee) {
        List<Session> sessions = new ArrayList<>();
        for (Session session : ronda.getSessions()) {
            if (session.getTutored().equals(mentee.getTutored())) {
                sessions.add(session);
            }
        }
        sessions.sort(Comparator.comparing(Session::getStartDate));
        return sessions;
    }

    private Map<Integer, List<SessionSummary>> generateSessionSummaries(List<Session> sessions) throws SQLException {
        Map<Integer, List<SessionSummary>> summaryDetails = new HashMap<>();
        int i = 1;
        for (Session session : sessions) {
            summaryDetails.put(i, getApplication().getSessionService()
                    .generateSessionSummary(session, null, false));
            i++;
        }
        return summaryDetails;
    }

    private void assignSessionScores(RondaSummary rondaSummary) {
        for (int i = 1; i <= 4; i++) {
            List<SessionSummary> summaries = rondaSummary.getSummaryDetails().get(i);
            if (summaries != null) {
                double score = determineSessionScore(summaries);
                setSessionScore(rondaSummary, i, score);
            }
        }
    }

    private void setSessionScore(RondaSummary rondaSummary, int sessionNumber, double score) {
        switch (sessionNumber) {
            case 1: rondaSummary.setSession1(score); break;
            case 2: rondaSummary.setSession2(score); break;
            case 3: rondaSummary.setSession3(score); break;
            case 4: rondaSummary.setSession4(score); break;
        }
    }

    private void showPrintResultMessage(boolean printSuccessful, Dialog progress) {
        dismissProgress(progress);
        String message = printSuccessful
                ? getRelatedActivity().getString(R.string.ronda_print_success)
                : getRelatedActivity().getString(R.string.ronda_print_failure);
        Utilities.displayAlertDialog(getRelatedActivity(), message).show();
    }

    private double determineSessionScore(List<SessionSummary> sessionSummaries) {
        int yesCount = 0;
        int noCount = 0;
        for (SessionSummary sessionSummary : sessionSummaries){
            yesCount += sessionSummary.getSimCount();
            noCount += sessionSummary.getNaoCount();
        }
        return (double) yesCount / (yesCount + noCount) * 100;
    }

    public void edit(Ronda ronda) {
        getExecutorService().execute(() -> {
            try {
                ronda.setSessions(getApplication().getSessionService().getAllOfRonda(ronda));
                runOnMainThread(() -> {
                    if (Utilities.listHasElements(ronda.getSessions())) {
                        Utilities.displayAlertDialog(getRelatedActivity(),
                                getRelatedActivity().getString(R.string.ronda_edit_error_msg)).show();
                        return;
                    }
                    navigateToEditRonda(ronda);
                });

            } catch (SQLException e) {
                runOnMainThread(() -> {
                    Log.e("Ronda Search VM", "Exception: " + e.getMessage());
                    String errorMessage = getRelatedActivity().getString(R.string.ronda_session_error);
                    Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                });
            }
        });
    }

    private void navigateToEditRonda(Ronda ronda) {
        Map<String, Object> params = new HashMap<>();
        params.put("ronda", ronda);
        params.put("title", ronda.isRondaZero()
                ? getRelatedActivity().getString(R.string.ronda_zero)
                : getRelatedActivity().getString(R.string.ronda_mentoria));

        getApplication().getApplicationStep().changeToEdit();
        getRelatedActivity().nextActivityFinishingCurrent(CreateRondaActivity.class, params);
    }

    public void delete(Ronda ronda) {
        this.selectedRonda = ronda;

        getExecutorService().execute(() -> {
            try {
                List<Session> sessions = getApplication().getSessionService().getAllOfRonda(ronda);
                ronda.setSessions(sessions);

                runOnMainThread(() -> {
                    if (Utilities.listHasElements(ronda.getSessions())) {
                        String errorMessage = getRelatedActivity().getString(R.string.ronda_delete_error_msg);
                        Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                        return;
                    }

                    String confirmationMessage = getRelatedActivity().getString(R.string.ronda_delete_confirmation);
                    Utilities.displayConfirmationDialog(getRelatedActivity(), confirmationMessage,
                            getRelatedActivity().getString(R.string.yes),
                            getRelatedActivity().getString(R.string.no), this).show();
                });

            } catch (SQLException e) {
                runOnMainThread(() -> {
                    Log.e("Ronda Search VM", "Exception: " + e.getMessage());
                    String errorMessage = getRelatedActivity().getString(R.string.ronda_session_error);
                    Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                });
            }
        });
    }

    @Override
    public void doOnConfirmed() {
        this.progress = Utilities.showLoadingDialog(getRelatedActivity(),
                getRelatedActivity().getString(R.string.processando));
        getApplication().getApplicationStep().changeToRemove();
        getApplication().isServerOnline(this);
    }

    @Override
    public void doOnDeny() { }

    @Override
    public void onServerStatusChecked(boolean isOnline, boolean isSlow) {
        if (isOnline) {
            if (isSlow) {
                showSlowConnectionWarning(getRelatedActivity());
            }
            if (getApplication().getApplicationStep().isApplicationStepRemove()) {
                getApplication().getRondaRestService().delete(selectedRonda, this);
            }
        }
    }

    @Override
    public void doOnResponse(String flag, List<Ronda> objects) {
        if (getApplication().getApplicationStep().isApplicationStepRemove()) {
            getApplication().getApplicationStep().changeToList();
            dismissProgress(this.progress);
            initSearch();
        }
    }

    @Override
    public void doOnRestErrorResponse(String errormsg) {
        if (getApplication().getApplicationStep().isApplicationStepRemove()) {
            getApplication().getApplicationStep().changeToList();
            dismissProgress(this.progress);
            Utilities.displayAlertDialog(getRelatedActivity(), errormsg).show();
        }
    }
}
