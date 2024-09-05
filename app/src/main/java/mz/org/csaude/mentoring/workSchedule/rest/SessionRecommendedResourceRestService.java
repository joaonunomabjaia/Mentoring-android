package mz.org.csaude.mentoring.workSchedule.rest;

import static androidx.fragment.app.FragmentManager.TAG;

import android.app.Application;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.session.SessionRecommendedResourceDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionRecommendedResource;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.service.session.SessionService;
import mz.org.csaude.mentoring.service.session.SessionServiceImpl;
import mz.org.csaude.mentoring.service.tutor.TutorService;
import mz.org.csaude.mentoring.service.tutored.TutoredService;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionRecommendedResourceRestService extends BaseRestService {

    private SessionService sessionService;
    private TutorService tutorService;

    private TutoredService tutoredService;
    public SessionRecommendedResourceRestService(Application application) {
        super(application);
    }

    public void postSessionRecommendedResource(RestResponseListener<SessionRecommendedResource> listener) {
        List<SessionRecommendedResourceDTO> resourceDTOList = null;
        try {
            List<SessionRecommendedResource> resources = getApplication().getSessionService().getPendingRecommendedResources();
            this.sessionService = getApplication().getSessionService();
            this.tutoredService = getApplication().getTutoredService();
            this.tutorService = getApplication().getTutorService();

            if (resources.isEmpty()) {
                listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Collections.emptyList());
                return;
            }
            resourceDTOList = Utilities.parse(resources, SessionRecommendedResourceDTO.class);
        } catch (SQLException e) {
            Log.e("SessionRecommendedResourceRestService", "Failed to get pending recommended resources", e);
        }

        Call<List<SessionRecommendedResourceDTO>> call = syncDataService.postSessionRecommendedResource(resourceDTOList);
        call.enqueue(new Callback<List<SessionRecommendedResourceDTO>>() {
            @Override
            public void onResponse(Call<List<SessionRecommendedResourceDTO>> call, Response<List<SessionRecommendedResourceDTO>> response) {
                if (response.isSuccessful()) {
                    getServiceExecutor().execute(()-> {
                        try {

                            List<SessionRecommendedResource> resources = convertFromSessionRecommendedResourceDTO(response.body());
                            for (SessionRecommendedResource resource : resources) {
                                resource.setSyncStatus(SyncSatus.SENT);
                                getApplication().getSessionService().updateRecommendedResources(resource);
                            }

                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, Collections.emptyList());
                        } catch (SQLException e) {
                            Log.e("SessionRecommendedResourceRestService", "Failed to update recommended resources", e);
                        }
                    });
                } else {
                    listener.doOnRestErrorResponse("Failed to save session recommended resource: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<SessionRecommendedResourceDTO>> call, Throwable t) {
                listener.doOnRestErrorResponse("Network error: " + t.getMessage());
            }
        });
    }

    private List<SessionRecommendedResource> convertFromSessionRecommendedResourceDTO(List<SessionRecommendedResourceDTO> sessionRecommendedResourceDTOList){

        List<SessionRecommendedResource> sessionRecommendedResources =new ArrayList<>();
        try {
        for (SessionRecommendedResourceDTO sessionRecommendedResourceDTO : sessionRecommendedResourceDTOList){

                Session session = this.sessionService.getByuuid(sessionRecommendedResourceDTO.getSessionUuid());
                Tutor tutor = this.tutorService.getByuuid(sessionRecommendedResourceDTO.getTutorUuid());
                Tutored tutored = this.tutoredService.getByuuid(sessionRecommendedResourceDTO.getTutoredUuid());

            SessionRecommendedResource sessionRecommendedResource = new SessionRecommendedResource(sessionRecommendedResourceDTO,  session,  tutored,  tutor);
            sessionRecommendedResources.add(sessionRecommendedResource);
        }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sessionRecommendedResources;
    }
}
