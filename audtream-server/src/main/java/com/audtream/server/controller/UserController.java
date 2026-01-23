package com.audtream.server.controller;

import com.audtream.server.model.dto.UserResponse;
import com.audtream.server.model.entity.Track;
import com.audtream.server.model.entity.User;
import com.audtream.server.model.repository.TrackRepository;
import com.audtream.server.model.repository.UserRepository;
import com.audtream.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrackRepository trackRepository;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getArtistStats() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Poprawione: używamy userRepository, a nie UserRepository (klasa)
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Track> tracks = trackRepository.findByUserId(user.getId());

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTracks", tracks.size());
        stats.put("totalPlays", tracks.stream().mapToInt(Track::getPlays).sum());
        stats.put("totalLikes", tracks.stream().mapToInt(Track::getLikes).sum());

        // Najpopularniejszy gatunek
        stats.put("topGenre", tracks.stream()
                .filter(t -> t.getGenre() != null && !t.getGenre().isEmpty())
                .collect(Collectors.groupingBy(Track::getGenre, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown"));

        // Średnia długość utworów
        if (!tracks.isEmpty()) {
            double avgDuration = tracks.stream()
                    .mapToInt(Track::getDuration)
                    .average()
                    .orElse(0.0);
            stats.put("averageDuration", Math.round(avgDuration));
        } else {
            stats.put("averageDuration", 0);
        }

        // Najpopularniejszy utwór (najwięcej odtworzeń)
        if (!tracks.isEmpty()) {
            Track topTrack = tracks.stream()
                    .max((t1, t2) -> Integer.compare(t1.getPlays(), t2.getPlays()))
                    .orElse(null);
            if (topTrack != null) {
                stats.put("mostPlayedTrack", topTrack.getTitle());
                stats.put("mostPlayedTrackPlays", topTrack.getPlays());
            }
        }

        return ResponseEntity.ok(stats);
    }
}