package mz.org.csaude.mentoring.dto.location;




import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.model.location.HealthFacility;




public class HealthFacilityDTO extends BaseEntityDTO {

    private String healthFacility;

    private DistrictDTO districtDTO;

    public HealthFacilityDTO(HealthFacility healthFacility){
        super(healthFacility);
        this.setHealthFacility(healthFacility.getDescription());
        if (healthFacility.getDistrict() != null) this.setDistrictDTO(new DistrictDTO(healthFacility.getDistrict()));

    }

    public HealthFacilityDTO() {
    }

    public String getHealthFacility() {
        return healthFacility;
    }

    public void setHealthFacility(String healthFacility) {
        this.healthFacility = healthFacility;
    }

    public DistrictDTO getDistrictDTO() {
        return districtDTO;
    }

    public void setDistrictDTO(DistrictDTO districtDTO) {
        this.districtDTO = districtDTO;
    }
    public HealthFacility getHealthFacilityObj() {
        HealthFacility healthFacility = new HealthFacility();
        healthFacility.setUuid(this.getUuid());
        healthFacility.setCreatedAt(this.getCreatedAt());
        healthFacility.setUpdatedAt(this.getUpdatedAt());
        healthFacility.setLifeCycleStatus(this.getLifeCycleStatus());
        healthFacility.setDescription(this.getHealthFacility());
        if(this.getDistrictDTO()!=null) healthFacility.setDistrict(new District(this.getDistrictDTO()));
        return healthFacility;
    }
}
