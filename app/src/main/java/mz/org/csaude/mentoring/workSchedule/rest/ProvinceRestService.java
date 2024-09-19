package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.location.ProvinceDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.location.Province;
import mz.org.csaude.mentoring.service.location.ProvinceService;
import mz.org.csaude.mentoring.service.location.ProvinceServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProvinceRestService extends BaseRestService {

    public ProvinceRestService(Application application) {
        super(application);
    }

    public  void restGetProvince(RestResponseListener<Province> listener){

        Call<List<ProvinceDTO>> callProvinceDTO = syncDataService.getProvinces();

        callProvinceDTO.enqueue(new Callback<List<ProvinceDTO>>() {
            @Override
            public void onResponse(Call<List<ProvinceDTO>> call, Response<List<ProvinceDTO>> response) {

                List<ProvinceDTO> data = response.body();

                if(data == null){
                    listener.doOnRestErrorResponse("No data received");
                    return;
                }
                getServiceExecutor().execute(() -> {
                    try {

                        ProvinceService provinceService = getApplication().getProvinceService();

                        List<Province> provinces = new ArrayList<>();
                        for (ProvinceDTO provinceDTO : data) {
                            provinceDTO.getProvince().setSyncStatus(SyncSatus.SENT);
                            provinces.add(provinceDTO.getProvince());
                        }
                        provinceService.savedOrUpdateProvince(data);
                        listener.doOnResponse(BaseRestService.REQUEST_SUCESS, provinces);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<ProvinceDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
                listener.doOnRestErrorResponse(t.getMessage());
            }
        });


    }
}
