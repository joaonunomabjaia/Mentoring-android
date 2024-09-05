package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.session.SessionStatusDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.session.SessionStatus;
import mz.org.csaude.mentoring.service.session.SessionStatusService;
import mz.org.csaude.mentoring.service.session.SessionStatusServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionStatusRestService extends BaseRestService {
    public SessionStatusRestService(Application application) {
        super(application);
    }

    public void restGetSessionStatuses(RestResponseListener<SessionStatus> listener){

        Call<List<SessionStatusDTO>> rondaTypesCall = syncDataService.getSessionStatuses();

        rondaTypesCall.enqueue(new Callback<List<SessionStatusDTO>>() {
            @Override
            public void onResponse(Call<List<SessionStatusDTO>> call, Response<List<SessionStatusDTO>> response) {

                List<SessionStatusDTO> data = response.body();

                if(Utilities.listHasElements(data)){
                    getServiceExecutor().execute(()-> {
                        try {
                            SessionStatusService sessionStatusService = getApplication().getSessionStatusService();
                            List<SessionStatus> sessionStatuses = new ArrayList<>();
                            for (SessionStatusDTO sessionStatusDTO : data) {
                                sessionStatusDTO.setSyncSatus(SyncSatus.SENT);
                                sessionStatusDTO.getSessionStatus().setSyncStatus(SyncSatus.SENT);
                                sessionStatuses.add(sessionStatusDTO.getSessionStatus());
                            }
                            sessionStatusService.saveOrUpdateSessionStatuses(data);
                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, sessionStatuses);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    listener.doOnResponse(REQUEST_NO_DATA, null);
                }
            }

            @Override
            public void onFailure(Call<List<SessionStatusDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });

    }

}
