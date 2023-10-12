package com.javenock.payways.repository;

import com.javenock.payways.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByBookId(Long bookId);
}
