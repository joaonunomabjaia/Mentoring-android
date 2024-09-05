package mz.org.csaude.mentoring.viewmodel.answer;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import java.sql.SQLException;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.model.question.Question;
import mz.org.csaude.mentoring.model.answer.Answer;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.service.answer.AnswerService;
import mz.org.csaude.mentoring.service.answer.AnswerServiceImpl;

public class AnswerVM extends BaseViewModel {

    private Answer answer;

    private AnswerService answerService;

    public AnswerVM(@NonNull Application application) {
        super(application);

        this.answerService = new AnswerServiceImpl(application);
    }

    @Override
    public void preInit() {

    }

    @Bindable
    public Form getForm(){
        return this.answer.getForm();
    }
    public void setForm(Form form) {
        this.answer.setForm(form);
        notifyPropertyChanged(BR.form);
    }
    @Bindable
    public Mentorship getMentorship(){
       return this.answer.getMentorship();
    }

    public void setMentorship(Mentorship mentorship){
        this.answer.setMentorship(mentorship);
        notifyPropertyChanged(BR.mentorship);
    }

    @Bindable
    public Question getQuestion(){
      return this.answer.getQuestion();
    }

    public void setQuestion(Question question){
        this.answer.setQuestion(question);
        notifyPropertyChanged(BR.question);
    }

    @Bindable
    public String getValue(){
      return  this.answer.getValue();
    }

    public void setValue(String value){
        this.answer.setValue(value);
        notifyPropertyChanged(BR.value);
    }
    public void save(){
        try {
            this.answerService.save(this.answer);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
