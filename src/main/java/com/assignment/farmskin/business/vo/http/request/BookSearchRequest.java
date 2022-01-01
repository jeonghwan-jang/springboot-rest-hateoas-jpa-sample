package com.assignment.farmskin.business.vo.http.request;

import lombok.Data;

@Data
public class BookSearchRequest {

    private String keywordType;

    private String keyword;

    private Long category;

}
