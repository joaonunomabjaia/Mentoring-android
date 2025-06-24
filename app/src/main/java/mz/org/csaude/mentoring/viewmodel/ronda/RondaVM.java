package mz.org.csaude.mentoring.viewmodel.ronda;

import android.app.Application;
import android.app.Dialog;
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
import mz.org.csaude.mentoring.dto.ronda.RondaDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.listner.rest.ServerStatusListener;
import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.location.Province;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.ronda.RondaMentor;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.service.ronda.RondaService;
import mz.org.csaude.mentoring.service.ronda.RondaTypeService;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.LifeCycleStatus;
import mz.org.csaude.mentoring.util.RondaTypeEnum;
import mz.org.csaude.mentoring.util.SimpleValue;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.ronda.CreateRondaActivity;
import mz.org.csaude.mentoring.view.ronda.RondaActivity;

public class RondaVM extends BaseViewModel implements RestResponseListener<Ronda>, ServerStatusListener {
    private RondaService rondaService;
    private RondaTypeService rondaTypeService;
    private Ronda ronda;
    private Province selectedProvince;
    private SimpleValue mentorType;
    private District selectedDistrict;
    private HealthFacility selectedHealthFacility;
    private List<Province> provinces;
    private List<District> districts;
    private List<HealthFacility> healthFacilities;
    private Tutored selectedMentee;
    private List<Tutored> menteeList;
    private Dialog progressDialog;
    private District districtToSelect;
    private List<Tutored> selectedMentees;
    private HealthFacility healthFacilityToSelect;


    private ObservableField<String> searchText = new ObservableField<>("");

    public RondaVM(@NonNull Application application) {
        super(application);
        this.ronda = new Ronda();
        rondaService = getApplication().getRondaService();
        rondaTypeService = getApplication().getRondaTypeService();
    }

    @Override
    public CreateRondaActivity getRelatedActivity() {
        return (CreateRondaActivity) super.getRelatedActivity();
    }

