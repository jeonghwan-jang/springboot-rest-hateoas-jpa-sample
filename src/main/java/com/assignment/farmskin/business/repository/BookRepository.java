package com.assignment.farmskin.business.repository;

import com.assignment.farmskin.business.repository.querydsl.BookQueryRepository;
import com.assignment.farmskin.business.vo.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookQueryRepository {


}
