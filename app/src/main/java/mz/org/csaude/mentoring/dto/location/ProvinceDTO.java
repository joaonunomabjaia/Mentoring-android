package mz.org.csaude.mentoring.dto.location;

import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.location.Province;

public class ProvinceDTO extends BaseEntityDTO {

    private String designation;
    public ProvinceDTO(Province province) {
        super(province);
        this.setDesignation(province.getDescription());
    }
    public String getDesignation() {
        return designation;
    }
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Province getProvince() {
        Province province = new Province();
        province.setUuid(this.getUuid());
        province.setCreatedAt(this.getCreatedAt());
        province.setDescription(this.getDesignation());
        province.setUpdatedAt(this.getUpdatedAt());
        return province;
    }

    public ProvinceDTO() {
    }
}
