package com.aphatheology.elibrarybackend.service;

import com.aphatheology.elibrarybackend.dto.BookDto;
import com.aphatheology.elibrarybackend.dto.BookResponseDto;
import com.aphatheology.elibrarybackend.entity.*;
import com.aphatheology.elibrarybackend.exception.ResourceNotFoundException;
import com.aphatheology.elibrarybackend.repository.BookRepository;
import com.aphatheology.elibrarybackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookResponseDto map2Dto(Books books) {
        BookResponseDto bookResponseDto = new BookResponseDto();
        bookResponseDto.setId(books.getId());
        bookResponseDto.setTitle(books.getTitle());
        bookResponseDto.setAuthor(books.getAuthor());
        bookResponseDto.setImage(books.getImage());
        bookResponseDto.setAverageRating(books.getAverageRating());
        bookResponseDto.setStatus(books.getStatus());
        bookResponseDto.setCategory(books.getCategory());
        bookResponseDto.setFeedbacks(books.getFeedbacks());
        bookResponseDto.setUploadedBy(books.getUploadedBy().getFullname());
        bookResponseDto.setCreatedAt(books.getCreatedAt());
        bookResponseDto.setUpdatedAt(books.getUpdatedAt());

        return bookResponseDto;
    }

    public Books map2Entity(BookDto bookDto, Users user) {
        Books books = new Books();
        books.setAuthor(bookDto.getAuthor());
        books.setTitle(bookDto.getTitle());
        books.setCategory(Category.valueOf(bookDto.getCategory()));
        books.setStatus(bookDto.getStatus());
        books.setYear(bookDto.getYear());
        books.setImage(bookDto.getImage());
        books.setUploadedBy(user);

        return books;
    }


    public List<BookResponseDto> getAllBooks() {
        List<Books> books = bookRepository.findAll();

        return books.stream().map(this::map2Dto).toList();
    }

    public BookResponseDto createBook(BookDto bookDto, Principal principal) {
        Users user = userRepository.findUserByEmail(principal.getName()).orElseThrow(() ->
                new ResourceNotFoundException("User Not Found"));

        if (user.getRole() == Role.ADMIN) {
            bookDto.setStatus(Status.APPROVED);
        } else {
            bookDto.setStatus(Status.PENDING);
        }

        Books books = map2Entity(bookDto, user);
        bookRepository.save(books);
        return map2Dto(books);
    }

    public BookResponseDto getBookById(Long bookId) {
        Books book = this.bookRepository.findById(bookId).orElseThrow(() ->
                new ResourceNotFoundException("Book Not Found"));

        return map2Dto(book);
    }
}
