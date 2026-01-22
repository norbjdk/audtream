package com.audtream.server.model.repository;

import com.audtream.server.model.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Override
    <S extends User> boolean exists(Example<S> example);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
