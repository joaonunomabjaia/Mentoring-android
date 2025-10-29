package mz.org.csaude.mentoring.dto.ronda;

import java.util.Date;

import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.tutored.TutoredDTO;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.tutored.FlowHistory;


public class RondaMenteeDTO extends BaseEntityDTO {
    private Date startDate;
    private Date endDate;
    private TutoredDTO mentee;
    private RondaDTO ronda;

    // NEW: carry flow history at the RondaMentee level
    private FlowHistory flowHistory;

    public RondaMenteeDTO(RondaMentee rondaMentee) {
        super(rondaMentee);
        this.setStartDate(rondaMentee.getStartDate());
        if (rondaMentee.getEndDate() != null) {
            this.setEndDate(rondaMentee.getEndDate());
        }
        if (rondaMentee.getTutored() != null) {
            this.setMentee(new TutoredDTO(rondaMentee.getTutored()));
        }
        if (rondaMentee.getRonda() != null) {
            this.setRonda(new RondaDTO(rondaMentee.getRonda()));
        }

        this.setFlowHistory(rondaMentee.getTutored().getFlowHistory());
    }

    public RondaMenteeDTO() { }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public TutoredDTO getMentee() { return mentee; }
    public void setMentee(TutoredDTO mentee) { this.mentee = mentee; }

    public RondaDTO getRonda() { return ronda; }
    public void setRonda(RondaDTO ronda) { this.ronda = ronda; }

    public FlowHistory getFlowHistory() { return flowHistory; }
    public void setFlowHistory(FlowHistory flowHistory) { this.flowHistory = flowHistory; }

    public RondaMentee getRondaMentee() {
        RondaMentee rondaMentee = new RondaMentee();
        rondaMentee.setUuid(this.getUuid());
        rondaMentee.setStartDate(this.getStartDate());
        rondaMentee.setEndDate(this.getEndDate());
        rondaMentee.setCreatedAt(this.getCreatedAt());
        rondaMentee.setUpdatedAt(this.getUpdatedAt());
        rondaMentee.setLifeCycleStatus(this.getLifeCycleStatus());
        rondaMentee.setCreatedByUuid(this.getCreatedByuuid());
        rondaMentee.setUpdatedByUuid(this.getUpdatedByuuid());

        if (this.getMentee() != null) {
            rondaMentee.setTutored(this.getMentee().getMentee());
        }
        if (this.getRonda() != null) {
            rondaMentee.setRonda(this.getRonda().getRonda());
        }
        return rondaMentee;
    }

    // ---- helpers (compile even if entity doesn't yet have flow history) ----
    private boolean hasRondaMenteeFlowHistory(RondaMentee rm) {
        try {
            rm.getClass().getMethod("getFlowHistory");
            Object val = rm.getClass().getMethod("getFlowHistory").invoke(rm);
            return val != null;
        } catch (Exception ignore) {
            return false;
        }
    }

    private boolean canSetRondaMenteeFlowHistory(RondaMentee rm) {
        try {
            rm.getClass().getMethod("setFlowHistory", FlowHistory.class);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }
}
