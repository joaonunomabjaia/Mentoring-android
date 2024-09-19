package mz.org.csaude.mentoring.view.resource;



import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.resource.ResourceAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivityResourceBinding;
import mz.org.csaude.mentoring.model.resourceea.Node;
import mz.org.csaude.mentoring.model.resourceea.Resource;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.viewmodel.resource.ResourceVM;

public class ResourceActivity extends BaseActivity {

    ActivityResourceBinding activityResourceBinding;

    private RecyclerView rcvResources;

    private ResourceAdapter resourceAdapter;
    List<Resource> resourceList = new ArrayList<>();
    List<Node> nodeList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityResourceBinding = DataBindingUtil.setContentView(this , R.layout.activity_resource);
        activityResourceBinding.setViewModel(this.getRelatedViewModel());



        this.rcvResources = activityResourceBinding.rcvResources;
        setUpToolbar();
    }

    private void setUpToolbar() {

        setSupportActionBar(activityResourceBinding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.ea_resources_title));

        getRelatedViewModel().setViewListEditButton(false);
        getRelatedViewModel().setViewListRemoveButton(false);

    }

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(ResourceVM.class);
    }

    @Override
    public ResourceVM getRelatedViewModel() {
        return (ResourceVM) super.getRelatedViewModel();
    }

    @Override
    public void displaySearchResults() {
        super.displaySearchResults();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rcvResources.setLayoutManager(mLayoutManager);
        rcvResources.setItemAnimator(new DefaultItemAnimator());
        rcvResources.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 0));

        resourceAdapter = new ResourceAdapter(rcvResources, getRelatedViewModel().getNodeList(), this);
        rcvResources.setAdapter(resourceAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getRelatedViewModel().downloadResource();
        } else {
            Utilities.displayAlertDialog(this, getString(R.string.permission_error)).show();
        }
    }

    public void downloadResource(Node node) {
        getRelatedViewModel().setSelectNode(node);
        boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        } else getRelatedViewModel().downloadResource();
    }
}