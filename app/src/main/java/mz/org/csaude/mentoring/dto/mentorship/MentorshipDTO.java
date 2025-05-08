package mz.org.csaude.mentoring.dto.mentorship;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;




import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.common.Syncable;
import mz.org.csaude.mentoring.dto.answer.AnswerDTO;
import mz.org.csaude.mentoring.dto.evaluationLocation.EvaluationLocationDTO;
import mz.org.csaude.mentoring.dto.evaluationType.EvaluationTypeDTO;
import mz.org.csaude.mentoring.dto.form.FormDTO;
import mz.org.csaude.mentoring.dto.location.CabinetDTO;
import mz.org.csaude.mentoring.dto.location.HealthFacilityDTO;
import mz.org.csaude.mentoring.dto.session.SessionDTO;
import mz.org.csaude.mentoring.dto.tutor.TutorDTO;
import mz.org.csaude.mentoring.dto.tutored.TutoredDTO;
import mz.org.csaude.mentoring.model.answer.Answer;
import mz.org.csaude.mentoring.model.evaluationLocation.EvaluationLocation;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.location.Cabinet;
import mz.org.csaude.mentoring.model.mentorship.Door;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.SyncSatus;




public class MentorshipDTO extends BaseEntityDTO implements Syncable {
    private Integer iterationNumber;
    private Date startDate;
    private Date endDate;
    private TutorDTO mentor;
    private TutoredDTO mentee;
    private SessionDTO session;
    private FormDTO form;
    private CabinetDTO cabinet;
    private DoorDTO door;
    private EvaluationTypeDTO evaluationType;
    private boolean demonstration;
    private String demonstrationDetails;
    private List<AnswerDTO> answers;
    private Date performedDate;
    private EvaluationLocationDTO evaluationLocationDTO;

    private String mentorUuid;
    private String menteeUuid;
    private String sessionUuid;
    private String formUuid;
    private String cabinetUuid;
    private String doorUuid;
    private String evaluationTypeUuid;
    private String evaluationLocationUuid;

    public MentorshipDTO() {
    }
    public MentorshipDTO(Mentorship mentorship) {
        super(mentorship);
        this.setStartDate(mentorship.getStartDate());
        this.setEndDate(mentorship.getEndDate());
        this.setIterationNumber(mentorship.getIterationNumber());
        this.setDemonstration(mentorship.isDemonstration());
        this.setDemonstrationDetails(mentorship.getDemonstrationDetails());
        this.setPerformedDate(mentorship.getPerformedDate());
        if(mentorship.getTutor()!=null) {
            this.setMentorUuid(mentorship.getTutor().getUuid());
        }
        if(mentorship.getTutored()!=null) {
            this.setMenteeUuid(mentorship.getTutored().getUuid());
        }
        if(mentorship.getSession()!=null) {
            this.setSessionUuid(mentorship.getSession().getUuid());
        }
        if(mentorship.getForm()!=null) {
            this.setFormUuid(mentorship.getForm().getUuid());
        }
        if(mentorship.getCabinet()!=null) {
            this.setCabinetUuid(mentorship.getCabinet().getUuid());
        }
        if(mentorship.getDoor()!=null) {
            this.setDoorUuid(mentorship.getDoor().getUuid());
        }
        if(mentorship.getEvaluationType()!=null) {
            this.setEvaluationTypeUuid(mentorship.getEvaluationType().getUuid());
        }
        if(mentorship.getEvaluationLocation()!=null) {
            this.setEvaluationLocationUuid(mentorship.getEvaluationLocation().getUuid());
        }
        if(mentorship.getAnswers()!=null) {
            List<AnswerDTO> answerDTOS = new ArrayList<>();
            for (Answer answer: mentorship.getAnswers()) {
                 answerDTOS.add(new AnswerDTO(answer));
            }
            this.setAnswers(answerDTOS);
        }
    }

    public Integer getIterationNumber() {
        return iterationNumber;
    }

