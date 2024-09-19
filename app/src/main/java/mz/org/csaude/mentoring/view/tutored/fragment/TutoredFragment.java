package mz.org.csaude.mentoring.view.tutored.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.tutored.TutoredAdapter;
import mz.org.csaude.mentoring.base.fragment.GenericFragment;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.FragmentTutoredsBinding;
import mz.org.csaude.mentoring.listner.dialog.IListbleDialogListener;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.SpacingItemDecoration;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.viewmodel.tutored.TutoredVM;

/**
 * @author Jose Julai Ritsure
 */
public class TutoredFragment extends GenericFragment implements IListbleDialogListener {
    private FragmentTutoredsBinding fragmentTutoredBinding;

    private RecyclerView rcvTutoreds;

    private List<Tutored> tutoreds;

    private TutoredAdapter tutoredItemAdapter;

    public TutoredFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentTutoredBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tutoreds, container, false);
        return fragmentTutoredBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentTutoredBinding.setViewModel(getRelatedViewModel());
        this.rcvTutoreds = fragmentTutoredBinding.rcvTutoreds;
        getRelatedViewModel().initSearch();
    }

    public void displaySearchResults() {
        try {
            this.tutoredItemAdapter = new TutoredAdapter(rcvTutoreds, getRelatedViewModel().getSearchResults(), getMyActivity());
            displayDataOnRecyclerView(rcvTutoreds, tutoredItemAdapter, getContext(), LinearLayoutManager.VERTICAL);
        } catch (Exception e) {
            // Log the error or handle it as necessary
            Log.e("TutoredFragment", "Error loading data", e);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public TutoredVM getRelatedViewModel() {
        return (TutoredVM) getMyActivity().getRelatedViewModel();
    }


    @Override
    public void remove(int position) {

        String errorMsg = getRelatedViewModel().tutoredHasSessions();

        if (!Utilities.stringHasValue(errorMsg)) {
            try {
                tutoreds.remove(getRelatedViewModel().getTutored());
                rcvTutoreds.getAdapter().notifyItemRemoved(position);
                rcvTutoreds.removeViewAt(position);
                rcvTutoreds.getAdapter().notifyItemRangeChanged(position, rcvTutoreds.getAdapter().getItemCount());
                getRelatedViewModel().deleteTutored(getRelatedViewModel().getTutored());
                Utilities.displayAlertDialog(TutoredFragment.this.getContext(), getString(R.string.record_sucessfully_removed)).show();
            } catch (SQLException e) {
                Utilities.displayAlertDialog(TutoredFragment.this.getContext(), errorMsg).show();
            }

        } else {
            Utilities.displayAlertDialog(TutoredFragment.this.getContext(), errorMsg).show();
        }
    }


    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(TutoredVM.class);
    }
}
