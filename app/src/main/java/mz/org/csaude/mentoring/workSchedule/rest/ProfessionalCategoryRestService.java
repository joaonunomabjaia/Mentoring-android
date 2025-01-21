package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.ApiResponse;
import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.professionalcategory.ProfessionalCategoryDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.professionalCategory.ProfessionalCategory;
import mz.org.csaude.mentoring.service.professionalCategory.ProfessionalCategoryService;
import mz.org.csaude.mentoring.service.professionalCategory.ProfessionalCategoryServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessionalCategoryRestService extends BaseRestService {

    public ProfessionalCategoryRestService(Application application) {
        super(application);
    }
    public void restGetProfessionalCategory(long offset, long limit, RestResponseListener<ProfessionalCategory> listener){

       Call<List<ProfessionalCategoryDTO>> callProfessionalCategoryDTO = syncDataService.getProfessionalCategory(offset, limit);

        callProfessionalCategoryDTO.enqueue(new Callback<List<ProfessionalCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<ProfessionalCategoryDTO>> call, Response<List<ProfessionalCategoryDTO>> response) {

               List<ProfessionalCategoryDTO> data = response.body();
               if ( data == null){

                         }
                getServiceExecutor().execute(()-> {
                    try {
                        ProfessionalCategoryService professionalCategoryService = getApplication().getProfessionalCategoryService();

                        List<ProfessionalCategory> professionalCategories = new ArrayList<>();

                        for (ProfessionalCategoryDTO professionalCategoryDTO : data) {
                            professionalCategoryDTO.getProfessionalCategory().setSyncStatus(SyncSatus.SENT);
                            professionalCategories.add(professionalCategoryDTO.getProfessionalCategory());
                        }
                        professionalCategoryService.saveOrUpdateProfessionalCategorys(data);
                        listener.doOnResponse(BaseRestService.REQUEST_SUCESS, professionalCategories);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

            }

            @Override
            public void onFailure(Call<List<ProfessionalCategoryDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });

    }

    public void restGetProfessionalCategory(long offset, long limit){

        Call<List<ProfessionalCategoryDTO>> callProfessionalCategoryDTO = syncDataService.getProfessionalCategory(offset, limit);

        callProfessionalCategoryDTO.enqueue(new Callback<List<ProfessionalCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<ProfessionalCategoryDTO>> call, Response<List<ProfessionalCategoryDTO>> response) {

                List<ProfessionalCategoryDTO> data = response.body();
                if ( data == null){

                }
                try {
                    ProfessionalCategoryService professionalCategoryService = getApplication().getProfessionalCategoryService();
                    professionalCategoryService.saveOrUpdateProfessionalCategorys(data);

                    List<ProfessionalCategory> professionalCategories = new ArrayList<>();

                    for (ProfessionalCategoryDTO professionalCategoryDTO : data){
                        professionalCategories.add(new ProfessionalCategory(professionalCategoryDTO));
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }


            }

            @Override
            public void onFailure(Call<List<ProfessionalCategoryDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });

    }
}
