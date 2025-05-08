package mz.org.csaude.mentoring.viewmodel.session;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Application;
import android.app.Dialog;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.searchparams.AbstractSearchParams;
import mz.org.csaude.mentoring.base.viewModel.SearchVM;
import mz.org.csaude.mentoring.listner.dialog.IDialogListener;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.resourceea.Node;
import mz.org.csaude.mentoring.model.resourceea.Resource;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionRecommendedResource;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.session.SessionClosureActivity;
import mz.org.csaude.mentoring.view.session.SessionSummaryActivity;

public class SessionResourcesVM extends SearchVM<Resource> implements IDialogListener {

    private boolean recommendResources;

    private String searchText;
    private List<Node> nodeList = new ArrayList<>();

    private List<SessionRecommendedResource> recommendedResources;
    private Session session;

    private boolean hivChecked;

    private boolean tbChecked;
    public SessionResourcesVM(@NonNull Application application) {
        super(application);
    }

    @Override
    public void preInit() {

    }

    @Bindable
    public boolean isRecommendResources() {
        return recommendResources;
    }


    public void setRecommendResources(boolean recommendResources) {
        this.recommendResources = recommendResources;
        notifyPropertyChanged(BR.recommendResources);
    }

    public void changeRecommendResourcesStatus(){
        setRecommendResources(!isRecommendResources());
    }
    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public List<Resource> doSearch(long offset, long limit) throws SQLException {
        return getApplication().getResourceService().getAll();
    }

    @Bindable
    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
        notifyPropertyChanged(BR.searchText);
    }

    @Override
    public void displaySearchResults() {
        try {
            JSONArray jsonArray = new JSONArray(getSearchResults().get(0).getResource());

            List<JSONObject> filteredChildren = getApplication().getResourceService().getChildrenWithNameAndDescription(jsonArray, null, null, null);
            this.nodeList.clear();

            List<Node> nodes = getApplication().getResourceService().convertToNodeList(filteredChildren);
            if (Utilities.listHasElements(nodes)) {
                if (Utilities.stringHasValue(getSearchText())) {
                    for (Node node : nodes) {
                        if (node.getName().contains(getSearchText())) {
                            this.nodeList.add(node);
                        }
                    }
                } else this.nodeList.addAll(nodes);

                if (!isHivChecked() || !isTbChecked()) {
                    Iterator<Node> iterator = this.nodeList.iterator();
                    while (iterator.hasNext()) {
                        Node node = iterator.next();
                        String programLower = node.getProgram().toLowerCase();
                        if ((!isHivChecked() && programLower.contains("hiv")) ||
                                (!isTbChecked() && programLower.contains("tb"))) {
                            iterator.remove();
                        }
                    }
                }
            }
            if (!Utilities.listHasElements(nodeList)) {
                doOnNoRecordFound();
                return;
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        getRelatedActivity().displaySearchResults();
    }

    @Override
    public AbstractSearchParams<Resource> initSearchParams() {
        return null;
    }

    @Override
    protected void doOnNoRecordFound() {
        Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.no_results_found)).show();
    }

    public void closeSession() {
        Utilities.displayConfirmationDialog(
                getRelatedActivity(),
                getRelatedActivity().getString(R.string.confirm_end_session),
                getRelatedActivity().getString(R.string.yes),
                getRelatedActivity().getString(R.string.no),
                this
        ).show();
    }

    @Override
    public void doOnConfirmed() {
        // close session
        goToSessionSummary();
    }

    private void goToSessionSummary() {
        Dialog progress = Utilities.showLoadingDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.processando));

        getExecutorService().execute(() -> {
            try {
                // Update session in the background
                getApplication().getSessionService().update(session);

                if (isRecommendResources()) {
                    if (!Utilities.listHasElements(recommendedResources)) {
                        // Show error dialog on the main thread
                        runOnMainThread(() -> {
                            dismissProgress(progress);
                            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.no_resource_selected_error)).show();
                        });
                        return;
                    }

                    // Save recommended resources in the background
                    getApplication().getSessionService().saveRecommendedResources(session, recommendedResources);
                }

                // Proceed to session summary on the main thread
                runOnMainThread(() -> {
                    dismissProgress(progress);
                    Map<String, Object> params = new HashMap<>();
                    session.setMentorships(Collections.emptyList());
                    params.put("session", session);
                    getRelatedActivity().nextActivityFinishingCurrent(SessionSummaryActivity.class, params);
                });

            } catch (SQLException e) {
                Log.e(TAG, "goToSessionSummary: ", e.getCause());

                // Handle error on the main thread
                runOnMainThread(() -> {
                    dismissProgress(progress);
                    Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.session_update_error)).show();
                });
            }
        });
    }


    @Override
    public void doOnDeny() {

    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void selectResource(int position) {
        Node node = this.nodeList.get(position);
        node.setItemSelected(!node.isSelected());

        if (node.isSelected()) {
            if (recommendedResources == null) {
                recommendedResources = new ArrayList<>();
            }
            recommendedResources.add(new SessionRecommendedResource(session, node));
        } else {
            recommendedResources.remove(new SessionRecommendedResource(session, node));
        }
    }

    public void changeHivChecked(){
        setHivChecked(!isHivChecked());
    }

    public void changeTBChecked(){
        setTbChecked(!isTbChecked());
    }


    @Bindable
    public boolean isHivChecked() {
        return hivChecked;
    }

    public void setHivChecked(boolean hivChecked) {
        this.hivChecked = hivChecked;
    }

    @Bindable
    public boolean isTbChecked() {
        return tbChecked;
    }

    public void setTbChecked(boolean tbChecked) {
        this.tbChecked = tbChecked;
        notifyPropertyChanged(BR.tbChecked);
    }
}
