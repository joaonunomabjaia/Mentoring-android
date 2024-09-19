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
import mz.org.csaude.mentoring.workSchedule.work.SessionGETWorker;
import mz.org.csaude.mentoring.workSchedule.work.SessionPOSTWorker;
import mz.org.csaude.mentoring.workSchedule.work.SessionRecommendedResourceWorker;
import mz.org.csaude.mentoring.workSchedule.work.SessionStatusWorker;
import mz.org.csaude.mentoring.workSchedule.work.TutorProgrammaticAreaWorker;
import mz.org.csaude.mentoring.workSchedule.work.TutorWorker;
import mz.org.csaude.mentoring.workSchedule.work.TutoredWorker;
import mz.org.csaude.mentoring.workSchedule.work.UserWorker;

public class WorkerScheduleExecutor {

    private static final String TAG = "WorkerScheduler";
    private static final long ONE_TIME_REQUEST_JOB_ID = System.currentTimeMillis();

    private static WorkerScheduleExecutor instance;

    private final WorkManager workManager;
    private final MentoringApplication application;
    private final SharedPreferences encryptedSharedPreferences;

    private WorkerScheduleExecutor(Application application) {
        this.application = (MentoringApplication) application;
        this.workManager = WorkManager.getInstance(application);
        this.encryptedSharedPreferences = this.application.getEncryptedSharedPreferences();
    }

    public static synchronized WorkerScheduleExecutor getInstance(Application application) {
        if (instance == null) {
            instance = new WorkerScheduleExecutor(application);
        }
        return instance;
    }

    public WorkManager getWorkManager() {
        return workManager;
    }

