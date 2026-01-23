package com.audtream.server.model.repository;

import com.audtream.server.model.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findByUserId(Long userId);
    List<Track> findByUserIdAndTitleContaining(Long userId, String title);

    List<Track> findByGenre(String genre);

    @Query("SELECT t FROM Track t ORDER BY t.plays DESC")
    List<Track> findTopTracks();

    @Query("SELECT t FROM Track t ORDER BY t.createdAt DESC")
    List<Track> findNewestTracks();
}
