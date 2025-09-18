package mz.org.csaude.mentoring.view.ronda;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import mz.org.csaude.mentoring.util.RondaTypeEnum;
import mz.org.csaude.mentoring.util.SimpleValue;
import mz.org.csaude.mentoring.util.SpacingItemDecoration;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.viewmodel.ronda.RondaVM;
import mz.org.csaude.mentoring.adapter.spinner.CustomSearchSpinnerView;

public class CreateRondaActivity extends BaseActivity {

    private ActivityRondaBinding rondaBinding;
    private ListableSpinnerAdapter districtAdapter;
    private ListableSpinnerAdapter provinceAdapter;
    private ListableSpinnerAdapter healthFacilityAdapter;
    private ListableSpinnerAdapter mentorTypeAdapter;
    private RecyclerView rcvSelectedMentees;
    private TutoredAdapter tutoredAdapter;
    private String title;
    private RondaType rondaTypeOption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        rondaBinding = DataBindingUtil.setContentView(this, R.layout.activity_ronda);
        rondaBinding.setViewModel(getRelatedViewModel());

        rcvSelectedMentees = rondaBinding.rcvSelectedMentees;
        setSupportActionBar(rondaBinding.toolbar.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();

        // Executa carregamento inicial em background
        getRelatedViewModel().getExecutorService().execute(() -> {
            initAdapters();

            if (intent != null && intent.getExtras() != null) {
                title = intent.getStringExtra("title");

                if (getApplicationStep().isApplicationstepCreate()) {
                    rondaTypeOption = (RondaType) intent.getSerializableExtra("rondaType");
                    getRelatedViewModel().getRonda().setRondaType(rondaTypeOption);
                } else {
                    Ronda ronda = (Ronda) intent.getSerializableExtra("ronda");
                    getRelatedViewModel().setRonda(ronda);
                    getRelatedViewModel().initRondaEdition();
                }
            }

            runOnUiThread(() -> {
                getSupportActionBar().setTitle(title);
            });
        });

        setupDatePicker();
    }

