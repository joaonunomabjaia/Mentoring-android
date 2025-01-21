package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.ApiResponse;
import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.program.ProgramDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.program.Program;
import mz.org.csaude.mentoring.service.program.ProgramService;
import mz.org.csaude.mentoring.service.program.ProgramServiceImpl;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgramRestService extends BaseRestService {
    public ProgramRestService(Application application) {
        super(application);
    }

    public void restGetPrograms(RestResponseListener<Program> listener){

        Call<ApiResponse<ProgramDTO>> programsCall = syncDataService.getAllPrograms();

        programsCall.enqueue(new Callback<ApiResponse<ProgramDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProgramDTO>> call, Response<ApiResponse<ProgramDTO>> response) {

                List<ProgramDTO> data = response.body().getContent();

                if(Utilities.listHasElements(data)){
                    getServiceExecutor().execute(()-> {
                        try {
                            ProgramService programService = getApplication().getProgramService();
                            List<Program> programs = new ArrayList<>();
                            for (ProgramDTO programDTO : data) {
                                programs.add(programDTO.getProgram());
                            }
                            programService.saveOrUpdatePrograms(data);
                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, programs);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    listener.doOnResponse(REQUEST_NO_DATA, null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProgramDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });

    }

}
