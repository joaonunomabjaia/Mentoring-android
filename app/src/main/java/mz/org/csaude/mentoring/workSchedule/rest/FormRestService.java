package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.form.FormDTO;
import mz.org.csaude.mentoring.dto.form.FormQuestionDTO;
import mz.org.csaude.mentoring.dto.tutored.TutoredDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.employee.Employee;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.formQuestion.FormQuestion;
import mz.org.csaude.mentoring.model.location.Location;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.evaluationType.EvaluationTypeService;
import mz.org.csaude.mentoring.service.evaluationType.EvaluationTypeServiceImpl;
import mz.org.csaude.mentoring.service.form.FormService;
import mz.org.csaude.mentoring.service.form.FormServiceImpl;
import mz.org.csaude.mentoring.service.formQuestion.FormQuestionService;
import mz.org.csaude.mentoring.service.formQuestion.FormQuestionServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormRestService extends BaseRestService {


    public FormRestService(Application application) {
        super(application);
    }

    public void restGetForm(RestResponseListener<Form> listener){
        Tutor mentor = getApplication().getCurrMentor();
        if(mentor==null) {
            try {
                User user =  getApplication().getUserService().getCurrentUser();
                if(user==null) return;
                mentor = getApplication().getTutorService().getByEmployee(user.getEmployee());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        Call<List<FormDTO>> formCall = syncDataService.getFormsByMentor(mentor.getUuid());
        FormService formService = getApplication().getFormService();

        formCall.enqueue(new Callback<List<FormDTO>>() {
            @Override
            public void onResponse(Call<List<FormDTO>> call, Response<List<FormDTO>> response) {
                List<FormDTO> data = response.body();
                if (Utilities.listHasElements(data)) {
                    getServiceExecutor().execute(()-> {
                        try {
                            List<Form> forms = Utilities.parse(data, Form.class);
                            for (Form form : forms) {
                                form.setSyncStatus(SyncSatus.SENT);
                                form.setPartner(getApplication().getPartnerService().getByuuid(form.getPartner().getUuid()));
                                form.setProgrammaticArea(getApplication().getProgrammaticAreaService().getByuuid(form.getProgrammaticArea().getUuid()));
                            }
                            formService.savedOrUpdateForms(forms);
                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, forms);
                        } catch (SQLException e) {
                            Log.e("METADATA LOAD --", e.getMessage(), e);
                        }
                    });
                } else {
                    listener.doOnResponse(BaseRestService.REQUEST_NO_DATA, null);
                }
            }

            @Override
            public void onFailure(Call<List<FormDTO>> call, Throwable t) {
                Log.i("METADATA LOAD --", t.getMessage(), t);
            }
        });

    }


    public void restPostForm(RestResponseListener<Form> listener){

        List<Form> forms = null;
        try {
            forms = getApplication().getFormService().getAllNotSynced();
        if (Utilities.listHasElements(forms)) {
            Call<List<FormDTO>> formCall = syncDataService.postForms(Utilities.parse(forms, FormDTO.class));
            formCall.enqueue(new Callback<List<FormDTO>>() {
                @Override
                public void onResponse(Call<List<FormDTO>> call, Response<List<FormDTO>> response) {
                    List<FormDTO> data = response.body();
                    if (response.code() == 201) {
                        try {
                            List<Form> formList = getApplication().getFormService().getAllNotSynced();
                            for (Form form : formList) {
                                form.setSyncStatus(SyncSatus.SENT);
                                getApplication().getFormService().update(form);
                            }

                            listener.doOnResponse(BaseRestService.REQUEST_SUCESS, formList);
                        } catch (SQLException  e) {
                            throw new RuntimeException(e);
                        }
                    } else listener.doOnRestErrorResponse(response.message());
                }

                @Override
                public void onFailure(Call<List<FormDTO>> call, Throwable t) {
                    Log.i("METADATA LOAD --", t.getMessage(), t);
                }
            });
        }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
