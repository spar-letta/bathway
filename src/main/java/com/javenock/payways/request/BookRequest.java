package com.javenock.payways.request;

import com.javenock.payways.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {
    @NotBlank
    private String bookName;
    @NotBlank
    private String author;
    @NotBlank
    private String datePublished;
    private User user;
}
