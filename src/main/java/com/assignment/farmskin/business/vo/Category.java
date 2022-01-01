package com.assignment.farmskin.business.vo;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, insertable = false, updatable = false, columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime created;

    @Column(insertable = false, updatable = false, columnDefinition = "datetime ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime modified;
}
