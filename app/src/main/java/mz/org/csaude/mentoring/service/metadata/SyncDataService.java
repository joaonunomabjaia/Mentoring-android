package mz.org.csaude.mentoring.service.metadata;

import mz.org.csaude.mentoring.base.auth.LoginResponse;
import mz.org.csaude.mentoring.base.service.ApiResponse;
import mz.org.csaude.mentoring.common.MentoringAPIError;
import mz.org.csaude.mentoring.dto.career.CareerTypeDTO;
import mz.org.csaude.mentoring.dto.evaluationLocation.EvaluationLocationDTO;
import mz.org.csaude.mentoring.dto.evaluationType.EvaluationTypeDTO;
import mz.org.csaude.mentoring.dto.form.FormDTO;
import mz.org.csaude.mentoring.dto.form.FormSectionQuestionDTO;
import mz.org.csaude.mentoring.dto.location.CabinetDTO;
import mz.org.csaude.mentoring.dto.location.DistrictDTO;
import mz.org.csaude.mentoring.dto.location.HealthFacilityDTO;
import mz.org.csaude.mentoring.dto.location.ProvinceDTO;
import mz.org.csaude.mentoring.dto.mentorship.DoorDTO;
import mz.org.csaude.mentoring.dto.mentorship.IterationTypeDTO;
import mz.org.csaude.mentoring.dto.mentorship.MentorshipDTO;
import mz.org.csaude.mentoring.dto.partner.PartnerDTO;
import mz.org.csaude.mentoring.dto.professionalcategory.ProfessionalCategoryDTO;
import mz.org.csaude.mentoring.dto.program.ProgramDTO;
import mz.org.csaude.mentoring.dto.programmaticArea.ProgrammaticAreaDTO;
import mz.org.csaude.mentoring.dto.programmaticArea.TutorProgrammaticAreaDTO;
import mz.org.csaude.mentoring.dto.question.QuestionCategoryDTO;
import mz.org.csaude.mentoring.dto.question.QuestionDTO;
import mz.org.csaude.mentoring.dto.resource.ResourceDTO;
import mz.org.csaude.mentoring.dto.responseType.ResponseTypeDTO;
import mz.org.csaude.mentoring.dto.ronda.RondaDTO;
import mz.org.csaude.mentoring.dto.ronda.RondaTypeDTO;
import mz.org.csaude.mentoring.dto.section.SectionDTO;
import mz.org.csaude.mentoring.dto.session.SessionDTO;
import mz.org.csaude.mentoring.dto.session.SessionRecommendedResourceDTO;
import mz.org.csaude.mentoring.dto.session.SessionStatusDTO;
import mz.org.csaude.mentoring.dto.setting.SettingDTO;
import mz.org.csaude.mentoring.dto.tutor.TutorDTO;
import mz.org.csaude.mentoring.dto.tutored.TutoredDTO;
import mz.org.csaude.mentoring.dto.user.UserDTO;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

/**
 * Created by ialuj
 */
public interface SyncDataService {

    @GET("settings/check")
    Call<Void> checkServerStatus();

    @GET("settings/tutor/{uuid}/{offset}/{limit}")
    Call<List<SettingDTO>> getSettings(@Path("uuid") final String uuid, @Path("offset") long offset, @Path("limit") long limit);
    @GET("settings/tutor/{uuid}")
    Call<List<SettingDTO>> getSettings(@Path("uuid") final String uuid);

    @GET("healthFacilities/tutor/{uuid}")
    Call<ApiResponse<HealthFacilityDTO>> getHealthFacilities(@Path("uuid") final String uuid);

    @GET("cabinets/getall")
    Call<List<CabinetDTO>> getCabinets(@Query("offset") long offset, @Query("limit") long limit);

    @GET("careers/careerTypes")
    Call<List<CareerTypeDTO>> getCareerTypes(@Query("offset") long offset, @Query("limit") long limit);

    @GET("tutored/getTutoreds")
    Call<List<TutoredDTO>> getTutoreds(@Query("uuids") List<String> uuids, @Query("offset") long offset, @Query("limit") long limit);
    @POST("login")
    Call<LoginResponse> login(@Body RequestBody body);

    @POST("auth/refresh")
    Call<ResponseBody> refreshToken(@Body RequestBody body);

    @GET("user/getByCredencials/{username}/{password}")
    Call<UserDTO> getByCredencials(@Path("username") final String username, @Path("password") final String password);

    @GET("tutor/tutors/{limit}/{offset}")
    Call<List<TutorDTO>> getTutors(@Path("limit") long limit , @Path("offset") long offset);

    @GET("mentor/employee/{uuid}")
    Call<TutorDTO> getTutorByEmployeeUuid(@Path("uuid") String userUuid);

    @GET("province/getall")
    Call<List<ProvinceDTO>> getProvinces();

    @GET("healthFacilities/getall")
    Call<List<HealthFacilityDTO>> getHealthFacilities(@Query("offset") long offset, @Query("limit") long limit);

    @GET("healthFacilities/getByDistricts")
    Call<List<HealthFacilityDTO>> getByDistricts(@Query("uuids") List<String> uuids);

