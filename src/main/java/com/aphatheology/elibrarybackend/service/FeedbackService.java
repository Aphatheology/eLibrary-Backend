package com.aphatheology.elibrarybackend.service;

import com.aphatheology.elibrarybackend.dto.FeedbackDto;
import com.aphatheology.elibrarybackend.dto.FeedbackResponseDto;
import com.aphatheology.elibrarybackend.entity.Books;
import com.aphatheology.elibrarybackend.entity.Feedbacks;
import com.aphatheology.elibrarybackend.entity.Users;
import com.aphatheology.elibrarybackend.exception.ResourceNotFoundException;
import com.aphatheology.elibrarybackend.repository.BookRepository;
import com.aphatheology.elibrarybackend.repository.FeedbackRepository;
import com.aphatheology.elibrarybackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final BookRepository bookRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    private Feedbacks map2Entity(FeedbackDto feedbackDto, Books books, Users users) {
        Feedbacks feedbacks = new Feedbacks();
        feedbacks.setBook(books);
        feedbacks.setBy(users);
        feedbacks.setMessage(feedbackDto.getMessage());
        feedbacks.setRating(feedbackDto.getRating());

        return feedbacks;
    }

    private FeedbackResponseDto map2Dto(Feedbacks feedbacks) {
        FeedbackResponseDto feedbackResponseDto = new FeedbackResponseDto();

        feedbackResponseDto.setId(feedbacks.getId());
        feedbackResponseDto.setMessage(feedbacks.getMessage());
        feedbackResponseDto.setRating(feedbacks.getRating());
        feedbackResponseDto.setBy(feedbacks.getBy().getFullname());
        feedbackResponseDto.setBookId(feedbacks.getBook().getId());
        feedbackResponseDto.setCreatedAt(feedbacks.getCreatedAt());
        feedbackResponseDto.setUpdatedAt(feedbacks.getUpdatedAt());

        return feedbackResponseDto;
    }

    public FeedbackResponseDto createFeedback(FeedbackDto feedbackDto, Long bookId, Principal principal) {
        Books book = this.bookRepository.findById(bookId).orElseThrow(() ->
                new ResourceNotFoundException("Book Not Found"));

        Users user = userRepository.findUserByEmail(principal.getName()).orElseThrow(() ->
                new ResourceNotFoundException("User Not Found"));

        Feedbacks feedback = map2Entity(feedbackDto, book, user);
        this.feedbackRepository.save(feedback);
        return this.map2Dto(feedback);

    }
}
