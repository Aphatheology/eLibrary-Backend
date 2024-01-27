package com.aphatheology.elibrarybackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Email cannot be blank")
    @Email(regexp = ".+[@].+[\\.].+", message = "Invalid email")
    private String email;

    @NotBlank(message = "Fullname cannot be blank")
    private String fullname;

    @NotBlank(message = "Password can not be blank")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?([^\\w\\s]|[_])).{8,}$", message = "Invalid password, it must have at least one lower case, one upper case, one number, one special character, and be between 8 and 20 characters long")
    private String password;

    private String role;
    private Boolean verified;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
