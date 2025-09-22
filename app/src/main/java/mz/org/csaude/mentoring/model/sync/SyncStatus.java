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
            case "MENTOR_DATA_FORM":
            case "GETFormWorker":
                return "Carregando Tabelas";
            case "MENTOR_DATA_HEALTH_FACILITY":
            case "GETHealthFacilityWorker":
                return "Carregando Unidades Sanitárias";
            case "MENTOR_DATA_TUTOR_PROGRAMMATIC_AREA":
            case "GETProgrammaticAreaWorker":
            case "INITIAL_SYNC_PROGRAMMATIC_AREA":
                return "Carregando Áreas de Mentoria";
            case "MENTOR_DATA_FORM_QUESTION":
            case "GETFormSectionQuestionWorker":
                return "Carregando Competências";
            case "MENTOR_DATA_RESOURCE":
            case "GETResourceworker":
                return "Carregando Recursos de EA";
            case "MENTOR_DATA_TUTORED": return "Carregando Mentorados";
            case "MENTOR_DATA_RONDA": return "Carregando Rondas";
            case "MENTOR_DATA_SESSION_GET": return "Carregando Sessões";

            case "POSTSessionWorker": return "Sincronizando Sessões";
            case "POSTRondaWorker": return "Sincronizando Rondas";
            case "POSTMentorshipWorker": return "Sincronizando Avaliações";
            case "POSTTutoredWorker": return "Sincronizando Mentorados";
            case "POSTSessionRecommendedResourceWorker": return "Sincronizando Recursos de EA Recomendados";
            case "GETProvinceWorker": return "Carregando Províncias";
            case "GETDistrictWorker": return "Carregando Distritos";
            case "GETPartnerWorker": return "Carregando Instituições";
            case "GETTutorWorker": return "Carregando Mentores";
            case "GETSettingWorker": return "Carregando Definições";
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

            case "INITIAL_SYNC_PROVINCE": return "Carregando Províncias";
            case "INITIAL_SYNC_DISTRICT": return "Carregando Distritos";
            case "INITIAL_SYNC_SECTION": return "Carregando Secções";
            case "INITIAL_SYNC_PROFESSIONAL_CATEGORY": return "Carregando Categorias Profissionais";
            case "INITIAL_SYNC_PARTNER": return "Carregando Instituições";
            case "INITIAL_SYNC_RONDA_TYPE": return "Carregando Tipos de Ronda";
            case "INITIAL_SYNC_RESPONSE_TYPE": return "Carregando Tipos de Resposta";
            case "INITIAL_SYNC_EVALUATION_LOCATION": return "Carregando Locais de Avaliação";
            case "INITIAL_SYNC_EVALUATION_TYPE": return "Carregando Tipos de Avaliação";
            case "INITIAL_SYNC_ITERATION_TYPE": return "Carregando Tipos de Iteração";
            case "INITIAL_SYNC_DOOR": return "Carregando Portas";
            case "INITIAL_SYNC_CABINET": return "Carregando Gabinetes";
            case "INITIAL_SYNC_SESSION_STATUS": return "Carregando Estados da Sessão";
            case "INITIAL_SYNC_PROGRAM": return "Carregando Programas";
            case "INITIAL_SYNC_SETTINGS": return "Carregando Definições";

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

