package com.audtream.desktop.model.event;

import com.audtream.desktop.model.dto.internal.EnterProfileRequest;

public class EnterProfileEvent {
    private final EnterProfileRequest request;

    public EnterProfileEvent(EnterProfileRequest request) {
        this.request = request;
    }

    public EnterProfileRequest getRequest() {
        return request;
    }
}
