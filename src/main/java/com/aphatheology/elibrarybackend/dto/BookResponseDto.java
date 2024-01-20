package com.aphatheology.elibrarybackend.dto;

import com.aphatheology.elibrarybackend.entity.Category;
import com.aphatheology.elibrarybackend.entity.Feedbacks;
import com.aphatheology.elibrarybackend.entity.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto {
    private Long id;
    private String title;
    private String author;
    private String image;
    private Category category;
    private Status status;
    private String uploadedBy;
    private Double averageRating;
    private List<Feedbacks> feedbacks;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
