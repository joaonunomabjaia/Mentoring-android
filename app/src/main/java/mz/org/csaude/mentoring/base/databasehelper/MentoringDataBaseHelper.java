package mz.org.csaude.mentoring.base.databasehelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.dao.answer.AnswerDAO;
import mz.org.csaude.mentoring.dao.career.CareerDAO;
import mz.org.csaude.mentoring.dao.career.CareerTypeDAO;
import mz.org.csaude.mentoring.dao.employee.EmployeeDAO;
import mz.org.csaude.mentoring.dao.evaluation.EvaluationTypeDAO;
import mz.org.csaude.mentoring.dao.form.FormDAO;
import mz.org.csaude.mentoring.dao.form.FormTargetDAO;
import mz.org.csaude.mentoring.dao.form.FormTypeDAO;
import mz.org.csaude.mentoring.dao.formQuestion.FormQuestionDAO;
import mz.org.csaude.mentoring.dao.indicator.IndicatorDAO;
import mz.org.csaude.mentoring.dao.location.CabinetDAO;
import mz.org.csaude.mentoring.dao.location.DistrictDAO;
import mz.org.csaude.mentoring.dao.location.HealthFacilityDAO;
import mz.org.csaude.mentoring.dao.location.LocationDAO;
import mz.org.csaude.mentoring.dao.location.ProvinceDAO;
import mz.org.csaude.mentoring.dao.mentorship.DoorDAO;
import mz.org.csaude.mentoring.dao.mentorship.IterationTypeDAO;
import mz.org.csaude.mentoring.dao.mentorship.MentorshipDAO;
import mz.org.csaude.mentoring.dao.mentorship.TimeOfDayDAO;
import mz.org.csaude.mentoring.dao.partner.PartnerDao;
import mz.org.csaude.mentoring.dao.professionalCategoryDAO.ProfessionalCategoryDAO;
import mz.org.csaude.mentoring.dao.program.ProgramDAO;
import mz.org.csaude.mentoring.dao.programmaticArea.ProgrammaticAreaDAO;
import mz.org.csaude.mentoring.dao.programmaticArea.TutorProgrammaticAreaDAO;
import mz.org.csaude.mentoring.dao.question.QuestionDAO;
import mz.org.csaude.mentoring.dao.question.QuestionTypeDAO;
import mz.org.csaude.mentoring.dao.question.QuestionsCategoryDAO;
import mz.org.csaude.mentoring.dao.resource.ResourceDAO;
import mz.org.csaude.mentoring.dao.responseType.ResponseTypeDAO;
import mz.org.csaude.mentoring.dao.ronda.RondaDAO;
import mz.org.csaude.mentoring.dao.ronda.RondaMenteeDAO;
import mz.org.csaude.mentoring.dao.ronda.RondaMentorDAO;
import mz.org.csaude.mentoring.dao.rondatype.RondaTypeDAO;
import mz.org.csaude.mentoring.dao.session.SessionDAO;
import mz.org.csaude.mentoring.dao.session.SessionRecommendedResourceDAO;
import mz.org.csaude.mentoring.dao.session.SessionStatusDAO;
import mz.org.csaude.mentoring.dao.setting.SettingDAO;
import mz.org.csaude.mentoring.dao.tutor.TutorDAO;
import mz.org.csaude.mentoring.dao.tutor.TutorLocationDAO;
import mz.org.csaude.mentoring.dao.tutor.TutorTutoredDao;
import mz.org.csaude.mentoring.dao.tutored.TutoredDao;
import mz.org.csaude.mentoring.dao.user.UserDao;
import mz.org.csaude.mentoring.model.answer.Answer;
import mz.org.csaude.mentoring.model.career.Career;
import mz.org.csaude.mentoring.model.career.CareerType;
import mz.org.csaude.mentoring.model.employee.Employee;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.form.FormTarget;
import mz.org.csaude.mentoring.model.form.FormType;
import mz.org.csaude.mentoring.model.formQuestion.FormQuestion;
import mz.org.csaude.mentoring.model.location.Cabinet;
import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.model.location.Location;
import mz.org.csaude.mentoring.model.location.Province;
import mz.org.csaude.mentoring.model.mentorship.Door;
import mz.org.csaude.mentoring.model.mentorship.IterationType;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.model.professionalCategory.ProfessionalCategory;
import mz.org.csaude.mentoring.model.program.Program;
import mz.org.csaude.mentoring.model.programmaticArea.ProgrammaticArea;
import mz.org.csaude.mentoring.model.programmaticArea.TutorProgrammaticArea;
import mz.org.csaude.mentoring.model.question.Question;
import mz.org.csaude.mentoring.model.question.QuestionType;
import mz.org.csaude.mentoring.model.question.QuestionsCategory;
import mz.org.csaude.mentoring.model.resourceea.Resource;
import mz.org.csaude.mentoring.model.responseType.ResponseType;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.ronda.RondaMentor;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionRecommendedResource;
import mz.org.csaude.mentoring.model.session.SessionStatus;
import mz.org.csaude.mentoring.model.setting.Setting;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutor.TutorLocation;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.model.user.User;

