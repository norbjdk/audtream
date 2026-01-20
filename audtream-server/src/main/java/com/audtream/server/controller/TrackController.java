package com.audtream.server.controller;

import com.audtream.server.model.entity.TrackEntity;
import com.audtream.server.model.entity.UserEntity;
import com.audtream.server.model.repository.TrackRepository;
import com.audtream.server.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tracks")
@CrossOrigin(origins = "https:localhost:5173")
public class TrackController {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<TrackEntity>> getUserTracks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TrackEntity> tracks = trackRepository.findByUserId(user.getId());
        return ResponseEntity.ok(tracks);
    }

    @PostMapping
    public ResponseEntity<TrackEntity> createTrack(@RequestBody TrackEntity track) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        track.setUser(user);
        track.setCreatedAt(LocalDateTime.now());

        TrackEntity savedTrack = trackRepository.save(track);
        return ResponseEntity.ok(savedTrack);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackEntity> updateTrack(@PathVariable Long id, @RequestBody TrackEntity trackDetails) {
        TrackEntity track = trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!track.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        track.setTitle(trackDetails.getTitle());
        track.setArtist(trackDetails.getArtist());
        track.setAlbum(trackDetails.getAlbum());
        track.setDuration(trackDetails.getDuration());
        track.setGenre(trackDetails.getGenre());

        TrackEntity updatedTrack = trackRepository.save(track);
        return ResponseEntity.ok(updatedTrack);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        TrackEntity track = trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!track.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        trackRepository.delete(track);
        return ResponseEntity.ok().build();
    }
}
