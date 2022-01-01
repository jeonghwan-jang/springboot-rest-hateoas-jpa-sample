package com.assignment.farmskin.business.vo.http.response;

import com.assignment.farmskin.business.vo.Category;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookListResponse {

    private Long id;

    private String title;

    private String author;

    private LocalDateTime created;

    private LocalDateTime modified;

    private List<Category> categories;


}
