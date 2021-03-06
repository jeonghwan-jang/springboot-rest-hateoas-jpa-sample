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
								parameterWithName("page").description("????????? ??????").optional(),
								parameterWithName("size").description("????????? ??????").optional(),
								parameterWithName("category").description("?????? ???????????? ID").optional(),
								parameterWithName("keyword").description("?????? ?????????").optional(),
								parameterWithName("keywordType").description("?????? ?????? (title, author)").optional()
						),
						responseFields(
								fieldWithPath("_embedded.bookListResponseList[]").description("?????? ????????? ??????")
						).andWithPrefix("_embedded.bookListResponseList[].",
								fieldWithPath("id").description("?????? ID"),
								fieldWithPath("title").description("?????? ??????"),
								fieldWithPath("author").description("?????????"),
								fieldWithPath("created").type(JsonFieldType.STRING).description("?????? ?????????"),
								fieldWithPath("modified").type(JsonFieldType.STRING).description("?????? ?????????").optional(),
								fieldWithPath("categories[]").description("???????????? ?????? ??????")
						).andWithPrefix("_embedded.bookListResponseList[].categories[].",
								fieldWithPath("id").description("???????????? ID"),
								fieldWithPath("name").description("???????????????"),
								fieldWithPath("created").type(JsonFieldType.STRING).description("???????????? ?????????"),
								fieldWithPath("modified").type(JsonFieldType.STRING).description("???????????? ?????????").optional()
						).andWithPrefix("_embedded.bookListResponseList[].",
								subsectionWithPath("_links").type(JsonFieldType.OBJECT).description("?????? ???????????? ?????? ??????")
						).and(
								subsectionWithPath("_links").type(JsonFieldType.OBJECT).description("????????? ???????????? ?????? ??????"),
								subsectionWithPath("page").type(JsonFieldType.OBJECT).description("????????? ??????")
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
								parameterWithName("id").description("?????? ID")
						),
						this.getBookResponseFields()
				));
	}

	@Test
	void bookAdd() throws Exception {
		BookSaveRequest request = new BookSaveRequest();
		request.setTitle("????????? ??????");
		request.setAuthor("?????????");
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
				.andExpect(MockMvcResultMatchers.jsonPath("$.categories", hasSize(request.getCategories().size()))) // ?????? ???????????? ?????? ??????
				.andDo(document("book-add",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("title").description("?????? ??????"),
								fieldWithPath("author").description("?????????"),
								fieldWithPath("categories").description("?????? ???????????? ID ?????????")
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
				.andExpectAll( // ?????? ?????? ??????
						MockMvcResultMatchers.jsonPath("$.categories[0].id").value(2L),
						MockMvcResultMatchers.jsonPath("$.categories[1].id").value(4L)
				)
				.andDo(document("book-modify",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("id").description("?????? ?????? ID")
						),
						requestFields(
								fieldWithPath("categories").description("?????? ???????????? ID ?????????")
						),
						this.getBookResponseFields()
				));
	}

	@Test
	void bookAbort() throws Exception {
		BookAbortRequest request = new BookAbortRequest();
		request.setId(1L);
		request.setIsAbort(true);
		request.setRemarks("??????");
		String content = objectMapper.writeValueAsString(request);

		mockMvc.perform(
						post("/api/v1/books/abort")
								.content(content)
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaTypes.HAL_JSON_VALUE)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.abort").isNotEmpty()) // ????????? ?????? ?????? ?????? ??????
				.andDo(document("book-abort",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("id").description("?????? ?????? ID"),
								fieldWithPath("isAbort").description("?????? ?????? (true - ??????, false - ?????? ??????)"),
								fieldWithPath("remarks").description("??????").optional()
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
				.andExpect(MockMvcResultMatchers.jsonPath("$.abort").isEmpty()) // ????????? ?????? ?????? ?????? ??????
				.andDo(document("book-abort-cancel",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("id").description("?????? ?????? ID"),
								fieldWithPath("isAbort").description("?????? ?????? (true - ??????, false - ?????? ??????)")
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
								fieldWithPath("_embedded.categoryList[]").description("?????? ????????? ??????")
						).andWithPrefix("_embedded.categoryList[].",
								fieldWithPath("id").description("???????????? ID"),
								fieldWithPath("name").description("???????????????"),
								fieldWithPath("created").type(JsonFieldType.STRING).description("???????????? ?????????").optional(),
								fieldWithPath("modified").type(JsonFieldType.STRING).description("???????????? ?????????").optional(),
								subsectionWithPath("_links").type(JsonFieldType.OBJECT).description("???????????? ???????????? ?????? ??????")
						).and(
								subsectionWithPath("_links").type(JsonFieldType.OBJECT).description("????????? ???????????? ?????? ??????")
						)
				));
	}

	ResponseFieldsSnippet getBookResponseFields() {
		return responseFields(
						fieldWithPath("id").description("?????? ID"),
						fieldWithPath("title").description("?????? ??????"),
						fieldWithPath("author").description("?????????"),
						fieldWithPath("created").type(JsonFieldType.STRING).description("?????? ?????????").optional(),
						fieldWithPath("modified").type(JsonFieldType.STRING).description("?????? ?????????").optional(),
						fieldWithPath("categories[]").description("???????????? ?????? ??????")
				).andWithPrefix("categories[].",
						fieldWithPath("id").description("???????????? ID"),
						fieldWithPath("name").description("???????????????"),
						fieldWithPath("created").type(JsonFieldType.STRING).description("???????????? ?????????").optional(),
						fieldWithPath("modified").type(JsonFieldType.STRING).description("???????????? ?????????").optional()
				).and(
						subsectionWithPath("abort").type(JsonFieldType.OBJECT).description("?????? ?????? ?????? ??????").optional()
				).andWithPrefix("abort.",
						fieldWithPath("id").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? ID"),
						fieldWithPath("remarks").type(JsonFieldType.STRING).description("??????")
				).and(
						subsectionWithPath("_links").type(JsonFieldType.OBJECT).description("???????????? ?????? ??????")
				);
	}
}
