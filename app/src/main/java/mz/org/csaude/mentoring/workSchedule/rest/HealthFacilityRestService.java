package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.location.HealthFacilityDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.location.Location;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.location.HealthFacilityService;
import mz.org.csaude.mentoring.service.location.HealthFacilityServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthFacilityRestService extends BaseRestService {
    public HealthFacilityRestService(Application application) {
        super(application);
    }
    public void restGetHealthFacility(RestResponseListener<HealthFacility> listener){

        List<String> districts = new ArrayList<>();
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
            districts.add(location.getDistrict().getUuid());
        }
       Call<List<HealthFacilityDTO>> callHealthFacilith = syncDataService.getByDistricts(districts);

        callHealthFacilith.enqueue(new Callback<List<HealthFacilityDTO>>() {
            @Override
            public void onResponse(Call<List<HealthFacilityDTO>> call, Response<List<HealthFacilityDTO>> response) {

             List<HealthFacilityDTO> datas = response.body();

             if(datas == null){

             }
                getServiceExecutor().execute(()-> {
                    try {

                        List<HealthFacility> healthFacilities = Utilities.parse(datas, HealthFacility.class);
                        for (HealthFacility healthFacility : healthFacilities) {
                            healthFacility.setSyncStatus(SyncSatus.SENT);
                        }
                        HealthFacilityService healthFacilityService = getApplication().getHealthFacilityService();
                        healthFacilityService.savedOrUpdatHealthFacilitys(healthFacilities);
                        listener.doOnResponse(BaseRestService.REQUEST_SUCESS, healthFacilities);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<HealthFacilityDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });

    }
}
