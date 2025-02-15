package com.bookslibrary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookslibrary.entity.BookEntity;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    
}