    @Override
    public void preInit() {
        getExecutorService().execute(()->{
            try {
                this.provinces = getApplication().getProvinceService().getAll();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Ronda getRonda() {
        return ronda;
    }

    @Bindable
    public Date getStartDate() {
        return this.ronda.getStartDate();
    }

    public void setStartDate(Date startDate) {
        this.ronda.setStartDate(startDate);
        notifyPropertyChanged(BR.startDate);
    }

    public void setRonda(Ronda ronda) {
        this.ronda = ronda;
    }

    @Bindable
    public Listble getHealthFacility() {
        /*if (getCurrentStep().isApplicationStepEdit() && ronda.getHealthFacility() != null) {
            selectedHealthFacility = ronda.getHealthFacility();
            return ronda.getHealthFacility();
        }*/
        return selectedHealthFacility;
    }

    public void setHealthFacility(Listble selectedHealthFacility) {
        if (selectedHealthFacility == null || StringUtils.isEmpty(((HealthFacility) selectedHealthFacility).getUuid())) {
            return;
        }
        this.selectedHealthFacility = (HealthFacility) selectedHealthFacility;
        if (!StringUtils.isEmpty(this.selectedHealthFacility.getUuid())) {
            this.ronda.setHealthFacility(this.selectedHealthFacility);
            notifyPropertyChanged(BR.healthFacility);
            if (!getCurrentStep().isApplicationStepEdit()) {
                getExecutorService().execute(() -> {

                    // Clear the existing mentee list
                    if (this.menteeList == null) {
                        this.menteeList = new ArrayList<>();
                    }
                    this.menteeList.clear();

                    try {
                        // Fetch the mentee list for the selected health facility and mentoring round
                        List<Tutored> fetchedMentees = getApplication().getTutoredService()
                                .getAllForMentoringRound(ronda.getHealthFacility(), !this.ronda.isRondaZero());

                        setMenteeList(fetchedMentees);

                    } catch (SQLException e) {
                        // Handle the SQL exception and show an error message on the main thread
                        runOnMainThread(() -> {
                            Log.e("RondaVM", "Error loading mentees", e);
                            String errorMessage = getRelatedActivity().getString(R.string.mentees_load_error);
                            Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                        });

                    }
                });
            }
        }

    }


    public void setMenteeList(List<Tutored> menteeList) {
        this.menteeList = menteeList;
        for (Tutored tutored : this.menteeList) {
            tutored.setListType(Listble.ListTypes.SELECTION_LIST);
        }
    }

    public List<Tutored> getrondaMenteeList() {
        List<Tutored> availableMentees = new ArrayList<>();

        if (!Utilities.listHasElements(getSelectedMentees()))  return menteeList;

            for (Tutored mentee : menteeList) {
                if (!getSelectedMentees().contains(mentee)) {
                    availableMentees.add(mentee);
                }
            }

        return availableMentees;
    }

    public void addMentee(Listble mentee) {
        if (this.ronda.getRondaMentees() == null) this.ronda.setRondaMentees(new ArrayList<>());
        if (!this.ronda.isRondaZero() && this.ronda.getRondaMentees().size() < 8) {
            this.ronda.getRondaMentees().add(RondaMentee.fastCreate(this.ronda, (Tutored) mentee));
        } else if (this.ronda.isRondaZero()) {
            this.ronda.getRondaMentees().add(RondaMentee.fastCreate(this.ronda, (Tutored) mentee));
        } else {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.max_mentees_reached)).show();
        }
    }


    @Bindable
    public Listble getSelectedProvince() {
        /*if (getCurrentStep().isApplicationStepEdit() && ronda.getHealthFacility() != null && ronda.getHealthFacility().getDistrict() != null) {
            selectedProvince = ronda.getHealthFacility().getDistrict().getProvince();
            return ronda.getHealthFacility().getDistrict().getProvince();
        }*/
        return selectedProvince;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setSelectedProvince(Listble selectedProvince) {
        if (selectedProvince == null || selectedProvince.getId() == null
                ) {
            return;
        }

        getExecutorService().execute(() -> {
            this.selectedProvince = (Province) selectedProvince;

            if (this.districts == null) this.districts = new ArrayList<>();
            if (this.healthFacilities == null) this.healthFacilities = new ArrayList<>();

            this.districts.clear();
            this.healthFacilities.clear();

            this.districts.add(new District());

            try {
                List<District> fetchedDistricts = getApplication().getDistrictService()
                        .getByProvinceAndMentor(this.selectedProvince, getApplication().getCurrMentor());

                if (fetchedDistricts != null && !fetchedDistricts.isEmpty()) {
                    this.districts.addAll(fetchedDistricts);
                }

                // Armazenamos o distrito para aplicar depois
                if (getCurrentStep().isApplicationStepEdit() && ronda.getHealthFacility() != null) {
                    districtToSelect = ronda.getHealthFacility().getDistrict();
                }

                runOnMainThread(() -> {
                    getRelatedActivity().reloadDistrictAdapter();

                    notifyPropertyChanged(BR.selectedProvince);

                    // ✅ Só agora chamamos setSelectedDistrict se for edição
                    if (districtToSelect != null) {
                        setSelectedDistrict(districtToSelect);
                        districtToSelect = null; // limpamos
                    }
                });

            } catch (SQLException e) {
                runOnMainThread(() -> {
                    e.printStackTrace();
                    String errorMessage = getRelatedActivity().getString(R.string.districts_load_error);
                    Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                });
            }
        });
    }

    @Bindable
    public Listble getMentorType() {
        return new SimpleValue(ronda.getMentorType());
    }

    public void setMentorType(Listble mentorType) {
        this.mentorType = (SimpleValue) mentorType;
        this.ronda.setMentorType(this.mentorType.getDescription());
        notifyPropertyChanged(BR.mentorType);
    }

    @Bindable
    public Listble getSelectedDistrict() {
        /*if (getCurrentStep().isApplicationStepEdit() && ronda.getHealthFacility() != null) {
            selectedDistrict = ronda.getHealthFacility().getDistrict();
            return ronda.getHealthFacility().getDistrict();
        }*/
        return selectedDistrict;
    }

    public void setSelectedDistrict(Listble selectedDistrict) {
        if (selectedDistrict == null || selectedDistrict.getId() == null
                ) return;

        getExecutorService().execute(() -> {
            if (this.healthFacilities == null) this.healthFacilities = new ArrayList<>();
            this.healthFacilities.clear();
            this.healthFacilities.add(new HealthFacility());

            try {
                this.selectedDistrict = (District) selectedDistrict;

                List<HealthFacility> facilities = getApplication().getHealthFacilityService()
                        .getHealthFacilityByDistrictAndMentor(this.selectedDistrict, getApplication().getCurrMentor());

                if (facilities != null && !facilities.isEmpty()) {
                    this.healthFacilities.addAll(facilities);
                }

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
                runOnMainThread(() -> {
                    e.printStackTrace();
                    String errorMessage = getRelatedActivity().getString(R.string.health_facilities_load_error);
                    Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                });
            }
        });
    }




    @Bindable
    public Tutored getSelectedMentee() {
        return selectedMentee;
    }

    public void setSelectedMentee(Tutored selectedMentee) {
        this.selectedMentee = selectedMentee;
        notifyPropertyChanged(BR.selectedMentee);
    }

    public void searchMentees() {
        if (ronda.getHealthFacility() == null || StringUtils.isEmpty(ronda.getHealthFacility().getUuid())) {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.health_facility_required)).show();
            return;
        } else {
            getExecutorService().execute(() -> {

                // Clear the existing mentee list
                if (this.menteeList == null) {
                    this.menteeList = new ArrayList<>();
                }
                this.menteeList.clear();

                try {
                    // Fetch the mentee list for the selected health facility and mentoring round
                    List<Tutored> fetchedMentees = getApplication().getTutoredService()
                            .getAllForMentoringRound(ronda.getHealthFacility(), !this.ronda.isRondaZero());

                    setMenteeList(fetchedMentees);

                    runOnMainThread(() -> {
                        getRelatedActivity().openSearchMenteesDialog();
                    });
                } catch (SQLException e) {
                    // Handle the SQL exception and show an error message on the main thread
                    runOnMainThread(() -> {
                        Log.e("RondaVM", "Error loading mentees", e);
                        String errorMessage = getRelatedActivity().getString(R.string.mentees_load_error);
                        Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
                    });

                }
            });
        }
    }

    public void addSelectedMentee() {
        // validate mentee here...
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
                String message = getRelatedActivity().getString(R.string.mentee_already_in_list);
                Utilities.displayAlertDialog(getRelatedActivity(), message).show();
            }
        } else {
            String message = getRelatedActivity().getString(R.string.mentee_field_empty);
            Utilities.displayAlertDialog(getRelatedActivity(), message).show();
        }
    }


    public void save() {
        this.doSave();
    }
    private void doSave() {
        if (!isValid()) return;
        // Perform the save operation in the background
        getExecutorService().execute(() -> {
            runOnMainThread(()-> {
                progressDialog = Utilities.showLoadingDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.processando));
            });
            try {
                prepareRonda();
                // Prepare the list of RondaMentees and RondaMentors
                List<RondaMentee> rondaMentees = createRondaMentees();
                List<RondaMentor> rondaMentors = createRondaMentors();

                // Set the mentees and mentors to the ronda
                this.ronda.setRondaMentees(rondaMentees);
                this.ronda.setRondaMentors(rondaMentors);

                // Validate the ronda
                String error = this.ronda.validade();
                if (Utilities.stringHasValue(error)) {
                    runOnMainThread(() -> Utilities.displayAlertDialog(getRelatedActivity(), error).show());
                    return;
                }


                this.ronda.setCreatedByUuid(getApplication().getAuthenticatedUser().getUuid());
                // Check if the server is online (this may run in a background thread, depending on your implementation)
                getApplication().isServerOnline(this);

            } catch (SQLException e) {
                runOnMainThread(() -> {
                    dismissProgress(progressDialog);
                    e.printStackTrace();
                    String errorMessage = getRelatedActivity().getString(R.string.ronda_save_error);
                    Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();

                });
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

        // Generate and set the description based on count
        int count = getApplication().getRondaService().countRondas() + 1;
        ronda.setDescription(ronda.getRondaType().getDescription() + " " + count);
    }

    private List<RondaMentee> createRondaMentees() throws SQLException {
        List<RondaMentee> rondaMentees = new ArrayList<>();

        for (Tutored tutored : this.getSelectedMentees()) {
            RondaMentee rondaMentee = new RondaMentee();
            rondaMentee.setUuid(Utilities.getNewUUID().toString());
            rondaMentee.setSyncStatus(SyncSatus.SENT);
            rondaMentee.setCreatedAt(DateUtilities.getCurrentDate());
            rondaMentee.setTutored(tutored);
            rondaMentee.setStartDate(this.getStartDate());
            rondaMentee.setCreatedByUuid(getApplication().getAuthenticatedUser().getUuid());
            rondaMentees.add(rondaMentee);
        }

        return rondaMentees;
    }

    private List<RondaMentor> createRondaMentors() throws SQLException {
        List<RondaMentor> rondaMentors = new ArrayList<>();
        Tutor tutor = this.getApplication().getCurrMentor();
        RondaMentor rondaMentor = new RondaMentor();

        if (!getApplication().getApplicationStep().isApplicationStepEdit()) {
            rondaMentor.setUuid(Utilities.getNewUUID().toString());
            rondaMentor.setCreatedAt(DateUtilities.getCurrentDate());
            rondaMentor.setCreatedByUuid(getApplication().getAuthenticatedUser().getUuid());
            rondaMentor.setStartDate(this.getStartDate());
        } else {
            rondaMentor.setUpdatedByUuid(getApplication().getAuthenticatedUser().getUuid());
        }

        rondaMentor.setSyncStatus(SyncSatus.SENT);
        rondaMentor.setTutor(tutor);
        rondaMentors.add(rondaMentor);

        return rondaMentors;
    }


    private boolean isValid() {
        if (this.ronda.getStartDate() == null) {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.start_date_required)).show();
            return false;
        }
        if (this.ronda.getHealthFacility() == null) {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.health_facility_required)).show();
            return false;
        }
        if (!Utilities.listHasElements(this.selectedMentees)) {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.mentees_required)).show();
            return false;
        }

        return true;
    }

    @Bindable
    public List<Tutored> getMenteeList() {
        if (menteeList == null) menteeList = new ArrayList<>();
        return menteeList;
    }



    public List<Tutored> getMentees() {
        try {
            return getApplication().getTutoredService().getAllOfRondaForNewRonda(selectedHealthFacility);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Province> getAllProvince() throws SQLException {
        List<Province> provinceList = new ArrayList<>();
        provinceList.add(new Province());
        provinceList.addAll(getApplication().getProvinceService().getAllOfTutor(getApplication().getCurrMentor()));
        return provinceList;
    }
    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }
    public List<HealthFacility> getHealthFacilities() {
        return healthFacilities;
    }
    public void setHealthFacilities(List<HealthFacility> healthFacilities) {
        this.healthFacilities = healthFacilities;
    }
    public void edit(Ronda ronda) {
        Map<String, Object> params = new HashMap<>();
        params.put("relatedRecord", ronda);
        params.put("title", ronda.isRondaZero() ? "Ronda Zero" : "Ronda de Mentoria");
        getCurrentStep().changeToEdit();
        getRelatedActivity().nextActivityFinishingCurrent(CreateRondaActivity.class, params);
    }

    public void setSelectedMentees(List<Tutored> mentees) {
        this.selectedMentees = mentees;
        notifyPropertyChanged(BR.selectedMentees);
    }

    @Bindable
    public List<Tutored> getSelectedMentees() {
        if (selectedMentees == null) selectedMentees = new ArrayList<>();
        return this.selectedMentees;
    }
    private boolean validateRondaMentee(Tutored selectedRondaMentee) {
        // TODO
        return true;
    }

    public void removeFromSelected(Tutored tutored) {
        this.selectedMentees.remove(tutored);
        getRelatedActivity().displaySelectedMentees();
        notifyPropertyChanged(BR.selectedMentees);

    }

    public void changeInitialDataViewStatus(View view){
        getRelatedActivity().changeFormSectionVisibility(view);
    }

    @Override
    public void onServerStatusChecked(boolean isOnline, boolean isSlow) {
        if (isOnline) {
            if (isSlow) {
                // Show warning: Server is slow
                showSlowConnectionWarning(getRelatedActivity());
            }
            if (getApplication().getApplicationStep().isApplicationStepEdit()) {
                getApplication().getRondaRestService().restPatchRonda(this.ronda, this);
            } else {
                getApplication().getRondaRestService().restPostRonda(this.ronda, this);
            }
        } else {
            runOnMainThread(()-> {
                Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(mz.org.csaude.mentoring.R.string.server_unavailable)).show();
            });
        }
    }
    @Override
    public void doOnRestErrorResponse(String errorMsg) {
        runOnMainThread(()-> {
            dismissProgress(progressDialog);
            Utilities.displayAlertDialog(getRelatedActivity(), errorMsg).show();
        });
    }

    @Override
    public void doOnResponse(String flag, List<Ronda> objects) {
        runOnMainThread(()->{
            dismissProgress(progressDialog);
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
        try {
            ronda.setRondaMentees(getApplication().getRondaMenteeService().getAllOfRonda(ronda));
            if (this.selectedMentees == null) {
                this.selectedMentees = new ArrayList<>();
            }
            for (RondaMentee rondaMentee : ronda.getRondaMentees()) {
                rondaMentee.getTutored().setListType(Listble.ListTypes.SELECTION_LIST);
                this.selectedMentees.add(rondaMentee.getTutored());
            }

            // Atualiza o distrito no objeto
            ronda.getHealthFacility().setDistrict(
                    getApplication().getDistrictService().getById(ronda.getHealthFacility().getDistrictId())
            );

            // ❌ NÃO chame setSelectedDistrict nem setHealthFacility diretamente aqui
            // ✅ Apenas setSelectedProvince → os outros serão encadeados automaticamente
            this.setSelectedProvince(ronda.getHealthFacility().getDistrict().getProvince());

            runOnMainThread(() -> getRelatedActivity().displaySelectedMentees());

            this.setMentorType(new SimpleValue(ronda.getMentorType()));
            this.setStartDate(ronda.getStartDate());

        } catch (Exception e) {
            runOnMainThread(() -> {
                Log.e("RondaVM", "initRondaEdition:" + e.getMessage());
                String errorMessage = getRelatedActivity().getString(R.string.ronda_load_error);
                Utilities.displayAlertDialog(getRelatedActivity(), errorMessage).show();
            });
        }
    }


    public void addToSelected(Tutored tutored) {
        if (this.selectedMentees == null) this.selectedMentees = new ArrayList<>();
        this.selectedMentees.add(tutored);
        notifyPropertyChanged(BR.selectedMentees);
    }

    public void removeAll(List<Tutored> toRemove) {
        getSelectedMentees().removeAll(toRemove);
        notifyPropertyChanged(BR.selectedMentees);
    }
}
