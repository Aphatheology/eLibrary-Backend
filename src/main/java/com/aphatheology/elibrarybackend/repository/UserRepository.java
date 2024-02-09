package com.aphatheology.elibrarybackend.repository;

import com.aphatheology.elibrarybackend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findUserByEmail(String email);
    Optional<Users> findUserByEmailAndIsVerified(String email, Boolean isVerified);
}
