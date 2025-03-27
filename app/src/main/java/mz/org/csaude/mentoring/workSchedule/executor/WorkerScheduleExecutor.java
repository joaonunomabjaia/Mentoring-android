package mz.org.csaude.mentoring.workSchedule.executor;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.util.Http;
import mz.org.csaude.mentoring.workSchedule.work.TriggerWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETCabinetWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETDistrictWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETDoorWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETEvaluationLocationWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETEvaluationTypeWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETFormSectionQuestionWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETFormWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETHealthFacilityWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETIterationTypeWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETPartnerWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETProfessionalCategoryWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETProgramWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETProgrammaticAreaWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETProvinceWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETResourceworker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETResponseTypeWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETRondaTypeWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETRondaWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETSectionWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETSessionStatusWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETTutorProgrammaticAreaWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETTutorWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETTutoredWorker;
import mz.org.csaude.mentoring.workSchedule.work.post.PATCHUserWorker;
import mz.org.csaude.mentoring.workSchedule.work.post.POSTMentorshipWorker;
import mz.org.csaude.mentoring.workSchedule.work.post.POSTRondaWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETSessionWorker;
import mz.org.csaude.mentoring.workSchedule.work.post.POSTSessionWorker;
import mz.org.csaude.mentoring.workSchedule.work.post.POSTSessionRecommendedResourceWorker;
import mz.org.csaude.mentoring.workSchedule.work.post.POSTTutoredWorker;
import mz.org.csaude.mentoring.workSchedule.work.get.GETUserWorker;

public class WorkerScheduleExecutor {

    private static final String TAG = "WorkerScheduler";
    private static final long ONE_TIME_REQUEST_JOB_ID = System.currentTimeMillis();

    private static WorkerScheduleExecutor instance;

    private final WorkManager workManager;
    private final MentoringApplication application;

