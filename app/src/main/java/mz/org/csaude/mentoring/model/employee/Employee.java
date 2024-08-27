package mz.org.csaude.mentoring.model.employee;

import androidx.core.util.PatternsCompat;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.employee.EmployeeDTO;
import mz.org.csaude.mentoring.dto.location.LocationDTO;
import mz.org.csaude.mentoring.model.location.Location;
import mz.org.csaude.mentoring.model.partner.Partner;
import mz.org.csaude.mentoring.model.professionalCategory.ProfessionalCategory;
import mz.org.csaude.mentoring.util.Utilities;

@Entity(tableName = Employee.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = ProfessionalCategory.class,
                        parentColumns = "id",
                        childColumns = Employee.COLUMN_PROFESSIONAL_CATEGORY,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Partner.class,
                        parentColumns = "id",
                        childColumns = Employee.COLUMN_PARTNER,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {Employee.COLUMN_PROFESSIONAL_CATEGORY}),
                @Index(value = {Employee.COLUMN_PARTNER}),
                @Index(value = {Employee.COLUMN_EMAIL}, unique = true),
                @Index(value = {Employee.COLUMN_PHONE_NUMBER}, unique = true),
                @Index(value = {Employee.COLUMN_NUIT}, unique = true)
        })
public class Employee extends BaseModel implements Listble {

    public static final String TABLE_NAME = "employee";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SURNAME = "surname";
    public static final String COLUMN_NUIT = "nuit";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PROFESSIONAL_CATEGORY = "professional_category_id";
    public static final String COLUMN_TRAINING_YEAR = "training_year";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_PARTNER = "partner_id";

    @ColumnInfo(name = COLUMN_NAME)
    private String name;

    @ColumnInfo(name = COLUMN_SURNAME)
    private String surname;

    @ColumnInfo(name = COLUMN_NUIT)
    private long nuit;

    @ColumnInfo(name = COLUMN_PROFESSIONAL_CATEGORY)
    private int professionalCategoryId;

    @Ignore
    @Relation(parentColumn = COLUMN_PROFESSIONAL_CATEGORY, entityColumn = "id")
    private ProfessionalCategory professionalCategory;

    @ColumnInfo(name = COLUMN_TRAINING_YEAR)
    private int trainingYear;

    @ColumnInfo(name = COLUMN_PHONE_NUMBER)
    private String phoneNumber;

    @ColumnInfo(name = COLUMN_EMAIL)
    private String email;

    @ColumnInfo(name = COLUMN_PARTNER)
    private int partnerId;

    @Ignore
    @Relation(parentColumn = COLUMN_PARTNER, entityColumn = "id")
    private Partner partner;

    @JsonIgnore
    @Ignore
    private List<Location> locations;

    public Employee() {
    }

    public Employee(String name, String surname, long nuit, ProfessionalCategory professionalCategory, int trainingYear, String phoneNumber, String email, Partner partner) {
        this.name = name;
        this.surname = surname;
        this.nuit = nuit;
        this.professionalCategory = professionalCategory;
        this.professionalCategoryId = professionalCategory.getId();
        this.trainingYear = trainingYear;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.partner = partner;
        this.partnerId = partner.getId();
    }

    public Employee(EmployeeDTO employeeDTO) {
        super(employeeDTO);
        this.setName(employeeDTO.getName());
        this.setSurname(employeeDTO.getSurname());
        this.setNuit(employeeDTO.getNuit());
        this.setTrainingYear(employeeDTO.getTrainingYear());
        this.setEmail(employeeDTO.getEmail());
        this.setPhoneNumber(employeeDTO.getPhoneNumber());
        if(employeeDTO.getLocationDTOSet() != null) {
            this.setLocations(retrieveLocations(employeeDTO.getLocationDTOSet()));
        }
        if(employeeDTO.getProfessionalCategoryDTO() != null) {
            this.setProfessionalCategory(new ProfessionalCategory(employeeDTO.getProfessionalCategoryDTO()));
        }
        if(employeeDTO.getPartnerDTO() != null) {
            this.setPartner(new Partner(employeeDTO.getPartnerDTO()));
        }
    }

    private List<Location> retrieveLocations(List<LocationDTO> locationDTOSet) {
        List<Location> locations = new ArrayList<>();
        for (LocationDTO locationDTO : locationDTOSet) {
            Location location = new Location(locationDTO);
            location.setEmployee(this);
            locations.add(location);
        }
        return locations;
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

    public ProfessionalCategory getProfessionalCategory() {
        return professionalCategory;
    }

    public void setProfessionalCategory(ProfessionalCategory professionalCategory) {
        this.professionalCategory = professionalCategory;
        this.professionalCategoryId = professionalCategory.getId();
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

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
        this.partnerId = partner.getId();
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getCode() {
        return null;
    }

    public String getFullName() {
        return this.name + " " + this.surname;
    }

    @Override
    public String validade() {
        if (StringUtils.isEmpty(getName())) return "Campo nome não pode estar vazio ";
        if (getName().length() <= 2) return "Campo nome tem que ter mais de dois caracteres";
        if (StringUtils.isEmpty(getSurname())) return "Campo apelido não pode estar vazio";
        if (getSurname().length() <= 2) return "Campo apelido tem que ter mais de dois caracteres";
        if (StringUtils.isEmpty(getPhoneNumber())) return "Campo Telefone não pode estar vazio";
        if (!(getPhoneNumber().startsWith("8") && getPhoneNumber().length() == 9)) return "Por favor indique um Telefone válido";
        if (StringUtils.isEmpty(getEmail())) return "Campo Email não pode estar vazio";
        if (!PatternsCompat.EMAIL_ADDRESS.matcher(getEmail()).matches()) return "Por favor indique um endereço de Email válido";
        if (this.getProfessionalCategory() == null) return "Campo Categoria Profissional não pode estar vazio";
        if (!Utilities.listHasElements(this.locations)) return "Por favor indique a unidade sanitária.";
        if (getNuit() == 0) return "Campo NUIT não pode estar vazio";
        if (Long.toString(getNuit()).length() != 9) return "Campo do NUIT tem que ter 9 dígitos";
        if (getTrainingYear() == 0) return "Campo Ano não pode estar vazio";
        if (getTrainingYear() < 1960 || getTrainingYear() > Calendar.getInstance().get(Calendar.YEAR)) return "Por favor indique um ano válido";
        return super.validade();
    }

    public void addLocation(Location location) {
        if (this.locations == null) this.locations = new ArrayList<>();
        location.setEmployee(this);
        this.locations.add(location);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", nuit=" + nuit +
                ", professionalCategory=" + professionalCategory +
                ", trainingYear=" + trainingYear +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", partner=" + partner +
                ", locations=" + locations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        if (!super.equals(o)) return false;
        Employee employee = (Employee) o;
        return nuit == employee.nuit && Objects.equals(phoneNumber, employee.phoneNumber) && Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nuit, phoneNumber, email);
    }

    public int getProfessionalCategoryId() {
        return professionalCategoryId;
    }

    public void setProfessionalCategoryId(int professionalCategoryId) {
        this.professionalCategoryId = professionalCategoryId;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }
}
