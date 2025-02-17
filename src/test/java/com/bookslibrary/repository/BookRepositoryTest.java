package com.bookslibrary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import com.bookslibrary.entity.BookEntity;

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
    void testFindByAuthorContaining() {
        List<BookEntity> books = bookRepository.findByAuthorContaining("Tolkien");
        assertThat(books).hasSize(2);
        assertThat(books).extracting(BookEntity::getTitle).contains("The Hobbit", "The Hobbit2");
    }

    @Test
    void testFindByTitleContaining() {
        List<BookEntity> books = bookRepository.findByTitleContaining("The");
        assertThat(books).hasSize(2);
        assertThat(books).extracting(BookEntity::getTitle).contains("The Hobbit", "The Hobbit2");
    }

    @Test
    void testFindByAuthorContainingAndTitleContaining() {
        List<BookEntity> books = bookRepository.findByAuthorContainingAndTitleContaining("Tolkien", "The Hobbit2");
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("The Hobbit2");
    }
}
