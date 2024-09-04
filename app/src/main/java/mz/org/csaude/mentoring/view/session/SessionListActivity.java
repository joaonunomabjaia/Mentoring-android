package mz.org.csaude.mentoring.view.session;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.adapter.recyclerview.mentorship.ZeroMentorshipAdapter;
import mz.org.csaude.mentoring.adapter.recyclerview.session.SessionAdapter;
import mz.org.csaude.mentoring.adapter.spinner.listble.ListableSpinnerAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivitySessionListBinding;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.viewmodel.session.SessionListVM;

public class SessionListActivity extends BaseActivity {

    private ActivitySessionListBinding sessionListBinding;

    private ListableSpinnerAdapter menteeAdapter;

    private SessionAdapter sessionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionListBinding = DataBindingUtil.setContentView(this, R.layout.activity_session_list);
        sessionListBinding.setViewModel(getRelatedViewModel());

        Intent intent = this.getIntent();
        getRelatedViewModel().setCurrRonda((Ronda) intent.getExtras().get("ronda"));


        setSupportActionBar(sessionListBinding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sessões de Mentoria");

        initAdapters();
    }

    private void initAdapters() {
        getRelatedViewModel().getExecutorService().execute(()->{
            List<Listble> mentees = getRelatedViewModel().getRondaMentees();
            runOnUiThread(()-> {
                menteeAdapter = new ListableSpinnerAdapter(SessionListActivity.this, R.layout.simple_auto_complete_item, mentees);
                sessionListBinding.spnMentees.setAdapter(menteeAdapter);
                sessionListBinding.setMenteesAdapter(menteeAdapter);
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getRelatedViewModel().getSelectedMentee() != null) {
            getRelatedViewModel().initSearch();
        }
    }

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(SessionListVM.class);
    }

    @Override
    public SessionListVM getRelatedViewModel() {
        return (SessionListVM) super.getRelatedViewModel();
    }

    public void populateSessions() {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            sessionListBinding.rcvSessions.setLayoutManager(mLayoutManager);
            sessionListBinding.rcvSessions.setItemAnimator(new DefaultItemAnimator());
            sessionListBinding.rcvSessions.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 0));

            sessionAdapter = new SessionAdapter(sessionListBinding.rcvSessions, getRelatedViewModel().getSearchResults(), this);
            sessionListBinding.rcvSessions.setAdapter(sessionAdapter);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle the back button click
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}