package mz.org.csaude.mentoring.model.sync;

import androidx.work.WorkInfo;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.model.BaseModel;

public class SyncStatus extends BaseModel {
    private final String tag;
    private WorkInfo.State state;

    public SyncStatus(String tag, WorkInfo.State state) {
        this.tag = tag;
        this.state = state;
    }

    public String getTag() {
        return tag;
    }

    public WorkInfo.State getState() {
        return state;
    }

    public void setState(WorkInfo.State state) {
        this.state = state;
    }

    public String getDisplayName() {
        switch (tag) {
            case "POSTSessionWorker": return "Sincronizando Sessões";
            case "POSTRondaWorker": return "Sincronizando Rondas";
            case "POSTMentorshipWorker": return "Sincronizando Avaliações";
            case "POSTTutoredWorker": return "Sincronizando Mentorados";
            case "POSTSessionRecommendedResourceWorker": return "Sincronizando Recursos de EA Recomendados";
            case "GETFormWorker": return "Carregando Tabelas";
            case "GETFormSectionQuestionWorker": return "Carregando Competências";
            case "GETProvinceWorker": return "Carregando Províncias";
            case "GETDistrictWorker": return "Carregando Distritos";
            case "GETResourceworker": return "Carregando Recursos de EA";
            case "GETPartnerWorker": return "Carregando Instituições";
            case "GETTutorWorker": return "Carregando Mentores";
            case "GETHealthFacilityWorker": return "Carregando Unidades Sanitárias";
            case "GETSettingWorker": return "Carregando Definições";
            case "GETProgrammaticAreaWorker": return "Carregando Áreas de Mentoria";
            case "GETEvaluationTypeWorker": return "Carregando Tipos de Avaliação";
            case "GETSessionStatusWorker": return "Carregando Estados da Sessão";
            case "GETIterationTypeWorker": return "Carregando Tipos de Iteração";
            case "GETEvaluationLocationWorker": return "Carregando Locais de Avaliação";
            case "GETDoorWorker": return "Carregando Portas";
            case "GETCabinetWorker": return "Carregando Gabinetes";
            case "GETResponseTypeWorker": return "Carregando Tipos de Resposta";
            case "GETProfessionalCategoryWorker": return "Carregando Categorias Profissionais";
            case "GETSectionWorker": return "Carregando Secções";
            case "GETProgramWorker": return "Carregando Programas";
            case "GETTutorProgrammaticAreaWorker": return "Carregando Áreas de Mentoria dos Mentores";
            case "PATCHUserWorker": return "Atualizando Informação do Usuário";
            case "GETUserWorker": return "Atualizando Perfil do Usuário";
            default: return tag;
        }
    }

    public int getIconResource() {
        if (state == null) return R.drawable.ic_pending;
        switch (state) {
            case RUNNING: return R.drawable.ic_sprint;
            case SUCCEEDED: return R.drawable.ic_check_circle;
            case FAILED: return R.drawable.ic_error;
            case CANCELLED: return R.drawable.ic_cancel;
            default: return R.drawable.ic_pending;
        }
    }

    public String getStatusText() {
        if (state == null) return "Em espera";
        switch (state) {
            case ENQUEUED:
            case BLOCKED:
                return "Em espera";
            case RUNNING:
                return "Em curso";
            case SUCCEEDED:
                return "Sucesso";
            case CANCELLED:
                return "Cancelado";
            case FAILED:
                return "Erro";
            default:
                return state.name(); // fallback
        }
    }

}

