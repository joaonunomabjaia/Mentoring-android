package mz.org.csaude.mentoring.base.application;

import static mz.org.csaude.mentoring.util.Constants.INITIAL_SETUP_STATUS;
import static mz.org.csaude.mentoring.util.Constants.INITIAL_SETUP_STATUS_COMPLETE;
import static mz.org.csaude.mentoring.util.Constants.LAST_SYNC_DATE;
import static mz.org.csaude.mentoring.util.Constants.LOGGED_USER;
import static mz.org.csaude.mentoring.util.Constants.PREF_METADATA_SYNC_TIME;
import static mz.org.csaude.mentoring.util.Constants.PREF_SELECTED_LANGUAGE;
import static mz.org.csaude.mentoring.util.Constants.PREF_SESSION_TIMEOUT;
import static mz.org.csaude.mentoring.util.Constants.PREF_SESSION_TIMEOUT_MINUTES;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import java.io.IOException;
import java.security.GeneralSecurityException;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import mz.org.csaude.mentoring.base.auth.AuthInterceptorImpl;
import mz.org.csaude.mentoring.base.auth.SessionManager;
import mz.org.csaude.mentoring.common.ApplicationStep;
import mz.org.csaude.mentoring.listner.rest.ServerStatusListener;
import mz.org.csaude.mentoring.model.employee.Employee;
import mz.org.csaude.mentoring.model.setting.Setting;
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
import mz.org.csaude.mentoring.service.evaluationLocation.EvaluationLocationService;
import mz.org.csaude.mentoring.service.evaluationLocation.EvaluationLocationServiceImpl;
import mz.org.csaude.mentoring.service.evaluationType.EvaluationTypeService;
import mz.org.csaude.mentoring.service.evaluationType.EvaluationTypeServiceImpl;
import mz.org.csaude.mentoring.service.form.FormService;
import mz.org.csaude.mentoring.service.form.FormServiceImpl;
import mz.org.csaude.mentoring.service.formSectionQuestion.FormSectionQuestionService;
import mz.org.csaude.mentoring.service.formSectionQuestion.FormSectionQuestionServiceImpl;
import mz.org.csaude.mentoring.service.fromSection.FormSectionService;
import mz.org.csaude.mentoring.service.fromSection.FormSectionServiceImpl;
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
import mz.org.csaude.mentoring.service.partner.PartnerService;
import mz.org.csaude.mentoring.service.partner.PartnerServiceImpl;
import mz.org.csaude.mentoring.service.professionalCategory.ProfessionalCategoryService;
import mz.org.csaude.mentoring.service.professionalCategory.ProfessionalCategoryServiceImpl;
import mz.org.csaude.mentoring.service.program.ProgramService;
import mz.org.csaude.mentoring.service.program.ProgramServiceImpl;
import mz.org.csaude.mentoring.service.question.QuestionService;
import mz.org.csaude.mentoring.service.question.QuestionServiceImpl;
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
import mz.org.csaude.mentoring.service.section.SectionService;
import mz.org.csaude.mentoring.service.section.SectionServiceImpl;
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
import mz.org.csaude.mentoring.util.Constants;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.NotificationHelper;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.workSchedule.executor.ExecutorThreadProvider;
import mz.org.csaude.mentoring.workSchedule.rest.FormSectionQuestionRestService;
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
    //public static final String BASE_URL = "https://mentdev.csaude.org.mz/api/";
    public static final String BASE_URL = "http://10.10.12.115:8087/api/";
    //public static final String BASE_URL = "http://192.168.1.32:8087/api/";
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

    private SessionManager sessionManager;

    private DoorService doorService;

    private EvaluationTypeService evaluationTypeService;

    private FormSectionQuestionService formSectionQuestionService;

    private QuestionService questionService;

    private ResponseTypeService responseTypeService;

    private SessionStatusService sessionStatusService;

    private SettingService settingService;


    private TutorProgrammaticAreaService tutorProgrammaticAreaService;

    private ProgrammaticAreaService programmaticAreaService;

    private ProgramService programService;

    private CabinetService cabinetService;

    private ResourceService resourceService;

    private SectionService sectionService;

    private FormSectionService formSectionService;

    private EvaluationLocationService evaluationLocationService;




    // Rest Services
    private PartnerRestService partnerRestService;
    private FormRestService formRestService;
    private TutorRestService tutorRestService;
    private FormSectionQuestionRestService formSectionQuestionRestService;
    private RondaRestService rondaRestService;
    private MentorshipRestService mentorshipRestService;
    private IterationTypeService iterationTypeService;

    private ResourceRestService resourceRestService;
    private AnswerService answerService;
    private SessionRestService sessionRestService;
    private SessionRecommendedResourceRestService sessionRecommendedResourceRestService;

    private String encryptedPassphrase;


    private ExecutorService serviceExecutor;

    private SharedPreferences encryptedSharedPreferences;

    private List<Setting> settingList;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        try {
            initEncryptedPassphraseStorage();
        } catch (GeneralSecurityException | IOException e) {
            Log.e("MentoringApplication", "Error initializing encrypted passphrase storage", e);
        }

        interceptor = new AuthInterceptorImpl(this);

        mapper = new ObjectMapper();

        setUpRetrofit();

        serviceExecutor = ExecutorThreadProvider.getInstance().getExecutorService();

        // Load the selected language from SharedPreferences
        SharedPreferences preferences = getEncryptedSharedPreferences(); // Make sure this returns encryptedSharedPreferences
        String selectedLanguageCode = preferences.getString(Constants.PREF_SELECTED_LANGUAGE, "pt"); // Default to English

        String autoLogoutDelay = encryptedSharedPreferences.getString(Constants.PREF_SESSION_TIMEOUT, null);
        if (autoLogoutDelay == null) {
            encryptedSharedPreferences.edit().putString(PREF_SESSION_TIMEOUT, String.valueOf(PREF_SESSION_TIMEOUT_MINUTES)).apply();
        }

        NotificationHelper.createNotificationChannel(this);

        Log.d("SelectedLanguage", "Language selected: " + selectedLanguageCode);
        // Set the locale
        setLocale(selectedLanguageCode);

        loadAppSettings();

    }

    private void loadAppSettings() {
        getServiceExecutor().execute(() -> {
            try {
                this.settingList = getSettingService().getAll();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Setting getSetting(String designation) {
        for (Setting setting : settingList) {
            if (setting.getDesignation().equals(designation)) {
                return setting;
            }
        }
        return null;
    }

    public static synchronized MentoringApplication getInstance() {
        return mInstance;
    }

    /**
     * Method to set the locale for the app.
     * @param languageCode The language code to set.
     */
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            config.setLocale(locale);
            getBaseContext().createConfigurationContext(config);
        } else {
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }

    private void applySavedLanguage() {
        SharedPreferences sharedPreferences = getEncryptedSharedPreferences();
        String languageCode = sharedPreferences.getString(PREF_SELECTED_LANGUAGE, "en"); // default to English
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
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
    public FormSectionQuestionRestService getFormQuestionRestService() {
        if (formSectionQuestionRestService == null) this.formSectionQuestionRestService = new FormSectionQuestionRestService(this);
        return formSectionQuestionRestService;
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
    public FormSectionQuestionService getFormSectionQuestionService() {
        if (formSectionQuestionService == null) this.formSectionQuestionService = new FormSectionQuestionServiceImpl(this);
        return formSectionQuestionService;
    }
    public QuestionService getQuestionService() {
        if (questionService == null) this.questionService = new QuestionServiceImpl(this);
        return questionService;
    }
    public SectionService getSectionService() {
        if (sectionService == null) this.sectionService = new SectionServiceImpl(this);
        return sectionService;
    }
    public FormSectionService getFormSectionService() {
        if (formSectionService == null) this.formSectionService = new FormSectionServiceImpl(this);
        return formSectionService;
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

    public EvaluationLocationService getEvaluationLocationService() {
        if (evaluationLocationService == null) this.evaluationLocationService = new EvaluationLocationServiceImpl(this);
        return evaluationLocationService;
    }

    public ApplicationStep getApplicationStep() {
        return this.applicationStep;
    }

    public Tutor getCurrMentor() {
        if (this.tutor == null && sessionManager != null && sessionManager.getActiveUser() != null) {
            try {
                User currUser = getUserService().getByuuid(sessionManager.getActiveUser());
                this.tutor = getTutorService().getByEmployee(new Employee(currUser.getEmployeeId()));
                this.tutor.setEmployee(getEmployeeService().getById(this.tutor.getEmployeeId()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else
        if (this.tutor != null && this.tutor.getEmployee() == null && this.authenticatedUser != null && this.authenticatedUser.getEmployee() != null) {
            this.tutor.setEmployee(this.authenticatedUser.getEmployee());
        } else if (this.authenticatedUser.getEmployee() == null) {
            try {
                this.tutor.setEmployee(getEmployeeService().getById(this.tutor.getEmployeeId()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return this.tutor;
    }

    public void setCurrTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public void init() throws SQLException {
        try {
            getServiceExecutor().submit(()->{
                try {
                    String prefix = Constants.PREF_USER_CREDENTIALS_PREFIX;

                    encryptedSharedPreferences.edit()
                            .putString(prefix + "username", getAuthenticatedUser().getUserName())
                            .putString(prefix + "password", getAuthenticatedUser().getPassword())
                            .apply();

                    this.applicationStep = ApplicationStep.fastCreate(ApplicationStep.STEP_INIT);
                    if (getAuthenticatedUser().getEmployee() == null) getAuthenticatedUser().setEmployee(getEmployeeService().getById(getAuthenticatedUser().getEmployeeId()));
                    setCurrTutor(getTutorService().getByEmployee(getAuthenticatedUser().getEmployee()));
                    getCurrMentor().getEmployee().setLocations(getLocationService().getAllOfEmploee(getAuthenticatedUser().getEmployee()));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ServerStatusChecker getServerStatusChecker() {
        if (serverStatusChecker == null) serverStatusChecker = new ServerStatusChecker(this);
        return serverStatusChecker;
    }

    public void isServerOnline(ServerStatusListener listener) {
        getServerStatusChecker().isServerOnline(listener);
    }

    public boolean isInitialSetupComplete() {
        String status = encryptedSharedPreferences.getString(INITIAL_SETUP_STATUS, null);
        if (!Utilities.stringHasValue(status)) return false;

        return encryptedSharedPreferences.getString(INITIAL_SETUP_STATUS, null).equals(INITIAL_SETUP_STATUS_COMPLETE);
    }

    public void setInitialSetUpComplete() {
        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
        editor.putString(INITIAL_SETUP_STATUS, INITIAL_SETUP_STATUS_COMPLETE);
        editor.apply();
    }

    private void saveUserName() {
        encryptedSharedPreferences.edit().putString(LOGGED_USER, authenticatedUser.getUserName()).apply();
    }

    public String getLastUser() {
        return encryptedSharedPreferences.getString(LOGGED_USER, null);
    }

    private void removeUserName() {
        encryptedSharedPreferences.edit().remove(LOGGED_USER).apply();
    }

    public void saveDefaultLastSyncDate(Date date) {
        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
        editor.putString(LAST_SYNC_DATE, encryptedSharedPreferences.getString(LAST_SYNC_DATE,DateUtilities.getStringDateFromDate(date, DateUtilities.DATE_FORMAT)));
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
        return encryptedSharedPreferences.getInt(PREF_METADATA_SYNC_TIME, 2);
    }

    private void initEncryptedPassphraseStorage() throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        encryptedSharedPreferences = EncryptedSharedPreferences.create(
                "mentoring_encrypted_prefs",
                masterKeyAlias,
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        // Initialize the encrypted passphrase
        encryptedPassphrase = encryptedSharedPreferences.getString("db_passphrase", null);

        if (encryptedPassphrase == null) {
            // Generate and store the passphrase
            encryptedPassphrase = generatePassphrase();
            encryptedSharedPreferences.edit().putString("db_passphrase", encryptedPassphrase).apply();
        }
    }

    private String generatePassphrase() {
        // Generate a secure random passphrase
        // In a real application, you might use a more complex passphrase generation method
        return Utilities.generateSalt(); // Replace with actual passphrase generation logic
    }

    public String getEncryptedPassphrase() {
        return encryptedPassphrase;
    }

    /**
     * Gracefully shuts down the ExecutorService.
     */
    public void shutdownExecutorService() {
        serviceExecutor.shutdown(); // Disable new tasks from being submitted

        try {
            // Wait a while for existing tasks to terminate
            if (!serviceExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                serviceExecutor.shutdownNow(); // Cancel currently executing tasks

                // Wait a while for tasks to respond to being cancelled
                if (!serviceExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            serviceExecutor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public SharedPreferences getEncryptedSharedPreferences() {
        return encryptedSharedPreferences;
    }

    public ExecutorService getServiceExecutor() {
        return serviceExecutor;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        shutdownExecutorService();
    }

    public void saveDefaultSyncSettings() {
        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
        editor.putInt(PREF_METADATA_SYNC_TIME, encryptedSharedPreferences.getInt(PREF_METADATA_SYNC_TIME, 2));
        editor.apply();
    }
}
