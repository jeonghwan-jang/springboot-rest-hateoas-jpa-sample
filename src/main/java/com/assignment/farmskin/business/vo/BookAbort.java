package com.assignment.farmskin.business.vo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class BookAbort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "abort")
    @JsonBackReference
    private Book book;

    private String remarks;
}
