package mz.org.csaude.mentoring.dto.tutored;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import mz.org.csaude.mentoring.model.tutored.EnumFlowHistory;
import mz.org.csaude.mentoring.model.tutored.EnumFlowHistoryProgressStatus;

public class FlowHistoryMenteeAuxDTO {
    @SerializedName("estagio")
    private EnumFlowHistory estagio;

    @SerializedName("estado")
    private EnumFlowHistoryProgressStatus estado;

    // Use a number (0–100). Keep null if unknown/not applicable.
    @SerializedName("classificação")
    @Nullable
    private Integer classificacao;

    public FlowHistoryMenteeAuxDTO() {}

    public FlowHistoryMenteeAuxDTO(EnumFlowHistory estagio,
                       EnumFlowHistoryProgressStatus estado,
                       @Nullable Integer classificacao) {
        this.estagio = estagio;
        this.estado = estado;
        this.classificacao = classificacao;
    }

    public EnumFlowHistory getEstagio() { return estagio; }
    public void setEstagio(EnumFlowHistory estagio) { this.estagio = estagio; }

    public EnumFlowHistoryProgressStatus getEstado() { return estado; }
    public void setEstado(EnumFlowHistoryProgressStatus estado) { this.estado = estado; }

    @Nullable public Integer getClassificacao() { return classificacao; }
    public void setClassificacao(@Nullable Integer classificacao) { this.classificacao = classificacao; }
}
