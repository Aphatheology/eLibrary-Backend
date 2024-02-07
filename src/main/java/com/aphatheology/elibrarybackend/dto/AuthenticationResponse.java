package com.aphatheology.elibrarybackend.dto;

import com.aphatheology.elibrarybackend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private Long id;
    private String email;
    private String fullname;
    private Role role;
    private Boolean isVerified;
    private String accessToken;
}
