package mz.org.csaude.mentoring.viewmodel.form;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.model.programmaticArea.ProgrammaticArea;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.ronda.RondaMentor;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.service.form.FormService;
import mz.org.csaude.mentoring.service.form.FormServiceImpl;

public class FormVM extends BaseViewModel {

    private FormService formService;

    private Form form;
    private List<Form> tutorForms;
    private Mentorship mentorship;
    private Form selectedForm;

    public FormVM(@NonNull Application application) {
        super(application);
        this.formService = new FormServiceImpl(application);
    }

    @Override
    public void preInit() {

    }
    @Bindable
    public String getCode(){
        return this.form.getCode();
    }

    public void setCode(String code){
        this.form.setCode(code);
        notifyPropertyChanged(BR.code);
    }

    @Bindable
    public String getName() {
        return this.form.getName();
    }

    public void setName(String name) {
        this.form.setName(name);
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getDescription() {
        return this.form.getDescription();
    }

    public void setDescription(String description) {
        this.form.setDescription(description);
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public ProgrammaticArea getProgrammaticArea() {
        return this.form.getProgrammaticArea();
    }

    public void setProgrammaticArea(ProgrammaticArea programmaticArea) {
        this.form.setProgrammaticArea(programmaticArea);
       notifyPropertyChanged(BR.programmaticArea);
    }

    @Bindable
    public Integer getTargetPatient() {
        return this.form.getTargetPatient();
    }

    public void setTargetPatient(Integer integer) {
        this.form.setTargetPatient(integer);
        notifyPropertyChanged(BR.targetPatient);
    }

    @Bindable
    public Integer getTargetFile() {
        return this.form.getTargetFile();
    }

    public void setTargetFile(Integer integer) {
        this.form.setTargetFile(integer);
        notifyPropertyChanged(BR.targetFile);
    }

    @Bindable
    public Partner getPartner() {
        return this.form.getPartner();
    }

    public void setPartner(Partner partner) {
        this.form.setPartner(partner);
        notifyPropertyChanged(BR.partner);
    }

    public void save() {
        try {
            this.formService.save(this.form);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Form> getTutorForms() {
        return this.tutorForms;
    }
    public void loadTutorForms(final Tutor tutor) {
        try {
            this.tutorForms = new ArrayList<>();
            this.tutorForms.addAll(formService.getAllOfTutor(tutor));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void nextStep() {
        Map<String, Object> params = new HashMap<>();
        Intent intent = getRelatedActivity().getIntent();
        String title = (String) intent.getExtras().get("title");
        RondaType rondaType = (RondaType) intent.getExtras().get("rondaType");
        Ronda ronda = (Ronda) intent.getExtras().get("createdRonda");
        Tutor currMentor = (Tutor) intent.getExtras().get("currMentor");
        List<RondaMentee> selectedMentees = (List<RondaMentee>) intent.getExtras().getSerializable("rondaMentees");
        RondaMentor rondaMentor = (RondaMentor) intent.getExtras().get("rondaMentor");
        params.put("rondaType", rondaType);
        params.put("title", title);
        params.put("createdRonda", ronda);
        params.put("currMentor", currMentor);
        params.put("rondaMentees", selectedMentees);
        params.put("rondaMentor", rondaMentor);
        this.mentorship.setTutor(currMentor);
        this.selectedForm = this.tutorForms.get(0);
        params.put("newMentorship", this.mentorship);
        params.put("selectedForm", this.selectedForm);
    }

    public Mentorship getMentorship() {
        return mentorship;
    }

    public void setMentorship(Mentorship mentorship) {
        this.mentorship = mentorship;
    }
}
