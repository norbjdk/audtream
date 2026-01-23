package com.audtream.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlaylistRequest {
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("isPublic")
    private Boolean isPublic = true;

    @JsonProperty("coverImageUrl")
    private String coverImageUrl;

    @JsonProperty("trackIds")
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