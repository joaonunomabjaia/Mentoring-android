package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.ApiResponse;
import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.evaluationLocation.EvaluationLocationDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.evaluationLocation.EvaluationLocation;
import mz.org.csaude.mentoring.service.evaluationLocation.EvaluationLocationService;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvaluationLocationRestService extends BaseRestService {

    public EvaluationLocationRestService(Application application) {
        super(application);
    }

    public void restGetEvaluationLocations(RestResponseListener<EvaluationLocation> listener) {

        Call<ApiResponse<EvaluationLocationDTO>> evaluationLocationsCall = syncDataService.getAllEvaluationLocations();

        evaluationLocationsCall.enqueue(new Callback<ApiResponse<EvaluationLocationDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<EvaluationLocationDTO>> call, Response<ApiResponse<EvaluationLocationDTO>> response) {

                List<EvaluationLocationDTO> data = response.body().getContent();

                if (Utilities.listHasElements(data)) {
                    getServiceExecutor().execute(() -> {
                        try {
                            EvaluationLocationService evaluationLocationService = getApplication().getEvaluationLocationService();

                            evaluationLocationService.saveOrUpdateEvaluationLocations(data);
                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Utilities.parse(data, EvaluationLocation.class));
                        } catch (SQLException e) {
                            Log.e("EvaluationLocationRestService", e.getMessage());
                        }
                    });
                } else {
                    listener.doOnResponse(REQUEST_NO_DATA, null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<EvaluationLocationDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });
    }
}
