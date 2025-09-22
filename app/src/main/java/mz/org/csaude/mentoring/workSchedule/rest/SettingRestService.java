package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.setting.SettingDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.setting.Setting;
import mz.org.csaude.mentoring.service.setting.SettingService;
import mz.org.csaude.mentoring.service.setting.SettingServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingRestService extends BaseRestService {

    public SettingRestService(Application application) {
        super(application);
    }

    public void restGetSettings(RestResponseListener<Setting> listener){

        Call<List<SettingDTO>> settingsCall = syncDataService.getSettings();

       settingsCall.enqueue(new Callback<List<SettingDTO>>() {
           @Override
           public void onResponse(Call<List<SettingDTO>> call, Response<List<SettingDTO>> response) {
               List<SettingDTO> data = response.body();

               if(data == null){
                   listener.doOnResponse(BaseRestService.REQUEST_NO_DATA, Collections.emptyList());
                  return;
               }
               getServiceExecutor().execute(()-> {
                   try {
                       SettingService settingService = getApplication().getSettingService();
                       List<Setting> settings = new ArrayList<>();

                       for (SettingDTO settingDTO : data) {
                           settingDTO.getSetting().setSyncStatus(SyncSatus.SENT);
                           settings.add(settingDTO.getSetting());
                       }
                       settingService.savedOrUpdateSettings(data);
                       listener.doOnResponse(BaseRestService.REQUEST_SUCESS, settings);
                   } catch (SQLException e) {
                       throw new RuntimeException(e);
                   }
               });
           }

           @Override
           public void onFailure(Call<List<SettingDTO>> call, Throwable t) {
               Log.i("METADATA LOAD --", t.getMessage(), t);
               listener.doOnRestErrorResponse(t.getMessage());
           }
       });

    }
}
