package com.assignment.farmskin.business.vo.http.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class BookSaveRequest {

    @NotEmpty
    private String title;

    @NotEmpty
    private String author;

    @NotEmpty
    private List<Long> categories;


}
