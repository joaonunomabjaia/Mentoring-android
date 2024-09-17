package mz.org.csaude.mentoring.view.home.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.fragment.GenericFragment;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.FragmentSettingsBinding;
import mz.org.csaude.mentoring.viewmodel.setting.SettingVM;

public class SettingsFragment extends GenericFragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setViewModel(getRelatedViewModel());

        binding.setLifecycleOwner(this);

        binding.rootLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboardAndClearFocus();
            }
            return false; // Return false to allow other touch events to be processed
        });

        // Initialize the language spinner
        Spinner languageSpinner = binding.getRoot().findViewById(R.id.languageSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Set the selected language in the spinner
        getRelatedViewModel().initSelectedLanguage(languageSpinner);

    }

    private void hideKeyboardAndClearFocus() {
        // Hide keyboard
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            // Clear focus
            view.clearFocus();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public BaseViewModel initViewModel() {
        return  new ViewModelProvider(this).get(SettingVM.class);
    }

    @Override
    public SettingVM getRelatedViewModel() {
        return (SettingVM) super.getRelatedViewModel();
    }
}
