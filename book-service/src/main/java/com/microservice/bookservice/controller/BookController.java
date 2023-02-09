package com.microservice.bookservice.controller;

import com.microservice.bookservice.model.Book;
import com.microservice.bookservice.proxy.CambioProxy;
import com.microservice.bookservice.repository.BookRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book endpoint")
@RestController
@RequestMapping("book-service")
public class BookController {

    @Autowired
    private Environment environment;

    @Autowired
    private BookRepository repository;

    @Autowired
    private CambioProxy proxy;

    @Operation(summary = "Find specific book by your ID!")
    @GetMapping(value = "/{id}/{currency}")
    public Book findBook(
            @PathVariable("id") long id,
            @PathVariable("currency") String currency
            ) {

        var book = repository.getReferenceById(id);
        if(book == null) throw new  RuntimeException("Book not found!");

        var cambio = proxy.getCambio(book.getPrice(), "USD", currency);

        var port = environment.getProperty("local.server.port");
        book.setEnvironment("Book port: " + port + " Cambio port: " + cambio.getEnvironment());
        book.setPrice(cambio.getConvertedValue());
        book.setCurrency(currency);

        return book;
    }

}
