package com.audtream.server.model.repository;

import com.audtream.server.model.entity.UserEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findById(long id);

    @Override
    <S extends UserEntity> boolean exists(Example<S> example);

    boolean existsById(long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
