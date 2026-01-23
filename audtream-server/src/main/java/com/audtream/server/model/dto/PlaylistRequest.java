package com.audtream.server.model.dto;

import jakarta.validation.constraints.NotBlank;

public class PlaylistRequest {

    @NotBlank(message = "Playlist name is required")
    private String name;

    private String description;

    private Boolean isPublic = true;

    private String coverImageUrl;

    private Long[] trackIds;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public Long[] getTrackIds() { return trackIds; }
    public void setTrackIds(Long[] trackIds) { this.trackIds = trackIds; }
}