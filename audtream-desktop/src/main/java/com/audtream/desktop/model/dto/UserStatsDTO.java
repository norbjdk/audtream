package com.audtream.desktop.model.dto;

public class UserStatsDTO {
    private Integer totalTracks;
    private Integer totalPlays;
    private Integer totalLikes;
    private String topGenre;
    private Integer averageDuration;
    private String mostPlayedTrack;
    private Integer mostPlayedTrackPlays;

    public Integer getTotalTracks() { return totalTracks; }
    public void setTotalTracks(Integer totalTracks) { this.totalTracks = totalTracks; }

    public Integer getTotalPlays() { return totalPlays; }
    public void setTotalPlays(Integer totalPlays) { this.totalPlays = totalPlays; }

    public Integer getTotalLikes() { return totalLikes; }
    public void setTotalLikes(Integer totalLikes) { this.totalLikes = totalLikes; }

    public String getTopGenre() { return topGenre; }
    public void setTopGenre(String topGenre) { this.topGenre = topGenre; }

    public Integer getAverageDuration() { return averageDuration; }
    public void setAverageDuration(Integer averageDuration) { this.averageDuration = averageDuration; }

    public String getMostPlayedTrack() { return mostPlayedTrack; }
    public void setMostPlayedTrack(String mostPlayedTrack) { this.mostPlayedTrack = mostPlayedTrack; }

    public Integer getMostPlayedTrackPlays() { return mostPlayedTrackPlays; }
    public void setMostPlayedTrackPlays(Integer mostPlayedTrackPlays) { this.mostPlayedTrackPlays = mostPlayedTrackPlays; }
}
