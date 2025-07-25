package mz.org.csaude.mentoring.base.service;

import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;

public class SuccessResponse<T extends BaseEntityDTO> {
    private int status;
    private String message;
    private T data;

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
