package mz.org.csaude.mentoring.viewmodel.resource;

import android.app.Application;

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
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.base.viewModel.SearchVM;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.listner.rest.ServerStatusListener;
import mz.org.csaude.mentoring.model.resourceea.Node;
import mz.org.csaude.mentoring.model.resourceea.Resource;
import mz.org.csaude.mentoring.service.resource.ResourceService;
import mz.org.csaude.mentoring.util.Utilities;


public class ResourceVM extends SearchVM<Resource> implements ServerStatusListener, RestResponseListener<Resource>{

    private String searchText;
    private List<Node> nodeList;

    private boolean hivChecked;

    private Node selectedNode;
    private boolean tbChecked;

    public ResourceVM(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void doOnNoRecordFound() {
        Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.no_record_found)).show();
        getRelatedActivity().displaySearchResults();
    }

    @Override
    public void preInit() {
        setHivChecked(true);
        setTbChecked(true);
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
    public List<Resource> doSearch(long offset, long limit) throws SQLException {
        return getApplication().getResourceService().getAll();
    }

    @Override
    public void displaySearchResults() {
        try {
            JSONArray jsonArray = new JSONArray(getSearchResults().get(0).getResource());

            List<JSONObject> filteredChildren = getApplication().getResourceService().getChildrenWithNameAndDescription(jsonArray, null, null, null);
            if (this.nodeList == null) {
                this.nodeList = new ArrayList<>();
            } else {
                this.nodeList.clear();
            }

            List<Node> nodes = getApplication().getResourceService().convertToNodeList(filteredChildren);

            if (Utilities.listHasElements(nodes)) {
                if (Utilities.stringHasValue(getSearchText())) {
                    for (Node node : nodes) {
                        if (node.getName().contains(getSearchText())) {
                            this.nodeList.add(node);
                        }
                    }
                } else {
                    this.nodeList.addAll(nodes);
                }

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

            if (!Utilities.listHasElements(this.nodeList)) {
                doOnNoRecordFound();
                return;
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        getRelatedActivity().displaySearchResults();
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

    public void changeHivChecked(){
        setHivChecked(!isHivChecked());
    }

    public void changeTBChecked(){
        setTbChecked(!isTbChecked());
    }

    @Override
    public AbstractSearchParams<Resource> initSearchParams() {
        return null;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void downloadResource() {
        getApplication().isServerOnline(this);
    }

    @Override
    public void onServerStatusChecked(boolean isOnline, boolean isSlow) {
        if (isOnline) {
            if (isSlow) {
                // Show warning: Server is slow
                showSlowConnectionWarning(getRelatedActivity());
            }
            getApplication().getResourceRestService().downloadFile(selectedNode.getName(), this);
        } else {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.server_unavailable)).show();
        }
    }

    @Override
    public void doOnRestSucessResponse(String flag) {
        Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.download_success)).show();
    }

    @Override
    public void doOnRestErrorResponse(String errormsg) {
        Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.download_failed)).show();
    }

    public void setSelectNode(Node node) {
        this.selectedNode = node;
    }
}
