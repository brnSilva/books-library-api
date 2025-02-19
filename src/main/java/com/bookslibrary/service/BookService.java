package com.bookslibrary.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bookslibrary.entity.BookEntity;
import com.bookslibrary.exception.ResouceNotFoundException;
import com.bookslibrary.repository.BookRepository;

import ch.qos.logback.core.util.StringUtil;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Page<BookEntity> findPageable(String query, Pageable pageable){

        if (!StringUtil.isNullOrEmpty(query))
            return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query, pageable);

        return bookRepository.findAll(pageable);
    }

    public BookEntity updateBook(Long id, BookEntity bookUpdated){

        Optional<BookEntity> bookToUpdate = bookRepository.findById(id);

        if(bookToUpdate.isPresent()){

            BookEntity book = bookToUpdate.get();

            book.setTitle(bookUpdated.getTitle());
            book.setAuthor(bookUpdated.getAuthor());
            book.setIsbn(bookUpdated.getIsbn());
            book.setPublicationYear(bookUpdated.getPublicationYear());
            book.setDescription(bookUpdated.getDescription());

            return bookRepository.save(book);
        }

        throw new ResouceNotFoundException("Book not found");
    }
}
