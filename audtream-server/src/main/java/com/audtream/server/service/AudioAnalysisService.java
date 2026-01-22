package com.audtream.server.service;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.sound.sampled.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
public class AudioAnalysisService {

    private final Tika tika = new Tika();

    public AudioMetadata extractMetadata(MultipartFile file) throws Exception {
        AudioMetadata metadata = new AudioMetadata();

        Metadata tikaMetadata = new Metadata();
        try (InputStream stream = file.getInputStream()) {
            tika.parse(stream, tikaMetadata);
        }

        metadata.setTitle(tikaMetadata.get("title"));
        metadata.setArtist(tikaMetadata.get("artist"));
        metadata.setAlbum(tikaMetadata.get("album"));
        metadata.setGenre(tikaMetadata.get("genre"));
        metadata.setYear(tikaMetadata.get("year"));

        if (metadata.getTitle() == null || metadata.getTitle().isEmpty()) {
            String filename = file.getOriginalFilename();
            if (filename != null) {
                metadata.setTitle(filename.substring(0, filename.lastIndexOf('.')));
            }
        }

        metadata.setDuration(calculateDuration(file));

        metadata.setFileSize(file.getSize());

        metadata.setMimeType(file.getContentType());

        if (metadata.getDuration() > 0) {
            long bitrate = (file.getSize() * 8L) / metadata.getDuration();
            metadata.setBitrate((int) bitrate);
        }

        return metadata;
    }

    private long calculateDuration(MultipartFile file) throws Exception {
        File tempFile = File.createTempFile("audio_", ".tmp");
        Files.copy(file.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(tempFile);
            AudioFormat format = audioStream.getFormat();

            long frames = audioStream.getFrameLength();
            double durationInSeconds = (frames + 0.0) / format.getFrameRate();

            audioStream.close();

            return (long) durationInSeconds;
        } finally {
            tempFile.delete();
        }
    }

    public static class AudioMetadata {
        private String title;
        private String artist;
        private String album;
        private String genre;
        private String year;
        private Long duration;
        private Long fileSize;
        private String mimeType;
        private Integer bitrate;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getArtist() { return artist; }
        public void setArtist(String artist) { this.artist = artist; }

        public String getAlbum() { return album; }
        public void setAlbum(String album) { this.album = album; }

        public String getGenre() { return genre; }
        public void setGenre(String genre) { this.genre = genre; }

        public String getYear() { return year; }
        public void setYear(String year) { this.year = year; }

        public Long getDuration() { return duration; }
        public void setDuration(Long duration) { this.duration = duration; }

        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }

        public Integer getBitrate() { return bitrate; }
        public void setBitrate(Integer bitrate) { this.bitrate = bitrate; }
    }
}
