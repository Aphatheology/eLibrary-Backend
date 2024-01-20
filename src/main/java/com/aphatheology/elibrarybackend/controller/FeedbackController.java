package com.aphatheology.elibrarybackend.controller;

import com.aphatheology.elibrarybackend.dto.FeedbackDto;
import com.aphatheology.elibrarybackend.dto.FeedbackResponseDto;
import com.aphatheology.elibrarybackend.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponseDto> createFeedback(@RequestBody @Valid FeedbackDto feedbackDto, @PathVariable Long bookId, Principal principal) {
        return new ResponseEntity<>(this.feedbackService.createFeedback(feedbackDto, bookId, principal), HttpStatus.CREATED);
    }
}
