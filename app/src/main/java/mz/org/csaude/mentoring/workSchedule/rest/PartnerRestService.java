package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.ApiResponse;
import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.partner.PartnerDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.util.SyncSatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PartnerRestService extends BaseRestService {

    public PartnerRestService(Application application) {
        super(application);
    }

    public  void restGetPartners(RestResponseListener<Partner> listener){

        Call<ApiResponse<PartnerDTO>> partnerCall= syncDataService.getPartners();

        partnerCall.enqueue(new Callback<ApiResponse<PartnerDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<PartnerDTO>> call, Response<ApiResponse<PartnerDTO>> response) {

                List<PartnerDTO> data = response.body().getContent();

                if(data == null){

                }
                getServiceExecutor().execute(()-> {
                    try {

                        List<Partner> partners = new ArrayList<>();
                        for (PartnerDTO partnerDTO : data) {
                            partnerDTO.getPartner().setSyncStatus(SyncSatus.SENT);
                            partners.add(partnerDTO.getPartner());
                        }
                        getApplication().getPartnerService().saveAll(partners);

                        listener.doOnResponse(BaseRestService.REQUEST_SUCESS, partners);
                    } catch (SQLException e) {
                        listener.doOnRestErrorResponse(e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse<PartnerDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
                listener.doOnRestErrorResponse(t.getMessage());

            }
        });


    }
}
