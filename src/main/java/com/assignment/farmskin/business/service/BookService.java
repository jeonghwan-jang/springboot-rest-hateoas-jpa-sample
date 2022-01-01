package com.assignment.farmskin.business.service;

import com.assignment.farmskin.business.repository.BookRepository;
import com.assignment.farmskin.business.vo.Book;
import com.assignment.farmskin.business.vo.BookAbort;
import com.assignment.farmskin.business.vo.Category;
import com.assignment.farmskin.business.vo.http.request.BookAbortRequest;
import com.assignment.farmskin.business.vo.http.request.BookModifyRequest;
import com.assignment.farmskin.business.vo.http.request.BookSaveRequest;
import com.assignment.farmskin.business.vo.http.request.BookSearchRequest;
import com.assignment.farmskin.business.vo.http.response.BookListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    private final CategoryService categoryService;

    public BookService(BookRepository bookRepository, CategoryService categoryService) {
        this.bookRepository = bookRepository;
        this.categoryService = categoryService;
    }

    public Page<BookListResponse> list(Pageable pageable, BookSearchRequest request) {
        return bookRepository.list(pageable, request);
    }

    public Book findById(Long bookId) {
        return bookRepository.findById(bookId).orElseThrow(EntityNotFoundException::new);
    }

    public Book add(BookSaveRequest request) {
        List<Category> categories = request.getCategories().stream().map(categoryService::findById).collect(Collectors.toList());
        Book book = Book.of(request);
        book.getCategories().addAll(categories);

        return bookRepository.save(book);
    }

    public Book modify(BookModifyRequest request) {
        Book book = this.findById(request.getId());
        book.setCategories(request.getCategories().stream().map(categoryService::findById).collect(Collectors.toList()));

        return bookRepository.save(book);
    }

    public Book processAbort(BookAbortRequest request) {
        if (request.getIsAbort()) {
            return this.abort(request);
        }
        return this.continuee(request);
    }

    private Book abort(BookAbortRequest request) {
        Book book = this.findById(request.getId());
        if (Optional.ofNullable(book.getAbort()).isEmpty()) {
            BookAbort bookAbort = new BookAbort();
            bookAbort.setBook(book);
            bookAbort.setRemarks(request.getRemarks());

            book.setAbort(bookAbort);
            bookRepository.save(book);
        }

        return book;
    }

    private Book continuee(BookAbortRequest request) {
        Book book = this.findById(request.getId());
        if (Optional.ofNullable(book.getAbort()).isPresent()) {
            book.setAbort(null);
            bookRepository.save(book);
        }

        return book;
    }
}
