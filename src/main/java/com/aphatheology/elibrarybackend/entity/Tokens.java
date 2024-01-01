package com.aphatheology.elibrarybackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@NoArgsConstructor
public class Tokens {
    private static final int EXPIRATION_TIME = 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime expirationTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_TOKEN_USER"))
    private Users user;

    private String tokenType;

    public Tokens(Users user, String token, String tokenType) {
        super();

        this.user = user;
        this.token = token;
        this.tokenType = tokenType;
        this.expirationTime = calculateExpirationTime();
    }

    public Tokens(String token) {
        super();
        this.token = token;
        this.expirationTime = calculateExpirationTime();
    }

    private LocalDateTime calculateExpirationTime() {
        return LocalDateTime.now().plusHours(Tokens.EXPIRATION_TIME);
    }

    public static boolean isValidToken(LocalDateTime expiryDate) {
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), expiryDate);
        return minutes >= 0;
    }

    public void updateToken(String code, String tokenType){
        this.token = code;
        this.tokenType = tokenType;
        this.expirationTime = calculateExpirationTime();
    }
}
