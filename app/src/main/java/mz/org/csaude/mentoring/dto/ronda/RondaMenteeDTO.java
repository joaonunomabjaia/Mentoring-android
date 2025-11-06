package mz.org.csaude.mentoring.dto.ronda;

import java.util.Date;
import java.util.List;

import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.tutored.TutoredDTO;
import mz.org.csaude.mentoring.model.ronda.RondaMentee;
import mz.org.csaude.mentoring.model.tutored.FlowHistory;

public class RondaMenteeDTO extends BaseEntityDTO {
    private Date startDate;
    private Date endDate;
    private TutoredDTO mentee;
    private RondaDTO ronda;

    // Updated: carry flow history as a LIST at the RondaMentee level
    private List<FlowHistory> flowHistory;

    public RondaMenteeDTO() { }

    public RondaMenteeDTO(RondaMentee rondaMentee) {
        super(rondaMentee);
        this.setStartDate(rondaMentee.getStartDate());
        if (rondaMentee.getEndDate() != null) {
            this.setEndDate(rondaMentee.getEndDate());
        }
        if (rondaMentee.getTutored() != null) {
            this.setMentee(new TutoredDTO(rondaMentee.getTutored()));
            // Pull the mentee's flow history list
            this.setFlowHistory(rondaMentee.getTutored().getFlowHistory());
        }
        if (rondaMentee.getRonda() != null) {
            this.setRonda(new RondaDTO(rondaMentee.getRonda()));
        }

        // If (optionally) RondaMentee itself has flowHistory(list), you can reflect and prefer it:
        if (hasRondaMenteeFlowHistoryList(rondaMentee)) {
            @SuppressWarnings("unchecked")
            List<FlowHistory> rmList = (List<FlowHistory>) invokeGetter(rondaMentee, "getFlowHistory");
            if (rmList != null) this.setFlowHistory(rmList);
        }
    }

    // Getters / Setters
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public TutoredDTO getMentee() { return mentee; }
    public void setMentee(TutoredDTO mentee) { this.mentee = mentee; }

    public RondaDTO getRonda() { return ronda; }
    public void setRonda(RondaDTO ronda) { this.ronda = ronda; }

    public List<FlowHistory> getFlowHistory() { return flowHistory; }
    public void setFlowHistory(List<FlowHistory> flowHistory) { this.flowHistory = flowHistory; }

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

        // If entity supports a flowHistory list, set it
        if (canSetRondaMenteeFlowHistoryList(rondaMentee)) {
            invokeSetter(rondaMentee, "setFlowHistory", List.class, this.getFlowHistory());
        }
        return rondaMentee;
    }

    // ---- helpers (compile even if entity doesn't yet have flow history list) ----
    private boolean hasRondaMenteeFlowHistoryList(RondaMentee rm) {
        try {
            rm.getClass().getMethod("getFlowHistory");
            Object val = rm.getClass().getMethod("getFlowHistory").invoke(rm);
            return (val instanceof List);
        } catch (Exception ignore) {
            return false;
        }
    }

    private boolean canSetRondaMenteeFlowHistoryList(RondaMentee rm) {
        try {
            rm.getClass().getMethod("setFlowHistory", List.class);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    private Object invokeGetter(Object target, String method) {
        try {
            return target.getClass().getMethod(method).invoke(target);
        } catch (Exception e) {
            return null;
        }
    }

    private void invokeSetter(Object target, String method, Class<?> argType, Object value) {
        try {
            target.getClass().getMethod(method, argType).invoke(target, value);
        } catch (Exception ignore) { }
    }
}
