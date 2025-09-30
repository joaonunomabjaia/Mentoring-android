package mz.org.csaude.mentoring.view.resource;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.resource.ResourceAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivityResourceBinding;
import mz.org.csaude.mentoring.model.resourceea.Node;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.viewmodel.resource.ResourceVM;

public class ResourceActivity extends BaseActivity {

    private static final int REQUEST_WRITE_STORAGE = 2002;

    ActivityResourceBinding activityResourceBinding;
    private RecyclerView rcvResources;
    private ResourceAdapter resourceAdapter;

    private final ActivityResultLauncher<Intent> createFileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    getRelatedViewModel().downloadResourceToUri(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityResourceBinding = DataBindingUtil.setContentView(this, R.layout.activity_resource);
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

    public void downloadResource(Node node) {
        getRelatedViewModel().setSelectNode(node);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startFileCreationFlow(node.getName());
        } else {
            // Apenas para Android 9 ou inferior
            boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            } else {
                startFileCreationFlow(node.getName());
            }
        }
    }

    private void startFileCreationFlow(String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        createFileLauncher.launch(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startFileCreationFlow(getRelatedViewModel().getSelectedNode().getName());
        } else {
            Utilities.displayAlertDialog(this, getString(R.string.permission_error)).show();
        }
    }
}
