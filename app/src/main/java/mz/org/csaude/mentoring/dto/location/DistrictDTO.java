package mz.org.csaude.mentoring.dto.location;




import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.model.location.Province;




public class DistrictDTO extends BaseEntityDTO {

    private String description;
    private ProvinceDTO provinceDTO;

    public DistrictDTO(District district) {
        super(district);
        this.setDescription(district.getDescription());
        if (district.getProvince() != null)  this.setProvinceDTO(new ProvinceDTO(district.getProvince()));
    }

    public DistrictDTO() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProvinceDTO getProvinceDTO() {
        return provinceDTO;
    }

    public void setProvinceDTO(ProvinceDTO provinceDTO) {
        this.provinceDTO = provinceDTO;
    }
    public District getDistrict() {
        District district = new District();
        district.setUuid(this.getUuid());
        district.setCreatedAt(this.getCreatedAt());
        district.setUpdatedAt(this.getUpdatedAt());
        district.setLifeCycleStatus(this.getLifeCycleStatus());
        district.setDescription(this.getDescription());
        if(this.getProvinceDTO()!=null) district.setProvince(new Province(this.getProvinceDTO()));
        return district;
    }
}
