package mz.org.csaude.mentoring.view.home.ui.credentials;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.fragment.GenericFragment;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.FragmentCredentialsBinding;
import mz.org.csaude.mentoring.databinding.FragmentGalleryBinding;

public class CredentialsFragment extends GenericFragment {

    private FragmentCredentialsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCredentialsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }


    @Override
    public BaseViewModel initViewModel() {
        return  new ViewModelProvider(this).get(CredentialsVM.class);
    }
}