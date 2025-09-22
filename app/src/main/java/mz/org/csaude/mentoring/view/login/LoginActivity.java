package mz.org.csaude.mentoring.view.login;


import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivityLoginBinding;
import mz.org.csaude.mentoring.util.Constants;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.viewmodel.login.LoginVM;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding loginBinding;

    @Override
    protected boolean isAutoLogoutEnabled() {
        return false; // Disable auto logout in LoginActivity
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginBinding.setViewModel(getRelatedViewModel());

        if (getRelatedViewModel().isBiometricEnabled()) {
            showBiometricPrompt();
        }
    }

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(LoginVM.class);
    }

    @Override
    public LoginVM getRelatedViewModel() {
        return (LoginVM) super.getRelatedViewModel();
    }

    public void showBiometricPrompt() {
        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            // Optional: show a message why it's not available
            Toast.makeText(this, "Biometric authentication not available", Toast.LENGTH_SHORT).show();
            return;
        }
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        // Load saved credentials from EncryptedSharedPreferences
                        SharedPreferences securePrefs = ((MentoringApplication) getApplication()).getEncryptedSharedPreferences();
                        String prefix = Constants.PREF_USER_CREDENTIALS_PREFIX;

                        String savedUsername = securePrefs.getString(prefix + "username", null);
                        String savedPassword = securePrefs.getString(prefix + "password", null);

                        if (Utilities.stringHasValue(savedUsername) && Utilities.stringHasValue(savedPassword)) {
                            getRelatedViewModel().setUserName(savedUsername);
                            getRelatedViewModel().setUserPassword(savedPassword);

                            getRelatedViewModel().doLogin(); // this will run local login
                        } else {
                            Toast.makeText(LoginActivity.this, "Credenciais não encontradas para login biométrico.", Toast.LENGTH_LONG).show();
                        }
                    }


                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        // Optional: show error
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        // Optional: retry or notify user
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.cancel))
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    @Override
    public void onBackPressed() {
        finishAffinity(); // This exits the app completely from Login screen
    }

}
