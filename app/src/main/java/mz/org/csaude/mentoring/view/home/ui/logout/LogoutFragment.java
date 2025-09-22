package mz.org.csaude.mentoring.view.home.ui.logout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import mz.org.csaude.mentoring.base.fragment.GenericFragment;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.FragmentLogoutBinding;
import mz.org.csaude.mentoring.listner.dialog.IDialogListener;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.home.MainActivity;
import mz.org.csaude.mentoring.view.login.LoginActivity;

public class LogoutFragment extends GenericFragment implements IDialogListener {


    public static LogoutFragment newInstance() {
        return new LogoutFragment();
    }

    private FragmentLogoutBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Utilities.displayConfirmationDialog(getMyActivity(), "Confirma terminar a sessão no aplicativo?", "SIM", "Não", this).show();
        binding = FragmentLogoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setViewModel(getRelatedViewModel());
    }

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(LogoutViewModel.class);
    }

    @Override
    public LogoutViewModel getRelatedViewModel() {
        return (LogoutViewModel) super.getRelatedViewModel();
    }

    @Override
    public void doOnConfirmed() {
        //sessionManager.clearUserSession();
        getMyActivity().nextActivity(LoginActivity.class);
    }

    @Override
    public void doOnDeny() {
        getMyActivity().nextActivity(MainActivity.class);
    }
}
