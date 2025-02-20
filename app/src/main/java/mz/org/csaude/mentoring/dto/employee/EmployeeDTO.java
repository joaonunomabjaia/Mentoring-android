package mz.org.csaude.mentoring.dto.employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.location.LocationDTO;
import mz.org.csaude.mentoring.dto.partner.PartnerDTO;
import mz.org.csaude.mentoring.dto.professionalcategory.ProfessionalCategoryDTO;
import mz.org.csaude.mentoring.model.employee.Employee;
import mz.org.csaude.mentoring.model.location.Location;
import mz.org.csaude.mentoring.model.partner.Partner;


public class EmployeeDTO extends BaseEntityDTO {

    private String name;

    private String surname;

    private long nuit;

    private ProfessionalCategoryDTO professionalCategoryDTO;

    private int trainingYear;

    private String phoneNumber;

    private String email;

    private PartnerDTO partnerDTO;

    private List<LocationDTO> locationDTOSet;

    public EmployeeDTO() {
    }

    public EmployeeDTO(Employee employee) {
        super(employee);
        this.setName(employee.getName());
        this.setSurname(employee.getSurname());
        this.setNuit(employee.getNuit());
        this.setEmail(employee.getEmail());
        this.setPhoneNumber(employee.getPhoneNumber());
        this.setTrainingYear(employee.getTrainingYear());
        if(employee.getLocations()!=null) {
            this.setLocationDTOSet(setLocations(employee.getLocations()));
        }
        if(employee.getProfessionalCategory() != null) this.setProfessionalCategoryDTO(new ProfessionalCategoryDTO(employee.getProfessionalCategory()));
       if(employee.getPartner() != null) this.setPartnerDTO(new PartnerDTO(employee.getPartner()));
    }

    private List<LocationDTO> setLocations(List<Location> locationSet) {
        List<LocationDTO> locationDTOSet = new ArrayList<>();

        for (Location location : locationSet) {
            locationDTOSet.add(new LocationDTO(location));
        }
        return locationDTOSet;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getNuit() {
        return nuit;
    }

    public void setNuit(long nuit) {
        this.nuit = nuit;
    }

    public int getTrainingYear() {
        return trainingYear;
    }

    public void setTrainingYear(int trainingYear) {
        this.trainingYear = trainingYear;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ProfessionalCategoryDTO getProfessionalCategoryDTO() {
        return professionalCategoryDTO;
    }

    public void setProfessionalCategoryDTO(ProfessionalCategoryDTO professionalCategoryDTO) {
        this.professionalCategoryDTO = professionalCategoryDTO;
    }

    public PartnerDTO getPartnerDTO() {
        return partnerDTO;
    }

    public void setPartnerDTO(PartnerDTO partnerDTO) {
        this.partnerDTO = partnerDTO;
    }

    public List<LocationDTO> getLocationDTOSet() {
        return locationDTOSet;
    }

    public void setLocationDTOSet(List<LocationDTO> locationDTOSet) {
        this.locationDTOSet = locationDTOSet;
    }

    public Employee getEmployee() {
        Employee employee = new Employee();
        employee.setUuid(this.getUuid());
        employee.setCreatedAt(this.getCreatedAt());
        employee.setUpdatedAt(this.getUpdatedAt());
        employee.setName(this.getName());
        employee.setSurname(this.getSurname());
        employee.setNuit(this.getNuit());
        employee.setTrainingYear(this.getTrainingYear());
        employee.setPhoneNumber(this.getPhoneNumber());
        employee.setEmail(this.getEmail());
        employee.setLifeCycleStatus(this.getLifeCycleStatus());
        employee.setCreatedByUuid(this.getCreatedByuuid());
        employee.setUpdatedByUuid(this.getUpdatedByuuid());
        if(this.getProfessionalCategoryDTO()!=null) {
            employee.setProfessionalCategory(this.getProfessionalCategoryDTO().getProfessionalCategory());
        }
        if(this.getPartnerDTO()!=null) {
            employee.setPartner(this.partnerDTO.getPartner());
        }
        if(this.getLocationDTOSet()!=null && !this.getLocationDTOSet().isEmpty()) {
            List<Location> locations = new ArrayList<>();
            for (LocationDTO locationDTO: this.getLocationDTOSet()) {
                Location location = locationDTO.getLocation();
                locations.add(location);
            }
            employee.setLocations(locations);
        }
        return employee;
    }
}
