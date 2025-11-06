package mz.org.csaude.mentoring.viewmodel.resource;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.searchparams.AbstractSearchParams;
import mz.org.csaude.mentoring.base.viewModel.SearchVM;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.listner.rest.ServerStatusListener;
import mz.org.csaude.mentoring.model.resourceea.Node;
import mz.org.csaude.mentoring.model.resourceea.Resource;
import mz.org.csaude.mentoring.util.Utilities;

public class ResourceVM extends SearchVM<Resource>
        implements ServerStatusListener, RestResponseListener<Resource> {

    private String searchText;
    private List<Node> nodeList;

    private boolean hivChecked;
    private boolean tbChecked;

    private Node selectedNode;

    public ResourceVM(@NonNull Application application) {
        super(application);
    }

    // === UX: M3 empty-state handles "no records", so don't show dialog ===
    @Override
    protected void doOnNoRecordFound() {
        // Just delegate to activity to update the UI (empty-state text will appear)
        getRelatedActivity().displaySearchResults();
    }

    @Override
    public void preInit() {
        setHivChecked(true);
        setTbChecked(true);
    }

    // === Bindable: search text from SearchView ===
    @Bindable
    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
        notifyPropertyChanged(BR.searchText);
        // Debounce + triggering is done by the Activity; no auto-search here.
    }

    @Override
    public List<Resource> doSearch(long offset, long limit) throws SQLException {
        // Full payload; we'll filter in displaySearchResults() to keep behavior consistent.
        return getApplication().getResourceService().getAll();
    }

    @Override
    public void displaySearchResults() {
        // Parse the first (and only) resource JSON payload if available.
        if (!Utilities.listHasElements(getSearchResults())) {
            // no data; show empty state
            ensureNodeListCleared();
            getRelatedActivity().displaySearchResults();
            return;
        }

        String payload = getSearchResults().get(0).getResource();
        if (!Utilities.stringHasValue(payload)) {
            ensureNodeListCleared();
            getRelatedActivity().displaySearchResults();
            return;
        }

        try {
            JSONArray jsonArray = new JSONArray(payload);

            List<JSONObject> filteredChildren =
                    getApplication().getResourceService()
                            .getChildrenWithNameAndDescription(jsonArray, null, null, null);

            if (this.nodeList == null) this.nodeList = new ArrayList<>();
            else this.nodeList.clear();

            List<Node> nodes = getApplication().getResourceService().convertToNodeList(filteredChildren);

            if (Utilities.listHasElements(nodes)) {
                final String q = Utilities.stringHasValue(getSearchText())
                        ? getSearchText().trim().toLowerCase()
                        : null;

                // 1) text filter (case-insensitive)
                for (Node node : nodes) {
                    if (q == null) {
                        this.nodeList.add(node);
                        continue;
                    }
                    String name = node.getName() == null ? "" : node.getName();
                    if (name.toLowerCase().contains(q)) {
                        this.nodeList.add(node);
                    }
                }

                // 2) chip filters (HIV/TB) — remove items from the working list accordingly
                if (!isHivChecked() || !isTbChecked()) {
                    Iterator<Node> iterator = this.nodeList.iterator();
                    while (iterator.hasNext()) {
                        Node node = iterator.next();
                        String program = node.getProgram();
                        String programLower = program == null ? "" : program.toLowerCase();

                        boolean isHiv = programLower.contains("hiv");
                        boolean isTb  = programLower.contains("tb");

                        // If HIV chip is off, remove HIV items
                        if (!isHivChecked() && isHiv) {
                            iterator.remove();
                            continue;
                        }
                        // If TB chip is off, remove TB items
                        if (!isTbChecked() && isTb) {
                            iterator.remove();
                        }
                    }
                }
            }

            // If nothing left, defer to empty-state; no dialog popups
            if (!Utilities.listHasElements(this.nodeList)) {
                doOnNoRecordFound();
                return;
            }

        } catch (JSONException e) {
            // In case of invalid payload, show empty-state
            ensureNodeListCleared();
        }

        getRelatedActivity().displaySearchResults();
    }

    private void ensureNodeListCleared() {
        if (this.nodeList == null) this.nodeList = new ArrayList<>();
        else this.nodeList.clear();
    }

    // === Bindables for chips ===
    @Bindable
    public boolean isHivChecked() {
        return hivChecked;
    }

    public void setHivChecked(boolean hivChecked) {
        this.hivChecked = hivChecked;
        notifyPropertyChanged(BR.hivChecked);
    }

    @Bindable
    public boolean isTbChecked() {
        return tbChecked;
    }

    public void setTbChecked(boolean tbChecked) {
        this.tbChecked = tbChecked;
        notifyPropertyChanged(BR.tbChecked);
    }

    // Toggle helpers (called by Chip onClick) — refresh results immediately
    public void changeHivChecked() {
        setHivChecked(!isHivChecked());
        initSearch(); // re-run with new filter
    }

    public void changeTBChecked() {
        setTbChecked(!isTbChecked());
        initSearch(); // re-run with new filter
    }

    @Override
    public AbstractSearchParams<Resource> initSearchParams() {
        return null; // not used; SearchVM will call doSearch directly
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    // === Download flow ===
    public void downloadResource() {
        getApplication().isServerOnline(this);
    }

    @Override
    public void onServerStatusChecked(boolean isOnline, boolean isSlow) {
        if (isOnline) {
            if (isSlow) {
                showSlowConnectionWarning(getRelatedActivity());
            }
            getApplication().getResourceRestService().downloadFile(
                    selectedNode != null ? selectedNode.getName() : null, this);
        } else {
            Utilities.displayAlertDialog(getRelatedActivity(),
                    getRelatedActivity().getString(R.string.server_unavailable)).show();
        }
    }

    @Override
    public void doOnRestSucessResponse(String flag) {
        Utilities.displayAlertDialog(getRelatedActivity(),
                getRelatedActivity().getString(R.string.download_success)).show();
    }

    @Override
    public void doOnRestErrorResponse(String errormsg) {
        Utilities.displayAlertDialog(getRelatedActivity(),
                getRelatedActivity().getString(R.string.download_failed)).show();
    }

    public void setSelectNode(Node node) {
        this.selectedNode = node;
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    public void downloadResourceToUri(Uri uri) {
        getApplication().getResourceRestService().downloadFileToUri(
                selectedNode != null ? selectedNode.getName() : null,
                uri,
                this
        );
    }
}
