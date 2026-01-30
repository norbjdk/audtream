package com.audtream.desktop.model.event;

public class DataRefreshedEvent {
    private final String dataType;

    public DataRefreshedEvent(String dataType) {
        this.dataType = dataType;
    }

    public String getDataType() {
        return dataType;
    }
}
