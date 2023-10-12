package com.javenock.payways.service;

import com.javenock.payways.model.Book;
import com.javenock.payways.model.User;
import com.javenock.payways.repository.BookRepository;
import com.javenock.payways.repository.UserRepository;
import com.javenock.payways.request.BookRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;


    public Book createBook(BookRequest bookRequest) {
        Optional<User> byId = Optional.ofNullable(userRepository.findById(bookRequest.getUser().getId()).orElseThrow(() -> new RuntimeException("")));

        User build1 = User.builder().id(byId.get().getId()).email(byId.get().getEmail()).build();
        Book build = Book.builder()
                .bookName(bookRequest.getBookName())
                .author(bookRequest.getAuthor())
                .datePublished(LocalDate.parse(bookRequest.getDatePublished()))
                .user(build1)
                .build();
                return  bookRepository.save(build);
    }
}
