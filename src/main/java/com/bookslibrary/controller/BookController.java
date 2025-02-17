package com.bookslibrary.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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



@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private InsightService insightService;

    @PostMapping
    public BookEntity create(@Valid @RequestBody BookEntity book) {
        return bookRepository.save(book);
    }

    @GetMapping
    public List<BookEntity> getAll() {
        return bookRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BookEntity> getById(@PathVariable Long id) {
        Optional<BookEntity> book = bookRepository.findById(id);
        return book.map(bookFounded -> new ResponseEntity<>(bookFounded, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BookEntity> update(@Valid @PathVariable Long id, @RequestBody BookEntity bookUpdated) {
        Optional<BookEntity> bookToUpdate = bookRepository.findById(id);
        if(bookToUpdate.isPresent()){
            BookEntity book = bookToUpdate.get();
            book.setTitle(bookUpdated.getTitle());
            book.setAuthor(bookUpdated.getAuthor());
            book.setIsbn(bookUpdated.getIsbn());
            book.setPublicationYear(bookUpdated.getPublicationYear());
            book.setDescription(bookUpdated.getDescription());
            return new ResponseEntity<>(bookRepository.save(book), HttpStatus.OK) ;
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/filterBy")   
    public ResponseEntity<List<BookEntity>> filterBy(@RequestParam(required = false) String title,
                                                    @RequestParam(required = false) String author) {
        List<BookEntity> books = bookService.filterBooksBy(title, author);

        if(!books.isEmpty())
            return new ResponseEntity<>(books, HttpStatus.OK);
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}/ai-insights")
    public ResponseEntity<BookEntity> getBookInsights(@PathVariable Long id) {
        
        return new ResponseEntity<>(insightService.getBookInsights(id), HttpStatus.OK);
    }
    
}
