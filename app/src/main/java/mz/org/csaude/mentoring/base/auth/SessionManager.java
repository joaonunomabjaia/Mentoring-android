package mz.org.csaude.mentoring.base.auth;

import static mz.org.csaude.mentoring.util.Constants.INITIAL_SETUP_STATUS;
import static mz.org.csaude.mentoring.util.Constants.INITIAL_SETUP_STATUS_COMPLETE;

import android.content.SharedPreferences;
import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.util.Utilities;

public class SessionManager {

    private static final String ACTIVE_USER_KEY = "active_user_uuid"; // Store UUID
    private static final String ACTIVE_USER = "active_user";
    private static final String USER_TOKEN_SUFFIX = "_user_token";
    private static final String REFRESH_TOKEN_SUFFIX = "_refresh_token";
    private static final String TOKEN_EXPIRE_TIME_SUFFIX = "_token_expiration";

    private MentoringApplication application;
    private SharedPreferences sharedPref;

    public SessionManager(MentoringApplication application) {
        this.application = application;
        this.sharedPref = application.getEncryptedSharedPreferences();
    }

    /**
     * Sets the currently active user.
     */
    public void setActiveUser(String uuid) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACTIVE_USER_KEY, uuid);
        editor.apply();
    }

    /**
     * Gets the currently active user.
     */
    public String getActiveUser() {
        return sharedPref.getString(ACTIVE_USER_KEY, null);
    }

    private String getKeyForUser(String username, String suffix) {
        return username + suffix;
    }

    public boolean isAccessTokenExpired(String username) {
        long currentTimeMillis = System.currentTimeMillis();
        long accessTokenExpirationTime = getTokenExpiration(username);
        return accessTokenExpirationTime > 0 && currentTimeMillis >= accessTokenExpirationTime;
    }

    public void saveAuthToken(String username, String token, String refreshToken, long accessTokenExpirationTime) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getKeyForUser(username, USER_TOKEN_SUFFIX), token);
        editor.putString(getKeyForUser(username, REFRESH_TOKEN_SUFFIX), refreshToken);
        editor.putLong(getKeyForUser(username, TOKEN_EXPIRE_TIME_SUFFIX), System.currentTimeMillis() + (accessTokenExpirationTime * 1000));
        editor.apply();
    }

    public void setInitialSetUpComplete(String username) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(username, INITIAL_SETUP_STATUS_COMPLETE);
        editor.apply();
    }

    public boolean isInitialSetupComplete(String username) {
        String status = sharedPref.getString(username, null);
        if (!Utilities.stringHasValue(status)) return false;

        return sharedPref.getString(username, null).equals(INITIAL_SETUP_STATUS_COMPLETE);
    }

    public String fetchAuthToken(String username) {
        return sharedPref.getString(getKeyForUser(username, USER_TOKEN_SUFFIX), null);
    }

    public String getRefreshToken(String username) {
        return sharedPref.getString(getKeyForUser(username, REFRESH_TOKEN_SUFFIX), null);
    }

    public Long getTokenExpiration(String username) {
        return sharedPref.getLong(getKeyForUser(username, TOKEN_EXPIRE_TIME_SUFFIX), 0L);
    }

    /**
     * Remove data for a specific user.
     */
    public void clearUserSession(String username) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(getKeyForUser(username, USER_TOKEN_SUFFIX));
        editor.remove(getKeyForUser(username, REFRESH_TOKEN_SUFFIX));
        editor.remove(getKeyForUser(username, TOKEN_EXPIRE_TIME_SUFFIX));
        editor.apply();
    }

    /**
     * Logout the currently active user.
     */
    public void logoutActiveUser() {
        String activeUser = getActiveUser();
        if (activeUser != null) {
            clearUserSession(activeUser);
            setActiveUser(null);
        }
    }

    /**
     * Checks if there is any user configured.
     *
     * @return true if at least one user has a saved session, false otherwise.
     */
    public boolean isAnyUserConfigured() {
        // Iterate through all the shared preferences keys
        for (String key : sharedPref.getAll().keySet()) {
            // Check if the key contains a user token suffix
            if (key.contains(USER_TOKEN_SUFFIX)) {
                String token = sharedPref.getString(key, null);
                if (token != null && !token.isEmpty()) {
                    return true; // At least one user is configured
                }
            }
        }
        return false; // No users configured
    }

}
