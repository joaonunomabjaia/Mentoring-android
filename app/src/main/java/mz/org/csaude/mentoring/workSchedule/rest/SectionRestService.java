package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;

import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.section.SectionDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.form.Section;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SectionRestService extends BaseRestService {
    public SectionRestService(Application application) {
        super(application);
    }

    public void restGetSections(RestResponseListener<Section> listener) {

        Call<List<SectionDTO>> evaluationTypesCall = syncDataService.getSections();

        evaluationTypesCall.enqueue(new Callback<List<SectionDTO>>() {
            @Override
            public void onResponse(Call<List<SectionDTO>> call, Response<List<SectionDTO>> response) {

                List<SectionDTO> data = response.body();

                if(Utilities.listHasElements(data)){
                    getServiceExecutor().execute(()-> {
                        List<Section> sections = Utilities.parse(data, Section.class);
                        for (Section section : sections) {
                            section.setSyncStatus(SyncSatus.SENT);
                        }
                        getApplication().getSectionService().saveOrUpdateSections(sections);
                        listener.doOnResponse(BaseRestService.REQUEST_SUCESS, sections);
                    });
                } else {
                    listener.doOnResponse(REQUEST_NO_DATA, null);
                }
            }

            @Override
            public void onFailure(Call<List<SectionDTO>> call, Throwable t) {
                Log.i("SECTION DOWNLOAD --", t.getMessage(), t);
            }
        });

    }

}
