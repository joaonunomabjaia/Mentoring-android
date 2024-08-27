package mz.org.csaude.mentoring.workSchedule.executor;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.model.setting.Setting;
import mz.org.csaude.mentoring.util.Http;
import mz.org.csaude.mentoring.workSchedule.work.CabinetWorker;
import mz.org.csaude.mentoring.workSchedule.work.DistrictWorker;
import mz.org.csaude.mentoring.workSchedule.work.DoorWorker;
import mz.org.csaude.mentoring.workSchedule.work.EvaluationTypeWorker;
import mz.org.csaude.mentoring.workSchedule.work.FormQuestionWorker;
import mz.org.csaude.mentoring.workSchedule.work.FormWorker;
import mz.org.csaude.mentoring.workSchedule.work.HealthFacilityWorker;
import mz.org.csaude.mentoring.workSchedule.work.IterationTypeWorker;
import mz.org.csaude.mentoring.workSchedule.work.MentorshipWorker;
import mz.org.csaude.mentoring.workSchedule.work.PartnerWorker;
import mz.org.csaude.mentoring.workSchedule.work.ProfessionalCategoryWorker;
import mz.org.csaude.mentoring.workSchedule.work.ProgramWorker;
import mz.org.csaude.mentoring.workSchedule.work.ProgrammaticAreaWorker;
import mz.org.csaude.mentoring.workSchedule.work.ProvinceWorker;
import mz.org.csaude.mentoring.workSchedule.work.Resourceworker;
import mz.org.csaude.mentoring.workSchedule.work.ResponseTypeWorker;
import mz.org.csaude.mentoring.workSchedule.work.RondaTypeWorker;
import mz.org.csaude.mentoring.workSchedule.work.RondaWorker;
import mz.org.csaude.mentoring.workSchedule.work.SessionPOSTWorker;
import mz.org.csaude.mentoring.workSchedule.work.SessionRecommendedResourceWorker;
import mz.org.csaude.mentoring.workSchedule.work.SessionStatusWorker;
import mz.org.csaude.mentoring.workSchedule.work.SessionGETWorker;
import mz.org.csaude.mentoring.workSchedule.work.TutorWorker;
import mz.org.csaude.mentoring.workSchedule.work.TutoredWorker;
import mz.org.csaude.mentoring.workSchedule.work.UserWorker;

public class WorkerScheduleExecutor {

    private static final String TAG = "WorkerScheduler";
    public static final int JOB_ID = 1000;
    public static final long ONE_TIME_REQUEST_JOB_ID = System.currentTimeMillis();
    private WorkManager workManager;

    private static WorkerScheduleExecutor instance;

    private MentoringApplication application;

    private List<Setting> settings;

    private SharedPreferences sharedPreferences;

    private WorkerScheduleExecutor(Application application) {
        this.application = (MentoringApplication) application;
        this.workManager = WorkManager.getInstance(application);
        this.sharedPreferences = ((MentoringApplication) application).getMentoringSharedPreferences();
    }

    public WorkManager getWorkManager() {
        return workManager;
    }

