package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.evaluationType.EvaluationTypeDTO;
import mz.org.csaude.mentoring.dto.question.QuestionCategoryDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.model.question.QuestionsCategory;
import mz.org.csaude.mentoring.service.question.QuestionsCategoryService;
import mz.org.csaude.mentoring.service.question.QuestionsCategoryServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionsCategoryRestService extends BaseRestService {
    public QuestionsCategoryRestService(Application application) {
        super(application);
    }

    public void restGetQuestionCategories(RestResponseListener<QuestionsCategory> listener) {

        Call<List<QuestionCategoryDTO>> evaluationTypesCall = syncDataService.getQuestionCategories();

        evaluationTypesCall.enqueue(new Callback<List<QuestionCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<QuestionCategoryDTO>> call, Response<List<QuestionCategoryDTO>> response) {

                List<QuestionCategoryDTO> data = response.body();

                if(Utilities.listHasElements(data)){
                    getServiceExecutor().execute(()-> {
                        try {
                            QuestionsCategoryService questionsCategoryService = getApplication().getQuestionsCategoryService();
                            List<QuestionsCategory> questionsCategories = new ArrayList<>();
                            for (QuestionCategoryDTO questionCategoryDTO : data) {
                                questionCategoryDTO.getQuestionCategory().setSyncStatus(SyncSatus.SENT);
                                questionsCategories.add(questionCategoryDTO.getQuestionCategory());
                            }
                            questionsCategoryService.saveOrUpdateQuestionCategories(data);
                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, questionsCategories);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    listener.doOnResponse(REQUEST_NO_DATA, null);
                }
            }

            @Override
            public void onFailure(Call<List<QuestionCategoryDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });

    }

}
