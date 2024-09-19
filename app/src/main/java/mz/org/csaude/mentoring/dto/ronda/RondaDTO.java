package mz.org.csaude.mentoring.dto.ronda;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;




import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.common.Syncable;
import mz.org.csaude.mentoring.dto.location.HealthFacilityDTO;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.ronda.RondaMentor;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;




public class RondaDTO extends BaseEntityDTO {
    private String description;
    private Date startDate;
    private Date endDate;

    private String mentorType;
    @JsonProperty(value = "rondaTypeDTO")
    private RondaTypeDTO rondaType;
    private HealthFacilityDTO healthFacility;
    private List<RondaMenteeDTO> rondaMentees;
    private List<RondaMentorDTO> rondaMentors;
    public RondaDTO() {
    }
    public RondaDTO(Ronda ronda) {
        super(ronda);
        this.setDescription(ronda.getDescription());
        this.setStartDate(ronda.getStartDate());
        this.setMentorType(ronda.getMentorType());
        if(ronda.getEndDate()!=null) {
            this.setEndDate(ronda.getEndDate());
        }
        if(ronda.getRondaType()!=null) {
            this.setRondaType(new RondaTypeDTO(ronda.getRondaType()));
        }
        if(ronda.getHealthFacility()!=null) {
            this.setHealthFacility(new HealthFacilityDTO(ronda.getHealthFacility()));
        }
        if (Utilities.listHasElements(ronda.getRondaMentees())) {
            List<RondaMenteeDTO> rondaMenteeDTOS = ronda.getRondaMentees().stream()
                    .map(RondaMenteeDTO::new)
                    .collect(Collectors.toList());
            this.setRondaMentees(rondaMenteeDTOS);
        }
        if (Utilities.listHasElements(ronda.getRondaMentors())) {
            List<RondaMentorDTO> rondaMentorDTOS = ronda.getRondaMentors().stream()
                    .map(RondaMentorDTO::new)
                    .collect(Collectors.toList());
            this.setRondaMentors(rondaMentorDTOS);
        }

    }

    public String getMentorType() {
        return mentorType;
    }

    public void setMentorType(String mentorType) {
        this.mentorType = mentorType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public RondaTypeDTO getRondaType() {
        return rondaType;
    }

    public void setRondaType(RondaTypeDTO rondaType) {
        this.rondaType = rondaType;
    }

    public HealthFacilityDTO getHealthFacility() {
        return healthFacility;
    }

    public void setHealthFacility(HealthFacilityDTO healthFacility) {
        this.healthFacility = healthFacility;
    }

    public List<RondaMenteeDTO> getRondaMentees() {
        return rondaMentees;
    }

    public void setRondaMentees(List<RondaMenteeDTO> rondaMentees) {
        this.rondaMentees = rondaMentees;
    }

    public List<RondaMentorDTO> getRondaMentors() {
        return rondaMentors;
    }

    public void setRondaMentors(List<RondaMentorDTO> rondaMentors) {
        this.rondaMentors = rondaMentors;
    }
    public Ronda getRonda() {
        Ronda ronda = new Ronda();
        ronda.setUuid(this.getUuid());
        ronda.setDescription(this.getDescription());
        ronda.setStartDate(this.getStartDate());
        ronda.setEndDate(this.getEndDate());
        ronda.setCreatedAt(this.getCreatedAt());
        ronda.setUpdatedAt(this.getUpdatedAt());
        ronda.setLifeCycleStatus(this.getLifeCycleStatus());
        ronda.setMentorType(this.getMentorType());
        if(this.getHealthFacility()!=null) {
            ronda.setHealthFacility(this.getHealthFacility().getHealthFacilityObj());
        }
        if(this.getRondaType()!=null) {
            ronda.setRondaType(this.getRondaType().getRondaType());
        }
        if(Utilities.listHasElements(this.getRondaMentors())) {
            List<RondaMentor> rondaMentors = new ArrayList<>();
            for (RondaMentorDTO rondaMentorDTO: this.getRondaMentors()) {
                RondaMentor rondaMentor = rondaMentorDTO.getRondaMentor();
                rondaMentors.add(rondaMentor);
            }
            ronda.setRondaMentors(rondaMentors);
        }
        if(Utilities.listHasElements(this.getRondaMentees())) {
            List<RondaMentee> rondaMentees = new ArrayList<>();
            for (RondaMenteeDTO rondaMenteeDTO: this.getRondaMentees()) {
                RondaMentee rondaMentee = rondaMenteeDTO.getRondaMentee();
                rondaMentees.add(rondaMentee);
            }
            ronda.setRondaMentees(rondaMentees);
        }
        return ronda;
    }
}
