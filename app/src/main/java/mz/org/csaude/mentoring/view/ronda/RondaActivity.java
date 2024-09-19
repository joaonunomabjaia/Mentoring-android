package mz.org.csaude.mentoring.view.ronda;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.ListbleRecycleViewAdapter;
import mz.org.csaude.mentoring.adapter.recyclerview.ronda.RondaAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivityMentoringCycleListBinding;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.util.SpacingItemDecoration;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.viewmodel.ronda.RondaSearchVM;

public class RondaActivity extends BaseActivity {
    private ActivityMentoringCycleListBinding mentoringCycleListBinding;
    private RecyclerView rondasRecyclerView;
    private RondaAdapter rondaAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mentoringCycleListBinding = DataBindingUtil.setContentView(this, R.layout.activity_mentoring_cycle_list);
        mentoringCycleListBinding.setViewModel(getRelatedViewModel());
        rondasRecyclerView = mentoringCycleListBinding.rcvRondas;
        Intent intent = this.getIntent();
        if(intent!=null && intent.getExtras()!=null) {
            getRelatedViewModel().setTitle((String) intent.getExtras().get("title"));
            getRelatedViewModel().setRondaType((RondaType) intent.getExtras().get("rondaType"));
            setUpToolbar(getRelatedViewModel().getTitle());
        }
        getRelatedViewModel().initSearch();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getRelatedViewModel().initSearch();
    }

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(RondaSearchVM.class);
    }
    @Override
    public RondaSearchVM getRelatedViewModel() {
        return (RondaSearchVM) super.getRelatedViewModel();
    }

    private void setUpToolbar(String title) {
        setSupportActionBar(mentoringCycleListBinding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
        getRelatedViewModel().setViewListEditButton(false);
        getRelatedViewModel().setViewListRemoveButton(false);
    }

    public void populateRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rondasRecyclerView.setLayoutManager(mLayoutManager);
        rondasRecyclerView.setItemAnimator(new DefaultItemAnimator());

        int spacingInPixels = getApplicationContext().getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        SpacingItemDecoration itemDecoration = new SpacingItemDecoration(spacingInPixels);
        rondasRecyclerView.addItemDecoration(itemDecoration);

        rondasRecyclerView.setHasFixedSize(true);
        rondaAdapter = new RondaAdapter(rondasRecyclerView, getRelatedViewModel().getSearchResults(), this);
        rondasRecyclerView.setAdapter(rondaAdapter);
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
            getRelatedViewModel().printRondaReport();
        } else {
            Utilities.displayAlertDialog(this, getString(R.string.permission_print_error)).show();
        }
    }

    public void checkStoragePermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        } else getRelatedViewModel().printRondaReport();
    }
}