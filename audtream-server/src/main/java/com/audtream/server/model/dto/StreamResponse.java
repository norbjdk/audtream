package com.audtream.server.model.dto;

public class StreamResponse {
    private String streamUrl;
    private String mimeType;
    private Long contentLength;
    private boolean rangeSupported;
    private Long bitrate;
    private String codec;

    // Konstruktory
    public StreamResponse() {}

    public StreamResponse(String streamUrl, String mimeType, Long contentLength) {
        this.streamUrl = streamUrl;
        this.mimeType = mimeType;
        this.contentLength = contentLength;
        this.rangeSupported = true;
    }

    public StreamResponse(String streamUrl, String mimeType, Long contentLength,
                          boolean rangeSupported, Long bitrate, String codec) {
        this.streamUrl = streamUrl;
        this.mimeType = mimeType;
        this.contentLength = contentLength;
        this.rangeSupported = rangeSupported;
        this.bitrate = bitrate;
        this.codec = codec;
    }

    // Gettery i Settery
    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public boolean isRangeSupported() {
        return rangeSupported;
    }

    public void setRangeSupported(boolean rangeSupported) {
        this.rangeSupported = rangeSupported;
    }

    public Long getBitrate() {
        return bitrate;
    }

    public void setBitrate(Long bitrate) {
        this.bitrate = bitrate;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }
}
