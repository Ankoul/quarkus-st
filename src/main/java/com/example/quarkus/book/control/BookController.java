package com.example.quarkus.book.control;

import com.example.quarkus.author.boundary.AuthorService;
import com.example.quarkus.book.boundary.BookRepository;
import com.example.quarkus.book.entity.Book;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

@ApplicationScoped
@SuppressWarnings("CdiInjectionPointsInspection")
public class BookController {

    @Inject
    BookRepository bookRepository;

    @Inject
    @RestClient
    AuthorService authorService;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found."));
    }

    public Book create(Book book) {
        validateAuthor(book);
        return bookRepository.save(book);
    }

    public Book update(Long id, Book book) {
        validateAuthor(book);
        final Book saved = findById(id);
        saved.setTitle(book.getTitle());
        saved.setAuthor(book.getAuthor());

        return bookRepository.save(saved);
    }

    private void validateAuthor(final Book book) {
        authorService.listAuthors().stream()
                .filter(it -> it.getName().equals(book.getAuthor()))
                .findAny().orElseThrow(() -> new BadRequestException("Invalid Author."));
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
