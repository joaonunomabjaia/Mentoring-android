package mz.org.csaude.mentoring.model.session;

import mz.org.csaude.mentoring.base.model.BaseModel;

public class SessionSummary extends BaseModel {

    private String title;
    private Integer simCount;
    private Integer naoCount;
    private double progressPercentage;

    // Constructor
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

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSimCount() {
        return simCount;
    }

    public void setSimCount(int simCount) {
        this.simCount = simCount;
    }

    public int getNaoCount() {
        return naoCount;
    }

    public void setNaoCount(int naoCount) {
        this.naoCount = naoCount;
    }

    public Integer getProgressPercentage() {
        if (simCount + naoCount == 0) {
            return 0; // To handle division by zero
        }
        double progress = (double) simCount / (simCount + naoCount) * 100;
        return (int) progress;
    }


    public void setProgressPercentage(String progressPercentage) {

    }

    // toString method for debugging
    @Override
    public String toString() {
        return "SessionSummary{" +
                "title='" + title + '\'' +
                ", simCount=" + simCount +
                ", naoCount=" + naoCount +
                ", progressPercentage=" + progressPercentage +
                '}';
    }
}
