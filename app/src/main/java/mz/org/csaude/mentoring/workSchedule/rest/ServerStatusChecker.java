package mz.org.csaude.mentoring.workSchedule.rest;

import android.app.Application;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import mz.org.csaude.mentoring.base.service.BaseRestService;
import mz.org.csaude.mentoring.listner.rest.ServerStatusListener;

public class ServerStatusChecker extends BaseRestService {

    private static final long SLOW_RESPONSE_THRESHOLD_MS = 5000; // 5 seconds

    public ServerStatusChecker(Application application) {
        super(application);
    }

    public void isServerOnline(ServerStatusListener listener) {
        long startTime = System.currentTimeMillis(); // Capture request start time

        Call<Void> call = syncDataService.checkServerStatus();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                long duration = System.currentTimeMillis() - startTime; // Calculate duration
                Log.d("ServerStatus", "Request duration: " + duration + "ms");

                if (response.isSuccessful()) {
                    if (duration > SLOW_RESPONSE_THRESHOLD_MS) {
                        Log.w("ServerStatus", "Warning: Slow server response (" + duration + " ms)");
                        listener.onServerStatusChecked(true, true); // Server OK, but slow
                    } else {
                        listener.onServerStatusChecked(true, false); // Server OK, fast
                    }
                } else {
                    listener.onServerStatusChecked(false, false);
                    Log.e("ServerStatus", "Server returned an error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                long duration = System.currentTimeMillis() - startTime; // Even if failed, measure
                Log.e("ServerStatus", "Failed to connect to server after " + duration + " ms", t);
                listener.onServerStatusChecked(false, false);
            }
        });
    }
}
