package com.ncc.web.rest;

import com.ncc.LoginApp;
import com.ncc.domain.Book;
import com.ncc.repository.BookRepository;
import com.ncc.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.ncc.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link BookResource} REST controller.
 */
@SpringBootTest(classes = LoginApp.class)
public class BookResourceIT {

    private static final Integer DEFAULT_YEAR = 1;
    private static final Integer UPDATED_YEAR = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restBookMockMvc;

    private Book book;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BookResource bookResource = new BookResource(bookRepository);
        this.restBookMockMvc = MockMvcBuilders.standaloneSetup(bookResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createEntity(EntityManager em) {
        Book book = new Book()
            .year(DEFAULT_YEAR)
            .name(DEFAULT_NAME)
            .author(DEFAULT_AUTHOR);
        return book;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createUpdatedEntity(EntityManager em) {
        Book book = new Book()
            .year(UPDATED_YEAR)
            .name(UPDATED_NAME)
            .author(UPDATED_AUTHOR);
        return book;
    }

    @BeforeEach
    public void initTest() {
        book = createEntity(em);
    }

    @Test
    @Transactional
    public void createBook() throws Exception {
        int databaseSizeBeforeCreate = bookRepository.findAll().size();

        // Create the Book
        restBookMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isCreated());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate + 1);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testBook.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBook.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
    }

    @Test
    @Transactional
    public void createBookWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bookRepository.findAll().size();

        // Create the Book with an existing ID
        book.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().size();
        // set the field null
        book.setName(null);

        // Create the Book, which fails.

        restBookMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAuthorIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().size();
        // set the field null
        book.setAuthor(null);

        // Create the Book, which fails.

        restBookMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBooks() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList
        restBookMockMvc.perform(get("/api/books?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId().intValue())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)));
    }
    
    @Test
    @Transactional
    public void getBook() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get the book
        restBookMockMvc.perform(get("/api/books/{id}", book.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(book.getId().intValue()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR));
    }

    @Test
    @Transactional
    public void getNonExistingBook() throws Exception {
        // Get the book
        restBookMockMvc.perform(get("/api/books/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBook() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book
        Book updatedBook = bookRepository.findById(book.getId()).get();
        // Disconnect from session so that the updates on updatedBook are not directly saved in db
        em.detach(updatedBook);
        updatedBook
            .year(UPDATED_YEAR)
            .name(UPDATED_NAME)
            .author(UPDATED_AUTHOR);

        restBookMockMvc.perform(put("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBook)))
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testBook.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBook.getAuthor()).isEqualTo(UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    public void updateNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Create the Book

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc.perform(put("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBook() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeDelete = bookRepository.findAll().size();

        // Delete the book
        restBookMockMvc.perform(delete("/api/books/{id}", book.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
