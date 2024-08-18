package com.example.bookminitest.service;

import com.example.bookminitest.model.Book;

import java.util.List;

public interface IBookService {
    List<Book> findAll();

    void save(Book book);

    Book findById(int id);

    void update(int id, Book book);

    void remove(int id);
}
