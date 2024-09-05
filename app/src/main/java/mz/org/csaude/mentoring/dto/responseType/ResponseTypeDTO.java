package mz.org.csaude.mentoring.dto.responseType;




import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.model.responseType.ResponseType;



public class ResponseTypeDTO extends BaseEntityDTO {
    private String code;
    private String description;
    public ResponseTypeDTO(ResponseType responseType) {
        super(responseType);
        this.setCode(responseType.getCode());
        this.setDescription(responseType.getDescription());
    }

    public ResponseTypeDTO() {
        super();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public ResponseType getResponseType() {
        ResponseType responseType = new ResponseType();
        responseType.setUuid(this.getUuid());
        responseType.setCreatedAt(this.getCreatedAt());
        responseType.setUpdatedAt(this.getUpdatedAt());
        responseType.setLifeCycleStatus(this.getLifeCycleStatus());
        responseType.setCode(this.getCode());
        responseType.setDescription(this.getDescription());
        return responseType;
    }
}
