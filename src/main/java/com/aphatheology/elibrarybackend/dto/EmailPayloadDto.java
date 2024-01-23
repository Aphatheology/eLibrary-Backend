package com.aphatheology.elibrarybackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailPayloadDto {
    @NotBlank(message = "receiverEmail: Receiver Email cannot be blank")
    @Email(regexp = ".+[@].+[\\.].+", message = "Invalid email")
    private String receiverEmail;

    @NotBlank(message = "subject: Email Subject cannot be blank")
    private String subject;

    @NotBlank(message = "contentHeader: Email Content Header cannot be blank")
    private String contentHeader;

    @NotBlank(message = "content: Email Content cannot be blank")
    private String content;

    @NotBlank(message = "receiverName: Receiver Name cannot be blank")
    private String receiverName;

    @NotBlank(message = "templateName: Template Name cannot be blank")
    private String templateName;

    private String token;
}
