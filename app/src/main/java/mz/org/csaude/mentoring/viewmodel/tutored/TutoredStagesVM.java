package mz.org.csaude.mentoring.viewmodel.tutored;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.List;

import mz.org.csaude.mentoring.model.tutored.Tutored;

/** VM específica da tela de estágios. */
public class TutoredStagesVM extends TutoredVM {

    public TutoredStagesVM(@NonNull Application application) {
        super(application);
    }

    /** Ponto de extensão (se usar adapter seccionado) */
    public void rebuildSectionsIfNeeded() {
        List<Tutored> base = getSearchResults();
        // TODO: agrupar por buckets e expor via LiveData se usar seções
    }

    /** Opcional: quando a VM base notifica resultados, refaça seções. */
    @Override
    public void displaySearchResults() {
        super.displaySearchResults();
        rebuildSectionsIfNeeded();
    }
}
