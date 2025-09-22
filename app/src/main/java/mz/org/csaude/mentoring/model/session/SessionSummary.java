package mz.org.csaude.mentoring.model.session;

import mz.org.csaude.mentoring.base.model.BaseModel;

public class SessionSummary extends BaseModel {

    private String title;
    private Integer simCount;
    private Integer naoCount;
    private double progressPercentage;

    // Constructors
    public SessionSummary(String title, int simCount, int naoCount, double progressPercentage) {
        this.title = title;
        this.simCount = simCount;
        this.naoCount = naoCount;
        this.progressPercentage = progressPercentage;
    }

    public SessionSummary() {
        this.simCount = 0;
        this.naoCount = 0;
    }

    // Fluent setters
    public SessionSummary setTitle(String title) {
        this.title = title;
        return this;
    }

    public SessionSummary setSimCount(int simCount) {
        this.simCount = simCount;
        return this;
    }

    public SessionSummary setNaoCount(int naoCount) {
        this.naoCount = naoCount;
        return this;
    }

    public SessionSummary setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
        return this;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public int getSimCount() {
        return simCount;
    }

    public int getNaoCount() {
        return naoCount;
    }

    public Integer getProgressPercentage() {
        if (simCount + naoCount == 0) {
            return 0;
        }
        return (int) ((double) simCount / (simCount + naoCount) * 100);
    }

    @Override
    public String toString() {
        return "SessionSummary{" +
                "title='" + title + '\'' +
                ", simCount=" + simCount +
                ", naoCount=" + naoCount +
                ", progressPercentage=" + getProgressPercentage() +
                '}';
    }
}
