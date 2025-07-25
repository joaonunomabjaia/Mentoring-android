package mz.org.csaude.mentoring.viewmodel.session;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionSummary;
import mz.org.csaude.mentoring.util.PDFGenerator;
import mz.org.csaude.mentoring.util.Utilities;
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

    @SuppressLint("StaticFieldLeak")
    public void downloadFile() {
        new AsyncTask<Void, Void, Boolean>() {
            private Dialog progress;

            @Override
            protected void onPreExecute() {
                progress = Utilities.showLoadingDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.processando));
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                return PDFGenerator.createPDF(getRelatedActivity(), sessionSummaryList, session.getTutored());
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissProgress(progress);
                String message = result ? getRelatedActivity().getString(R.string.print_success) : getRelatedActivity().getString(R.string.print_failure);
                Utilities.displayAlertDialog(getRelatedActivity(), message).show();
            }
        }.execute();
    }

    public void generateAndWritePDFToUri(Uri uri) {
        new AsyncTask<Void, Void, Boolean>() {
            private Dialog progress;

            @Override
            protected void onPreExecute() {
                progress = Utilities.showLoadingDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.processando));
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try (OutputStream out = getRelatedActivity().getContentResolver().openOutputStream(uri)) {
                    return PDFGenerator.createPDF(out, getRelatedActivity(), sessionSummaryList, session.getTutored());
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissProgress(progress);
                String message = result ? getRelatedActivity().getString(R.string.print_success) : getRelatedActivity().getString(R.string.print_failure);
                Utilities.displayAlertDialog(getRelatedActivity(), message).show();
            }
        }.execute();
    }



    public List<SessionSummary> getSessionSummaryList() {
        return sessionSummaryList;
    }

    public void generateSessionSummary() {
        sessionSummaryList = getApplication().getSessionService().generateSessionSummary(session, true);
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
