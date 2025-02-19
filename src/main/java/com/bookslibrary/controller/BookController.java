package com.bookslibrary.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.bookslibrary.entity.BookEntity;
import com.bookslibrary.repository.BookRepository;
import com.bookslibrary.service.BookService;
import com.bookslibrary.service.InsightService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/books")
@Tag(name = "Book Management", description = "Books Library System Controller")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private InsightService insightService;

    @PostMapping
    @Operation(summary = "Save a new book in the library", description = "Create a new book with mandatory fields using a json structure")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created the book",
                                            content = @Content(mediaType = "application/json", 
                                             schema = @Schema(implementation = BookEntity.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error for problems with process or application")
    })
    public ResponseEntity<BookEntity> create(@Valid @RequestBody BookEntity book){

        return new ResponseEntity<>(bookRepository.save(book), HttpStatus.CREATED) ;
    }

    @GetMapping
    @Operation(summary = "Get a list of all books with page, size list and sort.")
    public ResponseEntity<Page<BookEntity>> listPageable(
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "title,asc") String[] sort,
                                @RequestParam(required = false) String query){


        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        Page<BookEntity> booksPage = bookService.findPageable(query, pageable);

        return new ResponseEntity<>(booksPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by Id")
    public ResponseEntity<BookEntity> getById(@PathVariable Long id){

        Optional<BookEntity> book = bookRepository.findById(id);

        return book.map(bookFounded -> new ResponseEntity<>(bookFounded, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing book by Id")
    public ResponseEntity<BookEntity> update(@PathVariable Long id, @Valid @RequestBody BookEntity bookUpdated){

        return new ResponseEntity<>(bookService.updateBook(id, bookUpdated), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book by Id")
    public ResponseEntity<Void> delete(@PathVariable Long id){

        if (bookRepository.existsById(id)){

            bookRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}/ai-insights")
    @Operation(summary = "Get an AI summary for a specific book by Id")
    public ResponseEntity<BookEntity> getBookInsights(@PathVariable Long id){

        BookEntity bookEntity = insightService.getBookInsights(id);

        bookService.updateBook(id, bookEntity);
        return new ResponseEntity<>(bookEntity, HttpStatus.OK);
    }

}
