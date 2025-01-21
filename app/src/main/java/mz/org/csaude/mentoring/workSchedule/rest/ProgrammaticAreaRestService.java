package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.ApiResponse;
import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.programmaticArea.ProgrammaticAreaDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.programmaticArea.ProgrammaticArea;
import mz.org.csaude.mentoring.service.ProgrammaticArea.ProgrammaticAreaService;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgrammaticAreaRestService extends BaseRestService {
    public ProgrammaticAreaRestService(Application application) {
        super(application);
    }

    public void restGetProgrammaticAreas(RestResponseListener<ProgrammaticArea> listener){

        Call<ApiResponse<ProgrammaticAreaDTO>> programmaticAreasCall = syncDataService.getAllProgrammaticAreas();

        programmaticAreasCall.enqueue(new Callback<ApiResponse<ProgrammaticAreaDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProgrammaticAreaDTO>> call, Response<ApiResponse<ProgrammaticAreaDTO>> response) {

                List<ProgrammaticAreaDTO> data = response.body().getContent();

                if(Utilities.listHasElements(data)){
                    getServiceExecutor().execute(()-> {
                        try {
                            ProgrammaticAreaService programmaticAreaService = getApplication().getProgrammaticAreaService();

                            programmaticAreaService.saveOrUpdateProgrammaticAreas(data);
                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Utilities.parse(data, ProgrammaticArea.class));
                        } catch (SQLException e) {
                            Log.e("ProgrammaticAreaRestService", e.getMessage());
                        }
                    });
                } else {
                    listener.doOnResponse(REQUEST_NO_DATA, null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProgrammaticAreaDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });

    }

}
