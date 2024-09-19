package mz.org.csaude.mentoring.view.mentorship;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.mentorship.ZeroMentorshipAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivityZeroMentorshipListBinding;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.util.SpacingItemDecoration;
import mz.org.csaude.mentoring.viewmodel.mentorship.ZeroMentorshipSearchVM;

public class ZeroMentorshipListActivity extends BaseActivity {

    private ActivityZeroMentorshipListBinding binding;

    private ZeroMentorshipAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_zero_mentorship_list);
        binding.setViewModel(getRelatedViewModel());

        Intent intent = this.getIntent();
        getRelatedViewModel().setRonda((Ronda) intent.getExtras().get("ronda"));
        //getRelatedViewModel().initSearch();

        setSupportActionBar(binding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sessão Zero");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRelatedViewModel().initSearch();
    }

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(ZeroMentorshipSearchVM.class);
    }

    @Override
    public ZeroMentorshipSearchVM getRelatedViewModel() {
        return (ZeroMentorshipSearchVM) super.getRelatedViewModel();
    }

    public void populateRecyclerView(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rcvMentorships.setLayoutManager(mLayoutManager);
        binding.rcvMentorships.setItemAnimator(new DefaultItemAnimator());
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        SpacingItemDecoration itemDecoration = new SpacingItemDecoration(spacingInPixels);
        binding.rcvMentorships.addItemDecoration(itemDecoration);

        adapter = new ZeroMentorshipAdapter(binding.rcvMentorships, getRelatedViewModel().getSearchResults(), this);
        binding.rcvMentorships.setAdapter(adapter);
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
}