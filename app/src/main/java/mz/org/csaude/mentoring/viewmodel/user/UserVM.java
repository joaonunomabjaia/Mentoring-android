package mz.org.csaude.mentoring.viewmodel.user;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import java.sql.SQLException;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.user.UserService;
import mz.org.csaude.mentoring.util.Utilities;

public class UserVM extends BaseViewModel {

    protected User user;

    protected String userPassRepeat;

    public UserVM(@NonNull Application application) {
        super(application);
    }

    protected BaseService initRelatedService() {
        return getApplication().getUserService();
    }

    public User getRelatedRecord() {
        return this.user;
    }

    public UserService getRelatedService() {
        return getApplication().getUserService();
    }

    @Override
    public void preInit() {

    }

    @Bindable
    public String getUserName() {
        return this.user.getUserName();
    }

    public void setUserName(String userName) {
        this.user.setUserName(userName);
        notifyPropertyChanged(BR.userName);
    }

    @Bindable
    public String getPassword() {
        return this.user.getPassword();
    }

    public void setPassword(String password) {
        this.user.setPassword(password);
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getUserPassRepeat() {
        return userPassRepeat;
    }

    public void setUserPassRepeat(String userPassRepeat) {
        this.userPassRepeat = userPassRepeat;
        notifyPropertyChanged(BR.userPassRepeat);
    }

    public void updatePassword() {
        String errors = this.user.isValid();

        if (!Utilities.stringHasValue(errors)) {
            getExecutorService().execute(() -> {
                try {
                    // Perform the password update in the background
                    getRelatedService().updatePassword(getRelatedRecord());

                    // Notify success on the main thread
                    runOnMainThread(() ->
                            Utilities.displayAlertDialog(
                                    getRelatedFragment().getContext(),
                                    "Operação efectuada com sucesso!"
                            ).show()
                    );

                } catch (SQLException e) {
                    // Notify the error on the main thread
                    runOnMainThread(() ->
                            Utilities.displayAlertDialog(
                                    getRelatedFragment().getContext(),
                                    "Ocorreu um erro ao tentar realizar a operação desejada: " + e.getMessage()
                            ).show()
                    );
                    e.printStackTrace();
                }
            });
        } else {
            // Display validation errors on the main thread
            Utilities.displayAlertDialog(getRelatedFragment().getContext(), errors).show();
        }
    }

}
