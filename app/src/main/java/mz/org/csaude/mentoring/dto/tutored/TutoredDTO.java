package mz.org.csaude.mentoring.dto.tutored;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.dto.employee.EmployeeDTO;
import mz.org.csaude.mentoring.model.tutored.FlowHistory;
import mz.org.csaude.mentoring.model.tutored.Tutored;

/**
 * @author Jose Julai Ritsure
 */
public class TutoredDTO extends BaseEntityDTO {

    private EmployeeDTO employeeDTO;

    private boolean zeroEvaluationDone;

    private double zeroEvaluationScore;

    /**
     * NEW: preferred list field for flow history (array in JSON)
     */
    @SerializedName("flowHistoryMenteeAuxDTO")
    private List<FlowHistory> flowHistoryMenteeAuxDTOList;

    /**
     * Legacy single object field (kept for backward compatibility with older payloads).
     * If present, we will wrap it into a one-element list.
     */
    @SerializedName("flowHistoryMenteeAuxDTO")
    @Deprecated
    private FlowHistory flowHistoryMenteeAuxDTO;

    public TutoredDTO() {
    }

    public TutoredDTO(Tutored tutored) {
        super(tutored);
        setZeroEvaluationScore(tutored.getZeroEvaluationScore());
        setZeroEvaluationDone(tutored.isZeroEvaluationDone());
        this.setEmployeeDTO(tutored.getEmployee() != null ? new EmployeeDTO(tutored.getEmployee()) : null);

        // Map entity -> DTO list (preferred)
        if (tutored.getFlowHistory() != null && !tutored.getFlowHistory().isEmpty()) {
            this.flowHistoryMenteeAuxDTOList = new ArrayList<>(tutored.getFlowHistory());
        } else {
            this.flowHistoryMenteeAuxDTOList = null;
        }

        // No need to populate the legacy single field on output, but you may set it if required:
        // this.flowHistoryMenteeAuxDTO = (flowHistoryMenteeAuxDTOList != null && !flowHistoryMenteeAuxDTOList.isEmpty())
        //         ? flowHistoryMenteeAuxDTOList.get(0) : null;
    }

    public EmployeeDTO getEmployeeDTO() {
        return employeeDTO;
    }

    public void setEmployeeDTO(EmployeeDTO employeeDTO) {
        this.employeeDTO = employeeDTO;
    }

    public boolean isZeroEvaluationDone() {
        return zeroEvaluationDone;
    }

    public void setZeroEvaluationDone(boolean zeroEvaluationDone) {
        this.zeroEvaluationDone = zeroEvaluationDone;
    }

    public double getZeroEvaluationScore() {
        return zeroEvaluationScore;
    }

    public void setZeroEvaluationScore(double zeroEvaluationScore) {
        this.zeroEvaluationScore = zeroEvaluationScore;
    }

    // -------- NEW / PREFERRED LIST ACCESSORS --------
    public List<FlowHistory> getFlowHistoryMenteeAuxDTOList() {
        return flowHistoryMenteeAuxDTOList;
    }

    public void setFlowHistoryMenteeAuxDTOList(List<FlowHistory> flowHistoryMenteeAuxDTOList) {
        this.flowHistoryMenteeAuxDTOList = flowHistoryMenteeAuxDTOList;
    }

    // -------- LEGACY SINGLE FIELD (DEPRECATED) --------
    @Deprecated
    public FlowHistory getFlowHistoryMenteeAuxDTO() {
        return flowHistoryMenteeAuxDTO;
    }

    @Deprecated
    public void setFlowHistoryMenteeAuxDTO(FlowHistory flowHistoryMenteeAuxDTO) {
        this.flowHistoryMenteeAuxDTO = flowHistoryMenteeAuxDTO;
    }

    /**
     * Map DTO -> Entity (wrap legacy single value into a list if list is null/empty)
     */
    public Tutored getMentee() {
        Tutored tutored = new Tutored();
        tutored.setUuid(this.getUuid());
        tutored.setZeroEvaluationDone(this.isZeroEvaluationDone());
        tutored.setZeroEvaluationScore(this.getZeroEvaluationScore());
        tutored.setCreatedAt(this.getCreatedAt());
        tutored.setUpdatedAt(this.getUpdatedAt());
        tutored.setLifeCycleStatus(this.getLifeCycleStatus());
        tutored.setCreatedByUuid(this.getCreatedByuuid());
        tutored.setUpdatedByUuid(this.getUpdatedByuuid());

        if (this.getEmployeeDTO() != null) {
            tutored.setEmployee(this.getEmployeeDTO().getEmployee());
        }

        // Preferred: list
        List<FlowHistory> list = this.flowHistoryMenteeAuxDTOList;

        // Backward compat: if list is null/empty but legacy single exists, wrap it
        if ((list == null || list.isEmpty()) && this.flowHistoryMenteeAuxDTO != null) {
            list = new ArrayList<>();
            list.add(this.flowHistoryMenteeAuxDTO);
        }

        tutored.setFlowHistory(list);
        return tutored;
    }
}