    private WorkerScheduleExecutor(Application application) {
        this.application = (MentoringApplication) application;
        this.workManager = WorkManager.getInstance(application);
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
        OneTimeWorkRequest provinceWorkRequest = new OneTimeWorkRequest.Builder(GETProvinceWorker.class)
                .addTag("INITIAL_SYNC_PROVINCE_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest districtWorkRequest = new OneTimeWorkRequest.Builder(GETDistrictWorker.class)
                .addTag("INITIAL_SYNC_DISTRICT_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest sectionWorkRequest = new OneTimeWorkRequest.Builder(GETSectionWorker.class)
                .addTag("INITIAL_SYNC_SECTION_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest professionalCategoryWorkRequest = new OneTimeWorkRequest.Builder(GETProfessionalCategoryWorker.class)
                .addTag("INITIAL_SYNC_PROFESSIONAL_CATEGORY_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest partnerWorkRequest = new OneTimeWorkRequest.Builder(GETPartnerWorker.class)
                .addTag("INITIAL_SYNC_PARTNER_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest rondaTypeWorkRequest = new OneTimeWorkRequest.Builder(GETRondaTypeWorker.class)
                .addTag("INITIAL_SYNC_RONDA_TYPE_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest responseTypeWorkRequest = new OneTimeWorkRequest.Builder(GETResponseTypeWorker.class)
                .addTag("INITIAL_SYNC_RESPONSE_TYPE_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest evaluationLocationWorkRequest = new OneTimeWorkRequest.Builder(GETEvaluationLocationWorker.class)
                .addTag("INITIAL_SYNC_EVALUATION_LOCATION_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest evaluationTypeWorkRequest = new OneTimeWorkRequest.Builder(GETEvaluationTypeWorker.class)
                .addTag("INITIAL_SYNC_EVALUATION_TYPE_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest iterationTypeWorkRequest = new OneTimeWorkRequest.Builder(GETIterationTypeWorker.class)
                .addTag("INITIAL_SYNC_ITERATION_TYPE_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest doorWorkRequest = new OneTimeWorkRequest.Builder(GETDoorWorker.class)
                .addTag("INITIAL_SYNC_DOOR_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest cabinetWorkRequest = new OneTimeWorkRequest.Builder(GETCabinetWorker.class)
                .addTag("INITIAL_SYNC_CABINET_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest sessionStatusWorkRequest = new OneTimeWorkRequest.Builder(GETSessionStatusWorker.class)
                .addTag("INITIAL_SYNC_SESSION_STATUS_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest programWorkRequest = new OneTimeWorkRequest.Builder(GETProgramWorker.class)
                .addTag("INITIAL_SYNC_PROGRAM_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest programmaticAreaWorkRequest = new OneTimeWorkRequest.Builder(GETProgrammaticAreaWorker.class)
                .addTag("INITIAL_SYNC_PROGRAMMATIC_AREA_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        // Chain WorkRequests
        workManager.beginUniqueWork("INITIAL_APP_SETUP", ExistingWorkPolicy.KEEP, provinceWorkRequest)
                .then(Arrays.asList(districtWorkRequest, partnerWorkRequest, cabinetWorkRequest))
                .then(Arrays.asList(
                        rondaTypeWorkRequest,
                        sectionWorkRequest,
                        responseTypeWorkRequest,
                        evaluationTypeWorkRequest,
                        evaluationLocationWorkRequest,
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
        OneTimeWorkRequest tutorWorkRequest = new OneTimeWorkRequest.Builder(GETTutorWorker.class)
                .addTag("AFTER_LOGIN_SYNC_TUTOR_" + ONE_TIME_REQUEST_JOB_ID)
                .build();
        workManager.enqueue(tutorWorkRequest);
        return tutorWorkRequest;
    }

    /**
     * Downloads data specific to the mentor after login.
     */
    public OneTimeWorkRequest downloadMentorData() {
        OneTimeWorkRequest formWorkRequest = new OneTimeWorkRequest.Builder(GETFormWorker.class)
                .addTag("MENTOR_DATA_FORM_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest healthFacilityWorkRequest = new OneTimeWorkRequest.Builder(GETHealthFacilityWorker.class)
                .addTag("MENTOR_DATA_HEALTH_FACILITY_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest tutorProgrammaticAreaWorkRequest = new OneTimeWorkRequest.Builder(GETTutorProgrammaticAreaWorker.class)
                .addTag("MENTOR_DATA_TUTOR_PROGRAMMATIC_AREA_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest formQuestionWorkRequest = new OneTimeWorkRequest.Builder(GETFormSectionQuestionWorker.class)
                .addTag("MENTOR_DATA_FORM_QUESTION_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest resourceWorkRequest = new OneTimeWorkRequest.Builder(GETResourceworker.class)
                .addTag("MENTOR_DATA_RESOURCE_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest tutoredWorkRequest = new OneTimeWorkRequest.Builder(GETTutoredWorker.class)
                .addTag("MENTOR_DATA_TUTORED_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest rondaWorkRequest = new OneTimeWorkRequest.Builder(GETRondaWorker.class)
                .addTag("MENTOR_DATA_RONDA_" + ONE_TIME_REQUEST_JOB_ID)
                .build();

        OneTimeWorkRequest sessionGetWorkRequest = new OneTimeWorkRequest.Builder(GETSessionWorker.class)
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

        OneTimeWorkRequest tutoredWorkRequest = new OneTimeWorkRequest.Builder(POSTTutoredWorker.class)
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

    public OneTimeWorkRequest syncNowMeteData() {
        return chainMetaDataSyncWorkers();
    }

    /**
     * Schedules periodic data synchronization tasks.
     */
    public void schedulePeriodicDataSync() {
        // Define constraints for periodic work
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Schedule each task as a PeriodicWorkRequest
        schedulePeriodicWorker(POSTTutoredWorker.class, "PERIODIC_SYNC_TUTORED", constraints);
        schedulePeriodicWorker(POSTRondaWorker.class, "PERIODIC_SYNC_RONDA", constraints);
        schedulePeriodicWorker(POSTSessionWorker.class, "PERIODIC_SYNC_SESSION",  constraints);
        schedulePeriodicWorker(POSTMentorshipWorker.class, "PERIODIC_SYNC_MENTORSHIP",  constraints);
        schedulePeriodicWorker(POSTSessionRecommendedResourceWorker.class, "PERIODIC_SYNC_SESSION_RECOMMENDED", constraints);
        schedulePeriodicWorker(PATCHUserWorker.class, "PERIODIC_SYNC_USER", constraints);
    }

    private void schedulePeriodicWorker(
            Class<? extends BaseWorker> workerClass, String uniqueWorkName, Constraints constraints) {

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(workerClass, getApplication().getMetadataSyncInterval(), TimeUnit.HOURS)
                .addTag(uniqueWorkName + "_" + UUID.randomUUID())
                .setConstraints(constraints)
                .setInitialDelay(4, TimeUnit.HOURS)
                .build();

        workManager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWorkRequest
        );
    }

    /**
     * Cancels any scheduled periodic synchronization tasks.
     */
    public void cancelPeriodicSync() {
        workManager.cancelUniqueWork("PERIODIC_SYNC");
    }

    public OneTimeWorkRequest syncUserFromServer() {
        OneTimeWorkRequest userInfoUpdateWorkRequest = new OneTimeWorkRequest.Builder(GETUserWorker.class)
                .addTag("USER_INFO_UPDATE_" + UUID.randomUUID())
                .build();

        workManager.enqueue(userInfoUpdateWorkRequest);
        return  userInfoUpdateWorkRequest;
    }
    /**
     * Internal method to sync data by posting to the server.
     */
    private OneTimeWorkRequest syncPostData() {
        String jobId = "SYNC_POST_DATA_" + System.currentTimeMillis();

        Data inputData = new Data.Builder()
                .putString("requestType", String.valueOf(Http.POST))
                .build();

        OneTimeWorkRequest sessionPostWorkRequest = new OneTimeWorkRequest.Builder(POSTSessionWorker.class)
                .addTag("SESSION_POST_" + jobId)
                .build();

        OneTimeWorkRequest mentorshipPostWorkRequest = new OneTimeWorkRequest.Builder(POSTMentorshipWorker.class)
                .addTag("MENTORSHIP_POST_" + jobId)
                .setInputData(inputData)
                .build();

        OneTimeWorkRequest sessionRecommendedWorkRequest = new OneTimeWorkRequest.Builder(POSTSessionRecommendedResourceWorker.class)
                .addTag("SESSION_RECOMMENDED_POST_" + jobId)
                .setInputData(inputData)
                .build();

        OneTimeWorkRequest rondaPostWorkRequest = new OneTimeWorkRequest.Builder(POSTRondaWorker.class)
                .addTag("RONDA_POST_" + jobId)
                .setInputData(inputData)
                .build();

        OneTimeWorkRequest userInfoUpdateWorkRequest = new OneTimeWorkRequest.Builder(GETUserWorker.class)
                .addTag("USER_INFO_UPDATE_" + jobId)
                .build();

        OneTimeWorkRequest tutoredUpdateWorkRequest = new OneTimeWorkRequest.Builder(POSTTutoredWorker.class)
                .addTag("MENTEE_UPDATE_" + jobId)
                .setInputData(inputData)
                .build();

        OneTimeWorkRequest userUpdateWorkRequest = new OneTimeWorkRequest.Builder(PATCHUserWorker.class)
                .addTag("USER_INFO_UPDATE_" + jobId)
                .build();


        // Chain WorkRequests
        workManager.beginUniqueWork("   MENTORING_SYNC_NOW_DATA", ExistingWorkPolicy.REPLACE, rondaPostWorkRequest)
                .then(sessionPostWorkRequest)
                .then(mentorshipPostWorkRequest)
                .then(sessionRecommendedWorkRequest)
                .then(tutoredUpdateWorkRequest)
                .then(userInfoUpdateWorkRequest)
                .then(userUpdateWorkRequest)
                .enqueue();

        return userUpdateWorkRequest;
    }

    public MentoringApplication getApplication() {
        return application;
    }

    public OneTimeWorkRequest schedulePeriodicMetaDataSync() {
        // Define constraints for the periodic work
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Create the periodic trigger worker to run at regular intervals
        PeriodicWorkRequest periodicTrigger = new PeriodicWorkRequest.Builder(
                TriggerWorker.class, getApplication().getMetadataSyncInterval(), TimeUnit.HOURS)
                .addTag("PERIODIC_TRIGGER_" + UUID.randomUUID())
                .setConstraints(constraints)
                .setInitialDelay(5, TimeUnit.HOURS)
                .build();

        // Enqueue the periodic trigger worker
        workManager.enqueueUniquePeriodicWork(
                "PERIODIC_TRIGGER_WORK",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicTrigger
        );

        // Chain the metadata sync workers
        return chainMetaDataSyncWorkers();
    }

    @SuppressLint("EnqueueWork")
    private OneTimeWorkRequest chainMetaDataSyncWorkers() {
        // Create the initial trigger worker request
        OneTimeWorkRequest triggerWorkerRequest = createOneTimeWorkRequest(TriggerWorker.class, "TRIGGER_WORKER");

        // Create OneTimeWorkRequests for metadata sync tasks
        OneTimeWorkRequest formWorkerRequest = createOneTimeWorkRequest(GETFormWorker.class, "SYNC_FORM");
        OneTimeWorkRequest formQuestionWorkerRequest = createOneTimeWorkRequest(GETFormSectionQuestionWorker.class, "SYNC_FORM_QUESTION");
        OneTimeWorkRequest provinceWorkRequest = createOneTimeWorkRequest(GETProvinceWorker.class, "SYNC_PROVINCE");
        OneTimeWorkRequest districtWorkRequest = createOneTimeWorkRequest(GETDistrictWorker.class, "SYNC_DISTRICT");

        // Create parallel tasks as a list
        List<OneTimeWorkRequest> parallelTasks = Arrays.asList(
                createOneTimeWorkRequest(GETSectionWorker.class, "SYNC_SECTION"),
                createOneTimeWorkRequest(GETProfessionalCategoryWorker.class, "SYNC_PROFESSIONAL_CATEGORY"),
                createOneTimeWorkRequest(GETPartnerWorker.class, "SYNC_PARTNER"),
                createOneTimeWorkRequest(GETRondaTypeWorker.class, "SYNC_RONDA_TYPE"),
                createOneTimeWorkRequest(GETResponseTypeWorker.class, "SYNC_RESPONSE_TYPE"),
                createOneTimeWorkRequest(GETEvaluationTypeWorker.class, "SYNC_EVALUATION_TYPE"),
                createOneTimeWorkRequest(GETEvaluationLocationWorker.class, "SYNC_EVALUATION_LOCATION"),
                createOneTimeWorkRequest(GETIterationTypeWorker.class, "SYNC_ITERATION_TYPE"),
                createOneTimeWorkRequest(GETDoorWorker.class, "SYNC_DOOR"),
                createOneTimeWorkRequest(GETCabinetWorker.class, "SYNC_CABINET"),
                createOneTimeWorkRequest(GETSessionStatusWorker.class, "SYNC_SESSION_STATUS"),
                createOneTimeWorkRequest(GETProgramWorker.class, "SYNC_PROGRAM"),
                createOneTimeWorkRequest(GETProgrammaticAreaWorker.class, "SYNC_PROGRAMMATIC_AREA")
        );

        // Add new workers at the end
        OneTimeWorkRequest healthFacilityWorkerRequest = createOneTimeWorkRequest(GETHealthFacilityWorker.class, "SYNC_HEALTH_FACILITY");
        OneTimeWorkRequest tutorProgrammaticAreaWorkerRequest = createOneTimeWorkRequest(GETTutorProgrammaticAreaWorker.class, "SYNC_TUTOR_PROGRAMMATIC_AREA");
        OneTimeWorkRequest resourceWorkerRequest = createOneTimeWorkRequest(GETResourceworker.class, "SYNC_RESOURCE");
        OneTimeWorkRequest tutorWorkerRequest = createOneTimeWorkRequest(GETTutorWorker.class, "SYNC_MENTOR");

        // Start the chain with the trigger worker
        WorkContinuation workChain = workManager.beginUniqueWork(
                        "META_DATA_SYNC_CHAIN",
                        ExistingWorkPolicy.REPLACE,
                        triggerWorkerRequest
                ).then(formWorkerRequest)
                .then(formQuestionWorkerRequest)
                .then(provinceWorkRequest)
                .then(districtWorkRequest);

        // Add the parallel tasks to the chain using .then() with a list
        workChain = workChain.then(parallelTasks);

        // Continue with sequential tasks after parallel tasks
        workChain = workChain
                .then(healthFacilityWorkerRequest)
                .then(tutorProgrammaticAreaWorkerRequest)
                .then(resourceWorkerRequest)
                .then(tutorWorkerRequest);

        // Enqueue the entire chain
        workChain.enqueue();
        return tutorWorkerRequest;
    }

    private OneTimeWorkRequest createOneTimeWorkRequest(Class<? extends BaseWorker> workerClass, String tag) {
        return new OneTimeWorkRequest.Builder(workerClass)
                .addTag(tag + "_" + UUID.randomUUID())  // Add a unique tag
                .build();
    }





}