    private void setupDatePicker() {
        rondaBinding.rondaStartDate.setOnClickListener(view -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateRondaActivity.this, R.style.CustomDatePickerDialogTheme,
                    (view1, year, monthOfYear, dayOfMonth) ->
                            getRelatedViewModel().setStartDate(DateUtilities.createDate(
                                    dayOfMonth + "-" + (monthOfYear + 1) + "-" + year,
                                    DateUtilities.DATE_FORMAT)),
                    mYear, mMonth, mDay);
            datePickerDialog.show();
            Button positiveButton = datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            if (positiveButton != null) positiveButton.setTextColor(Color.BLACK);
            if (negativeButton != null) negativeButton.setTextColor(Color.BLACK);
        });
    }

    public void openSearchMenteesDialog() {
        setupSelectMenteesDialog();
    }

    private void setupSelectMenteesDialog() {
        // Infla o layout com DataBinding
        DialogSelectMenteesBinding binding = DialogSelectMenteesBinding.inflate(getLayoutInflater());
        binding.setViewModel(getRelatedViewModel());

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();

        EditText searchInput = binding.searchInput;
        RecyclerView recyclerMentees = binding.recyclerMentees;
        Button btnCancel = binding.btnCancel;
        Button btnAdd = binding.btnAdd;

        // Lista de mentees disponíveis e já selecionados
        List<Tutored> mentees = getRelatedViewModel().getrondaMenteeList();
        // Adapter com filtro
        TutoredSelectionAdapter adapter = new TutoredSelectionAdapter(binding.recyclerMentees, mentees, this, getRelatedViewModel());
        recyclerMentees.setLayoutManager(new LinearLayoutManager(this));
        recyclerMentees.setAdapter(adapter);

        // Filtro de busca em tempo real
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Ação do botão "Adicionar"
        btnAdd.setOnClickListener(v -> {
            displaySelectedMentees();
            if (Utilities.listHasElements(getRelatedViewModel().getSelectedMentees())) {
                for (Tutored tutored : getRelatedViewModel().getSelectedMentees()) {
                    if (tutored.isSelected()) {
                        tutored.setItemSelected(false);
                    }
                }
            }
            dialog.dismiss();
        });

        // Ação do botão "Cancelar"
        btnCancel.setOnClickListener(v -> {
            List<Tutored> toRemove = new ArrayList<>();
            if (Utilities.listHasElements(getRelatedViewModel().getSelectedMentees())) {
                for (Tutored tutored : getRelatedViewModel().getSelectedMentees()) {
                    if (tutored.isSelected()) {
                        tutored.setItemSelected(false);
                        toRemove.add(tutored);
                    }
                }
                getRelatedViewModel().removeAll(toRemove);
                displaySelectedMentees(); // Atualiza o RecyclerView principal
            }
            dialog.dismiss();
        });

        dialog.show();
    }



    private void initAdapters() {
        runOnUiThread(this::setupMentorTypeAdapter);
        try {
            List<Province> provinces = getRelatedViewModel().getAllProvince();
            runOnUiThread(() -> {
                provinceAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, provinces);
                rondaBinding.spnProvince.setAdapter(provinceAdapter);
                rondaBinding.setProvinceAdapter(provinceAdapter);
            });
        } catch (SQLException e) {
            runOnUiThread(() -> handleSQLException(e));
        }
    }

    private void setupMentorTypeAdapter() {
        List<SimpleValue> mentorTypes = getMentorTypes();
        mentorTypeAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, mentorTypes);
        rondaBinding.spnMentorType.setAdapter(mentorTypeAdapter);
    }

    private List<SimpleValue> getMentorTypes() {
        List<SimpleValue> mentorTypes = new ArrayList<>();
        mentorTypes.add(new SimpleValue(1, getString(R.string.interno)));
        mentorTypes.add(new SimpleValue(2, getString(R.string.externo)));
        return mentorTypes;
    }

    private void handleSQLException(SQLException e) {
        Log.e("Database Error", "Error while initializing adapters: ", e);
    }

    public void reloadDistrictAdapter() {
        runOnUiThread(() -> {
            districtAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, getRelatedViewModel().getDistricts());
            rondaBinding.spnDistrict.setAdapter(districtAdapter);
            rondaBinding.setDistrictAdapter(districtAdapter);
        });
    }

    public void reloadHealthFacility() {
        runOnUiThread(() -> {
            healthFacilityAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, getRelatedViewModel().getHealthFacilities());
            rondaBinding.spnHealthFacility.setAdapter(healthFacilityAdapter);
            rondaBinding.setHealthFacilityAdapter(healthFacilityAdapter);
        });
    }

    public void displaySelectedMentees() {
        if (tutoredAdapter != null) {
            tutoredAdapter.notifyDataSetChanged();
        } else {
            rcvSelectedMentees.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            rcvSelectedMentees.setItemAnimator(new DefaultItemAnimator());

            int spacingInPixels = getApplicationContext().getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
            rcvSelectedMentees.addItemDecoration(new SpacingItemDecoration(spacingInPixels));
            rcvSelectedMentees.setHasFixedSize(true);

            tutoredAdapter = new TutoredAdapter(rcvSelectedMentees, getRelatedViewModel().getSelectedMentees(), this);
            rcvSelectedMentees.setAdapter(tutoredAdapter);
        }
    }

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

    public void changeFormSectionVisibility(View view) {
        if (view.equals(rondaBinding.initialData)) {
            if (rondaBinding.initialDataLyt.getVisibility() == View.VISIBLE) {
                rondaBinding.btnShowCollapse.setImageResource(R.drawable.sharp_arrow_drop_up_24);
                Utilities.collapse(rondaBinding.initialDataLyt);
            } else {
                Utilities.expand(rondaBinding.initialDataLyt);
                rondaBinding.btnShowCollapse.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            }
        }
    }


}
