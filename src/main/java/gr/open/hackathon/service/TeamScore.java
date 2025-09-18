package gr.open.hackathon.service;

import java.util.Map;

public class TeamScore {
    private int teamId;
    private double average;
    private Map<String, Double> categoryAverages;
    private int totalVotes;

    public TeamScore(int teamId, double average) {
        this.teamId = teamId;
        this.average = average;
    }

    public TeamScore(int teamId, double average, Map<String, Double> categoryAverages, int totalVotes) {
        this.teamId = teamId;
        this.average = average;
        this.categoryAverages = categoryAverages;
        this.totalVotes = totalVotes;
    }

    public int getTeamId() {
        return teamId;
    }

    public double getAverage() {
        return average;
    }

    public Map<String, Double> getCategoryAverages() {
        return categoryAverages;
    }

    public int getTotalVotes() {
        return totalVotes;
    }
}
