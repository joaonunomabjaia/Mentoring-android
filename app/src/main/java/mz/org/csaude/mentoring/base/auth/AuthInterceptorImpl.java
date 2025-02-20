package mz.org.csaude.mentoring.base.auth;

import static mz.org.csaude.mentoring.base.application.MentoringApplication.BASE_URL;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

import mz.org.csaude.mentoring.base.application.MentoringApplication;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.service.metadata.SyncDataService;
import mz.org.csaude.mentoring.util.Utilities;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthInterceptorImpl implements Interceptor {

    private final SessionManager sessionManager;
    private final Context context;
    private static final int NEW_EXPIRATION_TIME = 900000;

    public AuthInterceptorImpl(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager((MentoringApplication) context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String activeUser = sessionManager.getActiveUser();
        String fallbackUserUuid = getCreatedByUuidFromRequest(chain.request());
        String userToAuthenticate = determineUserToAuthenticate(activeUser, fallbackUserUuid);

        /*if (userToAuthenticate == null) {
            throw new IOException("No active user or creator found for authentication.");
        }*/

        // Retrieve user information using UserService
        User user;
        String accessToken = null;
        if (Utilities.stringHasValue(userToAuthenticate)) {
            try {
                user = ((MentoringApplication) context).getUserService().getByuuid(userToAuthenticate);
                if (user == null) {
                    throw new IOException("User not found for UUID: " + userToAuthenticate);
                }
            } catch (SQLException e) {
                throw new IOException("Error retrieving user from database: " + e.getMessage(), e);
            }

            accessToken = sessionManager.fetchAuthToken(user.getUserName());


            // Check if the access token is expired and refresh if necessary
            if (accessToken != null && sessionManager.getTokenExpiration(user.getUserName()) <= System.currentTimeMillis()) {
                String refreshToken = sessionManager.getRefreshToken(user.getUserName());
                accessToken = refreshToken(user.getUserName(), refreshToken);
                if (accessToken == null) {
                    throw new IOException("Unable to refresh token for user: " + user.getUserName());
                }
            }
        }

        Request newRequest = chain.request().newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();

        return chain.proceed(newRequest);
    }

    private String determineUserToAuthenticate(String activeUser, String createdByUuid) {
        // Use the creator if it differs from the active user
        if (createdByUuid != null && !createdByUuid.equals(activeUser)) {
            return createdByUuid;
        }
        // Default to the active user
        return activeUser;
    }

    private String getCreatedByUuidFromRequest(Request request) {
        try {
            RequestBody body = request.body();
            if (body != null) {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                String bodyString = buffer.readUtf8();
                // Parse the JSON to extract `createdByUuid`
                JSONObject json = new JSONObject(bodyString);
                return json.optString("createdByUuid", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private String refreshToken(String username, String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return null;
        }

        // Convert refresh token to JSON or other required format
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), "{\"refresh_token\":\"" + refreshToken + "\"}");

        // Create Retrofit instance for auth service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SyncDataService authService = retrofit.create(SyncDataService.class);
        try {
            retrofit2.Response<ResponseBody> response = authService.refreshToken(body).execute();
            if (response.isSuccessful() && response.body() != null) {
                return handleTokenResponse(username, response.body().string());
            } else {
                return null; // Refresh token is invalid or expired
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String handleTokenResponse(String username, String jsonResponse) {
        // Parse the JSON response
        Gson gson = new Gson();
        TokenResponse tokenResponse = gson.fromJson(jsonResponse, TokenResponse.class);

        if (tokenResponse != null) {
            // Save the tokens in the SessionManager for the active user
            sessionManager.saveAuthToken(
                    username,
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    NEW_EXPIRATION_TIME
            );
            return tokenResponse.getAccessToken();
        }
        return null;
    }
}
