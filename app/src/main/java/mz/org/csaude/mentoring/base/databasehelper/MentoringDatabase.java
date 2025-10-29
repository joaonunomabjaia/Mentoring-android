package mz.org.csaude.mentoring.base.databasehelper;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import mz.org.csaude.mentoring.dao.answer.AnswerDAO;
import mz.org.csaude.mentoring.dao.career.CareerTypeDAO;
import mz.org.csaude.mentoring.dao.employee.EmployeeDAO;
import mz.org.csaude.mentoring.dao.evaluation.EvaluationTypeDAO;
import mz.org.csaude.mentoring.dao.evaluationLocation.EvaluationLocationDAO;
import mz.org.csaude.mentoring.dao.form.FormDAO;
import mz.org.csaude.mentoring.dao.form.FormTypeDAO;
import mz.org.csaude.mentoring.dao.formSection.FormSectionDAO;
import mz.org.csaude.mentoring.dao.formSectionQuestion.FormSectionQuestionDAO;
import mz.org.csaude.mentoring.dao.location.CabinetDAO;
import mz.org.csaude.mentoring.dao.location.DistrictDAO;
import mz.org.csaude.mentoring.dao.location.HealthFacilityDAO;
import mz.org.csaude.mentoring.dao.location.LocationDAO;
import mz.org.csaude.mentoring.dao.location.ProvinceDAO;
import mz.org.csaude.mentoring.dao.mentorship.DoorDAO;
import mz.org.csaude.mentoring.dao.mentorship.IterationTypeDAO;
import mz.org.csaude.mentoring.dao.mentorship.MentorshipDAO;
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
import mz.org.csaude.mentoring.dao.section.SectionDAO;
import mz.org.csaude.mentoring.dao.session.SessionDAO;
import mz.org.csaude.mentoring.dao.session.SessionRecommendedResourceDAO;
import mz.org.csaude.mentoring.dao.session.SessionStatusDAO;
import mz.org.csaude.mentoring.dao.setting.SettingDAO;
import mz.org.csaude.mentoring.dao.tutor.TutorDAO;
import mz.org.csaude.mentoring.dao.tutored.TutoredDao;
import mz.org.csaude.mentoring.dao.user.UserDao;
import mz.org.csaude.mentoring.model.answer.Answer;
import mz.org.csaude.mentoring.model.career.CareerType;
import mz.org.csaude.mentoring.model.employee.Employee;
import mz.org.csaude.mentoring.model.evaluationLocation.EvaluationLocation;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.form.FormSection;
import mz.org.csaude.mentoring.model.form.FormType;
import mz.org.csaude.mentoring.model.form.Section;
import mz.org.csaude.mentoring.model.formSectionQuestion.FormSectionQuestion;
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
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.util.Converters;

@Database(
        entities = {
                Tutored.class, Partner.class, Tutor.class,
                Form.class, Session.class, HealthFacility.class, Cabinet.class,
                District.class, Mentorship.class, Question.class, Answer.class,
                FormSectionQuestion.class, Setting.class, Program.class, ProgrammaticArea.class, TutorProgrammaticArea.class,
                CareerType.class, FormType.class, Door.class, IterationType.class, Province.class, QuestionType.class,
                SessionStatus.class, User.class, Ronda.class, RondaType.class, RondaMentee.class, RondaMentor.class,
                ProfessionalCategory.class, Employee.class, Location.class, EvaluationType.class, ResponseType.class,
                Resource.class, SessionRecommendedResource.class, FormSection.class, Section.class, EvaluationLocation.class
        },
        version = 5,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class MentoringDatabase extends RoomDatabase {

    private static volatile MentoringDatabase INSTANCE;

    public abstract TutoredDao getTutoredDao();
    public abstract PartnerDao getPartnerDao();
    public abstract TutorDAO getTutorDAO();
    public abstract ProgrammaticAreaDAO getProgrammaticAreaDAO();
    public abstract FormDAO getFormDAO();
    public abstract SessionDAO getSessionDAO();
    public abstract HealthFacilityDAO getHealthFacilityDAO();
    public abstract CabinetDAO getCabinetDAO();
    public abstract DistrictDAO getDistrictDAO();
    public abstract MentorshipDAO getMentorshipDAO();
    public abstract QuestionDAO getQuestionDAO();
    public abstract AnswerDAO getAnswerDAO();
    public abstract FormSectionQuestionDAO getFormSectionQuestionDAO();
    public abstract SettingDAO getSettingDAO();
    public abstract TutorProgrammaticAreaDAO getTutorProgrammaticAreaDAO();
    public abstract CareerTypeDAO getCareerTypeDAO();
    public abstract FormTypeDAO getFormTypeDAO();
    public abstract DoorDAO getDoorDAO();
    public abstract IterationTypeDAO getIterationTypeDAO();
    public abstract ProvinceDAO getProvinceDAO();
    public abstract QuestionTypeDAO getQuestionTypeDAO();
    public abstract SessionStatusDAO getSessionStatusDAO();
    public abstract RondaTypeDAO getRondaTypeDAO();
    public abstract RondaMenteeDAO getRondaMenteeDAO();
    public abstract RondaMentorDAO getRondaMentorDAO();
    public abstract RondaDAO getRondaDAO();
    public abstract ProfessionalCategoryDAO getProfessionalCategoryDAO();
    public abstract LocationDAO getLocationDAO();
    public abstract EmployeeDAO getEmployeeDAO();
    public abstract EvaluationTypeDAO getEvaluationTypeDAO();
    public abstract ResponseTypeDAO getResponseTypeDAO();
    public abstract ProgramDAO getProgramDAO();
    public abstract ResourceDAO getResourceDAO();
    public abstract UserDao getUserDAO();
    public abstract SessionRecommendedResourceDAO getSessionRecommendedResourceDAO();
    public abstract SectionDAO getSectionDAO();
    public abstract FormSectionDAO getFormSectionDAO();
    public abstract EvaluationLocationDAO getEvaluationLocationDAO();

    // Migration 4 -> 5: add flow_history column to Tutored
    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE " + Tutored.COLUMN_TABLE_NAME + " ADD COLUMN "
                    + Tutored.COLUMN_FLOW_HISTORY + " TEXT");
        }
    };

    public static MentoringDatabase getInstance(Context context, String passphrase) {
        if (INSTANCE == null) {
            synchronized (MentoringDatabase.class) {
                if (INSTANCE == null) {
                    byte[] passphraseBytes = SQLiteDatabase.getBytes(passphrase.toCharArray());
                    SupportFactory factory = new SupportFactory(passphraseBytes);

                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    MentoringDatabase.class,
                                    "mentoring.db"
                            )
                            .openHelperFactory(factory)
                            .addMigrations(MIGRATION_4_5)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
