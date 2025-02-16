package com.bookslibrary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookslibrary.entity.BookEntity;
import java.util.List;


@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    List<BookEntity> findByAuthorContaining(String author);
    List<BookEntity> findByTitleContaining(String title);
    List<BookEntity> findByAuthorContainingAndTitleContaining(String author, String title);
}