package mz.org.csaude.mentoring.viewmodel.tutored;

import android.app.Application;
import android.app.Dialog;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.fragment.GenericFragment;
import mz.org.csaude.mentoring.base.searchparams.AbstractSearchParams;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.base.viewModel.SearchVM;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.listner.rest.ServerStatusListener;
import mz.org.csaude.mentoring.model.employee.Employee;
import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.location.Location;
import mz.org.csaude.mentoring.model.location.Province;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.model.professionalCategory.ProfessionalCategory;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.service.employee.EmployeeService;
import mz.org.csaude.mentoring.service.tutored.TutoredService;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.LifeCycleStatus;
import mz.org.csaude.mentoring.util.SimpleValue;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.home.ui.personalinfo.PersonalInfoFragment;
import mz.org.csaude.mentoring.view.ronda.RondaActivity;
import mz.org.csaude.mentoring.view.tutored.CreateTutoredActivity;
import mz.org.csaude.mentoring.view.tutored.TutoredActivity;
import mz.org.csaude.mentoring.view.tutored.fragment.TutoredFragment;
import mz.org.csaude.mentoring.workSchedule.executor.WorkerScheduleExecutor;

public class TutoredVM extends SearchVM<Tutored> implements RestResponseListener<Tutored>, ServerStatusListener {
    private TutoredService tutoredService;
    private Tutored tutored;

    private Location location;

    private boolean initialDataVisible;

    private List<District> districts;

    private List<HealthFacility> healthFacilities;

    private List<SimpleValue> menteeLabors;

    private boolean ONGEmployee;

   private List<Tutored> tutoreds;

   private List<Partner> partners;

   private EmployeeService employeeService;

   private Dialog loading;

    public TutoredVM(@NonNull Application application) {
        super(application);
        this.tutoredService = getApplication().getTutoredService();
        this.employeeService = getApplication().getEmployeeService();

        initNewRecord();
        districts = new ArrayList<>();
        healthFacilities = new ArrayList<>();
        menteeLabors = new ArrayList<>();
        location = new Location();

        loadMeteeLabors();
    }

    @Override
    protected void doOnNoRecordFound() {
        this.displaySearchResults();
    }

    private void initNewRecord() {
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
        super.doOnlineSearch(offset, limit);
    }

    @Override
    public void preInit() {
        if (getCurrentStep().isApplicationStepEdit()) {
            this.tutored = (Tutored) this.selectedListble;
        } else {
            this.tutored = new Tutored();
            this.tutored.setEmployee(new Employee());
        }
    }

    @Bindable
    public String getName() {
        return this.tutored.getEmployee().getName();
    }

    public void setName(String name) {
        this.tutored.getEmployee().setName(name);
        notifyPropertyChanged(mz.org.csaude.mentoring.BR.name);
    }
    @Bindable
    public String getSurname(){
        return this.tutored.getEmployee().getSurname();
    }

    public void setSurname(String surname){
       this.tutored.getEmployee().setSurname(surname);
    }
    @Bindable
    public String getNuit() {
        if (this.tutored.getEmployee().getNuit() <= 0) return null;
        return Utilities.parseLongToString(this.tutored.getEmployee().getNuit());
    }
    public void setNuit(String nuit) {
        if (!Utilities.stringHasValue(nuit)) return;

        this.tutored.getEmployee().setNuit(Long.parseLong(nuit));
        notifyPropertyChanged(BR.nuit);
    }
    public List<ProfessionalCategory> getAllProfessionalCategys() throws SQLException{
        return getApplication().getProfessionalCategoryService().getAll();
    }
    @Bindable
    public Listble getProfessionalCategory() {
        return this.tutored.getEmployee().getProfessionalCategory();
    }
    public void setProfessionalCategory(Listble professionalCategory) {
        this.tutored.getEmployee().setProfessionalCategory((ProfessionalCategory) professionalCategory);
        notifyPropertyChanged(BR.professionalCategory);
    }

    @Bindable
    public String getTrainingYear() {
        if (this.tutored.getEmployee().getTrainingYear() == null || this.tutored.getEmployee().getTrainingYear() <= 0) return null;
        return String.valueOf(this.tutored.getEmployee().getTrainingYear());
    }
    public void setTrainingYear(String trainingYear) {
        if (!Utilities.stringHasValue(trainingYear)) return;
        this.tutored.getEmployee().setTrainingYear(Integer.parseInt(trainingYear));
        notifyPropertyChanged(BR.trainingYear);
    }
    @Bindable
    public String getPhoneNumber() {
        return this.tutored.getEmployee().getPhoneNumber();
    }
    public void setPhoneNumber(String phoneNumber) {
        this.tutored.getEmployee().setPhoneNumber(phoneNumber);
    }
    @Bindable
    public String getEmail() {
        return this.tutored.getEmployee().getEmail();
    }

    public void setEmail(String email) {
        this.tutored.getEmployee().setEmail(email);
    }
    @Bindable
    public Listble getPartner() {
        return this.tutored.getEmployee().getPartner();
    }

