package com.bookslibrary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookslibrary.entity.BookEntity;
import com.bookslibrary.repository.BookRepository;

import ch.qos.logback.core.util.StringUtil;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;

    public List<BookEntity> filterBooksBy(String title, String author) {
        
        if(!StringUtil.isNullOrEmpty(title) && !StringUtil.isNullOrEmpty(author)) {
            return bookRepository.findByAuthorContainingAndTitleContaining(author, title);
        } else if(!StringUtil.isNullOrEmpty(title)) {
            return bookRepository.findByTitleContaining(title);
        } else if(!StringUtil.isNullOrEmpty(author)) {
            return bookRepository.findByAuthorContaining(author);
        } else {
            return bookRepository.findAll();
        }
    }
}
