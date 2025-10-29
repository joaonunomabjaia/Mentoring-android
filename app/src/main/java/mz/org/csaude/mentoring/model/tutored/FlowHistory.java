// app module (Android) — mz.org.csaude.mentoring.model.tutored.FlowHistory
package mz.org.csaude.mentoring.model.tutored;

import com.google.gson.annotations.SerializedName;
import androidx.annotation.Nullable;
import java.io.Serializable;

public class FlowHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("estagio")
    private EnumFlowHistory estagio;

    @SerializedName("estado")
    private EnumFlowHistoryProgressStatus estado;

    @SerializedName("classificação")
    @Nullable
    private Integer classificacao;

    public FlowHistory() {}

    public FlowHistory(EnumFlowHistory estagio,
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
