package com.aphatheology.elibrarybackend.service;

import com.aphatheology.elibrarybackend.dto.UserDto;
import com.aphatheology.elibrarybackend.entity.Role;
import com.aphatheology.elibrarybackend.entity.Users;
import com.aphatheology.elibrarybackend.exception.ResourceNotFoundException;
import com.aphatheology.elibrarybackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


    public UserDto map2Dto(Users user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFullname(user.getFullname());
        userDto.setVerified(user.getIsVerified());
        userDto.setRole(user.getRole().toString());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        return userDto;
    }

    public Users map2Entity(UserDto userDto) {
        Users user = new Users();
        user.setEmail(userDto.getEmail());
        user.setFullname(userDto.getFullname());
        user.setRole(Role.USER);
        user.setPassword(this.passwordEncoder.encode(userDto.getPassword()));

        return user;
    }

    public List<UserDto> getAllUsers() {
        List<Users> users = userRepository.findAll();

        return users.stream().map(this::map2Dto).toList();
    }

    public Users createUser(UserDto userBody) {
        Users user = map2Entity(userBody);
        return userRepository.save(user);
    }

    public UserDto getUser(Long userId) {
        Users user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User Not Found"));
        return map2Dto(user);
    }

    public UserDto updateUser(Long userId, UserDto userBody) {
        Users user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User Not Found"));

        this.modelMapper.map(userBody, user);
        userRepository.save(user);
        return map2Dto(user);
    }

    public void deleteUser(Long userId) {
        Users user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User Not Found"));
        userRepository.delete(user);
    }
}
