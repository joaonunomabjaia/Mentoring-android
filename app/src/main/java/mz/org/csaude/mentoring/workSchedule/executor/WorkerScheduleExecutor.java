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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.util.Http;
import mz.org.csaude.mentoring.workSchedule.TaggedWorkRequest;
import mz.org.csaude.mentoring.workSchedule.work.CheckNextSessionWorker;
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
import mz.org.csaude.mentoring.workSchedule.work.get.GETSettingWorker;
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

    public OneTimeWorkRequest testNextSessionCheckNow() {
        OneTimeWorkRequest testWorkRequest = new OneTimeWorkRequest.Builder(CheckNextSessionWorker.class)
                .addTag("TEST_CHECK_NEXT_SESSION_" + UUID.randomUUID())
                .build();

        workManager.enqueue(testWorkRequest);
        return testWorkRequest;
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
    public List<TaggedWorkRequest> runInitialSync() {
        List<TaggedWorkRequest> requests = new ArrayList<>();

        TaggedWorkRequest province = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETProvinceWorker.class)
                        .addTag("INITIAL_SYNC_PROVINCE")
                        .build(),
                "INITIAL_SYNC_PROVINCE"
        );

        TaggedWorkRequest district = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETDistrictWorker.class)
                        .addTag("INITIAL_SYNC_DISTRICT")
                        .build(),
                "INITIAL_SYNC_DISTRICT"
        );

        TaggedWorkRequest section = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETSectionWorker.class)
                        .addTag("INITIAL_SYNC_SECTION")
                        .build(),
                "INITIAL_SYNC_SECTION"
        );

        TaggedWorkRequest professionalCategory = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETProfessionalCategoryWorker.class)
                        .addTag("INITIAL_SYNC_PROFESSIONAL_CATEGORY")
                        .build(),
                "INITIAL_SYNC_PROFESSIONAL_CATEGORY"
        );

        TaggedWorkRequest partner = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETPartnerWorker.class)
                        .addTag("INITIAL_SYNC_PARTNER")
                        .build(),
                "INITIAL_SYNC_PARTNER"
        );

        TaggedWorkRequest rondaType = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETRondaTypeWorker.class)
                        .addTag("INITIAL_SYNC_RONDA_TYPE")
                        .build(),
                "INITIAL_SYNC_RONDA_TYPE"
        );

        TaggedWorkRequest responseType = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETResponseTypeWorker.class)
                        .addTag("INITIAL_SYNC_RESPONSE_TYPE")
                        .build(),
                "INITIAL_SYNC_RESPONSE_TYPE"
        );

        TaggedWorkRequest evaluationLocation = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETEvaluationLocationWorker.class)
                        .addTag("INITIAL_SYNC_EVALUATION_LOCATION")
                        .build(),
                "INITIAL_SYNC_EVALUATION_LOCATION"
        );

        TaggedWorkRequest evaluationType = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETEvaluationTypeWorker.class)
                        .addTag("INITIAL_SYNC_EVALUATION_TYPE")
                        .build(),
                "INITIAL_SYNC_EVALUATION_TYPE"
        );

        TaggedWorkRequest iterationType = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETIterationTypeWorker.class)
                        .addTag("INITIAL_SYNC_ITERATION_TYPE")
                        .build(),
                "INITIAL_SYNC_ITERATION_TYPE"
        );

        TaggedWorkRequest door = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETDoorWorker.class)
                        .addTag("INITIAL_SYNC_DOOR")
                        .build(),
                "INITIAL_SYNC_DOOR"
        );

        TaggedWorkRequest cabinet = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETCabinetWorker.class)
                        .addTag("INITIAL_SYNC_CABINET")
                        .build(),
                "INITIAL_SYNC_CABINET"
        );

        TaggedWorkRequest sessionStatus = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETSessionStatusWorker.class)
                        .addTag("INITIAL_SYNC_SESSION_STATUS")
                        .build(),
                "INITIAL_SYNC_SESSION_STATUS"
        );

        TaggedWorkRequest program = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETProgramWorker.class)
                        .addTag("INITIAL_SYNC_PROGRAM")
                        .build(),
                "INITIAL_SYNC_PROGRAM"
        );

        TaggedWorkRequest programmaticArea = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETProgrammaticAreaWorker.class)
                        .addTag("INITIAL_SYNC_PROGRAMMATIC_AREA")
                        .build(),
                "INITIAL_SYNC_PROGRAMMATIC_AREA"
        );

        TaggedWorkRequest settings = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETSettingWorker.class)
                        .addTag("INITIAL_SYNC_SETTINGS")
                        .build(),
                "INITIAL_SYNC_SETTINGS"
        );

        // Build the full chain
        workManager.beginUniqueWork("INITIAL_APP_SETUP", ExistingWorkPolicy.KEEP, province.getRequest())
                .then(Arrays.asList(district.getRequest(), partner.getRequest(), cabinet.getRequest()))
                .then(Arrays.asList(
                        rondaType.getRequest(),
                        section.getRequest(),
                        responseType.getRequest(),
                        evaluationType.getRequest(),
                        evaluationLocation.getRequest(),
                        iterationType.getRequest(),
                        door.getRequest(),
                        sessionStatus.getRequest(),
                        program.getRequest(),
                        settings.getRequest()
                ))
                .then(programmaticArea.getRequest())
                .then(professionalCategory.getRequest())
                .enqueue();

        // Return all for monitoring
        requests.add(province);
        requests.add(district);
        requests.add(section);
        requests.add(professionalCategory);
        requests.add(partner);
        requests.add(rondaType);
        requests.add(responseType);
        requests.add(evaluationLocation);
        requests.add(evaluationType);
        requests.add(iterationType);
        requests.add(door);
        requests.add(cabinet);
        requests.add(sessionStatus);
        requests.add(program);
        requests.add(settings);
        requests.add(programmaticArea);

        return requests;
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
    public List<TaggedWorkRequest> downloadMentorData() {
        List<TaggedWorkRequest> requests = new ArrayList<>();

        TaggedWorkRequest formWorkRequest = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETFormWorker.class)
                        .addTag("MENTOR_DATA_FORM")
                        .build(),
                "MENTOR_DATA_FORM"
        );

        TaggedWorkRequest healthFacilityWorkRequest = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETHealthFacilityWorker.class)
                        .addTag("MENTOR_DATA_HEALTH_FACILITY")
                        .build(),
                "MENTOR_DATA_HEALTH_FACILITY"
        );

        TaggedWorkRequest tutorProgrammaticAreaWorkRequest = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETTutorProgrammaticAreaWorker.class)
                        .addTag("MENTOR_DATA_TUTOR_PROGRAMMATIC_AREA")
                        .build(),
                "MENTOR_DATA_TUTOR_PROGRAMMATIC_AREA"
        );

        TaggedWorkRequest formQuestionWorkRequest = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETFormSectionQuestionWorker.class)
                        .addTag("MENTOR_DATA_FORM_QUESTION")
                        .build(),
                "MENTOR_DATA_FORM_QUESTION"
        );

        TaggedWorkRequest resourceWorkRequest = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETResourceworker.class)
                        .addTag("MENTOR_DATA_RESOURCE")
                        .build(),
                "MENTOR_DATA_RESOURCE"
        );

        TaggedWorkRequest tutoredWorkRequest = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETTutoredWorker.class)
                        .addTag("MENTOR_DATA_TUTORED")
                        .build(),
                "MENTOR_DATA_TUTORED"
        );

        TaggedWorkRequest rondaWorkRequest = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETRondaWorker.class)
                        .addTag("MENTOR_DATA_RONDA")
                        .build(),
                "MENTOR_DATA_RONDA"
        );

        TaggedWorkRequest sessionGetWorkRequest = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETSessionWorker.class)
                        .addTag("MENTOR_DATA_SESSION_GET")
                        .build(),
                "MENTOR_DATA_SESSION_GET"
        );

        // Add all to list
        requests.add(formWorkRequest);
        requests.add(healthFacilityWorkRequest);
        requests.add(tutorProgrammaticAreaWorkRequest);
        requests.add(formQuestionWorkRequest);
        requests.add(resourceWorkRequest);
        requests.add(tutoredWorkRequest);
        requests.add(rondaWorkRequest);
        requests.add(sessionGetWorkRequest);

        // Chain and enqueue
        workManager.beginUniqueWork(
                        "INITIAL_MENTOR_DATA_SETUP",
                        ExistingWorkPolicy.REPLACE,
                        formWorkRequest.getRequest()
                )
                .then(healthFacilityWorkRequest.getRequest())
                .then(tutorProgrammaticAreaWorkRequest.getRequest())
                .then(formQuestionWorkRequest.getRequest())
                .then(resourceWorkRequest.getRequest())
                .then(tutoredWorkRequest.getRequest())
                .then(rondaWorkRequest.getRequest())
                .then(sessionGetWorkRequest.getRequest())
                .enqueue();

        return requests;
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
    public List<TaggedWorkRequest> syncNowData() {
        return syncPostData();
    }

    public List<TaggedWorkRequest> syncNowMeteData() {
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

        Constraints otherConstraints = new Constraints.Builder()
                .build();

        // Schedule each task as a PeriodicWorkRequest
        schedulePeriodicWorker(POSTTutoredWorker.class, "PERIODIC_SYNC_TUTORED", constraints);
        schedulePeriodicWorker(POSTRondaWorker.class, "PERIODIC_SYNC_RONDA", constraints);
        schedulePeriodicWorker(POSTSessionWorker.class, "PERIODIC_SYNC_SESSION",  constraints);
        schedulePeriodicWorker(POSTMentorshipWorker.class, "PERIODIC_SYNC_MENTORSHIP",  constraints);
        schedulePeriodicWorker(POSTSessionRecommendedResourceWorker.class, "PERIODIC_SYNC_SESSION_RECOMMENDED", constraints);
        schedulePeriodicWorker(PATCHUserWorker.class, "PERIODIC_SYNC_USER", constraints);
        schedulePeriodicWorker(GETSettingWorker.class, "PERIODIC_SYNC_SETTINGS", constraints);
        schedulePeriodicWorker(CheckNextSessionWorker.class, "PERIODIC_NEXT_SESSION_CHECKER", otherConstraints);
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

    private List<TaggedWorkRequest> syncPostData() {
        String jobId = "SYNC_POST_DATA_" + System.currentTimeMillis();

        Data inputData = new Data.Builder()
                .putString("requestType", String.valueOf(Http.POST))
                .build();

        TaggedWorkRequest rondaGet = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETRondaWorker.class)
                        .addTag("RONDA_GET_" + jobId)
                        .setInputData(inputData)
                        .build(),
                "GETRondaWorker"
        );

        TaggedWorkRequest rondaPost = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(POSTRondaWorker.class)
                        .addTag("RONDA_POST_" + jobId)
                        .setInputData(inputData)
                        .build(),
                "POSTRondaWorker"
        );

        TaggedWorkRequest sessionPost = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(POSTSessionWorker.class)
                        .addTag("SESSION_POST_" + jobId)
                        .build(),
                "POSTSessionWorker"
        );

        TaggedWorkRequest mentorshipPost = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(POSTMentorshipWorker.class)
                        .addTag("MENTORSHIP_POST_" + jobId)
                        .setInputData(inputData)
                        .build(),
                "POSTMentorshipWorker"
        );

        TaggedWorkRequest sessionRecommended = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(POSTSessionRecommendedResourceWorker.class)
                        .addTag("SESSION_RECOMMENDED_POST_" + jobId)
                        .setInputData(inputData)
                        .build(),
                "POSTSessionRecommendedResourceWorker"
        );

        TaggedWorkRequest tutoredUpdate = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(POSTTutoredWorker.class)
                        .addTag("MENTEE_UPDATE_" + jobId)
                        .setInputData(inputData)
                        .build(),
                "POSTTutoredWorker"
        );

        TaggedWorkRequest userInfoUpdate = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(GETUserWorker.class)
                        .addTag("USER_INFO_UPDATE_" + jobId)
                        .build(),
                "GETUserWorker"
        );

        TaggedWorkRequest userUpdate = new TaggedWorkRequest(
                new OneTimeWorkRequest.Builder(PATCHUserWorker.class)
                        .addTag("USER_INFO_UPDATE_" + jobId)
                        .build(),
                "PATCHUserWorker"
        );

        // Chain WorkRequests
        workManager.beginUniqueWork("MENTORING_SYNC_NOW_DATA", ExistingWorkPolicy.REPLACE, rondaGet.getRequest())
                .then(rondaPost.getRequest())
                .then(sessionPost.getRequest())
                .then(mentorshipPost.getRequest())
                .then(sessionRecommended.getRequest())
                .then(tutoredUpdate.getRequest())
                .then(userInfoUpdate.getRequest())
                .then(userUpdate.getRequest())
                .enqueue();

        return Arrays.asList(
                rondaPost,
                sessionPost,
                mentorshipPost,
                sessionRecommended,
                tutoredUpdate,
                userInfoUpdate,
                userUpdate
        );
    }



    public MentoringApplication getApplication() {
        return application;
    }

    public List<TaggedWorkRequest> schedulePeriodicMetaDataSync() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicTrigger = new PeriodicWorkRequest.Builder(
                TriggerWorker.class,
                getApplication().getMetadataSyncInterval(),
                TimeUnit.HOURS
        )
                .addTag("PERIODIC_TRIGGER_" + UUID.randomUUID())
                .setConstraints(constraints)
                .setInitialDelay(5, TimeUnit.HOURS)
                .build();

        workManager.enqueueUniquePeriodicWork(
                "PERIODIC_TRIGGER_WORK",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicTrigger
        );

        return chainMetaDataSyncWorkers();
    }

    @SuppressLint("EnqueueWork")
    private List<TaggedWorkRequest> chainMetaDataSyncWorkers() {
        List<TaggedWorkRequest> taggedRequests = new ArrayList<>();

        TaggedWorkRequest triggerWorker = createTaggedWorkRequest(TriggerWorker.class, "Inicializando");
        TaggedWorkRequest formWorker = createTaggedWorkRequest(GETFormWorker.class, "GETFormWorker");
        TaggedWorkRequest formQuestionWorker = createTaggedWorkRequest(GETFormSectionQuestionWorker.class, "GETFormSectionQuestionWorker");
        TaggedWorkRequest provinceWorker = createTaggedWorkRequest(GETProvinceWorker.class, "GETProvinceWorker");
        TaggedWorkRequest districtWorker = createTaggedWorkRequest(GETDistrictWorker.class, "GETDistrictWorker");

        List<TaggedWorkRequest> parallelTagged = Arrays.asList(
                createTaggedWorkRequest(GETSectionWorker.class, "GETSectionWorker"),
                createTaggedWorkRequest(GETProfessionalCategoryWorker.class, "GETProfessionalCategoryWorker"),
                createTaggedWorkRequest(GETPartnerWorker.class, "GETPartnerWorker"),
                createTaggedWorkRequest(GETRondaTypeWorker.class, "GETRondaTypeWorker"),
                createTaggedWorkRequest(GETResponseTypeWorker.class, "GETResponseTypeWorker"),
                createTaggedWorkRequest(GETEvaluationTypeWorker.class, "GETEvaluationTypeWorker"),
                createTaggedWorkRequest(GETEvaluationLocationWorker.class, "GETEvaluationLocationWorker"),
                createTaggedWorkRequest(GETIterationTypeWorker.class, "GETIterationTypeWorker"),
                createTaggedWorkRequest(GETDoorWorker.class, "GETDoorWorker"),
                createTaggedWorkRequest(GETCabinetWorker.class, "GETCabinetWorker"),
                createTaggedWorkRequest(GETSessionStatusWorker.class, "GETSessionStatusWorker"),
                createTaggedWorkRequest(GETProgramWorker.class, "GETProgramWorker"),
                createTaggedWorkRequest(GETProgrammaticAreaWorker.class, "GETProgrammaticAreaWorker"),
                createTaggedWorkRequest(GETSettingWorker.class, "GETSettingWorker")
        );

        TaggedWorkRequest healthFacilityWorker = createTaggedWorkRequest(GETHealthFacilityWorker.class, "GETHealthFacilityWorker");
        TaggedWorkRequest tutorProgrammaticAreaWorker = createTaggedWorkRequest(GETTutorProgrammaticAreaWorker.class, "GETTutorProgrammaticAreaWorker");
        TaggedWorkRequest resourceWorker = createTaggedWorkRequest(GETResourceworker.class, "GETResourceworker");
        TaggedWorkRequest tutorWorker = createTaggedWorkRequest(GETTutorWorker.class, "GETTutorWorker");

        taggedRequests.addAll(Arrays.asList(
                triggerWorker, formWorker, formQuestionWorker,
                provinceWorker, districtWorker
        ));
        taggedRequests.addAll(parallelTagged);
        taggedRequests.addAll(Arrays.asList(
                healthFacilityWorker, tutorProgrammaticAreaWorker,
                resourceWorker, tutorWorker
        ));

        WorkContinuation chain = workManager.beginUniqueWork(
                        "META_DATA_SYNC_CHAIN",
                        ExistingWorkPolicy.REPLACE,
                        triggerWorker.getRequest()
                ).then(formWorker.getRequest())
                .then(formQuestionWorker.getRequest())
                .then(provinceWorker.getRequest())
                .then(districtWorker.getRequest());

        chain = chain.then(parallelTagged.stream()
                .map(TaggedWorkRequest::getRequest)
                .collect(Collectors.toList()));

        chain = chain
                .then(healthFacilityWorker.getRequest())
                .then(tutorProgrammaticAreaWorker.getRequest())
                .then(resourceWorker.getRequest())
                .then(tutorWorker.getRequest());

        chain.enqueue();

        return taggedRequests;
    }

    private TaggedWorkRequest createTaggedWorkRequest(Class<? extends BaseWorker> workerClass, String tag) {
        String uniqueTag = tag + "_" + UUID.randomUUID();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(workerClass)
                .addTag(uniqueTag)
                .build();
        return new TaggedWorkRequest(request, tag);
    }





}
