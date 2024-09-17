package mz.org.csaude.mentoring.base.auth;

import android.content.SharedPreferences;
import mz.org.csaude.mentoring.base.application.MentoringApplication;

public class SessionManager {

    private static final String USER_TOKEN = "user_token";
    private static final String REFRESH_TOKEN = "user_refresh_token";
    private static final String TOKEN_EXPIRE_TIME = "token_expiration";
    private static final String DEFAULT_STRING = null;
    private static final long DEFAULT_LONG = 0L;

    private MentoringApplication application;
    private SharedPreferences sharedPref;

    public SessionManager(MentoringApplication application) {
        this.application = application;
        this.sharedPref = application.getEncryptedSharedPreferences();
    }

    public boolean isAccessTokenExpired() {
        long currentTimeMillis = System.currentTimeMillis();
        long accessTokenExpirationTime = getTokenExpiration();
        return accessTokenExpirationTime > 0 && currentTimeMillis >= accessTokenExpirationTime;
    }

    public void saveAuthToken(String token, String refreshToken, long accessTokenExpirationTime) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(USER_TOKEN, token);
        editor.putString(REFRESH_TOKEN, refreshToken);
        editor.putLong(TOKEN_EXPIRE_TIME, System.currentTimeMillis() + (accessTokenExpirationTime * 1000));
        editor.apply();
    }

    public String fetchAuthToken() {
        return sharedPref.getString(USER_TOKEN, DEFAULT_STRING);
    }

    public String getRefreshToken() {
        return sharedPref.getString(REFRESH_TOKEN, DEFAULT_STRING);
    }

    public Long getTokenExpiration() {
        return sharedPref.getLong(TOKEN_EXPIRE_TIME, DEFAULT_LONG);
    }
}
