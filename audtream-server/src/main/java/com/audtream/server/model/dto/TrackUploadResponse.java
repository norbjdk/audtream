package com.audtream.server.model.dto;

public class TrackUploadResponse {
    private boolean success;
    private String message;
    private TrackResponse track;
    private String fileUrl;
    private String coverUrl;

    // Konstruktory
    public TrackUploadResponse() {}

    public TrackUploadResponse(boolean success, String message, TrackResponse track) {
        this.success = success;
        this.message = message;
        this.track = track;
    }

    // Gettery i Settery
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TrackResponse getTrack() {
        return track;
    }

    public void setTrack(TrackResponse track) {
        this.track = track;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    // Helper metody do tworzenia odpowiedzi
    public static TrackUploadResponse success(TrackResponse track, String fileUrl, String coverUrl) {
        TrackUploadResponse response = new TrackUploadResponse();
        response.setSuccess(true);
        response.setMessage("Track uploaded successfully");
        response.setTrack(track);
        response.setFileUrl(fileUrl);
        response.setCoverUrl(coverUrl);
        return response;
    }

    public static TrackUploadResponse error(String message) {
        TrackUploadResponse response = new TrackUploadResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}
