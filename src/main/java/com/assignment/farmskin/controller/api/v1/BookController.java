package com.assignment.farmskin.controller.api.v1;

import com.assignment.farmskin.business.service.BookService;
import com.assignment.farmskin.business.vo.Book;
import com.assignment.farmskin.business.vo.http.request.BookAbortRequest;
import com.assignment.farmskin.business.vo.http.request.BookModifyRequest;
import com.assignment.farmskin.business.vo.http.request.BookSaveRequest;
import com.assignment.farmskin.business.vo.http.request.BookSearchRequest;
import com.assignment.farmskin.business.vo.http.response.BookListResponse;
import com.assignment.farmskin.common.annotation.ApiV1Controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@ApiV1Controller
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    private final String PREFIX = "/books";

    /**
     * GET /books(?page=&size=&category=&keyword=&keywordType=) - 도서 페이징 리스트
     *
     * @param pageable
     * @param assembler
     * @param request
     * @return
     */
    @GetMapping(PREFIX)
    public ResponseEntity<?> list(final Pageable pageable, final PagedResourcesAssembler<BookListResponse> assembler, BookSearchRequest request) {
        Page<BookListResponse> books = bookService.list(pageable, request);

        return ResponseEntity.status(HttpStatus.OK).body(assembler.toModel(books, book -> {
            EntityModel<BookListResponse> entityModel = EntityModel.of(book);
            entityModel.add(linkTo(methodOn(this.getClass()).findById(book.getId())).withRel("book-detail"));

            return entityModel;
        }));
    }

    /**
     * GET  /books/{bookId} - 도서 상세 정보
     *
     * @param bookId
     * @return
     */
    @GetMapping(PREFIX + "/{bookId}")
    public ResponseEntity<?> findById(@PathVariable("bookId") Long bookId) {
        Book book = bookService.findById(bookId);

        return ResponseEntity.status(HttpStatus.OK).body(EntityModel.of(book,
            bookDefaultLinks(book, linkTo(methodOn(this.getClass()).findById(bookId)).withSelfRel())
        ));
    }

    /**
     * POST /books - 신규 도서 등록
     *
     * @param request
     * @return
     */
    @PostMapping(PREFIX)
    public ResponseEntity<?> add(@RequestBody @Valid BookSaveRequest request) {
        Book book = bookService.add(request);
        return ResponseEntity.status(HttpStatus.OK).body(EntityModel.of(book,
            bookDefaultLinks(book, linkTo(methodOn(this.getClass()).add(null)).withSelfRel())
        ));
    }

    /**
     * POST /books/abort - 도서 대여 중단 or 중단 취소
     *
     * @param request
     * @return
     */
    @PostMapping(PREFIX + "/abort")
    public ResponseEntity<?> abort(@RequestBody @Valid BookAbortRequest request) {
        Book book = bookService.processAbort(request);

        return ResponseEntity.status(HttpStatus.OK).body(EntityModel.of(book,
            bookDefaultLinks(book, linkTo(methodOn(this.getClass()).abort(null)).withSelfRel())
        ));
    }

    /**
     * PUT  /books/{id} - 도서 카테고리 변경
     *
     * @param bookId
     * @param request
     * @return
     */
    @PutMapping(PREFIX + "/{bookId}")
    public ResponseEntity<?> modify(@PathVariable("bookId") Long bookId, @RequestBody @Valid BookModifyRequest request) {
        request.setId(bookId);
        Book book = bookService.modify(request);

        return ResponseEntity.status(HttpStatus.OK).body(EntityModel.of(book,
                bookDefaultLinks(book, linkTo(methodOn(this.getClass()).modify(bookId, null)).withSelfRel())
        ));
    }

    private List<Link> bookDefaultLinks(Book book, Link self) {
        return Arrays.asList(
                self,
                linkTo(methodOn(this.getClass()).list(null, null, null)).withRel("book-list"),
                linkTo(methodOn(this.getClass()).modify(book.getId(), null)).withRel("book-modify"),
                linkTo(methodOn(this.getClass()).abort(null)).withRel("book-abort"));
    }
}
