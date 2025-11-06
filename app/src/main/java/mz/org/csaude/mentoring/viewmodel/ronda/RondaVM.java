package mz.org.csaude.mentoring.viewmodel.ronda;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.location.Province;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.ronda.RondaMentor;
import mz.org.csaude.mentoring.model.rondatype.EnumRondaType;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutored.EnumFlowHistory;
import mz.org.csaude.mentoring.model.tutored.EnumFlowHistoryProgressStatus;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.service.ronda.RondaService;
import mz.org.csaude.mentoring.service.ronda.RondaTypeService;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.LifeCycleStatus;
import mz.org.csaude.mentoring.util.SimpleValue;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.ronda.CreateRondaActivity;
import mz.org.csaude.mentoring.view.ronda.RondaActivity;

public class RondaVM extends BaseViewModel implements mz.org.csaude.mentoring.listner.rest.RestResponseListener<Ronda>, mz.org.csaude.mentoring.listner.rest.ServerStatusListener {

    // Services
    private RondaService rondaService;
    private RondaTypeService rondaTypeService;

    // State
    private Ronda ronda;
    private Province selectedProvince;
    private SimpleValue mentorType;
    private District selectedDistrict;
    private HealthFacility selectedHealthFacility;

    // Data sources
    private List<Province> provinces;
    private List<District> districts;
    private List<HealthFacility> healthFacilities;

    // Mentees
    private Tutored selectedMentee;
    private List<Tutored> menteeList;
    private List<Tutored> selectedMentees;

    // Encadeamento durante Edição
    private District districtToSelect;
    private HealthFacility healthFacilityToSelect;

    // Search (dialog)
    private final ObservableField<String> searchText = new ObservableField<>("");

    // --- Save-state Card (M3) flags ---
    private boolean savingCardVisible = false;
    private boolean savingRunning = false;
    private boolean savingSuccess = false;
    private boolean savingError = false;
    private String savingMessage = "";

    public RondaVM(@NonNull Application application) {
        super(application);
        this.ronda = new Ronda();
        this.rondaService = getApplication().getRondaService();
        this.rondaTypeService = getApplication().getRondaTypeService();
    }

    @Override
    public CreateRondaActivity getRelatedActivity() {
        return (CreateRondaActivity) super.getRelatedActivity();
    }

