package com.audtream.desktop.model.dto;

import java.util.List;

public class PlaylistRequest {
    private String name;
    private String description;
    private Boolean isPublic;
    private String coverImageUrl;
    private List<Long> trackIds;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public List<Long> getTrackIds() { return trackIds; }
    public void setTrackIds(List<Long> trackIds) { this.trackIds = trackIds; }
}