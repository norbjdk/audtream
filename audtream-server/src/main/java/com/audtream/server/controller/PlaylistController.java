package com.audtream.server.controller;

import com.audtream.server.model.dto.*;
import com.audtream.server.model.entity.Playlist;
import com.audtream.server.model.entity.Track;
import com.audtream.server.model.entity.User;
import com.audtream.server.model.repository.PlaylistRepository;
import com.audtream.server.model.repository.TrackRepository;
import com.audtream.server.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/playlists")
@CrossOrigin(origins = "http://localhost:5173")
public class PlaylistController {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrackRepository trackRepository;

    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(@RequestBody PlaylistRequest playlistRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = getUserByUsername(username);

        Playlist playlist = new Playlist();
        playlist.setName(playlistRequest.getName());
        playlist.setDescription(playlistRequest.getDescription());
        playlist.setIsPublic(playlistRequest.getIsPublic());
        playlist.setCoverImageUrl(playlistRequest.getCoverImageUrl());
        playlist.setUser(user);

        if (playlistRequest.getTrackIds() != null) {
            for (Long trackId : playlistRequest.getTrackIds()) {
                Track track = trackRepository.findById(trackId)
                        .orElseThrow(() -> new RuntimeException("Track not found: " + trackId));
                playlist.addTrack(track);
            }
        }

        Playlist savedPlaylist = playlistRepository.save(playlist);
        return ResponseEntity.ok(convertToResponse(savedPlaylist, true));
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistResponse> getPlaylist(@PathVariable Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        // Sprawdź czy użytkownik może zobaczyć playlistę
        if (!playlist.getIsPublic()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = getUserByUsername(username);
            if (!playlist.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Playlist is private");
            }
        }

        return ResponseEntity.ok(convertToResponse(playlist, true));
    }

    @PutMapping("/{playlistId}")
    public ResponseEntity<PlaylistResponse> updatePlaylist(
            @PathVariable Long playlistId,
            @RequestBody PlaylistUpdateRequest updateRequest) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = getUserByUsername(username);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this playlist");
        }

