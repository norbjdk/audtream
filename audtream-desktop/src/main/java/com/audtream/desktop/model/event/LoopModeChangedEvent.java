package com.audtream.desktop.model.event;

import com.audtream.desktop.manager.PlayerStateManager.LoopMode;

public class LoopModeChangedEvent {
    private final LoopMode loopMode;

    public LoopModeChangedEvent(LoopMode loopMode) {
        this.loopMode = loopMode;
    }

    public LoopMode getLoopMode() {
        return loopMode;
    }
}
