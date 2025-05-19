package mz.org.csaude.mentoring.view.home.ui.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.session.SessionMentorAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivityNotificationsBinding;
import mz.org.csaude.mentoring.listner.dialog.IDialogListener;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.util.Utilities;

public class NotificationsActivity extends BaseActivity implements IDialogListener {

    private ActivityNotificationsBinding binding;

    private RecyclerView recyclerView;

    private SessionMentorAdapter sessionMentorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notifications);
        binding.setViewModel(getRelatedViewModel());

        recyclerView = binding.rcvMentorSessoes;
        populateSessionRecyclerView();
        setUpToolbar();
    }

    private void setUpToolbar(){
        setSupportActionBar(binding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Notificações");
    }
    @Override
    public NotificationsVM getRelatedViewModel() {
        return (NotificationsVM) super.getRelatedViewModel();
    }

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(NotificationsVM.class);
    }

    @Override
    public void doOnConfirmed() {

    }

    @Override
    public void doOnDeny() {

    }

    public void changeFormSectionVisibility(View view) {

        if (view.equals(binding.sessonData)) {
            if (binding.sessonDataResult.getVisibility() == View.VISIBLE) {
                binding.btnSessionData.setImageResource(R.drawable.sharp_arrow_drop_up_24);
                Utilities.collapse(binding.sessonDataResult);
                listSessonMent();

            } else {
                Utilities.expand(binding.sessonDataResult);
                binding.btnSessionData.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            }

        }
    }

    private void listSessonMent(){

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

    public void populateSessionRecyclerView(){

        getRelatedViewModel().getExecutorService().execute(()->{
            List<Session> sessions = getRelatedViewModel().getSessionsByMentor();
            runOnUiThread(()->{
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 0));

                sessionMentorAdapter = new SessionMentorAdapter(recyclerView, sessions, this);
                recyclerView.setAdapter(sessionMentorAdapter);
            });
        });

    }
}