package mz.org.csaude.mentoring.view.tutored;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
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
import mz.org.csaude.mentoring.model.professionalCategory.ProfessionalCategory;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.util.SimpleValue;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.viewmodel.tutored.TutoredVM;

public class CreateTutoredActivity extends BaseActivity implements IDialogListener {

    private ActivityCreateTutoredBinding binding;

    // Adapters para os dropdowns expostos (MaterialAutoCompleteTextView)
    private ListableSpinnerAdapter provinceAdapter;
    private ListableSpinnerAdapter districtAdapter;
    private ListableSpinnerAdapter healthfacilityAdapter;
    private ListableSpinnerAdapter professionalCategoryAdapter;
    private ListableSpinnerAdapter ngoAdapter;
    private ListableSpinnerAdapter menteeLaborfoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewModel + DataBinding
        getRelatedViewModel().preInit();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_tutored);
        binding.setViewModel(getRelatedViewModel());
        binding.setLifecycleOwner(this);

        // Toolbar
        setSupportActionBar(binding.toolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.tutored_title));
        }

        // Estado inicial (secção aberta/fechada)
        getRelatedViewModel().setInitialDataVisible(true);

        // Prepara adapters async
        initAdapters();

        // Se veio para editar
        if (getIntent() != null && getIntent().getExtras() != null) {
            Tutored relatedTutored = (Tutored) getIntent().getExtras().get("relatedRecord");
            if (relatedTutored != null) {
                getApplicationStep().changeToEdit();
                getRelatedViewModel().setTutored(relatedTutored);
            }
        }

        // Listeners dos cabeçalhos para expand/colapse (se preferires só via binding, pode remover)
        setSectionToggleListeners();

        // Listener do vínculo laboral para mostrar/ocultar ONG imediatamente
        setMenteeLaborListener();
    }

    private void initAdapters() {
        getRelatedViewModel().getExecutorService().execute(() -> {
            try {
                List<Province> provinces = getRelatedViewModel().getAllProvince();
                List<ProfessionalCategory> professionalCategories = getRelatedViewModel().getAllProfessionalCategys();
                List<SimpleValue> menteeLabors = getRelatedViewModel().getMenteeLabors();
                getRelatedViewModel().getPartnersList(); // carrega lista de parceiros no VM

                runOnUiThread(() -> {
                    // Province
                    provinceAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, provinces);
                    binding.actProvince.setAdapter(provinceAdapter);
                    binding.setProvinceAdapter(provinceAdapter);

                    // Professional Category
                    professionalCategoryAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, professionalCategories);
                    binding.actProfessionalCategory.setAdapter(professionalCategoryAdapter);
                    binding.setProfessionalCategoryAdapter(professionalCategoryAdapter);

                    // Mentee Labor (SNS/ONG)
                    menteeLaborfoAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, menteeLabors);
                    binding.actMenteeLaborInfo.setAdapter(menteeLaborfoAdapter);
                    binding.setMenteeLaborfoAdapter(menteeLaborfoAdapter);

                    // ONG (partners)
                    ngoAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, getRelatedViewModel().getAllPartners());
                    binding.actNgo.setAdapter(ngoAdapter);
                    binding.setNgoAdapter(ngoAdapter);
                });

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /** Atualiza adapter de Distrito quando a Província muda */
    public void reloadDistrcitAdapter() {
        districtAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, getRelatedViewModel().getDistricts());
        binding.actDistrict.setAdapter(districtAdapter);
        binding.setDistrictAdapter(districtAdapter);
    }

    /** Atualiza adapter de Unidade Sanitária quando o Distrito muda */
    public void reloadHealthFacility() {
        healthfacilityAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, getRelatedViewModel().getHealthFacilities());
        binding.actHealthfacility.setAdapter(healthfacilityAdapter);
        binding.setHealthfacilityAdapter(healthfacilityAdapter);
    }

    /** Cabeçalhos dos cards para expandir/colapsar as seções */
    private void setSectionToggleListeners() {
        binding.identificationData.setOnClickListener(this::changeFormSectionVisibility);
        binding.laboralData.setOnClickListener(this::changeFormSectionVisibility);
        binding.healtUnit.setOnClickListener(this::changeFormSectionVisibility);

        // Botões de seta também
        binding.btnIdentificationData.setOnClickListener(this::changeFormSectionVisibility);
        binding.btnLaboralData.setOnClickListener(this::changeFormSectionVisibility);
        binding.btnHealtUnit.setOnClickListener(this::changeFormSectionVisibility);
    }

    /** Mostra ONG quando vínculo = "ONG" (UX imediato além do binding reativo) */
    private void setMenteeLaborListener() {
        binding.actMenteeLaborInfo.setOnItemClickListener((parent, view, position, id) -> {
            Object sel = parent.getItemAtPosition(position);
            if (sel != null && "ONG".equalsIgnoreCase(sel.toString())) {
                binding.actNgo.setVisibility(View.VISIBLE);
                getRelatedViewModel().setONGEmployee(true);
            } else {
                binding.actNgo.setVisibility(View.GONE);
                getRelatedViewModel().setONGEmployee(false);
            }
        });
    }

    /** Alterna a visibilidade das seções (usa Utilities.expand/collapse) com rotação 180° do ícone. */
    public void changeFormSectionVisibility(View view) {
        if (view.equals(binding.laboralData) || view.equals(binding.btnLaboralData)) {
            toggleSection(binding.laboralLyt, binding.btnLaboralData);
        } else if (view.equals(binding.healtUnit) || view.equals(binding.btnHealtUnit)) {
            toggleSection(binding.healtUnitLyt, binding.btnHealtUnit);
        } else if (view.equals(binding.identificationData) || view.equals(binding.btnIdentificationData)) {
            toggleSection(binding.identificationDataLyt, binding.btnIdentificationData);
        }
    }

    /** Alterna a seção e anima o ícone para 0° (fechado) ou 180° (aberto). */
    private void toggleSection(View body, View iconView) {
        boolean expanding = body.getVisibility() != View.VISIBLE;

        if (expanding) {
            Utilities.expand(body);
        } else {
            Utilities.collapse(body);
        }

        // Rotação suave do ícone (ImageButton / ImageView)
        float target = expanding ? 180f : 0f;
        iconView.animate()
                .rotation(target)
                .setDuration(180L)
                .start();
    }


    // ======== Boilerplate ========

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(TutoredVM.class);
    }

    @Override
    public void doOnConfirmed() { }

    @Override
    public void doOnDeny() { }

    @Override
    public TutoredVM getRelatedViewModel() {
        return (TutoredVM) super.getRelatedViewModel();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.getRelatedViewModel().getRelatedActivity().nextActivityFinishingCurrent(TutoredActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
