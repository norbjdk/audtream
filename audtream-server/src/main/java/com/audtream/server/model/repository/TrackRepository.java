package com.audtream.server.model.repository;

import com.audtream.server.model.entity.TrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackRepository extends JpaRepository<TrackEntity, Long> {
    List<TrackEntity> findByUserId(Long userId);
    List<TrackEntity> findByUserIdAndTitleContaining(Long userId, String title);
}
