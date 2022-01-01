package com.assignment.farmskin.business.repository.querydsl;

import com.assignment.farmskin.business.vo.http.request.BookSearchRequest;
import com.assignment.farmskin.business.vo.http.response.BookListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookQueryRepository {

    public Page<BookListResponse> list(Pageable pageable, BookSearchRequest request);
}