    public void setIterationNumber(Integer iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public TutorDTO getMentor() {
        return mentor;
    }

    public void setMentor(TutorDTO mentor) {
        this.mentor = mentor;
    }

    public TutoredDTO getMentee() {
        return mentee;
    }

    public void setMentee(TutoredDTO mentee) {
        this.mentee = mentee;
    }

    public SessionDTO getSession() {
        return session;
    }

    public void setSession(SessionDTO session) {
        this.session = session;
    }

    public FormDTO getForm() {
        return form;
    }

    public void setForm(FormDTO form) {
        this.form = form;
    }

    public CabinetDTO getCabinet() {
        return cabinet;
    }

    public void setCabinet(CabinetDTO cabinet) {
        this.cabinet = cabinet;
    }

    public DoorDTO getDoor() {
        return door;
    }

    public void setDoor(DoorDTO door) {
        this.door = door;
    }

    public EvaluationTypeDTO getEvaluationType() {
        return evaluationType;
    }
    public void setEvaluationType(EvaluationTypeDTO evaluationType) {
        this.evaluationType = evaluationType;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }

    @Override
    public void setSyncSatus(SyncSatus syncSatus) {
        this.syncSatus = syncSatus;
    }

    @Override
    public SyncSatus getSyncSatus() {
        return this.syncSatus;
    }

    public boolean isDemonstration() {
        return demonstration;
    }

    public void setDemonstration(boolean demonstration) {
        this.demonstration = demonstration;
    }

    public String getDemonstrationDetails() {
        return demonstrationDetails;
    }

    public void setDemonstrationDetails(String demonstrationDetails) {
        this.demonstrationDetails = demonstrationDetails;
    }

    public Date getPerformedDate() {
        return performedDate;
    }

    public void setPerformedDate(Date performedDate) {
        this.performedDate = performedDate;
    }

    public Mentorship getMentorship() {
        Mentorship mentorship = new Mentorship();
        mentorship.setUuid(this.getUuid());
        mentorship.setCreatedAt(this.getCreatedAt());
        mentorship.setUpdatedAt(this.getUpdatedAt());
        mentorship.setLifeCycleStatus(this.getLifeCycleStatus());
        mentorship.setStartDate(this.getStartDate());
        mentorship.setEndDate(this.getEndDate());
        mentorship.setIterationNumber(this.getIterationNumber());
        mentorship.setDemonstration(this.isDemonstration());
        mentorship.setDemonstrationDetails(this.getDemonstrationDetails());
        mentorship.setPerformedDate(this.getPerformedDate());
        mentorship.setCreatedByUuid(this.getCreatedByuuid());
        mentorship.setUpdatedByUuid(this.getUpdatedByuuid());

        if(this.getMentor()!=null) {
            mentorship.setTutor(new Tutor(this.getMentor()));
        }
        if(this.getMentee()!=null) {
            mentorship.setTutored(new Tutored(this.getMentee()));
        }
        if(this.getSession()!=null) {
            mentorship.setSession(new Session(this.getSession()));
        }
        if(this.getForm()!=null) {
            mentorship.setForm(new Form(this.getForm()));
        }
        if(this.getCabinet()!=null) {
            mentorship.setCabinet(new Cabinet(this.getCabinet()));
        }
        if(this.getDoor()!=null) {
            mentorship.setDoor(new Door(this.getDoor()));
        }
        if(this.getEvaluationType()!=null) {
            mentorship.setEvaluationType(new EvaluationType(this.getEvaluationType()));
        }
        if(this.getEvaluationLocationDTO()!=null) {
            mentorship.setEvaluationLocation(new EvaluationLocation(this.getEvaluationLocationDTO()));
        }
        if(this.getAnswers()!=null) {
            List<Answer> answerList = new ArrayList<>();
            for (AnswerDTO answerDTO: this.getAnswers()) {
                answerList.add(new Answer(answerDTO));
            }
            mentorship.setAnswers(answerList);
        }
        return mentorship;
    }

    public String getMentorUuid() {
        return mentorUuid;
    }

    public void setMentorUuid(String mentorUuid) {
        this.mentorUuid = mentorUuid;
    }

    public String getMenteeUuid() {
        return menteeUuid;
    }

    public void setMenteeUuid(String menteeUuid) {
        this.menteeUuid = menteeUuid;
    }

    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getCabinetUuid() {
        return cabinetUuid;
    }

    public void setCabinetUuid(String cabinetUuid) {
        this.cabinetUuid = cabinetUuid;
    }

    public String getDoorUuid() {
        return doorUuid;
    }

    public void setDoorUuid(String doorUuid) {
        this.doorUuid = doorUuid;
    }

    public String getEvaluationTypeUuid() {
        return evaluationTypeUuid;
    }

    public void setEvaluationTypeUuid(String evaluationTypeUuid) {
        this.evaluationTypeUuid = evaluationTypeUuid;
    }

    public EvaluationLocationDTO getEvaluationLocationDTO() {
        return evaluationLocationDTO;
    }

    public void setEvaluationLocationDTO(EvaluationLocationDTO evaluationLocationDTO) {
        this.evaluationLocationDTO = evaluationLocationDTO;
    }

    public String getEvaluationLocationUuid() {
        return evaluationLocationUuid;
    }

    public void setEvaluationLocationUuid(String evaluationLocationUuid) {
        this.evaluationLocationUuid = evaluationLocationUuid;
    }
}
