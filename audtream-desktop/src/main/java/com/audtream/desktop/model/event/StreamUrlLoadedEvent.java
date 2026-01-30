package com.audtream.desktop.model.event;

public class StreamUrlLoadedEvent {
    private final String streamUrl;

    public StreamUrlLoadedEvent(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getStreamUrl() {
        return streamUrl;
    }
}