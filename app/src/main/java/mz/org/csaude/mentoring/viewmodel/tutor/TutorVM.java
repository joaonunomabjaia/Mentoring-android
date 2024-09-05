package mz.org.csaude.mentoring.viewmodel.tutor;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import java.sql.SQLException;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.service.tutor.TutorService;
import mz.org.csaude.mentoring.service.tutor.TutorServiceImpl;

public class TutorVM extends BaseViewModel {

    private Tutor tutor;
    private TutorService tutorService;

    public TutorVM(@NonNull Application application) {
        super(application);
        this.tutorService = new TutorServiceImpl(application);
    }
/*
    @Bindable
    public String getCode() {
        return this.tutor.getCode();
    }

    public void setCode(String code) {
        this.tutor.setCode(code);
        notifyPropertyChanged(BR.code);
    }

    @Bindable
    public String getName() {
        return this.tutor.getName();
    }

    public void setName(String name) {
        this.tutor.setName(name);
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getSurname() {
        return this.tutor.getSurname();
    }

    public void setSurname(String surname) {
        this.tutor.setSurname(surname);
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public Career getCareer() {
        return this.tutor.getCareer();
    }

    public void setCareer(Career career) {
        this.tutor.setCareer(career);
        notifyPropertyChanged(BR.name);
    }

    private String phoneNumber;

    @Bindable
    public String getPhoneNumber() {
        return this.tutor.getPhoneNumber();
    }

    public void setCareer(String phoneNumber) {
        this.tutor.setPhoneNumber(phoneNumber);
        notifyPropertyChanged(BR.phoneNumber);
    }

    @Bindable
    public String getEmail() {
        return this.tutor.getEmail();
    }

    public void setEmail(String email) {
        this.tutor.setEmail(email);
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public Partner getPartner() {
        return this.tutor.getPartner();
    }

    public void setUser(Partner partner) {
        this.tutor.setPartner(partner);
          notifyPropertyChanged(BR.partner);
    }*/


    @Override
    public void preInit() {

    }
    public void save() {
        try {
            this.tutorService.save(this.tutor);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
