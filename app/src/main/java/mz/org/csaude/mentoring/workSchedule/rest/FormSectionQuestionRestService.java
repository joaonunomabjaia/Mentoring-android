package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.form.FormSectionQuestionDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.formSectionQuestion.FormSectionQuestion;
import mz.org.csaude.mentoring.model.question.Question;
import mz.org.csaude.mentoring.service.formSectionQuestion.FormSectionQuestionService;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormSectionQuestionRestService extends BaseRestService {

    public FormSectionQuestionRestService(Application application) {
        super(application);
    }

    public void restGetFormSectionQuestion(RestResponseListener<FormSectionQuestion> listener, Long limit, Long offset){
        List<String> formsUuids = new ArrayList<>();
        try {
            List<Form> forms = getApplication().getFormService().getAllSynced(getApplication());
            if (Utilities.listHasElements(forms)) {
                formsUuids = forms.stream()
                        .map(Form::getUuid)
                        .collect(Collectors.toList());
            } else {
                listener.doOnResponse(BaseRestService.REQUEST_NO_DATA, null);
                return;
            }
        } catch (SQLException e) {
            Log.e("Error while loading synced forms Uuids -- ", e.getMessage(), e);
        }
        Call<List<FormSectionQuestionDTO>> formSectionQuestionsCall = syncDataService.getFormsQuestionsByFormsUuids(formsUuids, limit, offset);

        formSectionQuestionsCall.enqueue(new Callback<List<FormSectionQuestionDTO>>() {
            @Override
            public void onResponse(Call<List<FormSectionQuestionDTO>> call, Response<List<FormSectionQuestionDTO>> response) {
                List<FormSectionQuestionDTO> data = response.body();
                if (Utilities.listHasElements(data)) {
                    getServiceExecutor().execute(()-> {
                        try {
                            FormSectionQuestionService formSectionQuestionService = getApplication().getFormSectionQuestionService();

                            List<FormSectionQuestion> formSectionQuestions = new ArrayList<>();

                            List<Question> questions = new ArrayList<>();

                            for (FormSectionQuestionDTO formSectionQuestionDTO : data) {
                                FormSectionQuestion formSectionQuestion = new FormSectionQuestion(formSectionQuestionDTO);
                                formSectionQuestion.setSyncStatus(SyncSatus.SENT);
                                formSectionQuestion.setFormSection(getApplication().getFormSectionService().getByuuid(formSectionQuestion.getFormSection().getUuid()));
                                formSectionQuestion.setEvaluationType(getApplication().getEvaluationTypeService().getByuuid(formSectionQuestion.getEvaluationType().getUuid()));
                                formSectionQuestion.setResponseType(getApplication().getResponseTypeService().getByuuid(formSectionQuestion.getResponseType().getUuid()));
                                formSectionQuestions.add(formSectionQuestion);
                                questions.add(formSectionQuestion.getQuestion());
                            }

                            getApplication().getQuestionService().saveOrUpdateQuestionList(questions);
                            formSectionQuestionService.saveOrUpdate(formSectionQuestions);

                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, formSectionQuestions);
                        } catch (SQLException e) {
                            Log.e("Error saving FormQuestion: ", e.getMessage(), e);
                        }
                    });
                } else {
                    listener.doOnResponse(BaseRestService.REQUEST_NO_DATA, null);
                }
            }

            @Override
            public void onFailure(Call<List<FormSectionQuestionDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });
    }
}
