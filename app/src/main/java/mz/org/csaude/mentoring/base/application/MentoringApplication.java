package mz.org.csaude.mentoring.base.application;

import static mz.org.csaude.mentoring.util.Constants.INITIAL_SETUP_STATUS;
import static mz.org.csaude.mentoring.util.Constants.INITIAL_SETUP_STATUS_COMPLETE;
import static mz.org.csaude.mentoring.util.Constants.LAST_SYNC_DATE;
import static mz.org.csaude.mentoring.util.Constants.LOGGED_USER;
import static mz.org.csaude.mentoring.util.Constants.METADATA_SYNC_TIME;
import static mz.org.csaude.mentoring.util.Constants.SESSION_SYNC_TIME;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.auth.AuthInterceptorImpl;
import mz.org.csaude.mentoring.base.auth.SessionManager;
import mz.org.csaude.mentoring.common.ApplicationStep;
import mz.org.csaude.mentoring.listner.rest.ServerStatusListener;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.ProgrammaticArea.ProgrammaticAreaService;
import mz.org.csaude.mentoring.service.ProgrammaticArea.ProgrammaticAreaServiceImpl;
import mz.org.csaude.mentoring.service.ProgrammaticArea.TutorProgrammaticAreaService;
import mz.org.csaude.mentoring.service.ProgrammaticArea.TutorProgrammaticAreaServiceImpl;
import mz.org.csaude.mentoring.service.answer.AnswerService;
import mz.org.csaude.mentoring.service.answer.AnswerServiceImpl;
import mz.org.csaude.mentoring.service.employee.EmployeeService;
import mz.org.csaude.mentoring.service.employee.EmployeeServiceImpl;
import mz.org.csaude.mentoring.service.evaluationType.EvaluationTypeService;
import mz.org.csaude.mentoring.service.evaluationType.EvaluationTypeServiceImpl;
import mz.org.csaude.mentoring.service.form.FormService;
import mz.org.csaude.mentoring.service.form.FormServiceImpl;
import mz.org.csaude.mentoring.service.formQuestion.FormQuestionService;
import mz.org.csaude.mentoring.service.formQuestion.FormQuestionServiceImpl;
import mz.org.csaude.mentoring.service.location.CabinetService;
import mz.org.csaude.mentoring.service.location.CabinetServiceImpl;
import mz.org.csaude.mentoring.service.location.DistrictService;
import mz.org.csaude.mentoring.service.location.DistrictServiceImpl;
import mz.org.csaude.mentoring.service.location.HealthFacilityService;
import mz.org.csaude.mentoring.service.location.HealthFacilityServiceImpl;
import mz.org.csaude.mentoring.service.location.LocationService;
import mz.org.csaude.mentoring.service.location.LocationServiceImpl;
import mz.org.csaude.mentoring.service.location.ProvinceService;
import mz.org.csaude.mentoring.service.location.ProvinceServiceImpl;
import mz.org.csaude.mentoring.service.mentorship.DoorService;
import mz.org.csaude.mentoring.service.mentorship.DoorServiceImpl;
import mz.org.csaude.mentoring.service.mentorship.IterationTypeService;
import mz.org.csaude.mentoring.service.mentorship.IterationTypeServiceImpl;
import mz.org.csaude.mentoring.service.mentorship.MentorshipService;
import mz.org.csaude.mentoring.service.mentorship.MentorshipServiceImpl;
import mz.org.csaude.mentoring.service.mentorship.TimeOfDayService;
import mz.org.csaude.mentoring.service.mentorship.TimeOfDayServiceImpl;
import mz.org.csaude.mentoring.service.partner.PartnerService;
import mz.org.csaude.mentoring.service.partner.PartnerServiceImpl;
import mz.org.csaude.mentoring.service.professionalCategory.ProfessionalCategoryService;
import mz.org.csaude.mentoring.service.professionalCategory.ProfessionalCategoryServiceImpl;
import mz.org.csaude.mentoring.service.program.ProgramService;
import mz.org.csaude.mentoring.service.program.ProgramServiceImpl;
import mz.org.csaude.mentoring.service.question.QuestionService;
import mz.org.csaude.mentoring.service.question.QuestionServiceImpl;
import mz.org.csaude.mentoring.service.question.QuestionsCategoryService;
import mz.org.csaude.mentoring.service.question.QuestionsCategoryServiceImpl;
import mz.org.csaude.mentoring.service.resource.ResourceService;
import mz.org.csaude.mentoring.service.resource.ResourceServiceImpl;
import mz.org.csaude.mentoring.service.responseType.ResponseTypeService;
import mz.org.csaude.mentoring.service.responseType.ResponseTypeServiceImpl;
import mz.org.csaude.mentoring.service.ronda.RondaMenteeService;
import mz.org.csaude.mentoring.service.ronda.RondaMenteeServiceImpl;
import mz.org.csaude.mentoring.service.ronda.RondaMentorService;
import mz.org.csaude.mentoring.service.ronda.RondaMentorServiceImpl;
import mz.org.csaude.mentoring.service.ronda.RondaService;
import mz.org.csaude.mentoring.service.ronda.RondaServiceImpl;
import mz.org.csaude.mentoring.service.ronda.RondaTypeService;
import mz.org.csaude.mentoring.service.ronda.RondaTypeServiceImpl;
import mz.org.csaude.mentoring.service.session.SessionService;
import mz.org.csaude.mentoring.service.session.SessionServiceImpl;
import mz.org.csaude.mentoring.service.session.SessionStatusService;
import mz.org.csaude.mentoring.service.session.SessionStatusServiceImpl;
import mz.org.csaude.mentoring.service.setting.SettingService;
import mz.org.csaude.mentoring.service.setting.SettingServiceImpl;
import mz.org.csaude.mentoring.service.tutor.TutorService;
import mz.org.csaude.mentoring.service.tutor.TutorServiceImpl;
import mz.org.csaude.mentoring.service.tutored.TutoredService;
import mz.org.csaude.mentoring.service.tutored.TutoredServiceImpl;
import mz.org.csaude.mentoring.service.user.UserService;
import mz.org.csaude.mentoring.service.user.UserServiceImpl;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.workSchedule.rest.FormQuestionRestService;
import mz.org.csaude.mentoring.workSchedule.rest.FormRestService;
import mz.org.csaude.mentoring.workSchedule.rest.MentorshipRestService;
import mz.org.csaude.mentoring.workSchedule.rest.PartnerRestService;
import mz.org.csaude.mentoring.workSchedule.rest.ResourceRestService;
import mz.org.csaude.mentoring.workSchedule.rest.RondaRestService;
import mz.org.csaude.mentoring.workSchedule.rest.ServerStatusChecker;
import mz.org.csaude.mentoring.workSchedule.rest.SessionRecommendedResourceRestService;
import mz.org.csaude.mentoring.workSchedule.rest.SessionRestService;
import mz.org.csaude.mentoring.workSchedule.rest.TutorRestService;
import mz.org.csaude.mentoring.workSchedule.rest.TutoredRestService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MentoringApplication  extends Application {

    private static MentoringApplication mInstance;
    public static final String BASE_URL = "https://mentdev.csaude.org.mz/api/";
    //public static final String BASE_URL = "http://10.10.12.72:8087/api";
    //public static final String BASE_URL = "http://10.10.2.30:8087";
    private User authenticatedUser;

    private Tutor tutor;

    private Retrofit retrofit;
    
    private ObjectMapper mapper;

    private RondaService rondaService;

    private DistrictService districtService;

    private ProvinceService provinceService;

    private SessionService sessionService;

    private TutoredService tutoredService;

    private TutorService tutorService;
    private HealthFacilityService healthFacilityService;

    private FormService formService;

    private MentorshipService mentorshipService;

    private UserService userService;

    private EmployeeService employeeService;

    private PartnerService partnerService;

    private ProfessionalCategoryService professionalCategoryService;

    private ApplicationStep applicationStep;

    private LocationService locationService;

    private TutoredRestService tutoredRestService;

    private ServerStatusChecker serverStatusChecker;
    private RondaMenteeService rondaMenteeService;
    private RondaMentorService rondaMentorService;
    private RondaTypeService rondaTypeService;
    AuthInterceptorImpl interceptor;

    SharedPreferences mentoringSharedPreferences;

    private SessionManager sessionManager;

    private DoorService doorService;

    private EvaluationTypeService evaluationTypeService;

    private FormQuestionService formQuestionService;

    private QuestionService questionService;

    private QuestionsCategoryService questionsCategoryService;

    private ResponseTypeService responseTypeService;

    private SessionStatusService sessionStatusService;

    private SettingService settingService;

    private TimeOfDayService timeOfDayService;

    private TutorProgrammaticAreaService tutorProgrammaticAreaService;

    private ProgrammaticAreaService programmaticAreaService;

    private ProgramService programService;

    private CabinetService cabinetService;

    private ResourceService resourceService;


    // Rest Services
    private PartnerRestService partnerRestService;
    private FormRestService formRestService;
    private TutorRestService tutorRestService;
    private FormQuestionRestService formQuestionRestService;
    private RondaRestService rondaRestService;
    private MentorshipRestService mentorshipRestService;
    private IterationTypeService iterationTypeService;

    private ResourceRestService resourceRestService;
    private AnswerService answerService;
    private SessionRestService sessionRestService;
    private SessionRecommendedResourceRestService sessionRecommendedResourceRestService;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        interceptor = new AuthInterceptorImpl(this);

        mapper = new ObjectMapper();

        setUpRetrofit();

        Locale.setDefault(new Locale("en_ZA"));

    }


    public static synchronized MentoringApplication getInstance() {
        return mInstance;
    }

    private void setUpRetrofit() {
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(User authenticatedUser, boolean remeberMe) {
        this.authenticatedUser = authenticatedUser;
        if (remeberMe) {
          saveUserName();
        } else {
            removeUserName();
        }
    }



    public RondaService getRondaService() {
        if (this.rondaService == null) this.rondaService = new RondaServiceImpl(this);
        return rondaService;
    }

    public DistrictService getDistrictService() {
        if (this.districtService == null) this.districtService = new DistrictServiceImpl(this);
        return districtService;
    }

    public ProvinceService getProvinceService() {
        if (this.provinceService == null) this.provinceService = new ProvinceServiceImpl(this);
        return provinceService;
    }

    public HealthFacilityService getHealthFacilityService() {
        if (this.healthFacilityService == null) this.healthFacilityService = new HealthFacilityServiceImpl(this);
        return healthFacilityService;
    }

    public SessionService getSessionService() {
        if (this.sessionService == null) this.sessionService = new SessionServiceImpl(this);
        return sessionService;
    }

    public TutorService getTutorService() {
        if (this.tutorService == null) this.tutorService = new TutorServiceImpl(this);
        return tutorService;
    }

    public TutoredService getTutoredService() {
        if (this.tutoredService == null) this.tutoredService = new TutoredServiceImpl(this);
        return tutoredService;
    }

    public MentorshipService getMentorshipService() {
        if (this.mentorshipService == null) this.mentorshipService = new MentorshipServiceImpl(this);
        return mentorshipService;
    }

    public CabinetService getCabinetService() {
        if (this.cabinetService == null) this.cabinetService = new CabinetServiceImpl(this);
        return cabinetService;
    }

    public FormService getFormService() {
        if (this.formService == null) this.formService = new FormServiceImpl(this);
        return formService;
    }

    public UserService getUserService() {
        if (this.userService == null) this.userService = new UserServiceImpl(this);
        return userService;
    }

    public EmployeeService getEmployeeService() {
        if (this.employeeService == null) this.employeeService = new EmployeeServiceImpl(this);
        return employeeService;
    }

    public PartnerService getPartnerService() {
        if (this.partnerService == null) this.partnerService = new PartnerServiceImpl(this);
        return partnerService;
    }

    public ProfessionalCategoryService getProfessionalCategoryService() {
        if (this.professionalCategoryService == null) this.professionalCategoryService = new ProfessionalCategoryServiceImpl(this);
        return professionalCategoryService;
    }

    public LocationService getLocationService() {
        if (this.locationService == null) this.locationService = new LocationServiceImpl(this);
        return locationService;
    }

    public PartnerRestService getPartnerRestService() {
        if (partnerRestService == null) this.partnerRestService = new PartnerRestService(this);
        return partnerRestService;
    }

    public TutoredRestService getTutoredRestService() {
        if (tutoredRestService == null) this.tutoredRestService = new TutoredRestService(this);
        return tutoredRestService;
    }

    public RondaMenteeService getRondaMenteeService() {
        if (this.rondaMenteeService == null) this.rondaMenteeService = new RondaMenteeServiceImpl(this);
        return rondaMenteeService;
    }

    public RondaMentorService getRondaMentorService() {
        if (this.rondaMentorService == null) this.rondaMentorService = new RondaMentorServiceImpl(this);
        return rondaMentorService;
    }
    public RondaTypeService getRondaTypeService() {
        if (this.rondaTypeService == null) this.rondaTypeService = new RondaTypeServiceImpl(this);
        return rondaTypeService;
    }
    public FormRestService getFormRestService() {
        if (formRestService == null) this.formRestService = new FormRestService(this);
        return formRestService;
    }

    public TutorRestService getTutorRestService() {
        if (tutorRestService == null) this.tutorRestService = new TutorRestService(this);
        return tutorRestService;
    }
    public FormQuestionRestService getFormQuestionRestService() {
        if (formQuestionRestService == null) this.formQuestionRestService = new FormQuestionRestService(this);
        return formQuestionRestService;
    }
    public RondaRestService getRondaRestService() {
        if (rondaRestService == null) this.rondaRestService = new RondaRestService(this);
        return rondaRestService;
    }
    public MentorshipRestService getMentorshipRestService() {
        if (mentorshipRestService == null) this.mentorshipRestService = new MentorshipRestService(this);
        return mentorshipRestService;
    }

    public ResourceRestService getResourceRestService(){
        if(resourceRestService == null) this.resourceRestService = new ResourceRestService(this);
        return resourceRestService;
    }

    public ProgrammaticAreaService getProgrammaticAreaService() {
        if (programmaticAreaService == null) this.programmaticAreaService = new ProgrammaticAreaServiceImpl(this);
        return programmaticAreaService;
    }

    public ProgramService getProgramService() {
        if (programService == null) this.programService = new ProgramServiceImpl(this);
        return programService;
    }
    public DoorService getDoorService() {
        if (doorService == null) this.doorService = new DoorServiceImpl(this);
        return doorService;
    }
    public EvaluationTypeService getEvaluationTypeService() {
        if (evaluationTypeService == null) this.evaluationTypeService = new EvaluationTypeServiceImpl(this);
        return evaluationTypeService;
    }
    public FormQuestionService getFormQuestionService() {
        if (formQuestionService == null) this.formQuestionService = new FormQuestionServiceImpl(this);
        return formQuestionService;
    }
    public QuestionService getQuestionService() {
        if (questionService == null) this.questionService = new QuestionServiceImpl(this);
        return questionService;
    }
    public QuestionsCategoryService getQuestionsCategoryService() {
        if (questionsCategoryService == null) this.questionsCategoryService = new QuestionsCategoryServiceImpl(this);
        return questionsCategoryService;
    }
    public ResponseTypeService getResponseTypeService() {
        if (responseTypeService == null) this.responseTypeService = new ResponseTypeServiceImpl(this);
        return responseTypeService;
    }
    public SessionStatusService getSessionStatusService() {
        if (sessionStatusService == null) this.sessionStatusService = new SessionStatusServiceImpl(this);
        return sessionStatusService;
    }
    public SettingService getSettingService() {
        if (settingService == null) this.settingService = new SettingServiceImpl(this);
        return settingService;
    }
    public TimeOfDayService getTimeOfDayService() {
        if (timeOfDayService == null) this.timeOfDayService = new TimeOfDayServiceImpl(this);
        return timeOfDayService;
    }
    public TutorProgrammaticAreaService getTutorProgrammaticAreaService() {
        if (tutorProgrammaticAreaService == null) this.tutorProgrammaticAreaService = new TutorProgrammaticAreaServiceImpl(this);
        return tutorProgrammaticAreaService;
    }
    public IterationTypeService getIterationTypeService() {
        if (iterationTypeService == null) this.iterationTypeService = new IterationTypeServiceImpl(this);
        return iterationTypeService;
    }

    public ResourceService getResourceService(){

        if (resourceService == null) this.resourceService = new ResourceServiceImpl(this);
        return resourceService;
    }
    public SessionRestService getSessionRestService() {
        if (sessionRestService == null) this.sessionRestService = new SessionRestService(this);
        return sessionRestService;
    }
    public SessionRecommendedResourceRestService getSessionRecommendedResourceRestService() {
        if (sessionRecommendedResourceRestService == null) this.sessionRecommendedResourceRestService = new SessionRecommendedResourceRestService(this);
        return sessionRecommendedResourceRestService;
    }

    public ApplicationStep getApplicationStep() {
        return this.applicationStep;
    }

    public Tutor getCurrMentor() {
        return this.tutor;
    }

    public void setCurrTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public void init() throws SQLException {
        this.applicationStep = ApplicationStep.fastCreate(ApplicationStep.STEP_INIT);
        setCurrTutor(getTutorService().getByEmployee(getAuthenticatedUser().getEmployee()));
        getCurrMentor().getEmployee().setLocations(getLocationService().getAllOfEmploee(getAuthenticatedUser().getEmployee()));
    }

    public SharedPreferences getMentoringSharedPreferences() {
        if (mentoringSharedPreferences == null) mentoringSharedPreferences = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);
        return mentoringSharedPreferences;
    }

    public ServerStatusChecker getServerStatusChecker() {
        if (serverStatusChecker == null) serverStatusChecker = new ServerStatusChecker(this);
        return serverStatusChecker;
    }

    public void isServerOnline(ServerStatusListener listener) {
        getServerStatusChecker().isServerOnline(listener);
    }

    public boolean isInitialSetupComplete() {
        String status = mentoringSharedPreferences.getString(INITIAL_SETUP_STATUS, null);
        if (!Utilities.stringHasValue(status)) return false;

        return mentoringSharedPreferences.getString(INITIAL_SETUP_STATUS, null).equals(INITIAL_SETUP_STATUS_COMPLETE);
    }

    public void setInitialSetUpComplete() {
        SharedPreferences.Editor editor = mentoringSharedPreferences.edit();
        editor.putString(INITIAL_SETUP_STATUS, INITIAL_SETUP_STATUS_COMPLETE);
        editor.apply();
    }

    private void saveUserName() {
        SharedPreferences.Editor editor = getMentoringSharedPreferences().edit();
        editor.putString(LOGGED_USER, authenticatedUser.getUserName());
        editor.apply();
    }

    public String getLastUser() {
        return getMentoringSharedPreferences().getString(LOGGED_USER, null);
    }

    private void removeUserName() {
        SharedPreferences.Editor editor = getMentoringSharedPreferences().edit();
        editor.remove(LOGGED_USER);
        editor.apply();
    }
    public void saveDefaultSyncSettings() {
        SharedPreferences.Editor editor = getMentoringSharedPreferences().edit();
        editor.putInt(SESSION_SYNC_TIME, getMentoringSharedPreferences().getInt(SESSION_SYNC_TIME, 2));
        editor.putInt(METADATA_SYNC_TIME, getMentoringSharedPreferences().getInt(METADATA_SYNC_TIME, 2));
        editor.apply();
    }
    public void saveDefaultLastSyncDate(Date date) {
        SharedPreferences.Editor editor = getMentoringSharedPreferences().edit();
        editor.putString(LAST_SYNC_DATE, getMentoringSharedPreferences().getString(LAST_SYNC_DATE,DateUtilities.getStringDateFromDate(date, DateUtilities.DATE_FORMAT)));
        editor.apply();
    }
    public void initSessionManager() {
        this.sessionManager = new SessionManager(this);
    }


    public AnswerService getAnswerService() {
        if (answerService == null) this.answerService = new AnswerServiceImpl(this);
        return answerService;
    }
    public int getMetadataSyncInterval() {
        return getMentoringSharedPreferences().getInt(METADATA_SYNC_TIME, 2);
    }
    public int getSessionSyncInterval() {
        return getMentoringSharedPreferences().getInt(SESSION_SYNC_TIME, 2);
    }


}
