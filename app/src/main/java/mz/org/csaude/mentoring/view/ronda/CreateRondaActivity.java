package mz.org.csaude.mentoring.view.ronda;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.tutored.TutoredAdapter;
import mz.org.csaude.mentoring.adapter.recyclerview.tutored.TutoredSelectionAdapter;
import mz.org.csaude.mentoring.adapter.spinner.listble.ListableSpinnerAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivityRondaBinding;
import mz.org.csaude.mentoring.databinding.DialogSelectMenteesBinding;
import mz.org.csaude.mentoring.model.location.Province;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.SimpleValue;
import mz.org.csaude.mentoring.util.SpacingItemDecoration;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.viewmodel.ronda.RondaVM;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

public class CreateRondaActivity extends BaseActivity {

    private ActivityRondaBinding binding;

    // Dropdown adapters (M3)
    private ListableSpinnerAdapter mentorTypeAdapter;
    private ListableSpinnerAdapter provinceAdapter;
    private ListableSpinnerAdapter districtAdapter;
    private ListableSpinnerAdapter healthFacilityAdapter;

    private RecyclerView rcvSelectedMentees;
    private TutoredAdapter tutoredAdapter;

    private String title;
    private RondaType rondaTypeOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // ViewBinding + VM
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ronda);
        binding.setViewModel(getRelatedViewModel());
        binding.setLifecycleOwner(this);

        int primary = MaterialColors.getColor(binding.appBarLayout,
                com.google.android.material.R.attr.colorPrimary);
        getWindow().setStatusBarColor(primary);

        // Set icon color (dark icons if background is light)
        boolean isLight = MaterialColors.isColorLight(primary);
        new WindowInsetsControllerCompat(getWindow(), binding.getRoot())
                .setAppearanceLightStatusBars(isLight);

        // Push AppBar below the status bar (notification area)
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout, (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), sb.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        // Keep your scrolling content above the gesture/navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentContainer, (v, insets) -> {
            Insets nb = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), nb.bottom);
            return insets;
        });

        // Toolbar
        setSupportActionBar(binding.toolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        rcvSelectedMentees = binding.rcvSelectedMentees;

        // Inicial
        initStateFromIntent();
        initAdapters();
        setSectionToggleListeners();
        setupDatePicker();
    }

    private void initStateFromIntent() {
        Intent intent = getIntent();
        if (intent == null || intent.getExtras() == null) return;

        title = intent.getStringExtra("title");
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);

        if (getApplicationStep().isApplicationstepCreate()) {
            rondaTypeOption = (RondaType) intent.getSerializableExtra("rondaType");
            getRelatedViewModel().getRonda().setRondaType(rondaTypeOption);
        } else {
            Ronda ronda = (Ronda) intent.getSerializableExtra("ronda");
            getRelatedViewModel().setRonda(ronda);
            getRelatedViewModel().initRondaEdition();
        }
    }

    private void initAdapters() {
        // Mentor type (static)
        List<SimpleValue> mentorTypes = new ArrayList<>();
        mentorTypes.add(new SimpleValue(1, getString(R.string.interno)));
        mentorTypes.add(new SimpleValue(2, getString(R.string.externo)));
        mentorTypeAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, mentorTypes);
        binding.setMentorTypeAdapter(mentorTypeAdapter);
        binding.actMentorType.setAdapter(mentorTypeAdapter);

        // Province/District/US (carrega em background como no Tutored)
        getRelatedViewModel().getExecutorService().execute(() -> {
            try {
                List<Province> provinces = getRelatedViewModel().getAllProvince();
                runOnUiThread(() -> {
                    provinceAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, provinces);
                    binding.setProvinceAdapter(provinceAdapter);
                    binding.actProvince.setAdapter(provinceAdapter);
                });
            } catch (SQLException e) {
                runOnUiThread(() -> Utilities.displayAlertDialog(this, getString(R.string.error_loading)));
            }
        });
    }

    /** Chamado pelo VM quando a província muda */
    public void reloadDistrictAdapter() {
        districtAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, getRelatedViewModel().getDistricts());
        binding.setDistrictAdapter(districtAdapter);
        binding.actDistrict.setAdapter(districtAdapter);
    }

    /** Chamado pelo VM quando o distrito muda */
    public void reloadHealthFacility() {
        healthFacilityAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, getRelatedViewModel().getHealthFacilities());
        binding.setHealthFacilityAdapter(healthFacilityAdapter);
        binding.actHealthfacility.setAdapter(healthFacilityAdapter);
    }

    // CreateRondaActivity.java
    public void changeFormSectionVisibility(View view) {
        int id = view.getId();

        if (id == R.id.initial_data || id == R.id.btn_show_collapse) {
            toggleSection(binding.initialDataLyt, binding.btnShowCollapse);
            return;
        }

        if (id == R.id.healt_unit || id == R.id.btn_healt_unit) {
            toggleSection(binding.healtUnitLyt, binding.btnHealtUnit);
            return;
        }

        if (id == R.id.mentees_lyt || id == R.id.btn_mentees) {
            toggleSection(binding.menteeDataLyt, binding.btnMentees);
        }
    }

    private void toggleSection(View body, View iconView) {
        boolean expanding = body.getVisibility() != View.VISIBLE;

        if (expanding) {
            Utilities.expand(body);
        } else {
            Utilities.collapse(body);
        }

        // Rotação suave do ícone (0° fechado, 180° aberto)
        if (iconView != null) {
            iconView.animate()
                    .rotation(expanding ? 180f : 0f)
                    .setDuration(180L)
                    .start();
        }
    }


    private void setSectionToggleListeners() {
        binding.initialData.setOnClickListener(getRelatedViewModel()::changeInitialDataViewStatus);
        binding.btnShowCollapse.setOnClickListener(getRelatedViewModel()::changeInitialDataViewStatus);

        binding.healtUnit.setOnClickListener(getRelatedViewModel()::changeInitialDataViewStatus);
        binding.btnHealtUnit.setOnClickListener(getRelatedViewModel()::changeInitialDataViewStatus);

        binding.menteesLyt.setOnClickListener(getRelatedViewModel()::openCollapse);
        binding.btnMentees.setOnClickListener(getRelatedViewModel()::openCollapse);

        // Botão selecionar mentees
        binding.btnPickMentees.setOnClickListener(v -> openSearchMenteesDialog());
    }

    private void setupDatePicker() {
        binding.rondaStartDate.setOnClickListener(v -> {
            // (Opcional) Restringir datas futuras
            CalendarConstraints constraints = new CalendarConstraints.Builder()
                    .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText(R.string.select_date) // "Selecionar data"
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(constraints)
                    .setTheme(R.style.ThemeOverlay_App_DatePicker) // se quiser um overlay
                    .build();

            picker.addOnPositiveButtonClickListener(utcMillis -> {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(utcMillis);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                getRelatedViewModel().setStartDate(cal.getTime());
            });

            picker.show(getSupportFragmentManager(), "ronda_start_date");
        });
    }

    public void openSearchMenteesDialog() {
        DialogSelectMenteesBinding dlgBinding = DialogSelectMenteesBinding.inflate(getLayoutInflater());
        dlgBinding.setViewModel(getRelatedViewModel());

        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dlgBinding.getRoot());
        dialog.setCancelable(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        RecyclerView recycler = dlgBinding.recyclerMentees;
        TutoredSelectionAdapter adapter = new TutoredSelectionAdapter(recycler, getRelatedViewModel().getrondaMenteeList(), this, getRelatedViewModel());
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        recycler.setHasFixedSize(true);

        // Busca
        dlgBinding.searchInput.addTextChangedListener(Utilities.simpleFilterTextWatcher(adapter::filter));

        // Confirmar
        dlgBinding.btnAdd.setOnClickListener(v -> {
            displaySelectedMentees();
            if (Utilities.listHasElements(getRelatedViewModel().getSelectedMentees())) {
                for (Tutored t : getRelatedViewModel().getSelectedMentees()) {
                    if (t.isSelected()) t.setItemSelected(false);
                }
            }
            dialog.dismiss();
        });

        // Cancelar -> limpa os marcados no diálogo
        dlgBinding.btnCancel.setOnClickListener(v -> {
            List<Tutored> toRemove = new ArrayList<>();
            if (Utilities.listHasElements(getRelatedViewModel().getSelectedMentees())) {
                for (Tutored t : getRelatedViewModel().getSelectedMentees()) {
                    if (t.isSelected()) {
                        t.setItemSelected(false);
                        toRemove.add(t);
                    }
                }
                getRelatedViewModel().removeAll(toRemove);
                displaySelectedMentees();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    public void displaySelectedMentees() {
        if (tutoredAdapter != null) {
            tutoredAdapter.notifyDataSetChanged();
            return;
        }
        binding.rcvSelectedMentees.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rcvSelectedMentees.setItemAnimator(new DefaultItemAnimator());
        int spacing = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        binding.rcvSelectedMentees.addItemDecoration(new SpacingItemDecoration(spacing));
        binding.rcvSelectedMentees.setHasFixedSize(true);

        tutoredAdapter = new TutoredAdapter(binding.rcvSelectedMentees, getRelatedViewModel().getSelectedMentees(), this, null);
        binding.rcvSelectedMentees.setAdapter(tutoredAdapter);
    }

    // ======== Boilerplate ========
    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(RondaVM.class);
    }

    @Override
    public RondaVM getRelatedViewModel() {
        return (RondaVM) super.getRelatedViewModel();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Map<String, Object> params = new HashMap<>();
            params.put("title", getRelatedViewModel().getRonda().isRondaZero() ? getString(R.string.ronda_zero) : getString(R.string.ronda_mentoria));
            params.put("rondaType", getRelatedViewModel().getRonda().getRondaType());
            this.getRelatedViewModel().getRelatedActivity().nextActivityFinishingCurrent(RondaActivity.class, params);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
