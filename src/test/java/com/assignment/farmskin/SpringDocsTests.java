package com.assignment.farmskin;

import com.assignment.farmskin.business.service.BookService;
import com.assignment.farmskin.business.vo.http.request.BookAbortRequest;
import com.assignment.farmskin.business.vo.http.request.BookSaveRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
class SpringDocsTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookService bookService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void bookList() throws Exception {
		mockMvc.perform(
						get("/api/v1/books?size=3").accept(MediaTypes.HAL_JSON_VALUE)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(document("book-list",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParameters(
								parameterWithName("page").description("페이지 번호").optional(),
								parameterWithName("size").description("페이지 크기").optional(),
								parameterWithName("category").description("검색 카테고리 ID").optional(),
								parameterWithName("keyword").description("검색 키워드").optional(),
								parameterWithName("keywordType").description("검색 분류 (title, author)").optional()
						),
						responseFields(
								fieldWithPath("_embedded.bookListResponseList[]").description("응답 데이터 본문")
						).andWithPrefix("_embedded.bookListResponseList[].",
								fieldWithPath("id").description("도서 ID"),
								fieldWithPath("title").description("도서 제목"),
								fieldWithPath("author").description("지은이"),
								fieldWithPath("created").type(JsonFieldType.STRING).description("도서 등록일"),
								fieldWithPath("modified").type(JsonFieldType.STRING).description("도서 수정일").optional(),
								fieldWithPath("categories[]").description("카테고리 정보 참조")
						).andWithPrefix("_embedded.bookListResponseList[].categories[].",
								fieldWithPath("id").description("카테고리 ID"),
								fieldWithPath("name").description("카테고리명"),
								fieldWithPath("created").type(JsonFieldType.STRING).description("카테고리 등록일"),
								fieldWithPath("modified").type(JsonFieldType.STRING).description("카테고리 수정일").optional()
						).andWithPrefix("_embedded.bookListResponseList[].",
								subsectionWithPath("_links").type(JsonFieldType.OBJECT).description("도서 상호작용 링크 정보")
						).and(
								subsectionWithPath("_links").type(JsonFieldType.OBJECT).description("페이징 상호작용 링크 정보"),
								subsectionWithPath("page").type(JsonFieldType.OBJECT).description("페이징 정보")
						)
				));
	}

	@Test
	void bookDetail() throws Exception {
		mockMvc.perform(
						get("/api/v1/books/{id}", 1L).accept(MediaTypes.HAL_JSON_VALUE)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(document("book-detail",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("id").description("도서 ID")
						),
						this.getBookResponseFields()
				));
	}

