package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.common.HttpStatus;
import mz.org.csaude.mentoring.common.MentoringAPIError;
import mz.org.csaude.mentoring.dto.ronda.RondaDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentor;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.ronda.RondaMentorService;
import mz.org.csaude.mentoring.service.ronda.RondaService;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.LifeCycleStatus;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RondaRestService extends BaseRestService {

    public RondaRestService(Application application) {
        super(application);
    }

    public void restGetRondas(RestResponseListener<Ronda> listener) {
        if (!getSessionManager().isAnyUserConfigured()) {
            listener.doOnResponse(BaseRestService.REQUEST_NO_DATA, null);
            return;
        }

        List<String> tutorUUIDs = getAllTutorUUIDs();
        if (!Utilities.listHasElements(tutorUUIDs)) {
            listener.doOnResponse(BaseRestService.REQUEST_NO_DATA, null);
            return;
        }

        Call<List<RondaDTO>> rondasCall = syncDataService.getRondasAllOfMentors(tutorUUIDs);
        rondasCall.enqueue(new Callback<List<RondaDTO>>() {
            @Override
            public void onResponse(Call<List<RondaDTO>> call, Response<List<RondaDTO>> response) {
                if (!response.isSuccessful()) {
                    listener.doOnRestErrorResponse("HTTP " + response.code());
                    return;
                }

                final List<RondaDTO> dtoList = response.body();
                if (dtoList == null || dtoList.isEmpty()) {
                    // Nothing to process
                    getServiceExecutor().execute(() ->
                            listener.doOnResponse(BaseRestService.REQUEST_NO_DATA, Collections.emptyList()));
                    return;
                }

                getServiceExecutor().execute(() -> {
                    try {
                        RondaService rondaService = getApplication().getRondaService();
                        RondaMentorService rondaMentorService = getApplication().getRondaMentorService();

                        List<Ronda> localRondas = rondaService.getAll();
                        List<Ronda> fetchedRondas = convertAndPrepare(dtoList);

                        handleOldRondas(localRondas, fetchedRondas, rondaMentorService);

                        if (Utilities.listHasElements(fetchedRondas)) {
                            saveNewRondas(dtoList, localRondas, rondaService);
                        }

                        listener.doOnResponse(BaseRestService.REQUEST_SUCESS, fetchedRondas);
                    } catch (SQLException e) {
                        Log.e("RondaRestService", e.getMessage(), e);
                        listener.doOnRestErrorResponse(e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Call<List<RondaDTO>> call, Throwable t) {
                Log.e("RondaRestService", "Failed to fetch Rondas", t);
                listener.doOnRestErrorResponse(t.getMessage());
            }
        });
    }

    private List<String> getAllTutorUUIDs() {
        try {
            List<Tutor> tutors = getApplication().getTutorService().getAll();
            return tutors.stream()
                    .map(Tutor::getUuid)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            Log.e("RondaRestService", "Failed to load tutors", e);
            return new ArrayList<>();
        }
    }

    private List<Ronda> convertAndPrepare(List<RondaDTO> dtoList) {
        List<Ronda> result = new ArrayList<>();
        for (RondaDTO dto : dtoList) {
            Ronda ronda = new Ronda(dto);
            ronda.setSyncStatus(SyncSatus.SENT);
            dto.getHealthFacility().getHealthFacilityObj().setSyncStatus(SyncSatus.SENT);
            result.add(ronda);
        }
        return result;
    }

    private void handleOldRondas(List<Ronda> localRondas, List<Ronda> fetchedRondas, RondaMentorService mentorService) throws SQLException {
        if (!Utilities.listHasElements(localRondas)) return;

        Map<String, Ronda> fetchedRondaMap = fetchedRondas.stream()
                .collect(Collectors.toMap(Ronda::getUuid, Function.identity()));

        for (Ronda localRonda : localRondas) {
            String uuid = localRonda.getUuid();

            if (!fetchedRondaMap.containsKey(uuid)) {
                // Not in fetched list — update endDate of first mentor
                if (Utilities.listHasElements(localRonda.getRondaMentors())) {
                    localRonda.getRondaMentors().get(0).setEndDate(DateUtilities.getCurrentDate());
                    localRonda.getRondaMentors().get(0).setLifeCycleStatus(LifeCycleStatus.INACTIVE);
                    localRonda.setLifeCycleStatus(LifeCycleStatus.INACTIVE);
                    getApplication().getRondaService().update(localRonda);
                    mentorService.update(localRonda.getRondaMentors().get(0));
                }
            } else {
                Ronda fetched = fetchedRondaMap.get(uuid);
                if (fetched.isRondaZero()) continue;

                localRonda.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
                getApplication().getRondaService().update(localRonda);
                // Exists in both — delete old mentors and insert new ones
                mentorService.deleteByRondaId(localRonda.getId());
                if (Utilities.listHasElements(fetched.getRondaMentors())) {
                    for (RondaMentor newMentor : fetched.getRondaMentors()) {
                        newMentor.setRonda(localRonda);
                        newMentor.setTutor(getApplication().getTutorService().getByuuid(newMentor.getTutor().getUuid()));
                        mentorService.save(newMentor);
                    }
                }
            }
        }
    }


    private void saveNewRondas(List<RondaDTO> dtoList, List<Ronda> localRondas, RondaService rondaService) throws SQLException {
        Set<String> existingUUIDs = localRondas.stream()
                .map(Ronda::getUuid)
                .collect(Collectors.toSet());

        List<RondaDTO> newRondas = dtoList.stream()
                .filter(dto -> !existingUUIDs.contains(dto.getUuid()))
                .collect(Collectors.toList());

        if (!newRondas.isEmpty()) {
            rondaService.saveOrUpdateRondas(newRondas);
            Log.i("RondaRestService", "Saved " + newRondas.size() + " new rondas");
        } else {
            Log.i("RondaRestService", "No new rondas to save");
        }
    }



    public void restPostRondas(RestResponseListener<Ronda> listener){

        List<Ronda> rondas = null;
        try {
            rondas = getApplication().getRondaService().getAllNotSynced();
            if (Utilities.listHasElements(rondas)) {
                List<RondaDTO> rondaDTOs = new ArrayList<>();
                for (Ronda ronda : rondas) {
                    rondaDTOs.add(new RondaDTO(ronda));
                }
                Call<List<RondaDTO>> rondaCall = syncDataService.updateRondaInfo(rondaDTOs);

                //Call<List<RondaDTO>> rondaCall = syncDataService.updateRondaInfo(Utilities.parse(rondas, RondaDTO.class));
                rondaCall.enqueue(new Callback<List<RondaDTO>>() {
                    @Override
                    public void onResponse(Call<List<RondaDTO>> call, Response<List<RondaDTO>> response) {
                        List<RondaDTO> data = response.body();
                        if (response.code() == 200) {
                            getServiceExecutor().execute(()-> {
                                try {
                                    List<Ronda> rondaList = getApplication().getRondaService().getAllNotSynced();
                                    for (Ronda ronda : rondaList) {
                                        ronda.setSyncStatus(SyncSatus.SENT);
                                        getApplication().getRondaService().update(ronda);
                                    }

                                    listener.doOnResponse(BaseRestService.REQUEST_SUCESS, rondaList);
                                } catch (SQLException e) {
                                    Log.e("RONDA CLOSE REST SERVICE --", e.getMessage(), e);
                                    listener.doOnRestErrorResponse(response.message());
                                }
                            });
                        } else {
                            Log.e("RONDA CLOSE REST SERVICE --", response.message());
                            listener.doOnRestErrorResponse(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RondaDTO>> call, Throwable t) {
                        Log.e("RONDA CLOSE REST SERVICE --", t.getMessage(), t);
                        listener.doOnRestErrorResponse(t.getMessage());
                    }
                });
            } else {
                listener.doOnResponse(BaseRestService.REQUEST_NO_DATA, Collections.emptyList());
            }
        } catch (Exception e) {
            Log.e("RONDA CLOSE REST SERVICE --", e.getMessage(), e);
            listener.doOnRestErrorResponse(e.getMessage());
        }
    }

    public void restPostRonda(Ronda ronda, RestResponseListener<Ronda> listener){

        RondaDTO dto = new RondaDTO(ronda);
        Call<RondaDTO> rondaCall = syncDataService.postRonda(dto);
        rondaCall.enqueue(new Callback<RondaDTO>() {
            @Override
            public void onResponse(Call<RondaDTO> call, Response<RondaDTO> response) {
                RondaDTO data = response.body();
                if (response.code() == 201) {
                    getServiceExecutor().execute(()-> {
                        try {
                            Ronda ronda1 = data.getRonda();
                            ronda1.setSyncStatus(ronda.getSyncStatus());
                            getApplication().getRondaService().savedOrUpdateRonda(ronda1);

                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Utilities.parseToList(ronda));
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
            public void onFailure(Call<RondaDTO> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
                listener.doOnRestErrorResponse(t.getMessage());
            }
        });
    }

    public void restPatchRonda(Ronda ronda, RestResponseListener<Ronda> listener) {

        RondaDTO dto = new RondaDTO(ronda);
        Call<RondaDTO> rondaCall = syncDataService.patchtRonda(dto);
        rondaCall.enqueue(new Callback<RondaDTO>() {
            @Override
            public void onResponse(Call<RondaDTO> call, Response<RondaDTO> response) {
                RondaDTO data = response.body();
                if (response.code() == 201) {
                    getServiceExecutor().execute(()->{
                        try {
                            getApplication().getRondaService().savedOrUpdateRonda(ronda);

                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Utilities.parseToList(ronda));
                        } catch (SQLException  e) {
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
            public void onFailure(Call<RondaDTO> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
                listener.doOnRestErrorResponse(t.getMessage());
            }
        });
    }

    public void delete(Ronda ronda, RestResponseListener<Ronda> listener) {
        Call<ResponseBody> rondaCall = syncDataService.delete(ronda.getUuid());
        rondaCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody data = response.body();
                if (response.code() == 200) {
                    getServiceExecutor().execute(()->{
                        try {
                            getApplication().getRondaService().delete(ronda);
                        } catch (SQLException e) {
                            Log.e("RondaRestService", e.getMessage(), e);
                        }
                        listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Utilities.parseToList(ronda));
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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
                listener.doOnRestErrorResponse(t.getMessage());
            }
        });
    }
}