    @GET("district/getall")
    Call<List<DistrictDTO>> getDistricts(@Query("offset") long offset, @Query("limit") long limit);

    @GET("professionalCategories/getall")
    Call<List<ProfessionalCategoryDTO>> getProfessionalCategory(@Query("offset") long offset, @Query("limit") long limit);

    @GET("partner/getall")
    Call<ApiResponse<PartnerDTO>> getPartners();

    @POST("tutored/save")
    Call<TutoredDTO> postTutored(@Body TutoredDTO tutoredDTO);

    @PATCH("user/password-update")
    Call<UserDTO> patchUser(@Body UserDTO userDTO);

    @PATCH("tutored/batch-update")
    Call<List<TutoredDTO>> postTutoreds(@Body List<TutoredDTO> tutoredDTOS);
    @GET("rondaTypes/getall")
    Call<List<RondaTypeDTO>> getRondaTypes();
    @GET("forms/getall")
    Call<List<FormDTO>> getFormsByPartner(@Query("partnerId") Long partnerId);
    @POST("forms/save")
    Call<List<FormDTO>> postForms(@Body List<FormDTO> formDTOS);
    @GET("evaluationTypes/getAll")
    Call<List<EvaluationTypeDTO>> getEvaluationTypes();
    @GET("responseTypes/getAll")
    Call<List<ResponseTypeDTO>> getResponseTypes();

    @GET("section")
    Call<List<SectionDTO>> getSections();
    @GET("utils/iterationTypes")
    Call<List<IterationTypeDTO>> getIterationTypes();
    @GET("utils/doors")
    Call<List<DoorDTO>> getDoors();
    @GET("questions/getAll")
    Call<List<QuestionDTO>> getAllQuestions();
    @GET("formSectionQuestions/getByFormsUuids")
    Call<List<FormSectionQuestionDTO>> getFormsQuestionsByFormsUuids(@Query("formsUuids") List<String> formsUuids,
                                                                     @Query("limit") Long limit,
                                                                     @Query("offset") Long offset);
    @GET("rondas/getAllRondasOfMentor")
    Call<List<RondaDTO>> getAllRondasOfMentor(@Query("mentorId") Long mentorId);

    @PATCH("ronda/closeRonda")
    Call<List<RondaDTO>> updateRondaInfo(@Body List<RondaDTO> rondaDTOS);
    @GET("sessions/getAllOfRondas")
    Call<List<SessionDTO>> getAllOfRondas(@Query("rondasUuids") List<String> rondasUuids);
    @POST("mentorships/save")
    Call<List<MentorshipDTO>> postMenthorships(@Body List<MentorshipDTO> mentorshipDTOS);

    @GET("sessionStatuses/getall")
    Call<List<SessionStatusDTO>> getSessionStatuses();

    @GET("error")
    Call<MentoringAPIError> getError();

    @PATCH("mentor/update")
    Call<TutorDTO> patchTutor(@Body TutorDTO tutorDTO);
    @GET("programs/getAll")
    Call<ApiResponse<ProgramDTO>> getAllPrograms();
    @GET("programmaticareas/getAll")
    Call<ApiResponse<ProgrammaticAreaDTO>> getAllProgrammaticAreas();
    @GET("tutorprogrammaticareas/getByTutorUuidd/{tutorUuid}")
    Call<List<TutorProgrammaticAreaDTO>> getByTutorUuidd(@Path("tutorUuid") String tutorUuid);

    @GET("forms/getByTutorUuidd/{tutorUuid}")
    Call<List<FormDTO>> getFormsByMentor(@Path("tutorUuid") String tutorUuid);
    @GET("ronda/getAllOfMentor")
    Call<List<RondaDTO>> getAllOfMentor(@Query("mentorUuid") String mentorUuid);

    @GET("mentorships/getAllMentorshipSessionsOfMentorRondas")
    Call<List<MentorshipDTO>> getAllMentorshipSessionsOfMentorRondas(@Query("rondaUuidList") List<String> rondaUuidList);

    @POST("ronda/save")
    Call<RondaDTO> postRonda(@Body RondaDTO rondaDTO);


    @PATCH("ronda/update")
    Call<RondaDTO> patchtRonda(@Body RondaDTO rondaDTO);

    @GET("resources/getAll")
    Call<List<ResourceDTO>> getAllResource();

    @GET("resources/load")
    Call<ResponseBody> downloadFile(@Query("fileName") String fileName);

    @DELETE("ronda/delete")
    Call<ResponseBody> delete(@Query("uuid") String uuid);

    @POST("sessionsReommendedResources/save")
    Call<List<SessionRecommendedResourceDTO>> postSessionRecommendedResource(@Body List<SessionRecommendedResourceDTO> sessionRecommendedResourceDTOs);

    @GET("user/getByuuid/{uuid}")
    Call<UserDTO> getByuuid(@Path("uuid") String uuid);

    @POST("sessions/save")
    Call<List<SessionDTO>> postSessions(@Body List<SessionDTO> sessionDTOS);

    @GET("/evaluation-locations/getAll")
    Call<ApiResponse<EvaluationLocationDTO>> getAllEvaluationLocations();


}
