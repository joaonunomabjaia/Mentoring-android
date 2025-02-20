package mz.org.csaude.mentoring.dto.ronda;

import java.util.Date;




import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.tutor.TutorDTO;
import mz.org.csaude.mentoring.model.ronda.RondaMentor;



public class RondaMentorDTO extends BaseEntityDTO {
    private Date startDate;
    private Date endDate;
    private TutorDTO mentor;
    private RondaDTO ronda;
    public RondaMentorDTO(RondaMentor rondaMentor) {
        super(rondaMentor);
        this.setStartDate(rondaMentor.getStartDate());
        this.setEndDate(rondaMentor.getEndDate());
        if(rondaMentor.getEndDate()!=null) {
            this.setEndDate(rondaMentor.getEndDate());
        }
        if(rondaMentor.getTutor()!=null) {
            this.setMentor(new TutorDTO(rondaMentor.getTutor()));
        }
        if(rondaMentor.getRonda()!=null) {
            this.setRonda(new RondaDTO(rondaMentor.getRonda()));
        }
    }
    public RondaMentorDTO() {
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

    public RondaDTO getRonda() {
        return ronda;
    }

    public void setRonda(RondaDTO ronda) {
        this.ronda = ronda;
    }
    public RondaMentor getRondaMentor() {
        RondaMentor rondaMentor = new RondaMentor();
        rondaMentor.setUuid(this.getUuid());
        rondaMentor.setStartDate(this.getStartDate());
        rondaMentor.setEndDate(this.getEndDate());
        rondaMentor.setCreatedAt(this.getCreatedAt());
        rondaMentor.setUpdatedAt(this.getUpdatedAt());
        rondaMentor.setLifeCycleStatus(this.getLifeCycleStatus());
        rondaMentor.setCreatedByUuid(this.getCreatedByuuid());
        rondaMentor.setUpdatedByUuid(this.getUpdatedByuuid());
        if(this.getMentor()!=null) {
            rondaMentor.setTutor(this.getMentor().getTutor());
        }
        if(this.getRonda()!=null) {
            rondaMentor.setRonda(this.getRonda().getRonda());
        }
        return rondaMentor;
    }
}
