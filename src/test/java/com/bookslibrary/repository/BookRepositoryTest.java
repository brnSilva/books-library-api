package com.bookslibrary.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import com.bookslibrary.entity.BookEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@DataJpaTest
@ActiveProfiles("test")
@ComponentScan(basePackages = {"com.bookslibrary"})
public class BookRepositoryTest{
    
    @Autowired
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
    void testFindByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase_Success(){

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Page<BookEntity> result = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase("Hobbit", "Tolkien", pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(bookEntity1));
        assertTrue(result.getContent().contains(bookEntity2));
    }

    @Test
    void testFindByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase_NullParameters(){

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Page<BookEntity> result = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(null, null, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }
}
