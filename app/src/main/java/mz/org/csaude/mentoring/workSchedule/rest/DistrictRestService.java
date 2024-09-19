package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.location.DistrictDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.service.location.DistrictService;
import mz.org.csaude.mentoring.service.location.DistrictServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DistrictRestService extends BaseRestService {

    public DistrictRestService(Application application) {
        super(application);
    }

    public void restGetDistricts(long offset, long limit, RestResponseListener<District> listener){

        Call<List<DistrictDTO>> districtsCall = syncDataService.getDistricts(offset, limit);

        districtsCall.enqueue(new Callback<List<DistrictDTO>>() {
            @Override
            public void onResponse(Call<List<DistrictDTO>> call, Response<List<DistrictDTO>> response) {

                List<DistrictDTO> data = response.body();

                if(!Utilities.listHasElements(data)){

                } else {
                    getServiceExecutor().execute(()-> {
                        try {
                            DistrictService districtService = getApplication().getDistrictService();
                            List<District> districts = new ArrayList<>();
                            for (DistrictDTO districtDTO : data) {
                                districts.add(new District(districtDTO));
                            }
                            districtService.savedOrUpdateDistricts(data);
                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, districts);

                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

            }
            @Override
            public void onFailure(Call<List<DistrictDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);

            }
        });
    }
}
