package com.assignment.farmskin.business.vo.http.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BookAbortRequest {

    @NotNull
    private Long id;

    @NotNull
    private Boolean isAbort;

    private String remarks;


}