	@Test
	void bookAdd() throws Exception {
		BookSaveRequest request = new BookSaveRequest();
		request.setTitle("짱구야 놀자");
		request.setAuthor("장정환");
		request.setCategories(Arrays.asList(1L, 3L, 5L));
		String content = objectMapper.writeValueAsString(request);

		mockMvc.perform(
						post("/api/v1/books")
								.content(content)
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaTypes.HAL_JSON_VALUE)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.categories", hasSize(request.getCategories().size()))) // 복수 카테고리 입력 체크
				.andDo(document("book-add",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("title").description("도서 제목"),
								fieldWithPath("author").description("지은이"),
								fieldWithPath("categories").description("등록 카테고리 ID 리스트")
						),
						this.getBookResponseFields()
				));
	}

	@Test
	void bookModify() throws Exception {
		Map<String, List<Long>> requestSample = new HashMap<>();
		requestSample.put("categories", Arrays.asList(2L, 4L));
		String content = objectMapper.writeValueAsString(requestSample);

		mockMvc.perform(
						put("/api/v1/books/{id}", 2L)
								.content(content)
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaTypes.HAL_JSON_VALUE)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpectAll( // 변경 여부 체크
						MockMvcResultMatchers.jsonPath("$.categories[0].id").value(2L),
						MockMvcResultMatchers.jsonPath("$.categories[1].id").value(4L)
				)
				.andDo(document("book-modify",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("id").description("수정 도서 ID")
						),
						requestFields(
								fieldWithPath("categories").description("수정 카테고리 ID 리스트")
						),
						this.getBookResponseFields()
				));
	}

	@Test
	void bookAbort() throws Exception {
		BookAbortRequest request = new BookAbortRequest();
		request.setId(1L);
		request.setIsAbort(true);
		request.setRemarks("도난");
		String content = objectMapper.writeValueAsString(request);

		mockMvc.perform(
						post("/api/v1/books/abort")
								.content(content)
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaTypes.HAL_JSON_VALUE)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.abort").isNotEmpty()) // 참조가 있는 경우 대여 중단
				.andDo(document("book-abort",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("id").description("중단 도서 ID"),
								fieldWithPath("isAbort").description("중단 여부 (true - 중단, false - 중단 취소)"),
								fieldWithPath("remarks").description("비고").optional()
						),
						this.getBookResponseFields()
				));
	}

	@Test
	void bookAbortCancel() throws Exception {
		Map<String, Object> requestSample = new HashMap<>();
		requestSample.put("id", 1L);
		requestSample.put("isAbort", false);
		String content = objectMapper.writeValueAsString(requestSample);
		mockMvc.perform(
						post("/api/v1/books/abort")
								.content(content)
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaTypes.HAL_JSON_VALUE)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.abort").isEmpty()) // 참조가 없는 경우 대여 가능
				.andDo(document("book-abort-cancel",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("id").description("중단 도서 ID"),
								fieldWithPath("isAbort").description("중단 여부 (true - 중단, false - 중단 취소)")
						),
						this.getBookResponseFields()
				));
	}

	@Test
	void categoryList() throws Exception {
		mockMvc.perform(
						get("/api/v1/categories")
								.accept(MediaTypes.HAL_JSON_VALUE)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(document("category-list",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						responseFields(
								fieldWithPath("_embedded.categoryList[]").description("응답 데이터 본문")
						).andWithPrefix("_embedded.categoryList[].",
								fieldWithPath("id").description("카테고리 ID"),
								fieldWithPath("name").description("카테고리명"),
								fieldWithPath("created").type(JsonFieldType.STRING).description("카테고리 등록일").optional(),
								fieldWithPath("modified").type(JsonFieldType.STRING).description("카테고리 수정일").optional(),
								subsectionWithPath("_links").type(JsonFieldType.OBJECT).description("카테고리 상호작용 링크 참조")
						).and(
								subsectionWithPath("_links").type(JsonFieldType.OBJECT).description("리스트 상호작용 링크 참조")
						)
				));
	}

	ResponseFieldsSnippet getBookResponseFields() {
		return responseFields(
						fieldWithPath("id").description("도서 ID"),
						fieldWithPath("title").description("도서 제목"),
						fieldWithPath("author").description("지은이"),
						fieldWithPath("created").type(JsonFieldType.STRING).description("도서 등록일").optional(),
						fieldWithPath("modified").type(JsonFieldType.STRING).description("도서 수정일").optional(),
						fieldWithPath("categories[]").description("카테고리 정보 참조")
				).andWithPrefix("categories[].",
						fieldWithPath("id").description("카테고리 ID"),
						fieldWithPath("name").description("카테고리명"),
						fieldWithPath("created").type(JsonFieldType.STRING).description("카테고리 등록일").optional(),
						fieldWithPath("modified").type(JsonFieldType.STRING).description("카테고리 수정일").optional()
				).and(
						subsectionWithPath("abort").type(JsonFieldType.OBJECT).description("도서 중단 정보 참조").optional()
				).andWithPrefix("abort.",
						fieldWithPath("id").type(JsonFieldType.NUMBER).description("도서 중단 정보 ID"),
						fieldWithPath("remarks").type(JsonFieldType.STRING).description("비고")
				).and(
						subsectionWithPath("_links").type(JsonFieldType.OBJECT).description("상호작용 링크 참조")
				);
	}
}
