package gr.open.hackathon.service;

public class TeamScore {
    private int teamId;
    private double average;

    public TeamScore(int teamId, double average) {
        this.teamId = teamId;
        this.average = average;
    }

    public int getTeamId() {
        return teamId;
    }

    public double getAverage() {
        return average;
    }
}
