package mz.org.csaude.mentoring.view.tutored;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.spinner.listble.ListableSpinnerAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivityCreateTutoredBinding;
import mz.org.csaude.mentoring.listner.dialog.IDialogListener;
import mz.org.csaude.mentoring.model.location.Province;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.model.professionalCategory.ProfessionalCategory;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.SimpleValue;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.viewmodel.tutored.TutoredVM;

/**
 * @author Jose Julai Ritsure
 */
public class CreateTutoredActivity extends BaseActivity implements IDialogListener {
    private ActivityCreateTutoredBinding activityCreateTutoredBinding;
    private ListableSpinnerAdapter provinceAdapter;
    private ListableSpinnerAdapter districtAdapter;
    private ListableSpinnerAdapter healthfacilityAdapter;
    private ListableSpinnerAdapter professionalCategoryAdapter;

    private ListableSpinnerAdapter ngoAdapter;
    private ListableSpinnerAdapter menteeLaborfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔄 Recupera o ViewModel e faz setup inicial
        getRelatedViewModel().preInit();
        activityCreateTutoredBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_tutored);
        activityCreateTutoredBinding.setViewModel(getRelatedViewModel());
        getRelatedViewModel().setInitialDataVisible(true);
        setUpToolbar();
        initAdapters();
        activityCreateTutoredBinding.setLifecycleOwner(this);

        // ✅ Recupera o "relatedRecord" da Intent, se houver
        if (getIntent() != null && getIntent().getExtras() != null) {
            Tutored relatedTutored = (Tutored) getIntent().getExtras().get("relatedRecord");

            if (relatedTutored != null) {
                getApplicationStep().changeToEdit();
                getRelatedViewModel().setTutored(relatedTutored);
            }
            //activityCreateTutoredBinding.executePendingBindings();
        }
    }


    private void setUpToolbar() {
        setSupportActionBar(activityCreateTutoredBinding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.tutored_title));
    }

    private void switchLayout(){
        getRelatedViewModel().setInitialDataVisible(!getRelatedViewModel().isInitialDataVisible());

    }

    private void initAdapters(){
        getRelatedViewModel().getExecutorService().execute(() -> {
            try {
                List<Province> provinces = getRelatedViewModel().getAllProvince();
                List<ProfessionalCategory> professionalCategories = getRelatedViewModel().getAllProfessionalCategys();
                List<SimpleValue> menteeLabors = getRelatedViewModel().getMenteeLabors();
                getRelatedViewModel().getPartnersList();
                getRelatedViewModel().getCreateTutoredActivity().runOnUiThread(() -> {
                    provinceAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, provinces);
                    activityCreateTutoredBinding.spnProvince.setAdapter(provinceAdapter);
                    activityCreateTutoredBinding.setProvinceAdapter(provinceAdapter);

                    professionalCategoryAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, professionalCategories);
                    activityCreateTutoredBinding.spnProfessionalCategory.setAdapter(professionalCategoryAdapter);
                    activityCreateTutoredBinding.setProfessionalCategoryAdapter(professionalCategoryAdapter);

                    menteeLaborfoAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, menteeLabors);
                    activityCreateTutoredBinding.spnMenteeLaborInfo.setAdapter(menteeLaborfoAdapter);
                    activityCreateTutoredBinding.setMenteeLaborfoAdapter(menteeLaborfoAdapter);

                    ngoAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, getRelatedViewModel().getAllPartners());
                    activityCreateTutoredBinding.setNgoAdapter(ngoAdapter);
                });

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(TutoredVM.class);
    }

    @Override
    public void doOnConfirmed() {

    }

    @Override
    public void doOnDeny() {
    }

    @Override
    public TutoredVM getRelatedViewModel() {
        return (TutoredVM) super.getRelatedViewModel();
    }

    public void reloadDistrcitAdapter() {
        districtAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, getRelatedViewModel().getDistricts());
        activityCreateTutoredBinding.spnDistrict.setAdapter(districtAdapter);
        activityCreateTutoredBinding.setDistrictAdapter(districtAdapter);
    }

    public void reloadHealthFacility(){
        healthfacilityAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, getRelatedViewModel().getHealthFacilities());
        activityCreateTutoredBinding.spnHealthfacility.setAdapter(healthfacilityAdapter);
        activityCreateTutoredBinding.setHealthfacilityAdapter(healthfacilityAdapter);

    }

    public void changeFormSectionVisibility(View view){

        if(view.equals(activityCreateTutoredBinding.laboralData)){
            if(activityCreateTutoredBinding.laboralLyt.getVisibility() == View.VISIBLE){
                activityCreateTutoredBinding.btnLaboralData.setImageResource(R.drawable.sharp_arrow_drop_up_24);
                switchLayout();
                Utilities.collapse(activityCreateTutoredBinding.laboralLyt);
            } else {
                switchLayout();
                Utilities.expand(activityCreateTutoredBinding.laboralLyt);
                activityCreateTutoredBinding.btnLaboralData.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            }

        } else if(view.equals(activityCreateTutoredBinding.healtUnit)){
            if(activityCreateTutoredBinding.healtUnitLyt.getVisibility() == View.VISIBLE){
                activityCreateTutoredBinding.btnHealtUnit.setImageResource(R.drawable.sharp_arrow_drop_up_24);
                switchLayout();
                Utilities.collapse(activityCreateTutoredBinding.healtUnitLyt);
            } else {
                switchLayout();
                Utilities.expand(activityCreateTutoredBinding.healtUnitLyt);
                activityCreateTutoredBinding.btnHealtUnit.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            }

        }  else if(view.equals(activityCreateTutoredBinding.identificationData)){
            if(activityCreateTutoredBinding.identificationDataLyt.getVisibility() == View.VISIBLE){
                switchLayout();
                Utilities.collapse(activityCreateTutoredBinding.identificationDataLyt);
                activityCreateTutoredBinding.btnIdentificationData.setImageResource(R.drawable.sharp_arrow_drop_up_24);
            } else {
                switchLayout();
                Utilities.expand(activityCreateTutoredBinding.identificationDataLyt);
                activityCreateTutoredBinding.btnIdentificationData.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            }
        } else if(view.equals(activityCreateTutoredBinding.spnMenteeLaborInfo)){
            if (activityCreateTutoredBinding.spnMenteeLaborInfo.getSelectedItem() == "ONG"){
                activityCreateTutoredBinding.spnNgo.setVisibility(View.VISIBLE);
            }else{
                activityCreateTutoredBinding.spnNgo.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle the back button click
                this.getRelatedViewModel().getRelatedActivity().nextActivityFinishingCurrent(TutoredActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
