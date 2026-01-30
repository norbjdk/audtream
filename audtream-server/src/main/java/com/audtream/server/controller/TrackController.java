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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/all")
    public ResponseEntity<List<TrackResponse>> getAllTracks(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false, defaultValue = "newest") String sort) {

        List<Track> tracks;

        if (genre != null && !genre.isEmpty() && !genre.equalsIgnoreCase("all")) {
            tracks = trackRepository.findByGenre(genre);
        } else {
            tracks = trackRepository.findAll();
        }

        switch (sort.toLowerCase()) {
            case "popular":
                tracks.sort((a, b) -> b.getPlays().compareTo(a.getPlays()));
                break;
            case "newest":
                tracks.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                break;
            case "oldest":
                tracks.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
                break;
            case "liked":
                tracks.sort((a, b) -> b.getLikes().compareTo(a.getLikes()));
                break;
            default:
                tracks.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        }

        List<TrackResponse> responses = tracks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<TrackResponse>> getRecommendedTracks(
            @RequestParam(required = false, defaultValue = "20") int limit) {

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<Track> allTracks = trackRepository.findAll();

        List<Track> recommended = allTracks.stream()
                .filter(track -> track.getCreatedAt().isAfter(thirtyDaysAgo))
                .sorted((a, b) -> b.getPlays().compareTo(a.getPlays()))
                .limit(limit)
                .collect(Collectors.toList());

        List<TrackResponse> responses = recommended.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/new-releases")
    public ResponseEntity<List<TrackResponse>> getNewReleases(
            @RequestParam(required = false, defaultValue = "20") int limit) {

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        List<Track> newTracks = trackRepository.findAll().stream()
                .filter(track -> track.getCreatedAt().isAfter(sevenDaysAgo))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());

        List<TrackResponse> responses = newTracks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/top")
    public ResponseEntity<List<TrackResponse>> getTopTracks(
            @RequestParam(required = false, defaultValue = "50") int limit) {

        List<Track> topTracks = trackRepository.findAll().stream()
                .sorted((a, b) -> b.getPlays().compareTo(a.getPlays()))
                .limit(limit)
                .collect(Collectors.toList());

        List<TrackResponse> responses = topTracks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

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
            // Usuń parametry query jeśli istnieją
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }

            // Usuń endpoint i bucket z URL
            // http://localhost:9000/music-files/audio/filename.wav
            String[] parts = url.split("/");
            if (parts.length >= 5) {
                // parts[0] = "http:"
                // parts[1] = ""
                // parts[2] = "localhost:9000"
                // parts[3] = "music-files" (bucket)
                // parts[4+] = "audio/filename.wav" (objectName)
                StringBuilder objectName = new StringBuilder();
                for (int i = 4; i < parts.length; i++) {
                    if (objectName.length() > 0) objectName.append("/");
                    objectName.append(parts[i]);
                }
                return objectName.toString();
            }
        }
        return url;
    }

    @GetMapping("/{trackId}/url")
    public ResponseEntity<String> getTrackUrl(@PathVariable Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        try {
            // 1. Pobierz vanilla URL z bazy
            String vanillaUrl = track.getFileUrl(); // np: http://localhost:9000/music-files/audio/filename.wav

            // 2. Wyciągnij objectName z vanilla URL
            String objectName = extractObjectNameFromUrl(vanillaUrl);

            // 3. Wygeneruj NOWY pre-signed URL
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
