package mz.org.csaude.mentoring.view.resource;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

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

    private ActivityResourceBinding binding;
    private RecyclerView rcvResources;
    private ResourceAdapter resourceAdapter;

    private final ActivityResultLauncher<Intent> createFileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    getRelatedViewModel().downloadResourceToUri(uri);
                }
            });

    // Debounce for search
    private final android.os.Handler searchHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DEBOUNCE_MS = 250L;
    private boolean suppressSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_resource);
        binding.setLifecycleOwner(this);
        binding.setViewModel(getRelatedViewModel());

        // Status bar color = colorPrimary with light icons off
        final var root = binding.getRoot();
        int colorPrimary = MaterialColors.getColor(root, com.google.android.material.R.attr.colorPrimary);
        getWindow().setStatusBarColor(colorPrimary);
        new WindowInsetsControllerCompat(getWindow(), root).setAppearanceLightStatusBars(false);

        setUpToolbar();

        rcvResources = binding.rcvResources;

        // SearchBar + SearchView setup
        setupSearch(binding.searchBar, binding.searchView);

        // Initial load
        getRelatedViewModel().initSearch();
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.ea_resources_title));

        getRelatedViewModel().setViewListEditButton(false);
        getRelatedViewModel().setViewListRemoveButton(false);
    }

    private void setupSearch(SearchBar searchBar, SearchView searchView) {
        searchView.setupWithSearchBar(searchBar);
        searchBar.setOnClickListener(v -> searchView.show());

        EditText et = searchView.getEditText();

        // Mirror existing query into UI
        String q = getRelatedViewModel().getSearchText();
        if (q != null && !q.isEmpty()) {
            searchBar.setText(q);
            et.setText(q);
            et.setSelection(q.length());
        }

        // Show/hide clear action
        final Runnable updateClear = () -> {
            if (searchBar.getMenu() != null) {
                var clear = searchBar.getMenu().findItem(R.id.action_clear);
                if (clear != null) {
                    boolean hasText = et.getText() != null && et.getText().length() > 0;
                    clear.setVisible(hasText);
                }
            }
        };

        et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchBar.setText(s);
                if (suppressSearch) { updateClear.run(); return; }

                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                final String newQ = s == null ? "" : s.toString();
                searchRunnable = () -> {
                    if (!newQ.equals(getRelatedViewModel().getSearchText())) {
                        getRelatedViewModel().setSearchText(newQ);
                        getRelatedViewModel().initSearch();
                    }
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_MS);
                updateClear.run();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // IME action
        et.setOnEditorActionListener((v, actionId, event) -> {
            String vq = v.getText() == null ? "" : v.getText().toString();
            searchBar.setText(vq);
            if (!vq.equals(getRelatedViewModel().getSearchText())) {
                getRelatedViewModel().setSearchText(vq);
                getRelatedViewModel().initSearch();
            }
            searchView.hide();
            return true;
        });

        // Clear menu action
        searchBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_clear) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                suppressSearch = true;
                et.setText("");
                searchBar.setText("");
                suppressSearch = false;

                if (!"".equals(getRelatedViewModel().getSearchText())) {
                    getRelatedViewModel().setSearchText("");
                    getRelatedViewModel().initSearch();
                }
                updateClear.run();
                return true;
            }
            return false;
        });

        searchView.addTransitionListener((sv, old, now) -> sv.post(updateClear));
        updateClear.run();
    }

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(ResourceVM.class);
    }

    @Override
    public ResourceVM getRelatedViewModel() {
        return (ResourceVM) super.getRelatedViewModel();
    }

    /** Called by your BaseActivity when results are ready */
    @Override
    public void displaySearchResults() {
        super.displaySearchResults();

        rcvResources.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Material divider (M3 look)
        if (rcvResources.getItemDecorationCount() == 0) {
            MaterialDividerItemDecoration div =
                    new MaterialDividerItemDecoration(this, RecyclerView.VERTICAL);
            div.setLastItemDecorated(false);
            rcvResources.addItemDecoration(div);
        }

        resourceAdapter = new ResourceAdapter(rcvResources, getRelatedViewModel().getNodeList(), this);
        rcvResources.setAdapter(resourceAdapter);
        rcvResources.scheduleLayoutAnimation();
    }

    // === Download flow (unchanged) ===
    public void downloadResource(Node node) {
        getRelatedViewModel().setSelectNode(node);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startFileCreationFlow(node.getName());
        } else {
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
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
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
