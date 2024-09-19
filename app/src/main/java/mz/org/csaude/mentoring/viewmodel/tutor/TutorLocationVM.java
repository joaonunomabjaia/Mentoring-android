package mz.org.csaude.mentoring.viewmodel.tutor;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import java.sql.SQLException;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.tutor.Tutor;

public class TutorLocationVM extends BaseViewModel {



    public TutorLocationVM(@NonNull Application application) {
        super(application);
    }

    @Override
    public void preInit() {

    }

    @Bindable
    public Tutor getTutor() {
        return null;
    }

    public void setTutor(Tutor tutor) {
        notifyPropertyChanged(BR.userName);
    }

    @Bindable
    public HealthFacility getHealthFacility() {
        return null;
    }

    public void setHealthFacility(HealthFacility healthFacility) {
        notifyPropertyChanged(BR.healthFacility);
    }


    public void save() {

    }
}
