package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.common.HttpStatus;
import mz.org.csaude.mentoring.common.MentoringAPIError;
import mz.org.csaude.mentoring.dto.tutored.TutoredDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.location.Location;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.tutored.TutoredService;
import mz.org.csaude.mentoring.service.tutored.TutoredServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TutoredRestService extends BaseRestService {


    public TutoredRestService(Application application) {
        super(application);
    }

    public void restGetTutored(RestResponseListener<Tutored> listener, Long offset, Long limit){
        List<String> uuids = new ArrayList<>();
        List<Location> locations = new ArrayList<>();
        if (getApplication().getAuthenticatedUser() == null) {
            try {
                User user = getApplication().getUserService().getCurrentUser();
                user.getEmployee().setLocations(getApplication().getLocationService().getAllOfEmploee(user.getEmployee()));
                locations = user.getEmployee().getLocations();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            locations = getApplication().getAuthenticatedUser().getEmployee().getLocations();
        }

        for (Location location : locations) {
            uuids.add(location.getHealthFacility().getUuid());
        }
        Call<List<TutoredDTO>> tutoredCall = syncDataService.getTutoreds(uuids, offset, limit);

        tutoredCall.enqueue(new Callback<List<TutoredDTO>>() {
            @Override
            public void onResponse(Call<List<TutoredDTO>> call, Response<List<TutoredDTO>> response) {
                List<TutoredDTO> data = response.body();
                if (Utilities.listHasElements(data)) {
                    getServiceExecutor().execute(()-> {
                        try {
                            List<Tutored> tutoreds = Utilities.parse(data, Tutored.class);
                            for (Tutored tutored : tutoreds) {
                                tutored.setSyncStatus(SyncSatus.SENT);
                            }
                            getApplication().getTutoredService().savedOrUpdateTutoreds(tutoreds);
                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, tutoreds);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    listener.doOnResponse(BaseRestService.REQUEST_NO_DATA, null);
                }
            }

            @Override
            public void onFailure(Call<List<TutoredDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });
    }


    public void restPostTutored(RestResponseListener<Tutored> listener){

        List<Tutored> tutoreds = null;
        try {
            tutoreds = getApplication().getTutoredService().getAllNotSynced();
        if (Utilities.listHasElements(tutoreds)) {
            Call<List<TutoredDTO>> tutoredCall = syncDataService.postTutoreds(Utilities.parse(tutoreds, TutoredDTO.class));
            tutoredCall.enqueue(new Callback<List<TutoredDTO>>() {
                @Override
                public void onResponse(Call<List<TutoredDTO>> call, Response<List<TutoredDTO>> response) {
                    List<TutoredDTO> data = response.body();
                    if (response.code() == 200) {
                        getServiceExecutor().execute(()-> {
                            try {
                                List<Tutored> tutoreds = getApplication().getTutoredService().getAllNotSynced();
                                for (Tutored tutored : tutoreds) {
                                    tutored.setSyncStatus(SyncSatus.SENT);
                                    getApplication().getTutoredService().update(tutored);
                                }

                                listener.doOnResponse(BaseRestService.REQUEST_SUCESS, tutoreds);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else listener.doOnRestErrorResponse(response.message());
                }

                @Override
                public void onFailure(Call<List<TutoredDTO>> call, Throwable t) {
                    Log.i("METADATA LOAD --", t.getMessage(), t);
                }
            });
        }else {
            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Collections.emptyList());
        }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void restPostTutored(Tutored tutored, RestResponseListener<Tutored> listener){


        Call<TutoredDTO> tutoredCall = syncDataService.postTutored(new TutoredDTO(tutored));
        tutoredCall.enqueue(new Callback<TutoredDTO>() {
            @Override
            public void onResponse(Call<TutoredDTO> call, Response<TutoredDTO> response) {
                TutoredDTO data = response.body();
                if (response.code() == 201) {
                    getServiceExecutor().execute(()-> {
                        try {
                            getApplication().getTutoredService().savedOrUpdateTutored(tutored);

                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Utilities.parseToList(tutored));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
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
            public void onFailure(Call<TutoredDTO> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
                listener.doOnRestErrorResponse(t.getMessage());
            }
        });

    }

}
