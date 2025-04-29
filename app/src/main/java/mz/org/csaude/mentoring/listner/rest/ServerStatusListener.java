package mz.org.csaude.mentoring.listner.rest;

public interface ServerStatusListener {
    void onServerStatusChecked(boolean isOnline, boolean isSlow);
}
