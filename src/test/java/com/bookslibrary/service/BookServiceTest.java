package com.bookslibrary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.bookslibrary.entity.BookEntity;
import com.bookslibrary.repository.BookRepository;

@SpringBootTest
@ActiveProfiles("test")
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @MockitoBean
    private BookRepository bookRepository;

    private BookEntity bookEntity1;
    private BookEntity bookEntity2;

    @BeforeEach
    void setUp(){
        bookEntity1 = new BookEntity();
        bookEntity1.setTitle("The Hobbit");
        bookEntity1.setAuthor("J.R.R. Tolkien");
        bookEntity1.setIsbn("1111111111111");
        bookEntity1.setPublicationYear("2001");

        bookEntity2 = new BookEntity();
        bookEntity2.setTitle("The Hobbit2");
        bookEntity2.setAuthor("J.R.R. Tolkien2");
        bookEntity2.setIsbn("2222222222222");
        bookEntity2.setPublicationYear("2002");

        bookRepository.save(bookEntity1);
        bookRepository.save(bookEntity2);
    }
    
    @Test
    void testFilterBooksByAuthorAndTitle() {
        when(bookRepository.findByAuthorContainingAndTitleContaining(anyString(), anyString()))
                .thenReturn(List.of(bookEntity1));

        List<BookEntity> result = bookService.filterBooksBy("The Hobbit", "Tolkien");
        assertThat(result).hasSize(1).contains(bookEntity1);
    }

    @Test
    void testFilterBooksByTitle() {
        when(bookRepository.findByTitleContaining(anyString()))
                .thenReturn(List.of(bookEntity1, bookEntity2));

        List<BookEntity> result = bookService.filterBooksBy("The", null);
        assertThat(result).hasSize(2).contains(bookEntity1, bookEntity2);
    }

    @Test
    void testFilterBooksByAuthor() {
        when(bookRepository.findByAuthorContaining(anyString()))
                .thenReturn(List.of(bookEntity1, bookEntity2));

        List<BookEntity> result = bookService.filterBooksBy(null, "Tolkien");
        assertThat(result).hasSize(2).contains(bookEntity1, bookEntity2);
    }

    @Test
    void testFilterBooksByNoParams() {
        when(bookRepository.findAll())
                .thenReturn(List.of(bookEntity1, bookEntity2));

        List<BookEntity> result = bookService.filterBooksBy(null, null);
        assertThat(result).hasSize(2).contains(bookEntity1, bookEntity2);
    }
}
