package com.assignment.farmskin.business.vo.http.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class BookModifyRequest {

    private Long id;

    @NotEmpty
    private List<Long> categories;


}
