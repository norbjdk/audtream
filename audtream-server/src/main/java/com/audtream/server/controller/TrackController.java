package com.audtream.server.controller;

import com.audtream.server.model.dto.TrackRequest;
import com.audtream.server.model.dto.TrackResponse;
import com.audtream.server.model.entity.Track;
import com.audtream.server.model.entity.User;
import com.audtream.server.model.repository.TrackRepository;
import com.audtream.server.model.repository.UserRepository;
import com.audtream.server.service.AudioAnalysisService;
import com.audtream.server.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tracks")
@CrossOrigin(origins = "http://localhost:5173")
public class TrackController {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private AudioAnalysisService audioAnalysisService;

    @GetMapping
    public ResponseEntity<List<TrackResponse>> getUserTracks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository
                .findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("User not found"));


        List<Track> tracks = trackRepository.findByUserId(user.getId());

        List<TrackResponse> responses = tracks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrackResponse> createTrack(
            @RequestPart("audioFile") MultipartFile audioFile,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestPart("metadata") TrackRequest trackRequest) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository
                .findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("User not found"));


        try {
            AudioAnalysisService.AudioMetadata audioMetadata =
                    audioAnalysisService.extractMetadata(audioFile);

            String audioFileName = trackRequest.getTitle() != null ?
                    trackRequest.getTitle() : audioFile.getOriginalFilename();
            String audioFileUrl = fileStorageService.uploadAudioFile(audioFile, audioFileName);

            String coverUrl = null;
            if (coverImage != null && !coverImage.isEmpty()) {
                String coverFileName = trackRequest.getTitle() != null ?
                        trackRequest.getTitle() + "_cover" : "cover";
                coverUrl = fileStorageService.uploadCoverImage(coverImage, coverFileName);
            }

            Track track = new Track();
            track.setTitle(trackRequest.getTitle() != null ?
                    trackRequest.getTitle() : audioMetadata.getTitle());
            track.setArtist(trackRequest.getArtist() != null ?
                    trackRequest.getArtist() : audioMetadata.getArtist());
            track.setAlbum(trackRequest.getAlbum() != null ?
                    trackRequest.getAlbum() : audioMetadata.getAlbum());
            track.setDuration(trackRequest.getDuration() != null ?
                    trackRequest.getDuration() : audioMetadata.getDuration().intValue());
            track.setGenre(trackRequest.getGenre() != null ?
                    trackRequest.getGenre() : audioMetadata.getGenre());
            track.setYear(audioMetadata.getYear());
            track.setFileUrl(audioFileUrl);
            track.setFileSize(audioMetadata.getFileSize());
            track.setMimeType(audioMetadata.getMimeType());
            track.setBitrate(audioMetadata.getBitrate());
            track.setCoverUrl(coverUrl);
            track.setUser(user);

            Track savedTrack = trackRepository.save(track);

            return ResponseEntity.ok(convertToResponse(savedTrack));

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload track: " + e.getMessage(), e);
        }
    }

    @GetMapping("/stream/{trackId}")
    public ResponseEntity<?> streamTrack(@PathVariable Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        try {
            if (track.getFileUrl().contains("?")) {
                return ResponseEntity.status(302)
                        .header("Location", track.getFileUrl())
                        .build();
            }
            String objectName = extractObjectNameFromUrl(track.getFileUrl());
            String presignedUrl = fileStorageService.getPresignedUrl(objectName, 3600);

            return ResponseEntity.status(302)
                    .header("Location", presignedUrl)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to stream track", e);
        }
    }
    @DeleteMapping("/{trackId}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long trackId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository
                .findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("User not found"));


        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        if (!track.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this track");
        }

        try {
            String audioObjectName = extractObjectNameFromUrl(track.getFileUrl());
            fileStorageService.deleteFile(audioObjectName);

            if (track.getCoverUrl() != null) {
                String coverObjectName = extractObjectNameFromUrl(track.getCoverUrl());
                fileStorageService.deleteFile(coverObjectName);
            }

            trackRepository.delete(track);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete track", e);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TrackResponse> createTrackJson(
            @RequestBody TrackRequest trackRequest) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("User not found"));


        Track track = new Track();
        track.setTitle(trackRequest.getTitle());
        track.setArtist(trackRequest.getArtist());
        track.setAlbum(trackRequest.getAlbum());
        track.setDuration(trackRequest.getDuration());
        track.setGenre(trackRequest.getGenre());
        track.setUser(user);

        track.setFileUrl("PENDING_UPLOAD");
        track.setMimeType("audio/mpeg");
        track.setFileSize(0L);
        track.setBitrate(0);

        Track saved = trackRepository.save(track);

        return ResponseEntity.ok(convertToResponse(saved));
    }

    private String extractObjectNameFromUrl(String url) {
        if (url.startsWith("http")) {
            String[] parts = url.split("/");
            return String.join("/", Arrays.copyOfRange(parts, 3, parts.length));
        }
        return url;
    }

    @GetMapping("/{trackId}/url")
    public ResponseEntity<String> getTrackUrl(@PathVariable Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        try {
            String objectName = extractObjectNameFromUrl(track.getFileUrl());
            String presignedUrl = fileStorageService.getPresignedUrl(objectName, 3600);

            return ResponseEntity.ok(presignedUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get track URL", e);
        }
    }

    @PostMapping("/{trackId}/play")
    public ResponseEntity<Void> incrementPlayCount(@PathVariable Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        track.setPlays(track.getPlays() + 1);
        trackRepository.save(track);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{trackId}/like")
    public ResponseEntity<Void> toggleLike(@PathVariable Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        track.setLikes(track.getLikes() + 1);
        trackRepository.save(track);

        return ResponseEntity.ok().build();
    }


    private TrackResponse convertToResponse(Track track) {
        TrackResponse response = new TrackResponse();
        response.setId(track.getId());
        response.setTitle(track.getTitle());
        response.setArtist(track.getArtist());
        response.setAlbum(track.getAlbum());
        response.setDuration(track.getDuration());
        response.setFileUrl(track.getFileUrl());
        response.setFileSize(track.getFileSize());
        response.setMimeType(track.getMimeType());
        response.setBitrate(track.getBitrate());
        response.setGenre(track.getGenre());
        response.setYear(track.getYear());
        response.setCoverUrl(track.getCoverUrl());
        response.setPlays(track.getPlays());
        response.setLikes(track.getLikes());
        response.setCreatedAt(track.getCreatedAt());

        if (track.getUser() != null) {
            response.setUserId(track.getUser().getId());
            response.setUsername(track.getUser().getUsername());
        }

        return response;
    }
}
