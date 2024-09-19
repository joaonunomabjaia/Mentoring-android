package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.dto.mentorship.MentorshipDTO;
import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.answer.Answer;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.service.mentorship.MentorshipService;
import mz.org.csaude.mentoring.service.mentorship.MentorshipServiceImpl;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MentorshipRestService extends BaseRestService {
    public MentorshipRestService(Application application) {
        super(application);
    }
    public void restPostMentorships(RestResponseListener<Mentorship> listener){

        List<Mentorship> mentorships = null;
        try {
            mentorships = getApplication().getMentorshipService().getAllNotSynced(getApplication());
        if (Utilities.listHasElements(mentorships)) {
            for (Mentorship mentorship : mentorships) {
                List<Answer> answers = getApplication().getAnswerService().getAllOfMentorship(mentorship);
                for (Answer answer: answers) {
                    answer.setMentorship(mentorship);
                }
                mentorship.setAnswers(answers);
            }
            List<MentorshipDTO> mentorshipDTOList = Utilities.parse(mentorships, MentorshipDTO.class);
            Call<List<MentorshipDTO>> mentorshipsCall = syncDataService.postMenthorships(mentorshipDTOList);
            mentorshipsCall.enqueue(new Callback<List<MentorshipDTO>>() {
                @Override
                public void onResponse(Call<List<MentorshipDTO>> call, Response<List<MentorshipDTO>> response) {
                    List<MentorshipDTO> data = response.body();
                    if (Utilities.listHasElements(data)) {
                        getServiceExecutor().execute(()-> {
                            try {
                                List<Mentorship> mentorshipList = getApplication().getMentorshipService().getAllNotSynced(getApplication());
                                for (Mentorship mentorship : mentorshipList) {
                                    mentorship.setSyncStatus(SyncSatus.SENT);
                                    getApplication().getMentorshipService().update(mentorship);
                                }

                                listener.doOnResponse(BaseRestService.REQUEST_SUCESS, mentorshipList);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else listener.doOnRestErrorResponse(response.message());
                }

                @Override
                public void onFailure(Call<List<MentorshipDTO>> call, Throwable t) {
                    Log.i("MENTORSHIP REST --", t.getMessage(), t);
                }
            });
        } else
        {
            listener.doOnResponse(BaseRestService.REQUEST_NO_DATA, Collections.emptyList());
        }
        } catch (SQLException e) {
             listener.doOnRestErrorResponse(e.getMessage());
        }

    }

}
