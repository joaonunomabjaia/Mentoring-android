package mz.org.csaude.mentoring.dto.partner;


import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.partner.Partner;

public class PartnerDTO extends BaseEntityDTO {
    private String name;
    private String description;

    public PartnerDTO() {

    }

    public PartnerDTO(Partner partner) {
        super(partner);
        this.setName(partner.getName());
        this.setDescription(partner.getDescription());
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Partner getPartner() {
        Partner partner = new Partner();
        partner.setUpdatedAt(this.getUpdatedAt());
        partner.setName(this.getName());
        partner.setUuid(this.getUuid());
        partner.setDescription(this.getDescription());
        partner.setCreatedAt(this.getCreatedAt());
        partner.setLifeCycleStatus(this.getLifeCycleStatus());
        return partner;
    }
}