        if (updateRequest.getName() != null) {
            playlist.setName(updateRequest.getName());
        }
        if (updateRequest.getDescription() != null) {
            playlist.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getIsPublic() != null) {
            playlist.setIsPublic(updateRequest.getIsPublic());
        }
        if (updateRequest.getCoverImageUrl() != null) {
            playlist.setCoverImageUrl(updateRequest.getCoverImageUrl());
        }

        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return ResponseEntity.ok(convertToResponse(updatedPlaylist, true));
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long playlistId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = getUserByUsername(username);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this playlist");
        }

        playlistRepository.delete(playlist);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{playlistId}/tracks/{trackId}")
    public ResponseEntity<PlaylistResponse> addTrackToPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long trackId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = getUserByUsername(username);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to modify this playlist");
        }

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        playlist.addTrack(track);
        Playlist updatedPlaylist = playlistRepository.save(playlist);

        return ResponseEntity.ok(convertToResponse(updatedPlaylist, true));
    }

    @DeleteMapping("/{playlistId}/tracks/{trackId}")
    public ResponseEntity<PlaylistResponse> removeTrackFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long trackId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = getUserByUsername(username);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to modify this playlist");
        }

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        playlist.removeTrack(track);
        Playlist updatedPlaylist = playlistRepository.save(playlist);

        return ResponseEntity.ok(convertToResponse(updatedPlaylist, true));
    }

    @PostMapping("/{playlistId}/tracks/reorder")
    public ResponseEntity<PlaylistResponse> reorderTracks(
            @PathVariable Long playlistId,
            @RequestBody List<Long> trackIds) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = getUserByUsername(username);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to modify this playlist");
        }

        // Pobierz utwory w nowej kolejności
        List<Track> reorderedTracks = trackIds.stream()
                .map(trackId -> trackRepository.findById(trackId)
                        .orElseThrow(() -> new RuntimeException("Track not found: " + trackId)))
                .collect(Collectors.toList());

        playlist.setTracks(reorderedTracks);
        Playlist updatedPlaylist = playlistRepository.save(playlist);

        return ResponseEntity.ok(convertToResponse(updatedPlaylist, true));
    }

    @GetMapping("/user/my")
    public ResponseEntity<List<PlaylistResponse>> getUserPlaylists() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = getUserByUsername(username);

        List<Playlist> playlists = playlistRepository.findByUserId(user.getId());
        List<PlaylistResponse> responses = playlists.stream()
                .map(playlist -> convertToResponse(playlist, false))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlaylistResponse>> getUserPublicPlaylists(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Playlist> playlists = playlistRepository.findByUserId(user.getId());
        List<PlaylistResponse> responses = playlists.stream()
                .filter(Playlist::getIsPublic)
                .map(playlist -> convertToResponse(playlist, false))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/explore/trending")
    public ResponseEntity<List<PlaylistResponse>> getTrendingPlaylists(
            @RequestParam(defaultValue = "20") int limit) {

        List<Playlist> playlists = playlistRepository.findTrendingPlaylists().stream()
                .limit(limit)
                .collect(Collectors.toList());

        List<PlaylistResponse> responses = playlists.stream()
                .map(playlist -> convertToResponse(playlist, false))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/explore/new")
    public ResponseEntity<List<PlaylistResponse>> getNewPlaylists(
            @RequestParam(defaultValue = "20") int limit) {

        List<Playlist> playlists = playlistRepository.findNewestPlaylists().stream()
                .limit(limit)
                .collect(Collectors.toList());

        List<PlaylistResponse> responses = playlists.stream()
                .map(playlist -> convertToResponse(playlist, false))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/explore/top")
    public ResponseEntity<List<PlaylistResponse>> getTopPlaylists(
            @RequestParam(defaultValue = "20") int limit) {

        List<Playlist> playlists = playlistRepository.findTopPlaylists().stream()
                .limit(limit)
                .collect(Collectors.toList());

        List<PlaylistResponse> responses = playlists.stream()
                .map(playlist -> convertToResponse(playlist, false))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/explore/search")
    public ResponseEntity<List<PlaylistResponse>> searchPlaylists(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int limit) {

        List<Playlist> playlists = playlistRepository
                .findByIsPublicTrueAndNameContainingIgnoreCase(query).stream()
                .limit(limit)
                .collect(Collectors.toList());

        List<PlaylistResponse> responses = playlists.stream()
                .map(playlist -> convertToResponse(playlist, false))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{playlistId}/like")
    public ResponseEntity<Void> likePlaylist(@PathVariable Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getIsPublic()) {
            throw new RuntimeException("Cannot like private playlist");
        }

        playlist.setLikes(playlist.getLikes() + 1);
        playlistRepository.save(playlist);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{playlistId}/play")
    public ResponseEntity<Void> incrementPlayCount(@PathVariable Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        playlist.setPlays(playlist.getPlays() + 1);
        playlistRepository.save(playlist);

        return ResponseEntity.ok().build();
    }


    private User getUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private PlaylistResponse convertToResponse(Playlist playlist, boolean includeTracks) {
        PlaylistResponse response = new PlaylistResponse();
        response.setId(playlist.getId());
        response.setName(playlist.getName());
        response.setDescription(playlist.getDescription());
        response.setIsPublic(playlist.getIsPublic());
        response.setCoverImageUrl(playlist.getCoverImageUrl());
        response.setTrackCount(playlist.getTrackCount());
        response.setTotalDuration(playlist.getTotalDuration());
        response.setPlays(playlist.getPlays());
        response.setLikes(playlist.getLikes());
        response.setCreatedAt(playlist.getCreatedAt());
        response.setUpdatedAt(playlist.getUpdatedAt());

        if (playlist.getUser() != null) {
            response.setUserId(playlist.getUser().getId());
            response.setUsername(playlist.getUser().getUsername());
        }

        if (includeTracks && playlist.getTracks() != null) {
            List<TrackResponse> trackResponses = playlist.getTracks().stream()
                    .map(this::convertTrackToResponse)
                    .collect(Collectors.toList());
            response.setTracks(trackResponses);
        }

        return response;
    }

    private TrackResponse convertTrackToResponse(Track track) {
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
        response.setUpdatedAt(track.getUpdatedAt());

        if (track.getUser() != null) {
            response.setUserId(track.getUser().getId());
            response.setUsername(track.getUser().getUsername());
        }

        return response;
    }
}