package mz.org.csaude.mentoring.viewmodel.home;

import android.app.Application;

import androidx.annotation.NonNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.util.RondaTypeEnum;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.resource.ResourceActivity;
import mz.org.csaude.mentoring.view.ronda.RondaActivity;
import mz.org.csaude.mentoring.view.tutored.TutoredActivity;

public class HomeFragmentViewModel extends BaseViewModel {

    public HomeFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public void goToMentoringRounds() {
        getExecutorService().execute(() -> {
            try {
                RondaType rondaType = getApplication()
                        .getRondaTypeService()
                        .getRondaTypeByCode(RondaTypeEnum.MENTORIA_INTERNA.toString());

                if (rondaType != null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("title", "Ronda de Mentoria");
                    params.put("rondaType", rondaType);

                    // Navigate to the next activity on the main thread
                    getRelatedActivity().runOnUiThread(() -> {
                        getRelatedActivity().nextActivity(RondaActivity.class, params);
                    });
                } else {
                    // Handle the case where RondaType is not found
                    showError("RondaType not found. Please try again.");
                }
            } catch (SQLException e) {
                // Log the error and show a user-friendly message
                e.printStackTrace();
                showError("An error occurred while retrieving RondaType. Please try again later.");
            }
        });
    }

    // Helper method to show error messages
    private void showError(String message) {
        // Show error on the main thread
        getRelatedActivity().runOnUiThread(() -> {
            Utilities.displayAlertDialog(getRelatedActivity(), message).show();
        });
    }


    public void goToBaseSessions() {
        getExecutorService().execute(() -> {
            try {
                RondaType rondaType = getApplication()
                        .getRondaTypeService()
                        .getRondaTypeByCode(RondaTypeEnum.SESSAO_ZERO.toString());

                if (rondaType != null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("title", "Sessão Zero");
                    params.put("rondaType", rondaType);

                    // Navigate to the next activity on the main thread
                    getRelatedActivity().runOnUiThread(() -> {
                        getRelatedActivity().nextActivity(RondaActivity.class, params);
                    });
                } else {
                    // Handle the case where RondaType is not found
                    showError("RondaType not found. Please try again.");
                }
            } catch (SQLException e) {
                // Log the error and show a user-friendly message
                e.printStackTrace();
                showError("An error occurred while retrieving RondaType. Please try again later.");
            }
        });
    }

    public void goToMentees() {
        getRelatedActivity().nextActivity(TutoredActivity.class);
    }

    public void goToLearningResources() {
        getRelatedActivity().nextActivity(ResourceActivity.class);
    }
    @Override
    public void preInit() {

    }
}