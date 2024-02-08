package com.aphatheology.elibrarybackend.service;

import com.aphatheology.elibrarybackend.dto.BookDto;
import com.aphatheology.elibrarybackend.dto.BookResponseDto;
import com.aphatheology.elibrarybackend.entity.*;
import com.aphatheology.elibrarybackend.exception.BadRequestException;
import com.aphatheology.elibrarybackend.exception.ResourceNotFoundException;
import com.aphatheology.elibrarybackend.repository.BookRepository;
import com.aphatheology.elibrarybackend.repository.UserRepository;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    final Slugify slg = Slugify.builder().build();

    private BookResponseDto map2Dto(Books books) {
        BookResponseDto bookResponseDto = new BookResponseDto();
        bookResponseDto.setId(books.getId());
        bookResponseDto.setTitle(books.getTitle());
        bookResponseDto.setAuthor(books.getAuthor());
        bookResponseDto.setImage(books.getImage());
        bookResponseDto.setAverageRating(books.getAverageRating());
        bookResponseDto.setStatus(books.getStatus());
        bookResponseDto.setCategory(books.getCategory());
        bookResponseDto.setSlug(books.getSlug());
        bookResponseDto.setFeedbacks(books.getFeedbacks());
        bookResponseDto.setUploadedBy(books.getUploadedBy().getFullname());
        bookResponseDto.setCreatedAt(books.getCreatedAt());
        bookResponseDto.setUpdatedAt(books.getUpdatedAt());

        return bookResponseDto;
    }

    private Books map2Entity(BookDto bookDto, Users user) {
        Books books = new Books();
        books.setAuthor(bookDto.getAuthor());
        books.setTitle(bookDto.getTitle());
        books.setCategory(Category.valueOf(bookDto.getCategory()));
        books.setStatus(bookDto.getStatus());
        books.setYear(bookDto.getYear());
        books.setImage(bookDto.getImage());
        books.setSlug(bookDto.getSlug());
        books.setUploadedBy(user);

        return books;
    }

    public List<BookResponseDto> getAllBooks() {
        List<Books> books = bookRepository.findAll();

        return books.stream().map(this::map2Dto).toList();
    }

    public BookResponseDto createBook(BookDto bookDto, Principal principal) {
        Users user = userRepository.findUserByEmailAndIsVerified(principal.getName()).orElseThrow(() ->
                new BadRequestException("User Email not verified yet."));

        bookDto.setSlug(checkAndCreateSlug(bookDto));

        if (user.getRole() == Role.ADMIN) {
            bookDto.setStatus(Status.APPROVED);
        } else {
            bookDto.setStatus(Status.PENDING);
        }

        Books books = map2Entity(bookDto, user);
        bookRepository.save(books);
        return this.map2Dto(books);
    }

    public BookResponseDto getBookById(Long bookId) {
        Books book = this.bookRepository.findById(bookId).orElseThrow(() ->
                new ResourceNotFoundException("Book Not Found"));

        return this.map2Dto(book);
    }

    public BookResponseDto getBookBySlug(String slug) {
        Books book = this.bookRepository.findBySlug(slug).orElseThrow(() ->
                new ResourceNotFoundException("Book Not Found"));

        return this.map2Dto(book);
    }

    public BookResponseDto updateBookById(Long bookId, BookDto bookDto, Principal principal) {
        Books book = accessUserAndBook(bookId, principal);
        if(bookDto.getTitle() != null) {
            System.out.println("check and create going on");
            bookDto.setSlug(checkAndCreateSlug(bookDto));
        }

        this.modelMapper.map(bookDto, book);
        this.bookRepository.save(book);

        return this.map2Dto(book);
    }

    public void deleteBook(Long bookId, Principal principal) {
        Books book = this.accessUserAndBook(bookId, principal);

        this.bookRepository.delete(book);
    }

    private Books accessUserAndBook(Long bookId, Principal principal) {
        Books book = this.bookRepository.findById(bookId).orElseThrow(() ->
                new ResourceNotFoundException("Book Not Found"));

        Users user = userRepository.findUserByEmail(principal.getName()).orElseThrow(() ->
                new ResourceNotFoundException("User Not Found"));

        if (book.getUploadedBy().getId() != user.getId() && user.getRole() != Role.ADMIN ) {
            throw new AccessDeniedException("Access denied");
        }
        return book;
    }

    private String checkAndCreateSlug(BookDto bookDto) {
        String slug = slg.slugify(bookDto.getTitle());
        Optional<Books> book = this.bookRepository.findBySlug(slug);

        if(book.isPresent()) {
            Random random = new Random();
            int randomNumber = 100000 + random.nextInt(900000);

            slug += randomNumber;
        }

        return slug;
    }
}
