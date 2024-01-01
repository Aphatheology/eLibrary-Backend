package com.aphatheology.elibrarybackend.repository;

import com.aphatheology.elibrarybackend.entity.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Tokens, Long> {
    Tokens findByTokenAndTokenType(String token, String tokenType);
}
