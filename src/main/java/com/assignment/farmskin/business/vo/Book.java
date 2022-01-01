package com.assignment.farmskin.business.vo;

import com.assignment.farmskin.business.vo.http.request.BookSaveRequest;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, insertable = false, updatable = false, columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime created;

    @Column(insertable = false, updatable = false, columnDefinition = "datetime ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime modified;

    @ManyToMany
    @JoinTable(name = "category_book")
    private List<Category> categories;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "abort_id", referencedColumnName = "id")
    @JsonManagedReference
    private BookAbort abort;

    public static Book of(BookSaveRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setCategories(new ArrayList<>());

        return book;
    }
}
