package com.aphatheology.elibrarybackend.controller;

import com.aphatheology.elibrarybackend.dto.UpdateUserDto;
import com.aphatheology.elibrarybackend.dto.UserResponseDto;
import com.aphatheology.elibrarybackend.entity.Users;
import com.aphatheology.elibrarybackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or #userId == principal.id")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("userId") Long userId) {
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or #userId == principal.id")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable("userId") Long userId, @RequestBody @Valid UpdateUserDto userBody) {
        return new ResponseEntity<>(userService.updateUser(userId, userBody), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Users> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
