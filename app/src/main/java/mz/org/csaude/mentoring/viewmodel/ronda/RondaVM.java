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
    private Province province;
    private List<Province> provinces;
    private List<District> districts;
    private List<HealthFacility> healthFacilities;
    private Tutored selectedMentee;
    private List<Tutored> menteeList;
    private Dialog progressDialog;

    private List<Tutored> selectedMentees;

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
        return this.ronda.getHealthFacility();
    }

    public void setHealthFacility(Listble selectedHealthFacility) {
        if (selectedHealthFacility == null || StringUtils.isEmpty(((HealthFacility) selectedHealthFacility).getUuid())) {
            return;
        }

        this.selectedHealthFacility = (HealthFacility) selectedHealthFacility;

        // Clear the existing mentee list
        if (this.menteeList == null) {
            this.menteeList = new ArrayList<>();
        }
        this.menteeList.clear();

        // Perform the database operation in the background
        getExecutorService().execute(() -> {
            try {
                // Update health facility and load the mentees list in the background
                if (!StringUtils.isEmpty(this.selectedHealthFacility.getUuid())) {
                    this.ronda.setHealthFacility(this.selectedHealthFacility);

                    // Fetch the mentee list for the selected health facility and mentoring round
                    List<Tutored> fetchedMentees = getApplication().getTutoredService()
                            .getAllForMentoringRound(this.selectedHealthFacility, !this.ronda.isRondaZero());

                    // Update UI on the main thread
                    runOnMainThread(() -> {
                        setMenteeList(fetchedMentees);

                        // Reload the mentees adapter and notify property changes
                        getRelatedActivity().reloadMenteesAdapter();
                        notifyPropertyChanged(BR.healthFacility);
                    });
                }
            } catch (SQLException e) {
                // Handle the SQL exception and show an error message on the main thread
                runOnMainThread(() -> {
                    e.printStackTrace();
                    Utilities.displayAlertDialog(getRelatedActivity(), "Failed to load mentees").show();
                });
            }
        });
    }


    public void setMenteeList(List<Tutored> menteeList) {
        this.menteeList = menteeList;
        for (Tutored tutored : this.menteeList) {
            tutored.setListType(Listble.ListTypes.SELECTION_LIST);
        }
    }

    public List<Tutored> getrondaMenteeList() {
        return menteeList;
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
        return selectedProvince;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setSelectedProvince(Listble selectedProvince) {
        this.selectedProvince = (Province) selectedProvince;

        // Check if the province is valid before proceeding
        if (this.selectedProvince == null || this.selectedProvince.getId() == null) {
            return;
        }

        // Ensure districts and health facilities lists are initialized
        if (this.districts == null) this.districts = new ArrayList<>();
        if (this.healthFacilities == null) this.healthFacilities = new ArrayList<>();

        // Clear previous data
        this.districts.clear();
        this.healthFacilities.clear();

        // Add default empty district item
        this.districts.add(new District());

        // Fetch districts in a background thread to avoid locking the UI
        getExecutorService().execute(() -> {
            try {
                // Fetch districts from the database based on the selected province and mentor
                List<District> fetchedDistricts = getApplication().getDistrictService().getByProvinceAndMentor(this.selectedProvince, getApplication().getCurrMentor());

                // Update the districts and UI on the main thread
                runOnMainThread(() -> {
                    if (fetchedDistricts != null && !fetchedDistricts.isEmpty()) {
                        this.districts.addAll(fetchedDistricts);
                    }

                    // Refresh district adapter
                    getRelatedActivity().reloadDistrictAdapter();
                });

            } catch (SQLException e) {
                runOnMainThread(() -> {
                    e.printStackTrace();
                    // Display error alert
                    Utilities.displayAlertDialog(getRelatedActivity(), "Failed to load districts").show();
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
        return selectedDistrict;
    }

    public void setSelectedDistrict(Listble selectedDistrict) {
        if (selectedDistrict == null || selectedDistrict.getId() == null) return;

        // Clear existing health facilities before fetching new ones
        this.healthFacilities.clear();

        // Add a default empty HealthFacility item
        this.healthFacilities.add(new HealthFacility());

        // Perform the database operation in the background
        getExecutorService().execute(() -> {
            try {
                this.selectedDistrict = (District) selectedDistrict;

                // Fetch health facilities based on the selected district and mentor
                List<HealthFacility> facilities = getApplication().getHealthFacilityService()
                        .getHealthFacilityByDistrictAndMentor(this.selectedDistrict, getApplication().getCurrMentor());

                // Update the UI on the main thread
                runOnMainThread(() -> {
                    if (facilities != null && !facilities.isEmpty()) {
                        this.healthFacilities.addAll(facilities);
                    }

                    // Refresh the health facility spinner
                    getRelatedActivity().reloadHealthFacility();

                    // Notify that the selected district property has changed
                    notifyPropertyChanged(BR.selectedDistrict);
                });

            } catch (SQLException e) {
                runOnMainThread(() -> {
                    e.printStackTrace();
                    // Display an error alert if the query fails
                    Utilities.displayAlertDialog(getRelatedActivity(), "Failed to load health facilities").show();
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

    public void addSelectedMentee() {
        // validate mentee here...
        if (selectedMentees == null) selectedMentees = new ArrayList<>();
        if(selectedMentee != null){
            if (!selectedMentees.contains(selectedMentee)) {
                selectedMentee.setListPosition(selectedMentees.size()+1);
                selectedMentee.setListType(Listble.ListTypes.SELECTION_LIST);
                selectedMentees.add(selectedMentee);
                getRelatedActivity().displaySelectedMentees();
                setSelectedMentee(null);
                notifyPropertyChanged(BR.selectedMentee);
                notifyPropertyChanged(BR.selectedMentees);
            }else {
                Utilities.displayAlertDialog(getRelatedActivity(), "O Mentorando seleccionado já existe na lista!").show();
            }
        }else{
            Utilities.displayAlertDialog(getRelatedActivity(),"Campo Mentorando está vazio. Por favor, seleccione um Mentorando para adicionar à lista.").show();
        }
    }

    public void save() {
        this.doSave();
    }
    private void doSave() {
        if (!isValid()) return;

        // Perform the save operation in the background
        getExecutorService().execute(() -> {
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

                // Check if the server is online (this may run in a background thread, depending on your implementation)
                getApplication().isServerOnline(this);

            } catch (SQLException e) {
                runOnMainThread(() -> {
                    e.printStackTrace();
                    Utilities.displayAlertDialog(getRelatedActivity(), "Failed to save ronda").show();
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
            rondaMentor.setStartDate(this.getStartDate());
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
        getCurrentStep().changeToEdit();
        getRelatedActivity().nextActivity(CreateRondaActivity.class, params);
    }

    public void setSelectedMentees(List<Tutored> mentees) {
        this.selectedMentees = mentees;
    }

    @Bindable
    public List<Tutored> getSelectedMentees() {
        return this.selectedMentees;
    }
    private boolean validateRondaMentee(Tutored selectedRondaMentee) {
        // TODO
        return true;
    }

    public void removeFromSelected(Tutored tutored) {
        this.selectedMentees.remove(tutored);
        getRelatedActivity().displaySelectedMentees();

    }

    public void changeInitialDataViewStatus(View view){
        getRelatedActivity().changeFormSectionVisibility(view);
    }

    @Override
    public void onServerStatusChecked(boolean isOnline) {
        if (isOnline) {
            runOnMainThread(()-> {
                progressDialog = Utilities.showLoadingDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.processando));
            });
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
            params.put("title", objects.get(0).isRondaZero() ? "Ronda Zero" : "Ronda de Mentoria");
            getApplication().getApplicationStep().changeToList();
            getRelatedActivity().nextActivityFinishingCurrent(RondaActivity.class, params);
        });
    }

    public void initRondaEdition() {
        try {
            ronda.setRondaMentees(getApplication().getRondaMenteeService().getAllOfRonda(ronda));
            if (this.selectedMentees == null) this.selectedMentees = new ArrayList<>();
            for (RondaMentee rondaMentee : ronda.getRondaMentees()) {
                rondaMentee.getTutored().setListType(Listble.ListTypes.SELECTION_LIST);
                this.selectedMentees.add(rondaMentee.getTutored());
            }

            getRelatedActivity().displaySelectedMentees();

            ronda.getHealthFacility().setDistrict(getApplication().getDistrictService().getById(ronda.getHealthFacility().getDistrict().getId()));
            this.setSelectedProvince(getRonda().getHealthFacility().getDistrict().getProvince());
            this.setSelectedDistrict(getRonda().getHealthFacility().getDistrict());
            this.setHealthFacility(getRonda().getHealthFacility());
        } catch (SQLException e) {
            Log.e("RondaVM", "initRondaEdition:" + e.getMessage());
        }

    }
}
