package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.common.HttpStatus;
import mz.org.csaude.mentoring.common.MentoringAPIError;
import mz.org.csaude.mentoring.dto.tutor.TutorDTO;
import mz.org.csaude.mentoring.dto.tutored.TutoredDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.tutor.TutorService;
import mz.org.csaude.mentoring.service.tutor.TutorServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.home.ui.personalinfo.MentorVM;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TutorRestService extends BaseRestService {

    public TutorRestService(Application application) {
        super(application);
    }

    public void restGetTutor(long offset, long limit, RestResponseListener<Tutor> listener){

        Call<List<TutorDTO>> tutorCall = syncDataService.getTutors(limit, offset);

        tutorCall.enqueue(new Callback<List<TutorDTO>>() {
            @Override
            public void onResponse(Call<List<TutorDTO>> call, Response<List<TutorDTO>> response) {

                List<TutorDTO> data = response.body();

                if(data == null){

                }
                getServiceExecutor().execute(()-> {
                    try {

                        TutorService tutorService = getApplication().getTutorService();

                        List<Tutor> tutors = new ArrayList<>();
                        for (TutorDTO tutorDTO : data) {
                            tutorDTO.getTutor().getEmployee().setSyncStatus(SyncSatus.SENT);
                            tutorDTO.getTutor().setSyncStatus(SyncSatus.SENT);
                            tutors.add(tutorDTO.getTutor());
                        }
                        tutorService.saveOrUpdateTutors(data);
                        listener.doOnResponse(BaseRestService.REQUEST_SUCESS, tutors);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<TutorDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);

            }
        });

    }


    public void restGetByEmployeeUuid(RestResponseListener<Tutor> listener){

        if (getApplication().getAuthenticatedUser() == null) {
            try {
                User user = getApplication().getUserService().getCurrentUser();
                getApplication().setCurrTutor(getApplication().getTutorService().getByEmployee(user.getEmployee()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        Call<TutorDTO> tutorCall = syncDataService.getTutorByEmployeeUuid(getApplication().getAuthenticatedUser().getEmployee().getUuid());
        tutorCall.enqueue(new Callback<TutorDTO>() {
            @Override
            public void onResponse(Call<TutorDTO> call, Response<TutorDTO> response) {
              TutorDTO  data = response.body();

                if(data == null){

                }
                getServiceExecutor().execute(()-> {
                    try {

                        TutorService tutorService = getApplication().getTutorService();

                        Tutor tutor = tutorService.saveOrUpdate(new Tutor(data));

                        listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Collections.singletonList(tutor));

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            @Override
            public void onFailure(Call<TutorDTO> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });
    }

    public void restPatchTutor(Tutor tutor, RestResponseListener<Tutor> listener) {
        Call<TutorDTO> tutorCall = syncDataService.patchTutor(new TutorDTO(tutor));
        tutorCall.enqueue(new Callback<TutorDTO>() {
            @Override
            public void onResponse(Call<TutorDTO> call, Response<TutorDTO> response) {
                TutorDTO data = response.body();
                if (response.code() == 200) {
                    listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Utilities.parseToList(tutor));
                } else {
                    if (response.code() == HttpStatus.BAD_REQUEST) {
                        // Parse custom error response
                        try {
                            Gson gson = new Gson();
                            MentoringAPIError error = gson.fromJson(response.errorBody().string(), MentoringAPIError.class);
                            listener.doOnRestErrorResponse(error.getMessage());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        // Handle other error responses
                        listener.doOnRestErrorResponse(response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<TutorDTO> call, Throwable t) {
                Log.i("restPatchTutor--", t.getMessage(), t);
                listener.doOnRestErrorResponse(t.getMessage());
            }
        });
    }
}
