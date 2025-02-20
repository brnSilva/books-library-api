package com.bookslibrary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bookslibrary.entity.BookEntity;
import com.bookslibrary.exception.ResouceNotFoundException;
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

    Pageable pageable = PageRequest.of(0, 10);

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
    void testFindPageableWithQueryParam_Success(){
        
        Page<BookEntity> bookPage = new PageImpl<>(Arrays.asList(bookEntity1, bookEntity2));

        when(bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase("Hobbit", "Hobbit", pageable)).thenReturn(bookPage);
        
        Page<BookEntity> result = bookService.findPageable("Hobbit", pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(bookEntity1));
        assertTrue(result.getContent().contains(bookEntity2));
    }

    @Test
    void testFindPageableWithoutQueryParam_Success(){
        
        Page<BookEntity> bookPage = new PageImpl<>(Arrays.asList(bookEntity1, bookEntity2));

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        Page<BookEntity> result = bookService.findPageable(null, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(bookEntity1));
        assertTrue(result.getContent().contains(bookEntity2));
    }

    @Test
    void testFindPageableEmptyResult_Success(){

        Page<BookEntity> bookPage = new PageImpl<>(Collections.emptyList());

        when(bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase("Nonexistent", "Nonexistent", pageable)).thenReturn(bookPage);
        
        Page<BookEntity> result = bookService.findPageable("Nonexistent", pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testUpdateBook_Success(){

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookEntity1));
        when(bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity2);

        BookEntity result = bookService.updateBook(anyLong(), bookEntity1);

        assertNotNull(result);
        assertEquals("The Hobbit2", result.getTitle());
        assertEquals("J.R.R. Tolkien2", result.getAuthor());
        assertEquals("2222222222222", result.getIsbn());
        assertEquals("2002", result.getPublicationYear());
    }

    @Test
    void testUpdateBook_NotFound(){

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResouceNotFoundException.class, () -> {
            bookService.updateBook(anyLong(), bookEntity1);
        });
    }

    @Test
    void testUpdateBookInvalidId_IllegalArgumentException(){

        when(bookRepository.findById(anyLong())).thenThrow(new IllegalArgumentException("Invalid ID"));

        assertThrows(IllegalArgumentException.class, () -> {
            bookService.updateBook(-1L, bookEntity1);
        });
    }

    @Test
    void testUpdateBook_InternalServerError(){

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookEntity1));
        when(bookRepository.save(any(BookEntity.class))).thenThrow(new RuntimeException("Internal server error"));

        assertThrows(RuntimeException.class, () -> {
            bookService.updateBook(anyLong(), bookEntity2);
        });
    }
}
