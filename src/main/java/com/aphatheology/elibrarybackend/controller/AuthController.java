package com.aphatheology.elibrarybackend.controller;

import com.aphatheology.elibrarybackend.dto.ApiResponse;
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

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid UserDto registerBody, final HttpServletRequest request) {
        return new ResponseEntity<>(authService.register(registerBody, request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid LoginDto loginBody) {
        return new ResponseEntity<>(authService.login(loginBody), HttpStatus.CREATED);
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verifyToken(@RequestParam("token") String token, Principal principal) {
        String response = authService.verifyToken(token, principal);
        return ResponseEntity.ok(new ApiResponse(true, response, null));
    }

    @GetMapping("/resend-verification")
    public ResponseEntity<ApiResponse> resendVerificationToken(Principal principal, final HttpServletRequest request) {
        String response = authService.resendVerificationToken(principal, request);
        return ResponseEntity.ok(new ApiResponse(true, response, null));
    }
}
