package com.audtream.server.model.repository;

import com.audtream.server.model.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserId(Long userId);
    List<Playlist> findByUserIdAndNameContaining(Long userId, String name);
    List<Playlist> findByIsPublicTrue();

    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true ORDER BY p.plays DESC")
    List<Playlist> findTopPlaylists();

    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true ORDER BY p.createdAt DESC")
    List<Playlist> findNewestPlaylists();

    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true ORDER BY p.likes DESC")
    List<Playlist> findTrendingPlaylists();

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Playlist p WHERE p.id = :playlistId AND p.user.id = :userId")
    boolean isUserOwner(@Param("playlistId") Long playlistId, @Param("userId") Long userId);

    List<Playlist> findByIsPublicTrueAndNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Playlist p JOIN p.tracks t WHERE t.id = :trackId")
    List<Playlist> findByTrackId(@Param("trackId") Long trackId);

    @Query("SELECT p FROM Playlist p LEFT JOIN FETCH p.tracks WHERE p.id = :id")
    Optional<Playlist> findByIdWithTracks(@Param("id") Long id);
}