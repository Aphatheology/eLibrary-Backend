package com.aphatheology.elibrarybackend.dto;

import com.aphatheology.elibrarybackend.entity.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private Long id;

    @NotBlank(message = "Book title cannot be blank")
    private String title;

    @NotBlank(message = "Author name cannot be blank")
    private String author;

    private String image;

    @NotBlank(message = "Year of publication cannot be blank")
    private String year;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String slug;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
