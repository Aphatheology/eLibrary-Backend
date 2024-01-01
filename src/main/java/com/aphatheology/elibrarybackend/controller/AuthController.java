package com.aphatheology.elibrarybackend.controller;

import com.aphatheology.elibrarybackend.dto.AuthenticationResponse;
import com.aphatheology.elibrarybackend.dto.LoginDto;
import com.aphatheology.elibrarybackend.dto.UserDto;
import com.aphatheology.elibrarybackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<HashMap<String, String>> register(@RequestBody @Valid UserDto registerBody, final HttpServletRequest request) {
        return new ResponseEntity<>(authService.register(registerBody, request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid LoginDto loginBody) {
        return new ResponseEntity<>(authService.login(loginBody), HttpStatus.CREATED);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyToken(@RequestParam("token") String token) {
        return new ResponseEntity<>(authService.verifyToken(token), HttpStatus.OK);
    }

    @GetMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationToken(@RequestParam("token") String oldToken, final HttpServletRequest request) {
        return new ResponseEntity<>(authService.resendVerificationToken(oldToken, request), HttpStatus.OK);
    }




}