public class MentoringDataBaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME    = "mentoring.db";
    private static final int    DATABASE_VERSION = 1;

    private final Context context;

    private TutoredDao tutoredDao;

    private PartnerDao partnerDao;

    private CareerDAO careerDAO;

    private TutorDAO tutorDAO;

    private HealthFacilityDAO healthFacilityDAO;

    private TutorLocationDAO tutorLocationDAO;

    private ProgrammaticAreaDAO programmaticAreaDAO;

    private FormDAO formDAO;

    private FormTargetDAO formTargetDAO;

    private SessionDAO sessionDAO;

    private CabinetDAO cabinetDAO;

    private DistrictDAO districtDAO;

    private MentorshipDAO mentorshipDAO;

    private QuestionDAO questionDAO;

    private QuestionsCategoryDAO questionsCategoryDAO;

    private IndicatorDAO indicatorDAO;

    private AnswerDAO answerDAO;

    private FormQuestionDAO formQuestionDAO;

    private SettingDAO settingDAO;

    private TutorProgrammaticAreaDAO tutorProgrammaticAreaDAO;

    private CareerTypeDAO careerTypeDAO;

    private FormTypeDAO formTypeDAO;

    private DoorDAO doorDAO;

    private IterationTypeDAO iterationTypeDAO;

    private TimeOfDayDAO timeOfDayDAO;

    private ProvinceDAO provinceDAO;

    private QuestionTypeDAO questionTypeDAO;

    private SessionStatusDAO sessionStatusDAO;

    private TutorTutoredDao tutorTutoredDao;

    private UserDao userDao;

    private RondaDAO rondaDAO;

    private RondaMentorDAO rondaMentorDAO;

    private RondaMenteeDAO rondaMenteeDAO;

    private RondaTypeDAO rondaTypeDAO;

    private ProfessionalCategoryDAO professionalCategoryDAO;

    private LocationDAO locationDAO;

    private EmployeeDAO employeeDAO;
    private EvaluationTypeDAO evaluationTypeDAO;
    private ResponseTypeDAO responseTypeDAO;
    private ProgramDAO programDAO;

    private ResourceDAO resourceDAO;
    private SessionRecommendedResourceDAO sessionRecommendedResourceDAO;

    private static MentoringDataBaseHelper dataBaseHelper;

    private MentoringDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        this.context = context;
    }

    public static MentoringDataBaseHelper getInstance(Context context) {
        if (dataBaseHelper == null){
            dataBaseHelper = new MentoringDataBaseHelper(context);
        }
        return dataBaseHelper;
    }

    public UserDao getUserDao() throws SQLException{
        if(userDao == null){
            userDao = getDao(User.class);
        }
        return userDao;
    }
    public TutoredDao getTutoredDao() throws SQLException {
        if(tutoredDao == null){
            tutoredDao = getDao(Tutored.class);
        }
        return tutoredDao;
    }

    public PartnerDao getPartnerDao() throws SQLException{
        if (partnerDao == null){
            partnerDao = getDao(Partner.class);
        }
        return partnerDao;
    }
    public CareerDAO getCareerDAO() throws SQLException {
        if (careerDAO == null){
            careerDAO = getDao(Career.class);
        }
        return careerDAO;
    }

    public TutorDAO getTutorDAO() throws SQLException {
        if(tutorDAO == null){
            tutorDAO = getDao(Tutor.class);
        }
        return tutorDAO;
    }

    public TutorLocationDAO getTutorLocationDAO() throws SQLException {
        if(tutorLocationDAO == null){
            tutorLocationDAO = getDao(TutorLocation.class);
        }
        return tutorLocationDAO;
    }

    public ProgrammaticAreaDAO getProgrammaticAreaDAO() throws SQLException {
        if(programmaticAreaDAO == null){
            programmaticAreaDAO = getDao(ProgrammaticArea.class);
        }
        return programmaticAreaDAO;
    }

    public FormDAO getFormDAO() throws SQLException {
        if(formDAO == null){
            formDAO = getDao(Form.class);
        }
        return formDAO;
    }
    public FormTargetDAO getFormTargetDAO() throws SQLException {
        if(formTargetDAO == null){
            formTargetDAO = getDao(FormTarget.class);
        }
        return formTargetDAO;
    }

    public SessionDAO getSessionDAO() throws SQLException {
        if(sessionDAO == null){
            sessionDAO = getDao(Session.class);
        }
        return sessionDAO;
    }

    public HealthFacilityDAO getHealthFacilityDAO() throws SQLException {
        if(healthFacilityDAO == null){
            healthFacilityDAO = getDao(HealthFacility.class);
        }
        return healthFacilityDAO;
    }

    public CabinetDAO getCabinetDAO() throws SQLException {
        if(cabinetDAO == null){
            cabinetDAO = getDao(Cabinet.class);
        }
        return cabinetDAO;
    }
    public DistrictDAO getDistrictDAO() throws SQLException {
        if(districtDAO == null){
            districtDAO = getDao(District.class);
        }
        return districtDAO;
    }

    public MentorshipDAO getMentorshipDAO() throws SQLException {
        if(mentorshipDAO == null){
            mentorshipDAO = getDao(Mentorship.class);
        }
        return mentorshipDAO;
    }
    public QuestionDAO getQuestionDAO() throws SQLException {
        if(questionDAO == null){
            questionDAO = getDao(Question.class);
        }
        return questionDAO;
    }

    public QuestionsCategoryDAO getQuestionsCategoryDAO() throws SQLException {
        if(questionsCategoryDAO == null){
            questionsCategoryDAO = getDao(QuestionsCategory.class);
        }
        return questionsCategoryDAO;
    }
    public AnswerDAO getAnswerDAO() throws SQLException {
        if(answerDAO == null){
            answerDAO = getDao(Answer.class);
        }
        return answerDAO;
    }
    public FormQuestionDAO getFormQuestionDAO() throws SQLException {
        if(formQuestionDAO == null){
            formQuestionDAO = getDao(FormQuestion.class);
        }
        return formQuestionDAO;
    }
    public SettingDAO getSettingDAO() throws SQLException {
        if(settingDAO == null){
            settingDAO = getDao(Setting.class);
        }
        return settingDAO;
    }
    public TutorProgrammaticAreaDAO getTutorProgrammaticAreaDAO() throws SQLException {
        if(tutorProgrammaticAreaDAO == null){
            tutorProgrammaticAreaDAO = getDao(TutorProgrammaticArea.class);
        }
        return tutorProgrammaticAreaDAO;
    }
    public CareerTypeDAO getCareerTypeDAO() throws SQLException {
        if(careerTypeDAO == null){
            careerTypeDAO = getDao(CareerType.class);
        }
        return careerTypeDAO;
    }

    public FormTypeDAO getFormType() throws SQLException {
        if(formTypeDAO == null){
            formTypeDAO = getDao(FormType.class);
        }
        return formTypeDAO;
    }
    public DoorDAO getDoorDAO() throws SQLException {
        if(doorDAO == null){
            doorDAO = getDao(Door.class);
        }
        return doorDAO;
    }
    public IterationTypeDAO getIterationTypeDAO() throws SQLException {
        if(iterationTypeDAO == null){
            iterationTypeDAO = getDao(IterationType.class);
        }
        return iterationTypeDAO;
    }

    public ProvinceDAO getProvinceDAO() throws SQLException {
        if(provinceDAO == null){
            provinceDAO = getDao(Province.class);
        }
        return provinceDAO;
    }

    public QuestionTypeDAO getQuestionTypeDAO() throws SQLException {
        if(questionTypeDAO == null){
            questionTypeDAO = getDao(QuestionType.class);
        }
        return questionTypeDAO;
    }

    public SessionStatusDAO getSessionStatusDAO() throws SQLException {
        if(sessionStatusDAO == null){
            sessionStatusDAO = getDao(SessionStatus.class);
        }
        return sessionStatusDAO;
    }


    public RondaTypeDAO getRondaTypeDAO() throws SQLException {
        if(rondaTypeDAO == null){
            rondaTypeDAO = getDao(RondaType.class);
        }
        return rondaTypeDAO;
    }

    public RondaMenteeDAO getRondaMenteeDAO() throws SQLException {
        if(rondaMenteeDAO == null){
            rondaMenteeDAO = getDao(RondaMentee.class);
        }
        return rondaMenteeDAO;
    }

    public RondaMentorDAO getRondaMentorDAO() throws SQLException {
        if(rondaMentorDAO == null){
            rondaMentorDAO = getDao(RondaMentor.class);
        }
        return rondaMentorDAO;
    }

    public RondaDAO getRondaDAO() throws SQLException {
        if(rondaDAO == null){
            rondaDAO = getDao(Ronda.class);
        }
        return rondaDAO;
    }

    public ProfessionalCategoryDAO getProfessionalCategoryDAO() throws SQLException {
        if (professionalCategoryDAO == null){
            professionalCategoryDAO = getDao(ProfessionalCategory.class);
        }
        return professionalCategoryDAO;
    }

    public LocationDAO getLocationDAO() throws SQLException {
        if (locationDAO == null){
            locationDAO = getDao(Location.class);
        }
        return locationDAO;
    }

    public EmployeeDAO getEmployeeDAO() throws SQLException {
        if(employeeDAO == null){
            employeeDAO = getDao(Employee.class);
        }
        return employeeDAO;
    }
    public EvaluationTypeDAO getEvaluationTypeDAO() throws SQLException {
        if(evaluationTypeDAO == null){
            evaluationTypeDAO = getDao(EvaluationType.class);
        }
        return evaluationTypeDAO;
    }
    public ResponseTypeDAO getResponseTypeDAO() throws SQLException {
        if(responseTypeDAO == null){
            responseTypeDAO = getDao(ResponseType.class);
        }
        return responseTypeDAO;
    }
    public ProgramDAO getProgramDAO() throws SQLException {
        if(programDAO == null){
            programDAO = getDao(Program.class);
        }
        return programDAO;
    }
    public ResourceDAO getResourceDAO() throws SQLException{

        if(resourceDAO == null){
            resourceDAO = getDao(Resource.class);
        }
        return resourceDAO;
    }

    public SessionRecommendedResourceDAO getSessionRecommendedResourceDAO() throws SQLException {
        if(sessionRecommendedResourceDAO == null){
            sessionRecommendedResourceDAO = getDao(SessionRecommendedResource.class);
        }
        return sessionRecommendedResourceDAO;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            TableUtils.createTableIfNotExists(connectionSource, Tutored.class);
            TableUtils.createTableIfNotExists(connectionSource, Partner.class);
            TableUtils.createTableIfNotExists(connectionSource, Career.class);
            TableUtils.createTableIfNotExists(connectionSource, Tutor.class);
            TableUtils.createTableIfNotExists(connectionSource, TutorLocation.class);
            TableUtils.createTableIfNotExists(connectionSource, Form.class);
            TableUtils.createTableIfNotExists(connectionSource, FormTarget.class);
            TableUtils.createTableIfNotExists(connectionSource, Session.class);
            TableUtils.createTableIfNotExists(connectionSource, HealthFacility.class);
            TableUtils.createTableIfNotExists(connectionSource, Cabinet.class);
            TableUtils.createTableIfNotExists(connectionSource, District.class);
            TableUtils.createTableIfNotExists(connectionSource, Mentorship.class);
            TableUtils.createTableIfNotExists(connectionSource, Question.class);
            TableUtils.createTableIfNotExists(connectionSource, QuestionsCategory.class);
            TableUtils.createTableIfNotExists(connectionSource, Answer.class);
            TableUtils.createTableIfNotExists(connectionSource, FormQuestion.class);
            TableUtils.createTableIfNotExists(connectionSource, Setting.class);
            TableUtils.createTableIfNotExists(connectionSource, Program.class);
            TableUtils.createTableIfNotExists(connectionSource, ProgrammaticArea.class);
            TableUtils.createTableIfNotExists(connectionSource, TutorProgrammaticArea.class);
            TableUtils.createTableIfNotExists(connectionSource, CareerType.class);
            TableUtils.createTableIfNotExists(connectionSource, FormType.class);
            TableUtils.createTableIfNotExists(connectionSource, Door.class);
            TableUtils.createTableIfNotExists(connectionSource, IterationType.class);
            TableUtils.createTableIfNotExists(connectionSource, Province.class);
            TableUtils.createTableIfNotExists(connectionSource, QuestionType.class);
            TableUtils.createTableIfNotExists(connectionSource, SessionStatus.class);
            TableUtils.createTableIfNotExists(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, Ronda.class);
            TableUtils.createTableIfNotExists(connectionSource, RondaType.class);
            TableUtils.createTableIfNotExists(connectionSource, RondaMentee.class);
            TableUtils.createTableIfNotExists(connectionSource, RondaMentor.class);
            TableUtils.createTableIfNotExists(connectionSource, ProfessionalCategory.class);
            TableUtils.createTableIfNotExists(connectionSource, Employee.class);
            TableUtils.createTableIfNotExists(connectionSource, Location.class);
            TableUtils.createTableIfNotExists(connectionSource, EvaluationType.class);
            TableUtils.createTableIfNotExists(connectionSource, ResponseType.class);
            TableUtils.createTableIfNotExists(connectionSource, Resource.class);
            TableUtils.createTableIfNotExists(connectionSource, SessionRecommendedResource.class);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
            //onCreate(database, connectionSource);
    }


}
