package mz.org.csaude.mentoring.dto.session;




import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.common.Syncable;
import mz.org.csaude.mentoring.model.session.SessionStatus;
import mz.org.csaude.mentoring.util.SyncSatus;




public class SessionStatusDTO extends BaseEntityDTO implements Syncable {
    private String code;
    private String description;
    public SessionStatusDTO(SessionStatus sessionStatus) {
        super(sessionStatus);
        this.setCode(sessionStatus.getCode());
        this.setDescription(sessionStatus.getDescription());
    }

    public SessionStatusDTO() {
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

    @Override
    public void setSyncSatus(SyncSatus syncSatus) {
        this.syncSatus = syncSatus;
    }

    @Override
    public SyncSatus getSyncSatus() {
        return this.syncSatus;
    }

    public SessionStatus getSessionStatus() {
        SessionStatus sessionStatus = new SessionStatus();
        sessionStatus.setUuid(this.getUuid());
        sessionStatus.setCode(this.getCode());
        sessionStatus.setDescription(this.getDescription());
        return sessionStatus;
    }
}
