package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.programmaticArea.TutorProgrammaticAreaDTO;
import mz.org.csaude.mentoring.dto.tutor.TutorDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.programmaticArea.TutorProgrammaticArea;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.ProgrammaticArea.TutorProgrammaticAreaService;
import mz.org.csaude.mentoring.service.ProgrammaticArea.TutorProgrammaticAreaServiceImpl;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TutorProgrammaticAreaRestService extends BaseRestService {
    public TutorProgrammaticAreaRestService(Application application) {
        super(application);
    }

    public void restGetTutorProgrammaticAreas(RestResponseListener<TutorProgrammaticArea> listener){

        if (!getSessionManager().isAnyUserConfigured()) {
            listener.doOnResponse(REQUEST_NO_DATA, null);
            return;
        }
        List<String> uuids = new ArrayList<>();
        if (Utilities.stringHasValue(getSessionManager().getActiveUser())){
            uuids.add(getApplication().getCurrMentor().getUuid());
        } else {
            try {
                List<Tutor> tutors = getApplication().getTutorService().getAll();
                for (Tutor tutor : tutors) {
                    uuids.add(tutor.getUuid());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        Call<List<TutorProgrammaticAreaDTO>> tutorProgrammaticAreasCall = syncDataService.getByTutorUuids(uuids);

        tutorProgrammaticAreasCall.enqueue(new Callback<List<TutorProgrammaticAreaDTO>>() {
            @Override
            public void onResponse(Call<List<TutorProgrammaticAreaDTO>> call, Response<List<TutorProgrammaticAreaDTO>> response) {

                List<TutorProgrammaticAreaDTO> data = response.body();

                if(Utilities.listHasElements(data)){
                    getServiceExecutor().execute(()-> {
                        try {
                            TutorProgrammaticAreaService tutorProgrammaticAreaService = getApplication().getTutorProgrammaticAreaService();
                            List<TutorProgrammaticArea> tutorProgrammaticAreas = new ArrayList<>();
                            for (TutorProgrammaticAreaDTO tutorProgrammaticAreaDTO : data) {
                                tutorProgrammaticAreas.add(tutorProgrammaticAreaDTO.getTutorProgrammaticArea());
                            }
                            tutorProgrammaticAreaService.saveOrUpdateTutorProgrammaticAreas(data);
                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, tutorProgrammaticAreas);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    listener.doOnResponse(REQUEST_NO_DATA, null);
                }
            }

            @Override
            public void onFailure(Call<List<TutorProgrammaticAreaDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });

    }

}