    @Override
    public void preInit() {
        getExecutorService().execute(() -> {
            try {
                this.provinces = getApplication().getProvinceService().getAll();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // ===== Bindables: Save-state card =====
    @Bindable public boolean isSavingCardVisible() { return savingCardVisible; }
    private void setSavingCardVisible(boolean v) { this.savingCardVisible = v; notifyPropertyChanged(BR.savingCardVisible); }

    @Bindable public boolean isSavingRunning() { return savingRunning; }
    private void setSavingRunning(boolean v) { this.savingRunning = v; notifyPropertyChanged(BR.savingRunning); }

    @Bindable public boolean isSavingSuccess() { return savingSuccess; }
    private void setSavingSuccess(boolean v) { this.savingSuccess = v; notifyPropertyChanged(BR.savingSuccess); }

    @Bindable public boolean isSavingError() { return savingError; }
    private void setSavingError(boolean v) { this.savingError = v; notifyPropertyChanged(BR.savingError); }

    @Bindable public String getSavingMessage() { return savingMessage; }
    private void setSavingMessage(String m) { this.savingMessage = m; notifyPropertyChanged(BR.savingMessage); }

    private void showSavingCard(String msg) {
        setSavingMessage(msg);
        setSavingError(false);
        setSavingSuccess(false);
        setSavingRunning(true);
        setSavingCardVisible(true);
    }

    private void showSavingError(String msg) {
        setSavingRunning(false);
        setSavingSuccess(false);
        setSavingError(true);
        setSavingMessage(msg);
    }

    private void showSavingSuccess(String msg) {
        setSavingRunning(false);
        setSavingError(false);
        setSavingSuccess(true);
        setSavingMessage(msg);
    }

    /** Ação do botão primário no card de estado */
    public void onSaveCardPrimaryAction() {
        if (isSavingSuccess()) {
            // Após sucesso, regressa à lista (segurança extra caso a navegação já tenha ocorrido)
            Map<String, Object> params = new HashMap<>();
            params.put("rondaType", ronda.getRondaType());
            String title = ronda.isRondaZero()
                    ? getRelatedActivity().getString(R.string.ronda_zero)
                    : getRelatedActivity().getString(R.string.ronda_mentoria);
            params.put("title", title);
            getApplication().getApplicationStep().changeToList();
            getRelatedActivity().nextActivityFinishingCurrent(RondaActivity.class, params);
        } else if (isSavingError()) {
            // Tenta novamente
            save();
        }
    }

    // ====== Core Domain Getters/Setters ======
    public Ronda getRonda() { return ronda; }
    public void setRonda(Ronda ronda) { this.ronda = ronda; }

    @Bindable
    public Date getStartDate() {
        return (this.ronda != null) ? this.ronda.getStartDate() : null;
    }

    public void setStartDate(Date startDate) {
        this.ronda.setStartDate(startDate);
        notifyPropertyChanged(BR.startDate);
    }

    // --- Mentor Type (dropdown) ---
    @Bindable
    public Listble getMentorType() {
        if (this.ronda == null) return null;
        final String mt = this.ronda.getMentorType();
        return (mt == null || mt.trim().isEmpty()) ? null : new SimpleValue(mt);
    }

    public void setMentorType(Listble mentorType) {
        this.mentorType = (SimpleValue) mentorType;
        this.ronda.setMentorType(this.mentorType.getDescription());
        notifyPropertyChanged(BR.mentorType);
    }

    // --- Province/District/HealthFacility encadeados ---
    @Bindable
    public Listble getSelectedProvince() { return selectedProvince; }

    public void setSelectedProvince(Listble selectedProvince) {
        if (selectedProvince == null || selectedProvince.getId() == null) return;

        getExecutorService().execute(() -> {
            this.selectedProvince = (Province) selectedProvince;

            if (this.districts == null) this.districts = new ArrayList<>();
            if (this.healthFacilities == null) this.healthFacilities = new ArrayList<>();
            this.districts.clear();
            this.healthFacilities.clear();
            this.districts.add(new District());

            try {
                List<District> fetched = getApplication().getDistrictService()
                        .getByProvinceAndMentor(this.selectedProvince, getApplication().getCurrMentor());
                if (fetched != null && !fetched.isEmpty()) this.districts.addAll(fetched);

                if (getCurrentStep().isApplicationStepEdit() && ronda.getHealthFacility() != null) {
                    districtToSelect = ronda.getHealthFacility().getDistrict();
                }

                runOnMainThread(() -> {
                    getRelatedActivity().reloadDistrictAdapter();
                    notifyPropertyChanged(BR.selectedProvince);

                    if (districtToSelect != null) {
                        setSelectedDistrict(districtToSelect);
                        districtToSelect = null;
                    }
                });

            } catch (SQLException e) {
                runOnMainThread(() ->
                        Utilities.displayAlertDialog(getRelatedActivity(),
                                getRelatedActivity().getString(R.string.districts_load_error)).show());
            }
        });
    }

    public List<District> getDistricts() { return districts; }
    public void setDistricts(List<District> districts) { this.districts = districts; }

    @Bindable
    public Listble getSelectedDistrict() { return selectedDistrict; }

    public void setSelectedDistrict(Listble selectedDistrict) {
        if (selectedDistrict == null || selectedDistrict.getId() == null) return;

        getExecutorService().execute(() -> {
            if (this.healthFacilities == null) this.healthFacilities = new ArrayList<>();
            this.healthFacilities.clear();
            this.healthFacilities.add(new HealthFacility());

            try {
                this.selectedDistrict = (District) selectedDistrict;
                List<HealthFacility> facilities = getApplication().getHealthFacilityService()
                        .getHealthFacilityByDistrictAndMentor(this.selectedDistrict, getApplication().getCurrMentor());
                if (facilities != null && !facilities.isEmpty()) this.healthFacilities.addAll(facilities);

                if (getCurrentStep().isApplicationStepEdit() && ronda.getHealthFacility() != null) {
                    healthFacilityToSelect = ronda.getHealthFacility();
                }

                runOnMainThread(() -> {
                    getRelatedActivity().reloadHealthFacility();

                    if (healthFacilityToSelect != null) {
                        setHealthFacility(healthFacilityToSelect);
                        healthFacilityToSelect = null;
                    }

                    notifyPropertyChanged(BR.selectedDistrict);
                });

            } catch (SQLException e) {
                runOnMainThread(() ->
                        Utilities.displayAlertDialog(getRelatedActivity(),
                                getRelatedActivity().getString(R.string.health_facilities_load_error)).show());
            }
        });
    }

    public List<HealthFacility> getHealthFacilities() { return healthFacilities; }
    public void setHealthFacilities(List<HealthFacility> healthFacilities) { this.healthFacilities = healthFacilities; }

    @Bindable
    public Listble getHealthFacility() { return selectedHealthFacility; }

    public void setHealthFacility(Listble selectedHealthFacility) {
        if (selectedHealthFacility == null || StringUtils.isEmpty(((HealthFacility) selectedHealthFacility).getUuid())) return;

        this.selectedHealthFacility = (HealthFacility) selectedHealthFacility;
        if (!StringUtils.isEmpty(this.selectedHealthFacility.getUuid())) {
            this.ronda.setHealthFacility(this.selectedHealthFacility);
            notifyPropertyChanged(BR.healthFacility);

            if (!getCurrentStep().isApplicationStepEdit()) {
                getExecutorService().execute(() -> {
                    if (this.menteeList == null) this.menteeList = new ArrayList<>();
                    this.menteeList.clear();
                    try {
                        List<Tutored> fetchedMentees = getApplication().getTutoredService()
                                .getAllForMentoringRound(ronda.getHealthFacility(), !this.ronda.isRondaZero());
                        setMenteeList(fetchedMentees);
                    } catch (SQLException e) {
                        runOnMainThread(() -> {
                            Log.e("RondaVM", "Error loading mentees", e);
                            Utilities.displayAlertDialog(getRelatedActivity(),
                                    getRelatedActivity().getString(R.string.mentees_load_error)).show();
                        });
                    }
                });
            }
        }
    }

    // ====== Mentees ======
    public void setMenteeList(List<Tutored> menteeList) {
        this.menteeList = menteeList;
        for (Tutored t : this.menteeList) t.setListType(Listble.ListTypes.SELECTION_LIST);
    }

    public List<Tutored> getrondaMenteeList() {
        List<Tutored> available = new ArrayList<>();
        if (!Utilities.listHasElements(getSelectedMentees())) return menteeList;
        for (Tutored m : menteeList) if (!getSelectedMentees().contains(m)) available.add(m);
        return available;
    }

    @Bindable
    public Tutored getSelectedMentee() { return selectedMentee; }
    public void setSelectedMentee(Tutored selectedMentee) {
        this.selectedMentee = selectedMentee;
        notifyPropertyChanged(BR.selectedMentee);
    }

    public void addSelectedMentee() {
        if (selectedMentees == null) selectedMentees = new ArrayList<>();
        if (selectedMentee != null) {
            if (!selectedMentees.contains(selectedMentee)) {
                selectedMentee.setListPosition(selectedMentees.size() + 1);
                selectedMentee.setListType(Listble.ListTypes.SELECTION_LIST);
                selectedMentees.add(selectedMentee);
                getRelatedActivity().displaySelectedMentees();
                setSelectedMentee(null);
                notifyPropertyChanged(BR.selectedMentee);
                notifyPropertyChanged(BR.selectedMentees);
            } else {
                Utilities.displayAlertDialog(getRelatedActivity(),
                        getRelatedActivity().getString(R.string.mentee_already_in_list)).show();
            }
        } else {
            Utilities.displayAlertDialog(getRelatedActivity(),
                    getRelatedActivity().getString(R.string.mentee_field_empty)).show();
        }
    }

    public void addToSelected(Tutored tutored) {
        if (this.selectedMentees == null) this.selectedMentees = new ArrayList<>();
        if (!this.ronda.isRondaZero() && this.selectedMentees.size() > 7) {
            Utilities.displayAlertDialog(getRelatedActivity(),
                    getRelatedActivity().getString(R.string.max_mentees_reached)).show();
            return;
        }
        this.selectedMentees.add(tutored);
        notifyPropertyChanged(BR.selectedMentees);
    }

    public void removeFromSelected(Tutored tutored) {
        getSelectedMentees().remove(tutored);
        getRelatedActivity().displaySelectedMentees();
        notifyPropertyChanged(BR.selectedMentees);
    }

    public void removeAll(List<Tutored> toRemove) {
        getSelectedMentees().removeAll(toRemove);
        notifyPropertyChanged(BR.selectedMentees);
    }

    @Bindable
    public List<Tutored> getSelectedMentees() {
        if (selectedMentees == null) selectedMentees = new ArrayList<>();
        return selectedMentees;
    }

    @Bindable
    public List<Tutored> getMenteeList() {
        if (menteeList == null) menteeList = new ArrayList<>();
        return menteeList;
    }

    public void searchMentees() {
        if (ronda.getHealthFacility() == null || StringUtils.isEmpty(ronda.getHealthFacility().getUuid())) {
            Utilities.displayAlertDialog(getRelatedActivity(),
                    getRelatedActivity().getString(R.string.health_facility_required)).show();
            return;
        }
        getExecutorService().execute(() -> {
            if (this.menteeList == null) this.menteeList = new ArrayList<>();
            this.menteeList.clear();
            List<Tutored> fetched = new ArrayList<>();
            EnumRondaType type = EnumRondaType.fromCode(ronda.getRondaType().getCode());
            EnumFlowHistory flow;
            switch (type) {
                case SESSAO_ZERO:      flow = EnumFlowHistory.SESSAO_ZERO;      break;
                case RONDA_MENTORIA:   flow = EnumFlowHistory.RONDA_CICLO;      break;
                case RONDA_SEMESTRAL:  flow = EnumFlowHistory.SESSAO_SEMESTRAL; break;
                default:               flow = EnumFlowHistory.RONDA_CICLO;
            }

            fetched = getApplication().getTutoredService()
                    .getByFlowHistory(flow, EnumFlowHistoryProgressStatus.AGUARDA_INICIO, ronda.getHealthFacility());

            setMenteeList(fetched);
            runOnMainThread(() -> getRelatedActivity().openSearchMenteesDialog());
        });
    }

    // ====== Persistência ======
    public void save() { doSave(); }

    private void doSave() {
        if (!isValid()) return;

        getExecutorService().execute(() -> {
            runOnMainThread(() -> showSavingCard(getRelatedActivity().getString(R.string.processando)));
            try {
                prepareRonda();

                List<RondaMentee> rondaMentees = createRondaMentees();
                List<RondaMentor> rondaMentors = createRondaMentors();
                this.ronda.setRondaMentees(rondaMentees);
                this.ronda.setRondaMentors(rondaMentors);

                String error = this.ronda.validade();
                if (Utilities.stringHasValue(error)) {
                    runOnMainThread(() -> showSavingError(error));
                    return;
                }

                this.ronda.setCreatedByUuid(getApplication().getAuthenticatedUser().getUuid());
                getApplication().isServerOnline(this);

            } catch (SQLException e) {
                runOnMainThread(() -> showSavingError(
                        getRelatedActivity().getString(R.string.ronda_save_error)));
            }
        });
    }

    private void prepareRonda() throws SQLException {
        ronda.setSyncStatus(SyncSatus.SENT);

        if (!getApplication().getApplicationStep().isApplicationStepEdit()) {
            ronda.setUuid(Utilities.getNewUUID().toString());
            ronda.setCreatedAt(DateUtilities.getCurrentDate());
            ronda.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
            ronda.setUpdatedByUuid(getApplication().getAuthenticatedUser().getUuid());
        }

        ronda.setStartDate(this.getStartDate());
        ronda.setHealthFacility(this.selectedHealthFacility);

        int count = getApplication().getRondaService().countRondas() + 1;
        ronda.setDescription(ronda.getRondaType().getDescription() + " " + count);
    }

    private List<RondaMentee> createRondaMentees() {
        List<RondaMentee> list = new ArrayList<>();
        for (Tutored t : this.getSelectedMentees()) {
            RondaMentee rm = new RondaMentee();
            rm.setUuid(Utilities.getNewUUID().toString());
            rm.setSyncStatus(SyncSatus.SENT);
            rm.setCreatedAt(DateUtilities.getCurrentDate());
            rm.setTutored(t);
            rm.setStartDate(this.getStartDate());
            rm.setCreatedByUuid(getApplication().getAuthenticatedUser().getUuid());
            list.add(rm);
        }
        return list;
    }

    private List<RondaMentor> createRondaMentors() {
        List<RondaMentor> list = new ArrayList<>();
        Tutor tutor = this.getApplication().getCurrMentor();
        RondaMentor rm = new RondaMentor();

        if (!getApplication().getApplicationStep().isApplicationStepEdit()) {
            rm.setUuid(Utilities.getNewUUID().toString());
            rm.setCreatedAt(DateUtilities.getCurrentDate());
            rm.setCreatedByUuid(getApplication().getAuthenticatedUser().getUuid());
            rm.setStartDate(this.getStartDate());
        } else {
            rm.setUpdatedByUuid(getApplication().getAuthenticatedUser().getUuid());
        }

        rm.setSyncStatus(SyncSatus.SENT);
        rm.setTutor(tutor);
        list.add(rm);
        return list;
    }

    private boolean isValid() {
        if (this.ronda.getStartDate() == null) {
            Utilities.displayAlertDialog(getRelatedActivity(),
                    getRelatedActivity().getString(R.string.start_date_required)).show();
            return false;
        }
        if (this.ronda.getHealthFacility() == null) {
            Utilities.displayAlertDialog(getRelatedActivity(),
                    getRelatedActivity().getString(R.string.health_facility_required)).show();
            return false;
        }
        if (!Utilities.listHasElements(this.selectedMentees)) {
            Utilities.displayAlertDialog(getRelatedActivity(),
                    getRelatedActivity().getString(R.string.mentees_required)).show();
            return false;
        }
        return true;
    }

    public List<Province> getAllProvince() throws SQLException {
        List<Province> list = new ArrayList<>();
        list.add(new Province());
        list.addAll(getApplication().getProvinceService().getAllOfTutor(getApplication().getCurrMentor()));
        return list;
    }

    public void edit(Ronda ronda) {
        Map<String, Object> params = new HashMap<>();
        params.put("relatedRecord", ronda);
        params.put("title", ronda.isRondaZero() ? "Ronda Zero" : "Ronda de Mentoria");
        getCurrentStep().changeToEdit();
        getRelatedActivity().nextActivityFinishingCurrent(CreateRondaActivity.class, params);
    }

    public void changeInitialDataViewStatus(View view){
        getRelatedActivity().changeFormSectionVisibility(view);
    }

    // ====== Callbacks REST/Server ======
    @Override
    public void onServerStatusChecked(boolean isOnline, boolean isSlow) {
        if (isOnline) {
            if (isSlow) showSlowConnectionWarning(getRelatedActivity());

            if (getApplication().getApplicationStep().isApplicationStepEdit()) {
                getApplication().getRondaRestService().restPatchRonda(this.ronda, this);
            } else {
                getApplication().getRondaRestService().restPostRonda(this.ronda, this);
            }
        } else {
            runOnMainThread(() ->
                    showSavingError(getRelatedActivity().getString(R.string.server_unavailable)));
        }
    }

    @Override
    public void doOnRestErrorResponse(String errorMsg) {
        runOnMainThread(() -> showSavingError(errorMsg));
    }

    @Override
    public void doOnResponse(String flag, List<Ronda> objects) {
        runOnMainThread(() -> {
            //showSavingSuccess(getRelatedActivity().getString(R.string.saved_successfully));
            // Navega imediatamente como antes
            Map<String, Object> params = new HashMap<>();
            params.put("rondaType", objects.get(0).getRondaType());
            String title = objects.get(0).isRondaZero()
                    ? getRelatedActivity().getString(R.string.ronda_zero)
                    : getRelatedActivity().getString(R.string.ronda_mentoria);
            params.put("title", title);

            getApplication().getApplicationStep().changeToList();
            getRelatedActivity().nextActivityFinishingCurrent(RondaActivity.class, params);
        });
    }

    public void initRondaEdition() {
        getExecutorService().execute(() -> {
            try {
                // 1) DB: carregar mentees da ronda
                List<RondaMentee> rondaMentees =
                        getApplication().getRondaMenteeService().getAllOfRonda(ronda);

                // 2) Preparar lista selecionada (em memória)
                List<Tutored> selected = new ArrayList<>();
                for (RondaMentee rm : rondaMentees) {
                    rm.getTutored().setListType(Listble.ListTypes.SELECTION_LIST);
                    selected.add(rm.getTutored());
                }

                // 3) DB: completar localização (district + province)
                District district = getApplication()
                        .getDistrictService()
                        .getById(ronda.getHealthFacility().getDistrictId());
                Province province = district.getProvince();

                // 4) Capturar valores simples
                Date start = ronda.getStartDate();
                String mentorType = ronda.getMentorType();

                // 5) Atualizar UI/VM na main thread
                runOnMainThread(() -> {
                    ronda.setRondaMentees(rondaMentees);
                    if (this.selectedMentees == null) this.selectedMentees = new ArrayList<>();
                    this.selectedMentees.clear();
                    this.selectedMentees.addAll(selected);
                    notifyPropertyChanged(BR.selectedMentees);

                    ronda.getHealthFacility().setDistrict(district);
                    this.setSelectedProvince(province);

                    this.setMentorType(new SimpleValue(mentorType));
                    this.setStartDate(start);

                    getRelatedActivity().displaySelectedMentees();
                });

            } catch (Exception e) {
                runOnMainThread(() -> {
                    Log.e("RondaVM", "initRondaEdition: " + e.getMessage(), e);
                    Utilities.displayAlertDialog(
                            getRelatedActivity(),
                            getRelatedActivity().getString(R.string.ronda_load_error)
                    ).show();
                });
            }
        });
    }

}
