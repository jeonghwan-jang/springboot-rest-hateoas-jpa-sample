package com.assignment.farmskin.business.repository;

import com.assignment.farmskin.business.repository.querydsl.BookQueryRepository;
import com.assignment.farmskin.business.vo.Category;
import com.assignment.farmskin.business.vo.http.request.BookSearchRequest;
import com.assignment.farmskin.business.vo.http.response.BookListResponse;
import com.assignment.farmskin.common.constants.SearchTypeConstants;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.support.FetchableQueryBase;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.assignment.farmskin.business.vo.QBook.book;
import static com.assignment.farmskin.business.vo.QBookAbort.bookAbort;
import static com.assignment.farmskin.business.vo.QCategory.category;

public class BookRepositoryImpl implements BookQueryRepository {

    private final JPAQueryFactory queryFactory;

    public BookRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public Page<BookListResponse> list(Pageable pageable, BookSearchRequest request) {
        // set base query
        JPAQuery<?> query = queryFactory.from(book)
                .leftJoin(book.abort(), bookAbort)
                .join(book.categories, category)
                .distinct()
                .where(bookAbort.isNull());

        // set search where
        query.where(this.search(request));

        // get total count for paging
        long total = query
                .select(book.id)
                .fetch().size();

        // get list data
        List<BookListResponse> results = ((FetchableQueryBase) query
                .select(Projections.bean(BookListResponse.class,
                        book.id,
                        book.title,
                        book.author,
                        book.created,
                        book.modified
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()))
                .fetch();

        // inject category
        this.injectCategory(results);

        return new PageImpl<>(results, pageable, total);
    }

    private Predicate search(BookSearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        // keyword
        if (Optional.ofNullable(request.getKeyword()).isPresent()) {
            BooleanExpression titleEx = book.title.like("%" + request.getKeyword() + "%");
            BooleanExpression authorEx = book.author.like("%" + request.getKeyword() + "%");
            switch (Optional.ofNullable(request.getKeywordType()).orElseGet(String::new)) {
                case SearchTypeConstants.TYPE_TITLE:
                    builder.and(titleEx);
                    break;
                case SearchTypeConstants.TYPE_AUTHOR:
                    builder.and(authorEx);
                    break;
                default:
                    builder.or(titleEx)
                            .or(authorEx);
                    break;
            }
        }

        // category
        if (Optional.ofNullable(request.getCategory()).isPresent()) {
            builder.and(category.id.eq(request.getCategory()));
        }

        return builder;
    }

    private void injectCategory(List<BookListResponse> results) {
        List<Long> ids = results.stream().map(BookListResponse::getId).collect(Collectors.toList());
        Map<Long, List<Category>> subResults = queryFactory
                .select(book.id, category)
                .from(book)
                .leftJoin(book.abort(), bookAbort)
                .join(book.categories, category)
                .where(book.id.in(ids))
                .fetch()
                .stream()
                .collect(
                    Collectors.groupingBy(
                        tuple -> tuple.get(book.id),
                        Collectors.mapping(tuple -> tuple.get(1, Category.class),
                            Collectors.toList()
                        )
                    )
                );

        for (BookListResponse response : results) {
            response.setCategories(subResults.get(response.getId()));
        }
    }
}
