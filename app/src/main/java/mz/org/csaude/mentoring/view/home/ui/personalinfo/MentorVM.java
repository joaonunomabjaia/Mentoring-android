package mz.org.csaude.mentoring.view.home.ui.personalinfo;

import android.app.Application;
import android.app.Dialog;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
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
import mz.org.csaude.mentoring.service.professionalCategory.ProfessionalCategoryService;
import mz.org.csaude.mentoring.service.session.SessionService;
import mz.org.csaude.mentoring.service.tutor.TutorService;
import mz.org.csaude.mentoring.util.SimpleValue;
import mz.org.csaude.mentoring.util.Utilities;

public class MentorVM extends BaseViewModel implements RestResponseListener<Tutor>, ServerStatusListener {

    private Tutor tutor;
    private TutorService tutorService;
    private Location location;
    private HealthFacility healthFacility;
    private boolean initialDataVisible;
    private List<District> districts;
    private List<HealthFacility> healthFacilities;
    private List<SimpleValue> menteeLabors;
    private boolean ONGEmployee;

    private String nuit;
    private String trainingYear;

    public MentorVM(@NonNull Application application) {

        super(application);

        tutorService = getApplication().getTutorService();
        districts = new ArrayList<>();
        healthFacilities = new ArrayList<>();
        menteeLabors = new ArrayList<>();
        location = new Location();

        loadMeteeLabors();
        preInit();
    }

    public void changeInitialDataViewStatus(View view){
        getPersonalInfoFragment().changeFormSectionVisibility(view);
    }