    /**
     * Runs the initial synchronization tasks required for the application setup.
     */
    public OneTimeWorkRequest runInitialSync() {
        // Create individual WorkRequests
        OneTimeWorkRequest provinceWorkRequest = new OneTimeWorkRequest.Builder(ProvinceWorker.class)
                .addTag("INITIAL_SYNC_PROVINCE_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest districtWorkRequest = new OneTimeWorkRequest.Builder(DistrictWorker.class)
                .addTag("INITIAL_SYNC_DISTRICT_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest professionalCategoryWorkRequest = new OneTimeWorkRequest.Builder(ProfessionalCategoryWorker.class)
                .addTag("INITIAL_SYNC_PROFESSIONAL_CATEGORY_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest partnerWorkRequest = new OneTimeWorkRequest.Builder(PartnerWorker.class)
                .addTag("INITIAL_SYNC_PARTNER_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest rondaTypeWorkRequest = new OneTimeWorkRequest.Builder(RondaTypeWorker.class)
                .addTag("INITIAL_SYNC_RONDA_TYPE_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest responseTypeWorkRequest = new OneTimeWorkRequest.Builder(ResponseTypeWorker.class)
                .addTag("INITIAL_SYNC_RESPONSE_TYPE_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest evaluationTypeWorkRequest = new OneTimeWorkRequest.Builder(EvaluationTypeWorker.class)
                .addTag("INITIAL_SYNC_EVALUATION_TYPE_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest iterationTypeWorkRequest = new OneTimeWorkRequest.Builder(IterationTypeWorker.class)
                .addTag("INITIAL_SYNC_ITERATION_TYPE_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest doorWorkRequest = new OneTimeWorkRequest.Builder(DoorWorker.class)
                .addTag("INITIAL_SYNC_DOOR_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest cabinetWorkRequest = new OneTimeWorkRequest.Builder(CabinetWorker.class)
                .addTag("INITIAL_SYNC_CABINET_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest sessionStatusWorkRequest = new OneTimeWorkRequest.Builder(SessionStatusWorker.class)
                .addTag("INITIAL_SYNC_SESSION_STATUS_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest programWorkRequest = new OneTimeWorkRequest.Builder(ProgramWorker.class)
                .addTag("INITIAL_SYNC_PROGRAM_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest programmaticAreaWorkRequest = new OneTimeWorkRequest.Builder(ProgrammaticAreaWorker.class)
                .addTag("INITIAL_SYNC_PROGRAMMATIC_AREA_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        // Chain WorkRequests
        workManager.beginUniqueWork("INITIAL_APP_SETUP", ExistingWorkPolicy.KEEP, provinceWorkRequest)
                .then(Arrays.asList(districtWorkRequest, partnerWorkRequest, cabinetWorkRequest))
                .then(Arrays.asList(
                        rondaTypeWorkRequest,
                        responseTypeWorkRequest,
                        evaluationTypeWorkRequest,
                        iterationTypeWorkRequest,
                        doorWorkRequest,
                        sessionStatusWorkRequest,
                        programWorkRequest
                ))
                .then(programmaticAreaWorkRequest)
                .then(professionalCategoryWorkRequest)
                .enqueue();

        return professionalCategoryWorkRequest;
    }

    /**
     * Runs synchronization tasks that should occur after the user logs in.
     */
    public OneTimeWorkRequest runPostLoginSync() {
        OneTimeWorkRequest tutorWorkRequest = new OneTimeWorkRequest.Builder(TutorWorker.class)
                .addTag("POST_LOGIN_SYNC_TUTOR_" + ONE_TIME_REQUEST_JOB_ID)
                .build();
        workManager.enqueue(tutorWorkRequest);
        return tutorWorkRequest;
    }

    /**
     * Downloads data specific to the mentor after login.
     */
    public OneTimeWorkRequest downloadMentorData() {
        OneTimeWorkRequest formWorkRequest = new OneTimeWorkRequest.Builder(FormWorker.class)
                .addTag("MENTOR_DATA_FORM_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest healthFacilityWorkRequest = new OneTimeWorkRequest.Builder(HealthFacilityWorker.class)
                .addTag("MENTOR_DATA_HEALTH_FACILITY_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest tutorProgrammaticAreaWorkRequest = new OneTimeWorkRequest.Builder(TutorProgrammaticAreaWorker.class)
                .addTag("MENTOR_DATA_TUTOR_PROGRAMMATIC_AREA_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest formQuestionWorkRequest = new OneTimeWorkRequest.Builder(FormQuestionWorker.class)
                .addTag("MENTOR_DATA_FORM_QUESTION_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest resourceWorkRequest = new OneTimeWorkRequest.Builder(Resourceworker.class)
                .addTag("MENTOR_DATA_RESOURCE_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest tutoredWorkRequest = new OneTimeWorkRequest.Builder(TutoredWorker.class)
                .addTag("MENTOR_DATA_TUTORED_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest rondaWorkRequest = new OneTimeWorkRequest.Builder(RondaWorker.class)
                .addTag("MENTOR_DATA_RONDA_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest sessionGetWorkRequest = new OneTimeWorkRequest.Builder(SessionGETWorker.class)
                .addTag("MENTOR_DATA_SESSION_GET_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        // Chain WorkRequests
        workManager.beginUniqueWork("INITIAL_MENTOR_DATA_SETUP", ExistingWorkPolicy.KEEP, formWorkRequest)
                .then(healthFacilityWorkRequest)
                .then(tutorProgrammaticAreaWorkRequest)
                .then(formQuestionWorkRequest)
                .then(resourceWorkRequest)
                .then(tutoredWorkRequest)
                .then(rondaWorkRequest)
                .then(sessionGetWorkRequest)
                .enqueue();

        return sessionGetWorkRequest;
    }

    /**
     * Uploads mentees data to the server.
     */
    public OneTimeWorkRequest uploadMentees() {
        Data inputData = new Data.Builder()
                .putString("requestType", String.valueOf(Http.POST))
                .build();

        OneTimeWorkRequest tutoredWorkRequest = new OneTimeWorkRequest.Builder(TutoredWorker.class)
                .addTag("UPLOAD_MENTEES_" + ONE_TIME_REQUEST_JOB_ID)
                .setInputData(inputData)
                .build();

        workManager.enqueue(tutoredWorkRequest);
        return tutoredWorkRequest;
    }

    /**
     * Synchronizes data immediately by posting to the server.
     */
    public OneTimeWorkRequest syncNowData() {
        return syncPostData();
    }

    /**
     * Schedules periodic data synchronization tasks.
     */
    public void syncPeriodicData() {
        Data inputData = new Data.Builder()
                .putString("requestType", String.valueOf(Http.POST))
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Create PeriodicWorkRequests
        PeriodicWorkRequest menteesPeriodicWorkRequest = new PeriodicWorkRequest.Builder(TutoredWorker.class, application.getMetadataSyncInterval(), TimeUnit.HOURS)
                .addTag("PERIODIC_SYNC_MENTEES_" + ONE_TIME_REQUEST_JOB_ID)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();

        PeriodicWorkRequest userPeriodicWorkRequest = new PeriodicWorkRequest.Builder(UserWorker.class, application.getMetadataSyncInterval(), TimeUnit.HOURS)
                .addTag("PERIODIC_SYNC_USER_" + ONE_TIME_REQUEST_JOB_ID)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();

        // Enqueue PeriodicWorkRequests
        workManager.enqueueUniquePeriodicWork("PERIODIC_SYNC_MENTEES", ExistingPeriodicWorkPolicy.KEEP, menteesPeriodicWorkRequest);
        workManager.enqueueUniquePeriodicWork("PERIODIC_SYNC_USER", ExistingPeriodicWorkPolicy.KEEP, userPeriodicWorkRequest);

        // Add additional periodic sync tasks as needed...
    }

    /**
     * Schedules periodic synchronization based on the provided interval.
     *
     * @param intervalMinutes The interval in minutes for the periodic sync.
     */
    public void schedulePeriodicSync(int intervalMinutes) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicSyncWorkRequest = new PeriodicWorkRequest.Builder(SessionPOSTWorker.class, intervalMinutes, TimeUnit.MINUTES)
                .addTag("PERIODIC_SYNC")
                .setConstraints(constraints)
                .build();

        workManager.enqueueUniquePeriodicWork("PERIODIC_SYNC", ExistingPeriodicWorkPolicy.REPLACE, periodicSyncWorkRequest);
    }

    /**
     * Cancels any scheduled periodic synchronization tasks.
     */
    public void cancelPeriodicSync() {
        workManager.cancelUniqueWork("PERIODIC_SYNC");
    }

    /**
     * Internal method to sync data by posting to the server.
     */
    private OneTimeWorkRequest syncPostData() {
        String jobId = "SYNC_POST_DATA_" + System.currentTimeMillis();

        Data inputData = new Data.Builder()
                .putString("requestType", String.valueOf(Http.POST))
                .build();

        OneTimeWorkRequest sessionPostWorkRequest = new OneTimeWorkRequest.Builder(SessionPOSTWorker.class)
                .addTag("SESSION_POST_" + jobId)
                .build();

        OneTimeWorkRequest mentorshipPostWorkRequest = new OneTimeWorkRequest.Builder(MentorshipWorker.class)
                .addTag("MENTORSHIP_POST_" + jobId)
                .setInputData(inputData)
                .build();

        OneTimeWorkRequest sessionRecommendedWorkRequest = new OneTimeWorkRequest.Builder(SessionRecommendedResourceWorker.class)
                .addTag("SESSION_RECOMMENDED_POST_" + jobId)
                .setInputData(inputData)
                .build();

        OneTimeWorkRequest rondaPostWorkRequest = new OneTimeWorkRequest.Builder(RondaWorker.class)
                .addTag("RONDA_POST_" + jobId)
                .setInputData(inputData)
                .build();

        OneTimeWorkRequest userInfoUpdateWorkRequest = new OneTimeWorkRequest.Builder(UserWorker.class)
                .addTag("USER_INFO_UPDATE_" + jobId)
                .build();

        // Chain WorkRequests
        workManager.beginUniqueWork("SYNC_NOW_DATA", ExistingWorkPolicy.REPLACE, sessionPostWorkRequest)
                .then(mentorshipPostWorkRequest)
                .then(sessionRecommendedWorkRequest)
                .then(rondaPostWorkRequest)
                .then(userInfoUpdateWorkRequest)
                .enqueue();

        return userInfoUpdateWorkRequest;
    }

    public MentoringApplication getApplication() {
        return application;
    }
}
