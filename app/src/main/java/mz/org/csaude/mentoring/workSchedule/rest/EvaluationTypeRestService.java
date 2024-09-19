package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.evaluationType.EvaluationTypeDTO;
import mz.org.csaude.mentoring.dto.responseType.ResponseTypeDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.model.responseType.ResponseType;
import mz.org.csaude.mentoring.service.evaluationType.EvaluationTypeService;
import mz.org.csaude.mentoring.service.evaluationType.EvaluationTypeServiceImpl;
import mz.org.csaude.mentoring.service.responseType.ResponseTypeService;
import mz.org.csaude.mentoring.service.responseType.ResponseTypeServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvaluationTypeRestService extends BaseRestService {
    public EvaluationTypeRestService(Application application) {
        super(application);
    }

    public void restGetEvaluationTypes(RestResponseListener<EvaluationType> listener){

        Call<List<EvaluationTypeDTO>> evaluationTypesCall = syncDataService.getEvaluationTypes();

        evaluationTypesCall.enqueue(new Callback<List<EvaluationTypeDTO>>() {
            @Override
            public void onResponse(Call<List<EvaluationTypeDTO>> call, Response<List<EvaluationTypeDTO>> response) {

                List<EvaluationTypeDTO> data = response.body();

                if(Utilities.listHasElements(data)){
                    getServiceExecutor().execute(()-> {
                        try {
                            EvaluationTypeService evaluationTypeService = getApplication().getEvaluationTypeService();
                            List<EvaluationType> evaluationTypes = new ArrayList<>();
                            for (EvaluationTypeDTO evaluationTypeDTO : data) {
                                evaluationTypeDTO.getEvaluationType().setSyncStatus(SyncSatus.SENT);
                                evaluationTypes.add(evaluationTypeDTO.getEvaluationType());
                            }
                            evaluationTypeService.saveOrUpdateEvaluationTypes(data);
                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, evaluationTypes);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    listener.doOnResponse(REQUEST_NO_DATA, null);
                }
            }

            @Override
            public void onFailure(Call<List<EvaluationTypeDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });

    }

}
