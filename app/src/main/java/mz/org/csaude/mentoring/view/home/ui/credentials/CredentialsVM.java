package mz.org.csaude.mentoring.view.home.ui.credentials;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.service.user.UserService;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.viewmodel.user.UserVM;

public class CredentialsVM extends UserVM {

    protected String userPassWordCorrente;

    protected String userNovaPassWord;

    public CredentialsVM(@NonNull Application application) {
        super(application);

    }
    @Override
    public void preInit() {
        this.user = getCurrentUser();
    }

    @Bindable
    public String getUserPassWordCorrente() {
        return userPassWordCorrente;
    }

    public void setUserPassWordCorrente(String userPassWordCorrente) {
        this.userPassWordCorrente = userPassWordCorrente;
        notifyPropertyChanged(BR.userPassWordCorrente);
    }

    @Bindable
    public String getUserNovaPassWord() {
        return userNovaPassWord;
    }

    public void setUserNovaPassWord(String userNovaPassWord) {
        this.userNovaPassWord = userNovaPassWord;
        notifyPropertyChanged(BR.userNovaPassWord);
    }

    @Override
    public void updatePassword() {
        if (Utilities.encryptPassword(userPassWordCorrente.trim(),getCurrentUser().getSalt()).equals(Utilities.encryptPassword(getPassword(),getCurrentUser().getSalt()))){
            if (!userNovaPassWord.trim().equals(getUserPassRepeat())){
                Utilities.displayAlertDialog(getRelatedFragment().getContext(), "As senhas indicadas não conferem, por favor verificar.").show();
            }else {
                getRelatedRecord().setPassword(Utilities.encryptPassword(userNovaPassWord.trim(),getRelatedRecord().getSalt()));
                getRelatedRecord().setSyncStatus(SyncSatus.PENDING);
                getRelatedRecord().setUpdatedAt(DateUtilities.getCurrentDate());
                super.updatePassword();
            }
        }else Utilities.displayAlertDialog(getRelatedFragment().getContext(), "A senha corrente indicada é inválida.").show();
    }

}