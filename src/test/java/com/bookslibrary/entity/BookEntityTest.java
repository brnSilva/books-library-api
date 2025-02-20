package com.bookslibrary.entity;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class BookEntityTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Test
    public void testValidBookEntity() {
        BookEntity book = new BookEntity();
        book.setTitle("Valid Title");
        book.setAuthor("Valid Author");
        book.setIsbn("1234567890123");
        book.setPublicationYear("2021");

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);
        assertThat(violations).isEmpty();
    }

    @Test
    public void testInvalidBookEntityTitleBlank() {
        BookEntity book = new BookEntity();
        book.setTitle("");
        book.setAuthor("Valid Author");
        book.setIsbn("1234567890123");
        book.setPublicationYear("2021");

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The Title cannot be blank or empty");
    }

    @Test
    public void testInvalidBookEntityTitleNull() {
        BookEntity book = new BookEntity();
        book.setTitle(null);
        book.setAuthor("Valid Author");
        book.setIsbn("1234567890123");
        book.setPublicationYear("2021");

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The Title cannot be blank or empty");
    }

    @Test
    public void testInvalidBookEntityAuthorBlank() {
        BookEntity book = new BookEntity();
        book.setTitle("Valid Title");
        book.setAuthor("");
        book.setIsbn("1234567890123");
        book.setPublicationYear("2021");

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The Author cannot be blank or empty");
    }

    @Test
    public void testInvalidBookEntityIsbnPattern() {
        BookEntity book = new BookEntity();
        book.setTitle("Valid Title");
        book.setAuthor("Valid Author");
        book.setIsbn("12345678");
        book.setPublicationYear("2021");

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The ISBN should be a 13-digit number (e.g., 9788533302273). ");
    }

    @Test
    public void testInvalidBookEntityPublicationYearPattern() {
        BookEntity book = new BookEntity();
        book.setTitle("Valid Title");
        book.setAuthor("Valid Author");
        book.setIsbn("1234567890123");
        book.setPublicationYear("21");

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Publication Year should be a 4-digit number (e.g., 1993).");
    }

    @Test
    public void testInvalidBookEntityPublicationYearDigit() {
        BookEntity book = new BookEntity();
        book.setTitle("Valid Title");
        book.setAuthor("Valid Author");
        book.setIsbn("1234567890123");
        book.setPublicationYear("21a");

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Publication Year should be a 4-digit number (e.g., 1993).");
    }
}
