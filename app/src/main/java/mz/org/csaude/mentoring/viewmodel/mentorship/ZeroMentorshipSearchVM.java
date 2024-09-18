package mz.org.csaude.mentoring.viewmodel.mentorship;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.searchparams.AbstractSearchParams;
import mz.org.csaude.mentoring.base.viewModel.SearchVM;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.ronda.RondaMentor;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionStatus;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.service.mentorship.MentorshipService;
import mz.org.csaude.mentoring.service.mentorship.MentorshipServiceImpl;
import mz.org.csaude.mentoring.service.session.SessionService;
import mz.org.csaude.mentoring.service.session.SessionServiceImpl;
import mz.org.csaude.mentoring.view.form.ListFormActivity;
import mz.org.csaude.mentoring.view.mentorship.CreateMentorshipActivity;
import mz.org.csaude.mentoring.view.mentorship.ZeroMentorshipListActivity;

public class ZeroMentorshipSearchVM extends AbstractSearchMentorshipVM {


    public ZeroMentorshipSearchVM(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void doOnNoRecordFound() {
        getRelatedActivity().populateRecyclerView();
    }


    @Override
    public void preInit() {
    }


    public void createNewMentorship() {
        Map<String, Object> params = new HashMap<>();
        params.put("ronda", ronda);
        params.put("CURR_MENTORSHIP_STEP", MentorshipVM.CURR_MENTORSHIP_STEP_TABLE_SELECTION);
        getCurrentStep().changetocreate();
        getRelatedActivity().nextActivity(CreateMentorshipActivity.class, params);
    }

    @Override
    public void edit(Mentorship mentorship) {
        getExecutorService().execute(() -> {
            super.edit(mentorship);
            runOnMainThread(() -> {
                Map<String, Object> params = new HashMap<>();
                params.put("mentorship", mentorship);
                params.put("CURR_MENTORSHIP_STEP", MentorshipVM.CURR_MENTORSHIP_STEP_PERIOD_SELECTION);
                getApplication().getApplicationStep().changeToEdit();
                getRelatedActivity().nextActivity(CreateMentorshipActivity.class, params);
            });
        });
    }

    @Override
    public List<Mentorship> doSearch(long offset, long limit) throws SQLException {
        return getApplication().getMentorshipService().getAllOfRonda(this.ronda);
    }

    @Override
    public ZeroMentorshipListActivity getRelatedActivity() {
        return (ZeroMentorshipListActivity) super.getRelatedActivity();
    }

    @Override
    public void displaySearchResults() {
        getRelatedActivity().populateRecyclerView();
    }

    @Override
    public AbstractSearchParams<Mentorship> initSearchParams() {
        return null;
    }
}
