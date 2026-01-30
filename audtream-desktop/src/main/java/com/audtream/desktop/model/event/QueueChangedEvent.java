package com.audtream.desktop.model.event;

import com.audtream.desktop.model.dto.TrackDTO;
import java.util.List;

public class QueueChangedEvent {
    private final List<TrackDTO> queue;

    public QueueChangedEvent(List<TrackDTO> queue) {
        this.queue = queue;
    }

    public List<TrackDTO> getQueue() {
        return queue;
    }
}
