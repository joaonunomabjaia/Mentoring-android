package mz.org.csaude.mentoring.view.mentorship;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.mentorship.MentorshipAdapter;
import mz.org.csaude.mentoring.adapter.recyclerview.ronda.RondaAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivityListMentorshipBinding;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.ronda.RondaMentor;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.SpacingItemDecoration;
import mz.org.csaude.mentoring.viewmodel.mentorship.MentorshipSearchVM;

import java.io.Serializable;
import java.util.List;

public class MentorshipActivity extends BaseActivity {
    private ActivityListMentorshipBinding binding;
    private MentorshipAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_mentorship);
        binding.setViewModel(getRelatedViewModel());

        Intent intent = this.getIntent();
        if(intent!=null && intent.getExtras()!=null) {
            Session s = null;
            Ronda r = (Ronda) intent.getExtras().get("ronda");
            if (r == null) {
                s = (Session) intent.getExtras().get("session");
                r = s.getRonda();
            }
            getRelatedViewModel().setRonda(r);
            getRelatedViewModel().setSession(s);
        }

        setSupportActionBar(binding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.evaluations_title));

    }

    @Override
    protected void onResume() {
        super.onResume();
        getRelatedViewModel().initSearch();
    }

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(MentorshipSearchVM.class);
    }

    @Override
    public MentorshipSearchVM getRelatedViewModel() {
        return (MentorshipSearchVM) super.getRelatedViewModel();
    }


    public void populateRecyclerView(){
        adapter = new MentorshipAdapter(binding.rcvMentorships, getRelatedViewModel().getSearchResults(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rcvMentorships.setLayoutManager(mLayoutManager);
        binding.rcvMentorships.setItemAnimator(new DefaultItemAnimator());
        if (adapter == null) {
            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
            SpacingItemDecoration itemDecoration = new SpacingItemDecoration(spacingInPixels);
            binding.rcvMentorships.addItemDecoration(itemDecoration);
        }
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