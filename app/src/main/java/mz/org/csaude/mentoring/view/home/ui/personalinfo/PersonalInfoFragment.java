package mz.org.csaude.mentoring.view.home.ui.personalinfo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.spinner.listble.ListableSpinnerAdapter;
import mz.org.csaude.mentoring.base.fragment.GenericFragment;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.FragmentMentorInfoBinding;
import mz.org.csaude.mentoring.model.location.Province;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.model.professionalCategory.ProfessionalCategory;
import mz.org.csaude.mentoring.util.SimpleValue;
import mz.org.csaude.mentoring.util.Utilities;

public class PersonalInfoFragment extends GenericFragment {

    private FragmentMentorInfoBinding binding;

    private ListableSpinnerAdapter provinceAdapter;
    private ListableSpinnerAdapter districtAdapter;
    private ListableSpinnerAdapter healthfacilityAdapter;
    private ListableSpinnerAdapter professionalCategoryAdapter;

    private ListableSpinnerAdapter ngoAdapter;
    private ListableSpinnerAdapter menteeLaborfoAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMentorInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setViewModel(getRelatedViewModel());
        initAdapters();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public BaseViewModel initViewModel() {
        return  new ViewModelProvider(this).get(MentorVM.class);
    }

    @Override
    public MentorVM getRelatedViewModel(){
        return (MentorVM) super.getRelatedViewModel();
    }


    private void switchLayout(){
        getRelatedViewModel().setInitialDataVisible(!getRelatedViewModel().isInitialDataVisible());

    }

    private void initAdapters() {
        getRelatedViewModel().getExecutorService().execute(() -> {
            try {
                // Fetch data in the background thread
                List<Province> provinces = getRelatedViewModel().getAllProvince();
                List<ProfessionalCategory> professionalCategories = getRelatedViewModel().getAllProfessionalCategys();
                List<SimpleValue> menteeLabors = getRelatedViewModel().getMenteeLabors();
                List<Partner> partners = getRelatedViewModel().getAllPartners();

                // Switch back to the main thread to update the UI
                getActivity().runOnUiThread(() -> {
                    // Ensure the lists are not empty before using them
                    if (provinces != null && !provinces.isEmpty()) {
                        provinceAdapter = new ListableSpinnerAdapter(this.getActivity(), R.layout.simple_auto_complete_item, provinces);
                        binding.spnProvince.setAdapter(provinceAdapter);
                        binding.setProvinceAdapter(provinceAdapter);
                    }

                    if (professionalCategories != null && !professionalCategories.isEmpty()) {
                        professionalCategoryAdapter = new ListableSpinnerAdapter(this.getActivity(), R.layout.simple_auto_complete_item, professionalCategories);
                        binding.spnProfessionalCategory.setAdapter(professionalCategoryAdapter);
                        binding.setProfessionalCategoryAdapter(professionalCategoryAdapter);
                    }

                    if (menteeLabors != null && !menteeLabors.isEmpty()) {
                        menteeLaborfoAdapter = new ListableSpinnerAdapter(this.getActivity(), R.layout.simple_auto_complete_item, menteeLabors);
                        binding.spnMenteeLaborInfo.setAdapter(menteeLaborfoAdapter);
                        binding.setMenteeLaborfoAdapter(menteeLaborfoAdapter);
                    }

                    if (partners != null && !partners.isEmpty()) {
                        ngoAdapter = new ListableSpinnerAdapter(this.getActivity(), R.layout.simple_auto_complete_item, partners);
                        binding.spnNgo.setAdapter(ngoAdapter);
                        binding.setNgoAdapter(ngoAdapter);
                    }
                });

            } catch (SQLException e) {
                // Log the error or handle it as necessary
                Log.e("PersonalInfoFragment", "Error loading data", e);

            }
        });
    }


    public void reloadDistrcitAdapter() {
        districtAdapter = new ListableSpinnerAdapter(this.getActivity(), R.layout.simple_auto_complete_item, getRelatedViewModel().getDistricts());
        binding.spnDistrict.setAdapter(districtAdapter);
        binding.setDistrictAdapter(districtAdapter);
    }

    public void reloadHealthFacility(){
        healthfacilityAdapter = new ListableSpinnerAdapter(this.getActivity(), R.layout.simple_auto_complete_item, getRelatedViewModel().getHealthFacilities());
        binding.spnHealthfacility.setAdapter(healthfacilityAdapter);
        binding.setHealthfacilityAdapter(healthfacilityAdapter);

    }
    public void changeFormSectionVisibility(View view){

        if(view.equals(binding.laboralData)){
            if(binding.laboralLyt.getVisibility() == View.VISIBLE){
                binding.btnLaboralData.setImageResource(R.drawable.sharp_arrow_drop_up_24);
                switchLayout();
                Utilities.collapse(binding.laboralLyt);
            } else {
                switchLayout();
                Utilities.expand(binding.laboralLyt);
                binding.btnLaboralData.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            }

        } else if(view.equals(binding.healtUnit)){
            if(binding.healtUnitLyt.getVisibility() == View.VISIBLE){
                binding.btnHealtUnit.setImageResource(R.drawable.sharp_arrow_drop_up_24);
                switchLayout();
                Utilities.collapse(binding.healtUnitLyt);
            } else {
                switchLayout();
                Utilities.expand(binding.healtUnitLyt);
                binding.btnHealtUnit.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            }

        }  else if(view.equals(binding.identificationData)){
            if(binding.identificationDataLyt.getVisibility() == View.VISIBLE){
                switchLayout();
                Utilities.collapse(binding.identificationDataLyt);
                binding.btnIdentificationData.setImageResource(R.drawable.sharp_arrow_drop_up_24);
            } else {
                switchLayout();
                Utilities.expand(binding.identificationDataLyt);
                binding.btnIdentificationData.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            }
        } else if(view.equals(binding.spnMenteeLaborInfo)){
            if (binding.spnMenteeLaborInfo.getSelectedItem() == "ONG"){
                binding.spnNgo.setVisibility(View.VISIBLE);
            }else{
                binding.spnNgo.setVisibility(View.GONE);
            }
        }
    }
}
