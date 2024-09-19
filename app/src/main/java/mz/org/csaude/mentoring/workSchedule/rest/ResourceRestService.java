package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.resource.ResourceDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.resourceea.Resource;
import mz.org.csaude.mentoring.service.resource.ResourceService;
import mz.org.csaude.mentoring.util.Utilities;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResourceRestService extends BaseRestService {

    public ResourceRestService(Application application) {
        super(application);
    }

   public void restGetResources(RestResponseListener<Resource> listener){

       Call<List<ResourceDTO>> resourceCall = syncDataService.getAllResource();

       resourceCall.enqueue(new Callback<List<ResourceDTO>>() {
           @Override
           public void onResponse(Call<List<ResourceDTO>> call, Response<List<ResourceDTO>> response) {

              List<ResourceDTO> data = response.body();

              if(Utilities.listHasElements(data)){
                  getServiceExecutor().execute(()-> {
                      try {
                          ResourceService resourceService = getApplication().getResourceService();
                          List<Resource> resourceList = new ArrayList<>();

                          for (ResourceDTO resourceDTO : data) {
                              resourceList.add(resourceDTO.getResourceModel());
                          }
                          resourceService.savedOrUpdateResources(resourceList);
                          listener.doOnResponse(BaseRestService.REQUEST_SUCESS, resourceList);
                      } catch (SQLException e) {
                          throw new RuntimeException(e);
                      }
                  });
              }else{
                  listener.doOnResponse(REQUEST_NO_DATA, null);
              }
           }

           @Override
           public void onFailure(Call<List<ResourceDTO>> call, Throwable t) {
               Log.i("METADATA LOAD --", t.getMessage(), t);

           }
       });

   }

    public void downloadFile(String fileName, RestResponseListener<Resource> listener) {
        Call<ResponseBody> call = syncDataService.downloadFile(fileName);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(), fileName);
                    if (writtenToDisk) {
                        listener.doOnRestSucessResponse(BaseRestService.REQUEST_SUCESS);
                    } else {
                        listener.doOnRestErrorResponse(BaseRestService.REQUEST_ERROR);
                    }
                } else {
                    listener.doOnRestErrorResponse(BaseRestService.REQUEST_ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Download Error", t.getMessage());
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + fileName);

            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
