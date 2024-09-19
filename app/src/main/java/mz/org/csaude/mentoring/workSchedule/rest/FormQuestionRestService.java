package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.form.FormQuestionDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.formQuestion.FormQuestion;
import mz.org.csaude.mentoring.model.question.Question;
import mz.org.csaude.mentoring.service.form.FormService;
import mz.org.csaude.mentoring.service.form.FormServiceImpl;
import mz.org.csaude.mentoring.service.formQuestion.FormQuestionService;
import mz.org.csaude.mentoring.service.formQuestion.FormQuestionServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormQuestionRestService extends BaseRestService {

    public FormQuestionRestService(Application application) {
        super(application);
    }

    public void restGetFormQuestion(RestResponseListener<FormQuestion> listener, Long limit, Long offset){
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
        Call<List<FormQuestionDTO>> formQuestionsCall = syncDataService.getFormsQuestionsByFormsUuids(formsUuids, limit, offset);

        formQuestionsCall.enqueue(new Callback<List<FormQuestionDTO>>() {
            @Override
            public void onResponse(Call<List<FormQuestionDTO>> call, Response<List<FormQuestionDTO>> response) {
                List<FormQuestionDTO> data = response.body();
                if (Utilities.listHasElements(data)) {
                    getServiceExecutor().execute(()-> {
                        try {
                            FormQuestionService formQuestionService = getApplication().getFormQuestionService();

                            List<FormQuestion> formQuestions = new ArrayList<>();

                            List<Question> questions = new ArrayList<>();

                            for (FormQuestionDTO formQuestionDTO : data) {
                                FormQuestion formQuestion = formQuestionDTO.getFormQuestion();
                                formQuestion.setSyncStatus(SyncSatus.SENT);
                                formQuestion.setForm(getApplication().getFormService().getByuuid(formQuestionDTO.getFormUuid()));
                                formQuestion.setEvaluationType(getApplication().getEvaluationTypeService().getByuuid(formQuestion.getEvaluationType().getUuid()));
                                formQuestion.setResponseType(getApplication().getResponseTypeService().getByuuid(formQuestion.getResponseType().getUuid()));
                                formQuestion.setQuestion(formQuestionDTO.getFormQuestion().getQuestion());
                                formQuestions.add(formQuestion);
                                questions.add(formQuestionDTO.getFormQuestion().getQuestion());
                            }

                            getApplication().getQuestionService().saveOrUpdateQuestionList(questions);
                            formQuestionService.saveOrUpdate(formQuestions);

                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, formQuestions);
                        } catch (SQLException e) {
                            Log.e("Error saving FormQuestion: ", e.getMessage(), e);
                        }
                    });
                } else {
                    listener.doOnResponse(BaseRestService.REQUEST_NO_DATA, null);
                }
            }

            @Override
            public void onFailure(Call<List<FormQuestionDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });
    }
}