    public static WorkerScheduleExecutor getInstance(Application application) {
        if (instance == null) {
            instance = new WorkerScheduleExecutor(application);
        }
        return instance;
    }
    public OneTimeWorkRequest runInitialSync() {
        OneTimeWorkRequest provinceOneTimeWorkRequest = new OneTimeWorkRequest.Builder(ProvinceWorker.class).addTag("ONE_TIME_CABINET_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest districtOneTimeWorkRequest = new OneTimeWorkRequest.Builder(DistrictWorker.class).addTag("ONE_TIME_DISTRICT_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest categoriesOneTimeWorkRequest = new OneTimeWorkRequest.Builder(ProfessionalCategoryWorker.class).addTag("ONE_TIME_CATEGORIES_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest partnersOneTimeWorkRequest = new OneTimeWorkRequest.Builder(PartnerWorker.class).addTag("ONE_TIME_PARTNERS_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest rondaTypesOneTimeWorkRequest = new OneTimeWorkRequest.Builder(RondaTypeWorker.class).addTag("ONE_TIME_RONDA_TYPES_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest responseTypesOneTimeWorkRequest = new OneTimeWorkRequest.Builder(ResponseTypeWorker.class).addTag("ONE_TIME_RESPONSE_TYPES_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest evaluationTypesOneTimeWorkRequest = new OneTimeWorkRequest.Builder(EvaluationTypeWorker.class).addTag("ONE_TIME_EVALUATION_TYPES_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest iterationTypesOneTimeWorkRequest = new OneTimeWorkRequest.Builder(IterationTypeWorker.class).addTag("ONE_TIME_ITERATION_TYPES_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest doorsOneTimeWorkRequest = new OneTimeWorkRequest.Builder(DoorWorker.class).addTag("ONE_TIME_DOORS_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest cabinetOneTimeWorkRequest = new OneTimeWorkRequest.Builder(CabinetWorker.class).addTag("ONE_TIME_CABINET_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest sessionStatusOneTimeWorkRequest = new OneTimeWorkRequest.Builder(SessionStatusWorker.class).addTag("ONE_TIME_SESSION_STATUS_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest programsOneTimeWorkRequest = new OneTimeWorkRequest.Builder(ProgramWorker.class).addTag("ONE_TIME_PROGRAMS_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest programmaticAreaOneTimeWorkRequest = new OneTimeWorkRequest.Builder(ProgrammaticAreaWorker.class).addTag("ONE_TIME_PROGRAMMATIC_AREA_ID" + ONE_TIME_REQUEST_JOB_ID).build();

        workManager.beginUniqueWork("INITIAL_APP_SETUP", ExistingWorkPolicy.KEEP, provinceOneTimeWorkRequest)
                .then(Arrays.asList(districtOneTimeWorkRequest, partnersOneTimeWorkRequest, cabinetOneTimeWorkRequest))
                .then(Arrays.asList(rondaTypesOneTimeWorkRequest, responseTypesOneTimeWorkRequest,
                        evaluationTypesOneTimeWorkRequest, iterationTypesOneTimeWorkRequest,
                        doorsOneTimeWorkRequest, sessionStatusOneTimeWorkRequest, programsOneTimeWorkRequest)).then(programmaticAreaOneTimeWorkRequest)
                .then(categoriesOneTimeWorkRequest).enqueue();
        return categoriesOneTimeWorkRequest;

    }

    public OneTimeWorkRequest runPostLoginSync() {
        OneTimeWorkRequest tutorOneTimeWorkRequest = new OneTimeWorkRequest.Builder(TutorWorker.class).addTag("ONE_TIME_TUTOR_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        workManager.enqueue(tutorOneTimeWorkRequest);
        return tutorOneTimeWorkRequest;
    }

    public OneTimeWorkRequest downloadMentorData() {
        Data inputData = new Data.Builder().putString("requestType", String.valueOf(Http.POST)).build();

        OneTimeWorkRequest menteesOneTimeWorkRequest = new OneTimeWorkRequest.Builder(TutoredWorker.class).addTag("ONE_TIME_MENTEES_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest hfOneTimeWorkRequest = new OneTimeWorkRequest.Builder(HealthFacilityWorker.class).addTag("ONE_TIME_HF_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest mentorFormsOneTimeWorkRequest = new OneTimeWorkRequest.Builder(FormWorker.class).addTag("ONE_TIME_MENTOR_FORMS_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest mentorFormsQuestionsOneTimeWorkRequest = new OneTimeWorkRequest.Builder(FormQuestionWorker.class).addTag("ONE_TIME_MENTOR_FORMS_QUESTIONS_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest mentorRondasOneTimeWorkRequest = new OneTimeWorkRequest.Builder(RondaWorker.class).addTag("ONE_TIME_MENTOR_RONDAS_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest resourceTimeWorkRequest = new OneTimeWorkRequest.Builder(Resourceworker.class).addTag("ONE_RESOURCE_ID" + ONE_TIME_REQUEST_JOB_ID).build();
        OneTimeWorkRequest mentorSessionsOneTimeWorkRequest = new OneTimeWorkRequest.Builder(SessionGETWorker.class).addTag("ONE_TIME_MENTOR_SESSIONS_ID" + ONE_TIME_REQUEST_JOB_ID).build();

        workManager.beginUniqueWork("INITIAL_MENTOR_DATA_APP_SETUP", ExistingWorkPolicy.KEEP, hfOneTimeWorkRequest)
                .then(Arrays.asList(menteesOneTimeWorkRequest, mentorFormsOneTimeWorkRequest))
                .then(mentorFormsQuestionsOneTimeWorkRequest)
                .then(mentorRondasOneTimeWorkRequest)
                .then(resourceTimeWorkRequest)
                .then(mentorSessionsOneTimeWorkRequest)
                .enqueue();
        return mentorRondasOneTimeWorkRequest;
    }

    public OneTimeWorkRequest uploadMentees() {
        Data inputData = new Data.Builder()
                .putString("requestType", String.valueOf(Http.POST))
                .build();
        OneTimeWorkRequest menteesOneTimeWorkRequest = new OneTimeWorkRequest.Builder(TutoredWorker.class).addTag("ONE_TIME_MENTEES_ID" + ONE_TIME_REQUEST_JOB_ID).setInputData(inputData).build();
        workManager.enqueue(menteesOneTimeWorkRequest);
        return menteesOneTimeWorkRequest;
    }

    public OneTimeWorkRequest syncPostData() {
        // Define a unique job ID
        String jobId = "ONE_TIME_REQUEST_JOB_ID_" + System.currentTimeMillis();

        // Define input data for the requests
        Data inputData = new Data.Builder().putString("requestType", String.valueOf(Http.POST)).build();

        OneTimeWorkRequest sessionPostTimeWorkRequest = new OneTimeWorkRequest.Builder(SessionPOSTWorker.class)
                .addTag("ONE_TIME_SESSIONS_" + jobId)
                .build();

        OneTimeWorkRequest rondaPostTimeWorkRequest = new OneTimeWorkRequest.Builder(RondaWorker.class)
                .addTag("ONE_TIME_RONDAS_" + jobId)
                .setInputData(inputData)
                .build();

        // Create the first OneTimeWorkRequest
        OneTimeWorkRequest mentorshipPostTimeWorkRequest = new OneTimeWorkRequest.Builder(MentorshipWorker.class)
                .addTag("ONE_TIME_MENTORSHIPS_" + jobId)
                .setInputData(inputData)
                .build();

        // Create the second OneTimeWorkRequest
        OneTimeWorkRequest sessionRecommendedPostTimeWorkRequest = new OneTimeWorkRequest.Builder(SessionRecommendedResourceWorker.class)
                .addTag("ONE_TIME_SESSIONS_RECOMMENDED_RESOURCES_" + jobId)
                .setInputData(inputData)
                .build();

        OneTimeWorkRequest userInfoUpdateRequest = new OneTimeWorkRequest.Builder(UserWorker.class)
                .addTag("ONE_TIME_USER_UPDATE_" + jobId)
                .build();


        // Chain the WorkRequests and enqueue
        workManager
                .beginWith(sessionPostTimeWorkRequest)
                .then(mentorshipPostTimeWorkRequest)
                .then(sessionRecommendedPostTimeWorkRequest)
                .then(rondaPostTimeWorkRequest)
                .then(userInfoUpdateRequest)
                .enqueue();

        // Return the final WorkRequest
        return userInfoUpdateRequest;
    }


    public OneTimeWorkRequest syncNowData() {
         return syncPostData();
    }

    public void syncPeriodicData() {
        Data inputData = new Data.Builder().putString("requestType", String.valueOf(Http.POST)).build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(true)
                .build();

        PeriodicWorkRequest menteesPeriodicTimeWorkRequest = new PeriodicWorkRequest.Builder(TutoredWorker.class, getApplication().getMetadataSyncInterval(), TimeUnit.HOURS)
                .addTag("PERIODIC_MENTEES_ID" + ONE_TIME_REQUEST_JOB_ID)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setInitialDelay(2, TimeUnit.HOURS).build();

        PeriodicWorkRequest userPeriodicTimeWorkRequest = new PeriodicWorkRequest.Builder(UserWorker.class, getApplication().getMetadataSyncInterval(), TimeUnit.HOURS)
                .addTag("PERIODIC_USER_ID" + ONE_TIME_REQUEST_JOB_ID)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setInitialDelay(2, TimeUnit.HOURS).build();

        PeriodicWorkRequest mentorFormsPeriodicTimeWorkRequest = new PeriodicWorkRequest.Builder(FormWorker.class, getApplication().getMetadataSyncInterval(), TimeUnit.HOURS)
                .addTag("PERIODIC_MENTOR_FORMS_ID" + ONE_TIME_REQUEST_JOB_ID)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setInitialDelay(2, TimeUnit.HOURS).build();

        PeriodicWorkRequest mentorFormsQuestionsPeriodicTimeWorkRequest = new PeriodicWorkRequest.Builder(FormQuestionWorker.class, getApplication().getMetadataSyncInterval(), TimeUnit.HOURS)
                .addTag("PERIODIC_MENTOR_FORMS_QUESTIONS_ID" + ONE_TIME_REQUEST_JOB_ID)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setInitialDelay(2, TimeUnit.HOURS).build();

        PeriodicWorkRequest mentorRondasPeriodicTimeWorkRequest = new PeriodicWorkRequest.Builder(RondaWorker.class, getApplication().getMetadataSyncInterval(), TimeUnit.HOURS)
                .addTag("PERIODIC_MENTOR_RONDAS_ID" + ONE_TIME_REQUEST_JOB_ID)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setInitialDelay(2, TimeUnit.HOURS).build();

        PeriodicWorkRequest sessionPeriodicTimeWorkRequest = new PeriodicWorkRequest.Builder(SessionPOSTWorker.class, getApplication().getMetadataSyncInterval(), TimeUnit.HOURS)
                .addTag("PERIODIC_SESSION_ID" + ONE_TIME_REQUEST_JOB_ID)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setInitialDelay(2, TimeUnit.HOURS).build();

        PeriodicWorkRequest mentorshipPeriodicWorkRequest = new PeriodicWorkRequest.Builder(MentorshipWorker.class, getApplication().getSessionSyncInterval(), TimeUnit.HOURS)
                .addTag("PERIODIC_MENTORSHIPS_ID" + ONE_TIME_REQUEST_JOB_ID)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setInitialDelay(2, TimeUnit.HOURS).build();

        PeriodicWorkRequest sessionPeriodicWorkRequest = new PeriodicWorkRequest.Builder(SessionGETWorker.class, getApplication().getSessionSyncInterval(), TimeUnit.HOURS)
                .addTag("ONE_TIME_SESSIONS_ID" + ONE_TIME_REQUEST_JOB_ID)
                .setInputData(inputData)
                .setInitialDelay(2, TimeUnit.HOURS).build();


        PeriodicWorkRequest sessionRecommendedResourcePeriodicWorkRequest = new PeriodicWorkRequest.Builder(SessionRecommendedResourceWorker.class, 1, TimeUnit.HOURS)
                .addTag("ONE_TIME_SESSIONS_RECOMMENDED_RESOURCES_ID" + ONE_TIME_REQUEST_JOB_ID)
                .setInputData(inputData)
                .setInitialDelay(2, TimeUnit.HOURS)
                .build();

        workManager.enqueueUniquePeriodicWork("PERIODIC_MENTEES_ID", ExistingPeriodicWorkPolicy.KEEP, menteesPeriodicTimeWorkRequest);
        workManager.enqueueUniquePeriodicWork("PERIODIC_MENTOR_FORMS_ID", ExistingPeriodicWorkPolicy.KEEP, mentorFormsPeriodicTimeWorkRequest);
        workManager.enqueueUniquePeriodicWork("PERIODIC_MENTOR_FORMS_QUESTIONS_ID", ExistingPeriodicWorkPolicy.KEEP, mentorFormsQuestionsPeriodicTimeWorkRequest);
        workManager.enqueueUniquePeriodicWork("PERIODIC_MENTOR_RONDAS_ID", ExistingPeriodicWorkPolicy.KEEP, mentorRondasPeriodicTimeWorkRequest);
        workManager.enqueueUniquePeriodicWork("PERIODIC_SESSION_ID", ExistingPeriodicWorkPolicy.KEEP, sessionPeriodicTimeWorkRequest);
        workManager.enqueueUniquePeriodicWork("PERIODIC_MENTORSHIPS_ID", ExistingPeriodicWorkPolicy.KEEP, mentorshipPeriodicWorkRequest);
        workManager.enqueueUniquePeriodicWork("PERIODIC_SESSIONS_ID", ExistingPeriodicWorkPolicy.KEEP, sessionPeriodicWorkRequest);
        workManager.enqueueUniquePeriodicWork("PERIODIC_SESSIONS_RECOMMENDED_RESOURCES_ID", ExistingPeriodicWorkPolicy.KEEP, sessionRecommendedResourcePeriodicWorkRequest);
        workManager.enqueueUniquePeriodicWork("PERIODIC_USER_ID", ExistingPeriodicWorkPolicy.KEEP, userPeriodicTimeWorkRequest);

    }

    public MentoringApplication getApplication() {
        return application;
    }

    public void setApplication(MentoringApplication application) {
        this.application = application;
    }
}