    @Override
    public void preInit() {
        getExecutorService().execute(()->{
            this.tutor = getApplication().getCurrMentor();
            List<Location> locations;
            try {
                locations = getApplication().getLocationService().getAllOfEmploee(this.tutor.getEmployee());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            this.location = locations.get(0);
        });
    }

    @Bindable
    public String getName() {
        return this.tutor.getEmployee().getName();
    }

    public void setName(String name) {
        this.tutor.getEmployee().setName(name);
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getSurname(){
        return this.tutor.getEmployee().getSurname();
    }

    public void setSurname(String surname){
        this.tutor.getEmployee().setSurname(surname);
    }
    @Bindable
    public void setNuit(long nuit) {
        this.tutor.getEmployee().setNuit(nuit);
    }

    public List<ProfessionalCategory> getAllProfessionalCategys() throws SQLException {
        return getApplication().getProfessionalCategoryService().getAll();
    }
    @Bindable
    public Listble getProfessionalCategory() {
        return this.tutor.getEmployee().getProfessionalCategory();
    }
    public void setProfessionalCategory(Listble professionalCategory) {
        this.tutor.getEmployee().setProfessionalCategory((ProfessionalCategory) professionalCategory);
        notifyPropertyChanged(BR.professionalCategory);
    }

    @Bindable
    public String getPhoneNumber() {
        return this.tutor.getEmployee().getPhoneNumber();
    }
    public void setPhoneNumber(String phoneNumber) {
        this.tutor.getEmployee().setPhoneNumber(phoneNumber);
    }
    @Bindable
    public String getEmail() {
        return this.tutor.getEmployee().getEmail();
    }
    public void setEmail(String email) {
        this.tutor.getEmployee().setEmail(email);
    }
    @Bindable
    public Listble getPartner() {
        return this.tutor.getEmployee().getPartner();
    }
    public void setPartner(Partner partner) {
        this.tutor.getEmployee().setPartner(partner);
    }

    public List<Province> getAllProvince() throws SQLException {
        List<Province> provinceList = new ArrayList<>();
        provinceList.addAll(getApplication().getProvinceService().getAllOfTutor(getApplication().getCurrMentor()));
        return provinceList;
    }
    @Bindable
    public Listble getSelectedNgo() {
        return  this.tutor.getEmployee().getPartner();
    }
    public void setSelectedNgo(Listble selectedNgo) {
        this.tutor.getEmployee().setPartner((Partner) selectedNgo);
        notifyPropertyChanged(BR.selectedNgo);
    }

    private void doUpdate(){

        this.tutor.getEmployee().setProfessionalCategory((ProfessionalCategory) getProfessionalCategory());
        this.tutor.setEmployee(getEmployee());

        this.location.setEmployee(tutor.getEmployee());
        this.location.setProvince((Province) getProvince());
        this.location.setDistrict((District) getDistrict());
        this.location.setHealthFacility((HealthFacility) getHealthFacility());

        String error = this.tutor.validade();
        if (Utilities.stringHasValue(error)) {
            Utilities.displayAlertDialog(getRelatedActivity(), error).show();
            return;
        }

        getApplication().isServerOnline(this);

    }

    public void update(){
        this.doUpdate();
    }

    @Bindable
    public Tutor getTutor() {
        return this.tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public Employee getEmployee(){
        return this.tutor.getEmployee();
    }
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
                if (province.getId() == null) return;
                this.districts.addAll(getApplication().getDistrictService().getByProvinceAndMentor(this.location.getProvince(), getApplication().getCurrMentor()));
                getRelatedActivity().runOnUiThread(()-> {
                    getPersonalInfoFragment().reloadDistrcitAdapter();
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
                this.healthFacilities.addAll(getApplication().getHealthFacilityService().getHealthFacilityByDistrictAndMentor((District) district, getApplication().getCurrMentor()));
                getRelatedActivity().runOnUiThread(()-> {
                    getPersonalInfoFragment().reloadHealthFacility();
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public List<District> getDistricts() {
        return districts;
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
    @Bindable
    public Listble getHealthFacility(){
        return this.location.getHealthFacility();
    }

    public void setHealthFacility(Listble healthFacility){
        this.location.setHealthFacility((HealthFacility) healthFacility);
    }
    private void loadMeteeLabors(){
        this.menteeLabors.add(SimpleValue.fastCreate("SNS"));
        this.menteeLabors.add(SimpleValue.fastCreate("ONG"));
    }
    @Bindable
    public boolean isInitialDataVisible() {
        return initialDataVisible;
    }
    public void setInitialDataVisible(boolean initialDataVisible) {
        this.initialDataVisible = initialDataVisible;
        this.notifyPropertyChanged(BR.initialDataVisible);
    }
    public List<SimpleValue> getMenteeLabors() {
        return menteeLabors;
    }

    @Bindable
    public Listble getMenteeLabor(){
        return Utilities.findOnArray(this.menteeLabors, SimpleValue.fastCreate("SNS"));
    }

    @Bindable
    public String getTrainingYear() {
        this.trainingYear = Utilities.parseIntToString(this.tutor.getEmployee().getTrainingYear());
        return this.trainingYear;
    }
    public void setTrainingYear(String trainingYear) {
        this.trainingYear = trainingYear;

        if(!StringUtils.isEmpty(trainingYear) & StringUtils.isNumeric(trainingYear)  ){
            this.tutor.getEmployee().setTrainingYear(Integer.parseInt(trainingYear));
        }
    }

    @Bindable
    public String getNuit() {
        this.nuit = Utilities.parseLongToString(this.tutor.getEmployee().getNuit());
        return this.nuit;
    }
    public void setNuit(String nuit) {
        this.nuit = nuit;
        if(!StringUtils.isEmpty(nuit) & StringUtils.isNumeric(nuit)) this.tutor.getEmployee().setNuit(Long.parseLong(nuit));
    }

    public void setMenteeLabor(Listble menteeLabor){
        getExecutorService().execute(()-> {
            if (this.tutor.getEmployee() == null) return;
            SimpleValue selectSimpleValue = (SimpleValue) menteeLabor;
            if (selectSimpleValue.getDescription().equals("ONG")) {
                setONGEmployee(true);
            } else {
                setONGEmployee(false);
                getExecutorService().execute(() -> {
                    try {
                        this.tutor.getEmployee().setPartner(getApplication().getPartnerService().getMISAU());
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

    public PersonalInfoFragment getPersonalInfoFragment(){
        return (PersonalInfoFragment) super.getRelatedFragment();
    }

    public List<Partner> getAllPartners() {
        try {
            return getApplication().getPartnerService().getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doOnRestErrorResponse(String errormsg) {
        Utilities.displayAlertDialog(getRelatedActivity(), errormsg).show();
    }

    @Override
    public void doOnResponse(String flag, List<Tutor> objects) {
        Dialog loadingDialog =Utilities.showLoadingDialog(getRelatedActivity(), "Processando...");
        getExecutorService().execute(()-> {

            try {
                getApplication().getEmployeeService().saveOrUpdateEmployee(tutor.getEmployee());
                this.tutorService.saveOrUpdate(tutor);
                this.getApplication().getLocationService().saveOrUpdate(location);

                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Utilities.displayAlertDialog(getRelatedActivity(), "Dados actualizados com sucesso.").show();
            } catch (SQLException e) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.e("MentorVM", e.getMessage());
            }
        });
    }

    @Override
    public void onServerStatusChecked(boolean isOnline, boolean isSlow) {
        if (isOnline) {
            if (isSlow) {
                // Show warning: Server is slow
                showSlowConnectionWarning(getRelatedActivity());
            }
            getApplication().getTutorRestService().restPatchTutor(this.tutor, this);
        } else {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.server_unavailable)).show();
        }
    }


}
