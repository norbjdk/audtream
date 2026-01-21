package com.audtream.server.controller;

import com.audtream.server.model.entity.Track;
import com.audtream.server.model.entity.User;
import com.audtream.server.model.repository.TrackRepository;
import com.audtream.server.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@CrossOrigin(origins = "http://localhost:5173")
public class TrackController {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Track>> getUserTracks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Track> tracks = trackRepository.findByUserId(user.getId());
        return ResponseEntity.ok(tracks);
    }

    @PostMapping
    public ResponseEntity<Track> createTrack(@RequestBody Track track) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        track.setUser(user);
        track.setCreatedAt(LocalDateTime.now());

        Track savedTrack = trackRepository.save(track);
        return ResponseEntity.ok(savedTrack);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Track> updateTrack(@PathVariable Long id, @RequestBody Track trackDetails) {
        Track track = trackRepository.findById(id)
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

        Track updatedTrack = trackRepository.save(track);
        return ResponseEntity.ok(updatedTrack);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!track.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        trackRepository.delete(track);
        return ResponseEntity.ok().build();
    }
}
