package com.aphatheology.elibrarybackend.controller;

import com.aphatheology.elibrarybackend.dto.BookDto;
import com.aphatheology.elibrarybackend.dto.BookResponseDto;
import com.aphatheology.elibrarybackend.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> getAllBooks() {
        return new ResponseEntity<>(bookService.getAllBooks(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BookResponseDto> createBook(@RequestBody @Valid BookDto bookDto, Principal principal) {
        return new ResponseEntity<>(bookService.createBook(bookDto, principal), HttpStatus.CREATED);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable("bookId") Long bookId) {
        return new ResponseEntity<>(this.bookService.getBookById(bookId), HttpStatus.OK);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable("slug") String slug) {
        return new ResponseEntity<>(this.bookService.getBookBySlug(slug), HttpStatus.OK);
    }
}