    public void setPartner(Partner partner) {
        this.tutored.getEmployee().setPartner(partner);
    }

    public List<Tutored> getAllTutoreds() {
        return this.tutoreds;
    }

    public List<Tutored> getTutoredsList() {
        try {
            setTutoreds(tutoredService.getAll());
            return this.tutoreds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Province> getAllProvince() throws SQLException {
        List<Province> provinceList = new ArrayList<>();
        //provinceList.add(new Province());
        provinceList.addAll(getApplication().getProvinceService().getAllOfTutor(getApplication().getCurrMentor()));
        return provinceList;
    }

    @Bindable
    public Listble getSelectedNgo() {
        return  this.tutored.getEmployee().getPartner();
    }

    public void setSelectedNgo(Listble selectedNgo) {
        this.tutored.getEmployee().setPartner((Partner) selectedNgo);
        notifyPropertyChanged(BR.selectedNgo);
    }

    private void doSave(){
        loading = Utilities.showLoadingDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.processando));
        getExecutorService().execute(() -> {
            try {
                tutored.setSyncStatus(SyncSatus.SENT);
                tutored.setUuid(Utilities.getNewUUID().toString());
                tutored.getEmployee().setUuid(Utilities.getNewUUID().toString());
                location.setUuid(Utilities.getNewUUID().toString());
                location.setProvince((Province) getProvince());
                location.setDistrict((District) getDistrict());
                location.setHealthFacility((HealthFacility) getHealthFacility());
                location.setLocationLevel("N/A");
                location.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
                location.setCreatedByUuid(getApplication().getAuthenticatedUser().getUuid());

                tutored.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
                tutored.getEmployee().setLifeCycleStatus(LifeCycleStatus.ACTIVE);
                tutored.getEmployee().addLocation(location);
                tutored.getEmployee().setCreatedByUuid(getApplication().getAuthenticatedUser().getUuid());
                tutored.setCreatedByUuid(getApplication().getAuthenticatedUser().getUuid());

                String error = this.tutored.validade();
                if (Utilities.stringHasValue(error)) {
                    Utilities.displayAlertDialog(getRelatedActivity(), error).show();
                    return;
                }
                getApplication().isServerOnline(this);
            }
            catch (Exception e) {
                dismissProgress(loading);
                runOnMainThread(() -> {
                    Log.e("MentorVM", e.getMessage());
                    Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.failed_to_save_tutored)).show();
                });
            }
        });
    }

    @Override
    public void doOnResponse(String flag, List<Tutored> objects) {
        try {
            getApplication().getEmployeeService().saveOrUpdateEmployee(tutored.getEmployee());
            this.tutoredService.savedOrUpdateTutored(tutored);
            this.getApplication().getLocationService().saveOrUpdate(location);

            runOnMainThread(() -> {
                dismissProgress(loading);
                Map<String, Object> params = new HashMap<>();
                params.put("createdTutored", tutored);
                getRelatedActivity().nextActivityFinishingCurrent(TutoredActivity.class, params);
            });
        } catch (SQLException e) {
            dismissProgress(loading);
            Log.e("MentorVM", e.getMessage());
            runOnMainThread(() -> {
                Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.failed_to_save_tutored)).show();
            });
        }
    }

    @Override
    public void doOnRestErrorResponse(String errorMsg) {
        dismissProgress(loading);
        runOnMainThread(()-> {
            Utilities.displayAlertDialog(getRelatedActivity(), errorMsg).show();
        });
    }

    public void save(){
        this.doSave();
    }

    @Bindable
    public Tutored getTutored() {
        return this.tutored;
    }

    public void setTutored(Tutored tutored) {
        this.tutored = tutored;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    public void deleteTutored(Tutored tutored) throws SQLException {
        this.tutoredService.delete(tutored);
    }

    public String tutoredHasSessions() {
        return "";
    }

    @Bindable
    public Listble getProvince() {
        return this.location.getProvince();
    }
    public void setProvince(Listble province) {
        this.location.setProvince((Province) province);
        getExecutorService().execute(()-> {
            try {
                this.districts.clear();
                this.healthFacilities.clear();
                if (province.getId() == null) return;
                //this.districts.add(new District());
                if (province.getId() == null) return;
                this.districts.addAll(getApplication().getDistrictService().getByProvinceAndMentor(this.location.getProvince(), getApplication().getCurrMentor()));
                getRelatedActivity().runOnUiThread(()-> {
                    getCreateTutoredActivity().reloadDistrcitAdapter();
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Bindable
    public Listble getDistrict(){
        return this.location.getDistrict();
    }
    public void setDistrict(Listble district){
        getExecutorService().execute(()-> {
            try {
                this.location.setDistrict((District) district);
                this.healthFacilities.clear();
                if (district.getId() == null) return;
                //this.healthFacilities.add(new HealthFacility());
                this.healthFacilities.addAll(getApplication().getHealthFacilityService().getHealthFacilityByDistrictAndMentor((District) district, getApplication().getCurrMentor()));
                getRelatedActivity().runOnUiThread(()-> {
                    getCreateTutoredActivity().reloadHealthFacility();
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Bindable
    public Listble getHealthFacility(){
        return this.location.getHealthFacility();
    }

    public void setHealthFacility(Listble healthFacility){
        this.location.setHealthFacility((HealthFacility) healthFacility);
    }

    @Bindable
    public boolean isInitialDataVisible() {
        return initialDataVisible;
    }

    public void setInitialDataVisible(boolean initialDataVisible) {
        this.initialDataVisible = initialDataVisible;
        this.notifyPropertyChanged(BR.initialDataVisible);
    }

    public void changeInitialDataViewStatus(View view){
        getCreateTutoredActivity().changeFormSectionVisibility(view);
    }

    public List<District> getDistricts() {
        return districts;
    }

    public List<HealthFacility> getHealthFacilities() {
        return healthFacilities;
    }

    private void loadMeteeLabors(){
        this.menteeLabors.add(SimpleValue.fastCreate("SNS"));
        this.menteeLabors.add(SimpleValue.fastCreate("ONG"));
    }
    public List<SimpleValue> getMenteeLabors() {
        return menteeLabors;
    }

    @Bindable
    public Listble getMenteeLabor(){
        return Utilities.findOnArray(this.menteeLabors, SimpleValue.fastCreate("SNS"));
    }

    public void setMenteeLabor(Listble menteeLabor){
        getExecutorService().execute(()-> {
            if (this.tutored.getEmployee() == null) return;
            SimpleValue selectSimpleValue = (SimpleValue) menteeLabor;
            if (selectSimpleValue.getDescription().equals("ONG")) {
                setONGEmployee(true);
            } else {
                setONGEmployee(false);
                getExecutorService().execute(() -> {
                    try {
                        this.tutored.getEmployee().setPartner(getApplication().getPartnerService().getMISAU());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }

    @Bindable
    public boolean isONGEmployee() {
        return ONGEmployee;
    }

    public void setONGEmployee(boolean ONGEmployee) {
        this.ONGEmployee = ONGEmployee;
        notifyPropertyChanged(BR.oNGEmployee);
    }

    private TutoredActivity getTutoredActivity() {
        return (TutoredActivity) super.getRelatedActivity();
    }

    public CreateTutoredActivity getCreateTutoredActivity() {
        return (CreateTutoredActivity) super.getRelatedActivity();
    }
    @Override
    public BaseActivity getRelatedActivity() {
        return super.getRelatedActivity();
    }

    public void createNewTutored() {
        getRelatedActivity().nextActivityFinishingCurrent(CreateTutoredActivity.class);
    }
    public List getAllPartners() {
        return this.partners;
    }

    public void getPartnersList() {
        getExecutorService().execute(()-> {
        try {
            setPartners(getApplication().getPartnerService().getAll());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        });
    }

    public void initMenteeUpload() {
        OneTimeWorkRequest request = WorkerScheduleExecutor.getInstance(getApplication()).uploadMentees();
        getApplication().saveDefaultLastSyncDate(DateUtilities.getCurrentDate());
        WorkerScheduleExecutor.getInstance(getApplication()).getWorkManager().getWorkInfoByIdLiveData(request.getId()).observe(getRelatedActivity(), workInfo -> {
            if (workInfo != null) {
                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.tutored_data_upload_success)).show();
                }
            }
        });
    }

    public void edit(Tutored tutored) {
        Map<String, Object> params = new HashMap<>();
        params.put("relatedRecord", tutored);
        getCurrentStep().changeToEdit();
        getRelatedActivity().nextActivity(CreateTutoredActivity.class, params);
    }

    public void delete(Tutored tutored) {

    }

    @Override
    public void onServerStatusChecked(boolean isOnline) {
        if (isOnline) {
            getApplication().getTutoredRestService().restPostTutored(tutored, this);
        } else {
            dismissProgress(loading);
            runOnMainThread(()-> {
                Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(mz.org.csaude.mentoring.R.string.server_unavailable)).show();
            });
        }
    }
    public void nextStep() {

    }

    public Employee getEmployee(){
        return this.tutored.getEmployee();
    }

    public List<Tutored> getTutoreds() {
        return tutoreds;
    }

    public void setTutoreds(List<Tutored> tutoreds) {
        this.tutoreds = tutoreds;
    }

    public void setPartners(List<Partner> partners) {
        this.partners = partners;
    }

    @Override
    public List<Tutored> doSearch(long offset, long limit) throws SQLException {
        return this.tutoredService.getAllPagenated(getApplication().getCurrMentor().getEmployee().getLocations(), offset, limit);
    }

    @Override
    public void displaySearchResults() {
        getRelatedFragment().displaySearchResults();
    }

    @Override
    public AbstractSearchParams<Tutored> initSearchParams() {
        return null;
    }

    @Override
    public TutoredFragment getRelatedFragment() {
        FragmentManager fragmentManager = getRelatedActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            return (TutoredFragment) fragment;
            // This is the currently active fragment
        }
        return (TutoredFragment) super.getRelatedFragment();
    }
}
