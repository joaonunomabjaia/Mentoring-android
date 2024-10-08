package mz.org.csaude.mentoring.dto.session;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;
import java.util.List;




import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.common.Syncable;
import mz.org.csaude.mentoring.dto.form.FormDTO;
import mz.org.csaude.mentoring.dto.mentorship.MentorshipDTO;
import mz.org.csaude.mentoring.dto.ronda.RondaDTO;
import mz.org.csaude.mentoring.dto.tutored.TutoredDTO;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.session.SessionStatus;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.SyncSatus;




public class SessionDTO extends BaseEntityDTO implements Syncable {
    private Date startDate;
    private Date endDate;
    private Date performedDate;
    private Date nextSessionDate;
    private SessionStatusDTO sessionStatus;
    private String reason;
    private FormDTO form;
    private RondaDTO ronda;
    private TutoredDTO mentee;
    private boolean demonstration;
    private String demonstrationDetails;
    private String strongPoints;
    private String pointsToImprove;
    private String workPlan;
    private String observations;

    private List<MentorshipDTO> mentorships;

    public SessionDTO() {
    }

    public SessionDTO(Session session) {
        super(session);
        this.setStartDate(session.getStartDate());
        this.setEndDate(session.getEndDate());
        this.setNextSessionDate(session.getNextSessionDate());
        this.setPerformedDate(session.getPerformedDate());
        this.setPointsToImprove(session.getPointsToImprove());
        this.setStrongPoints(session.getStrongPoints());
        this.setObservations(session.getObservations());
        this.setWorkPlan(session.getWorkPlan());
        if(session.getStatus()!=null) {
            this.setSessionStatus(new SessionStatusDTO(session.getStatus()));
        }
        if(session.getForm()!=null) {
            this.setForm(new FormDTO(session.getForm()));
        }
        if(session.getRonda()!=null) {
            this.setRonda(new RondaDTO(session.getRonda()));
        }
        if(session.getTutored()!=null) {
            this.setMentee(new TutoredDTO(session.getTutored()));
        }
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

    public Date getPerformedDate() {
        return performedDate;
    }

    public void setPerformedDate(Date performedDate) {
        this.performedDate = performedDate;
    }

    public SessionStatusDTO getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(SessionStatusDTO sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public FormDTO getForm() {
        return form;
    }

    public void setForm(FormDTO form) {
        this.form = form;
    }

    public RondaDTO getRonda() {
        return ronda;
    }

    public void setRonda(RondaDTO ronda) {
        this.ronda = ronda;
    }

    public TutoredDTO getMentee() {
        return mentee;
    }

    public void setMentee(TutoredDTO mentee) {
        this.mentee = mentee;
    }

    public String getStrongPoints() {
        return strongPoints;
    }

    public void setStrongPoints(String strongPoints) {
        this.strongPoints = strongPoints;
    }

    public String getPointsToImprove() {
        return pointsToImprove;
    }

    public void setPointsToImprove(String pointsToImprove) {
        this.pointsToImprove = pointsToImprove;
    }

    public String getWorkPlan() {
        return workPlan;
    }

    public void setWorkPlan(String workPlan) {
        this.workPlan = workPlan;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public List<MentorshipDTO> getMentorships() {
        return mentorships;
    }

    public void setMentorships(List<MentorshipDTO> mentorships) {
        this.mentorships = mentorships;
    }

    public Session getSession() {
        Session session = new Session();
        session.setUpdatedAt(this.getUpdatedAt());
        session.setUuid(this.getUuid());
        session.setCreatedAt(this.getCreatedAt());
        session.setLifeCycleStatus(this.getLifeCycleStatus());
        session.setStartDate(this.getStartDate());
        session.setEndDate(this.getEndDate());
        session.setPerformedDate(this.getPerformedDate());
        session.setPointsToImprove(this.getPointsToImprove());
        session.setStrongPoints(this.getStrongPoints());
        session.setObservations(this.getObservations());
        session.setWorkPlan(this.getWorkPlan());
        session.setNextSessionDate(this.getNextSessionDate());
        if(session.getStatus()!=null) {
            session.setStatus(new SessionStatus(this.getSessionStatus()));
        }
        if(session.getForm()!=null) {
            session.setForm(new Form(this.getForm()));
        }
        if(session.getRonda()!=null) {
            session.setRonda(new Ronda(this.getRonda()));
        }
        if(session.getTutored()!=null) {
            session.setTutored(new Tutored(this.getMentee()));
        }
        return session;
    }

    @Override
    public void setSyncSatus(SyncSatus syncSatus) {

    }

    @Override
    public SyncSatus getSyncSatus() {
        return null;
    }

    public Date getNextSessionDate() {
        return nextSessionDate;
    }

    public void setNextSessionDate(Date nextSessionDate) {
        this.nextSessionDate = nextSessionDate;
    }
}
