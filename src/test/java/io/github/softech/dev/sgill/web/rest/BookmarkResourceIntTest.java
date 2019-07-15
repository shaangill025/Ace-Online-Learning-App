package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.Bookmark;
import io.github.softech.dev.sgill.domain.Section;
import io.github.softech.dev.sgill.repository.BookmarkRepository;
import io.github.softech.dev.sgill.repository.search.BookmarkSearchRepository;
import io.github.softech.dev.sgill.service.BookmarkService;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.BookmarkCriteria;
import io.github.softech.dev.sgill.service.BookmarkQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static io.github.softech.dev.sgill.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BookmarkResource REST controller.
 *
 * @see BookmarkResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class BookmarkResourceIntTest {

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final Integer DEFAULT_SLIDE = 1;
    private static final Integer UPDATED_SLIDE = 2;

    private static final String DEFAULT_TIMESTAMP = "AAAAAAAAAA";
    private static final String UPDATED_TIMESTAMP = "BBBBBBBBBB";

    private static final String DEFAULT_MODULE = "AAAAAAAAAA";
    private static final String UPDATED_MODULE = "BBBBBBBBBB";

    private static final Integer DEFAULT_SECONDS = 1;
    private static final Integer UPDATED_SECONDS = 2;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    

    @Autowired
    private BookmarkService bookmarkService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.BookmarkSearchRepositoryMockConfiguration
     */
    @Autowired
    private BookmarkSearchRepository mockBookmarkSearchRepository;

    @Autowired
    private BookmarkQueryService bookmarkQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBookmarkMockMvc;

    private Bookmark bookmark;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BookmarkResource bookmarkResource = new BookmarkResource(bookmarkService, bookmarkQueryService);
        this.restBookmarkMockMvc = MockMvcBuilders.standaloneSetup(bookmarkResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bookmark createEntity(EntityManager em) {
        Bookmark bookmark = new Bookmark()
            .text(DEFAULT_TEXT)
            .slide(DEFAULT_SLIDE)
            .timestamp(DEFAULT_TIMESTAMP)
            .module(DEFAULT_MODULE)
            .seconds(DEFAULT_SECONDS);
        // Add required entity
        Section section = SectionResourceIntTest.createEntity(em);
        em.persist(section);
        em.flush();
        bookmark.setSection(section);
        return bookmark;
    }

    @Before
    public void initTest() {
        bookmark = createEntity(em);
    }

    @Test
    @Transactional
    public void createBookmark() throws Exception {
        int databaseSizeBeforeCreate = bookmarkRepository.findAll().size();

        // Create the Bookmark
        restBookmarkMockMvc.perform(post("/api/bookmarks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmark)))
            .andExpect(status().isCreated());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeCreate + 1);
        Bookmark testBookmark = bookmarkList.get(bookmarkList.size() - 1);
        assertThat(testBookmark.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testBookmark.getSlide()).isEqualTo(DEFAULT_SLIDE);
        assertThat(testBookmark.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testBookmark.getModule()).isEqualTo(DEFAULT_MODULE);
        assertThat(testBookmark.getSeconds()).isEqualTo(DEFAULT_SECONDS);

        // Validate the Bookmark in Elasticsearch
        verify(mockBookmarkSearchRepository, times(1)).save(testBookmark);
    }

    @Test
    @Transactional
    public void createBookmarkWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bookmarkRepository.findAll().size();

        // Create the Bookmark with an existing ID
        bookmark.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookmarkMockMvc.perform(post("/api/bookmarks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmark)))
            .andExpect(status().isBadRequest());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeCreate);

        // Validate the Bookmark in Elasticsearch
        verify(mockBookmarkSearchRepository, times(0)).save(bookmark);
    }

    @Test
    @Transactional
    public void checkTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookmarkRepository.findAll().size();
        // set the field null
        bookmark.setText(null);

        // Create the Bookmark, which fails.

        restBookmarkMockMvc.perform(post("/api/bookmarks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmark)))
            .andExpect(status().isBadRequest());

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBookmarks() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList
        restBookmarkMockMvc.perform(get("/api/bookmarks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookmark.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].slide").value(hasItem(DEFAULT_SLIDE)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)))
            .andExpect(jsonPath("$.[*].module").value(hasItem(DEFAULT_MODULE)))
            .andExpect(jsonPath("$.[*].seconds").value(hasItem(DEFAULT_SECONDS)));
    }
    

    @Test
    @Transactional
    public void getBookmark() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get the bookmark
        restBookmarkMockMvc.perform(get("/api/bookmarks/{id}", bookmark.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(bookmark.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT))
            .andExpect(jsonPath("$.slide").value(DEFAULT_SLIDE))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP))
            .andExpect(jsonPath("$.module").value(DEFAULT_MODULE))
            .andExpect(jsonPath("$.seconds").value(DEFAULT_SECONDS));
    }

    @Test
    @Transactional
    public void getAllBookmarksByTextIsEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where text equals to DEFAULT_TEXT
        defaultBookmarkShouldBeFound("text.equals=" + DEFAULT_TEXT);

        // Get all the bookmarkList where text equals to UPDATED_TEXT
        defaultBookmarkShouldNotBeFound("text.equals=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    public void getAllBookmarksByTextIsInShouldWork() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where text in DEFAULT_TEXT or UPDATED_TEXT
        defaultBookmarkShouldBeFound("text.in=" + DEFAULT_TEXT + "," + UPDATED_TEXT);

        // Get all the bookmarkList where text equals to UPDATED_TEXT
        defaultBookmarkShouldNotBeFound("text.in=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    public void getAllBookmarksByTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where text is not null
        defaultBookmarkShouldBeFound("text.specified=true");

        // Get all the bookmarkList where text is null
        defaultBookmarkShouldNotBeFound("text.specified=false");
    }

    @Test
    @Transactional
    public void getAllBookmarksBySlideIsEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where slide equals to DEFAULT_SLIDE
        defaultBookmarkShouldBeFound("slide.equals=" + DEFAULT_SLIDE);

        // Get all the bookmarkList where slide equals to UPDATED_SLIDE
        defaultBookmarkShouldNotBeFound("slide.equals=" + UPDATED_SLIDE);
    }

    @Test
    @Transactional
    public void getAllBookmarksBySlideIsInShouldWork() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where slide in DEFAULT_SLIDE or UPDATED_SLIDE
        defaultBookmarkShouldBeFound("slide.in=" + DEFAULT_SLIDE + "," + UPDATED_SLIDE);

        // Get all the bookmarkList where slide equals to UPDATED_SLIDE
        defaultBookmarkShouldNotBeFound("slide.in=" + UPDATED_SLIDE);
    }

    @Test
    @Transactional
    public void getAllBookmarksBySlideIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where slide is not null
        defaultBookmarkShouldBeFound("slide.specified=true");

        // Get all the bookmarkList where slide is null
        defaultBookmarkShouldNotBeFound("slide.specified=false");
    }

    @Test
    @Transactional
    public void getAllBookmarksBySlideIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where slide greater than or equals to DEFAULT_SLIDE
        defaultBookmarkShouldBeFound("slide.greaterOrEqualThan=" + DEFAULT_SLIDE);

        // Get all the bookmarkList where slide greater than or equals to UPDATED_SLIDE
        defaultBookmarkShouldNotBeFound("slide.greaterOrEqualThan=" + UPDATED_SLIDE);
    }

    @Test
    @Transactional
    public void getAllBookmarksBySlideIsLessThanSomething() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where slide less than or equals to DEFAULT_SLIDE
        defaultBookmarkShouldNotBeFound("slide.lessThan=" + DEFAULT_SLIDE);

        // Get all the bookmarkList where slide less than or equals to UPDATED_SLIDE
        defaultBookmarkShouldBeFound("slide.lessThan=" + UPDATED_SLIDE);
    }


    @Test
    @Transactional
    public void getAllBookmarksByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where timestamp equals to DEFAULT_TIMESTAMP
        defaultBookmarkShouldBeFound("timestamp.equals=" + DEFAULT_TIMESTAMP);

        // Get all the bookmarkList where timestamp equals to UPDATED_TIMESTAMP
        defaultBookmarkShouldNotBeFound("timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllBookmarksByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where timestamp in DEFAULT_TIMESTAMP or UPDATED_TIMESTAMP
        defaultBookmarkShouldBeFound("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP);

        // Get all the bookmarkList where timestamp equals to UPDATED_TIMESTAMP
        defaultBookmarkShouldNotBeFound("timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllBookmarksByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where timestamp is not null
        defaultBookmarkShouldBeFound("timestamp.specified=true");

        // Get all the bookmarkList where timestamp is null
        defaultBookmarkShouldNotBeFound("timestamp.specified=false");
    }

    @Test
    @Transactional
    public void getAllBookmarksByModuleIsEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where module equals to DEFAULT_MODULE
        defaultBookmarkShouldBeFound("module.equals=" + DEFAULT_MODULE);

        // Get all the bookmarkList where module equals to UPDATED_MODULE
        defaultBookmarkShouldNotBeFound("module.equals=" + UPDATED_MODULE);
    }

    @Test
    @Transactional
    public void getAllBookmarksByModuleIsInShouldWork() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where module in DEFAULT_MODULE or UPDATED_MODULE
        defaultBookmarkShouldBeFound("module.in=" + DEFAULT_MODULE + "," + UPDATED_MODULE);

        // Get all the bookmarkList where module equals to UPDATED_MODULE
        defaultBookmarkShouldNotBeFound("module.in=" + UPDATED_MODULE);
    }

    @Test
    @Transactional
    public void getAllBookmarksByModuleIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where module is not null
        defaultBookmarkShouldBeFound("module.specified=true");

        // Get all the bookmarkList where module is null
        defaultBookmarkShouldNotBeFound("module.specified=false");
    }

    @Test
    @Transactional
    public void getAllBookmarksBySecondsIsEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where seconds equals to DEFAULT_SECONDS
        defaultBookmarkShouldBeFound("seconds.equals=" + DEFAULT_SECONDS);

        // Get all the bookmarkList where seconds equals to UPDATED_SECONDS
        defaultBookmarkShouldNotBeFound("seconds.equals=" + UPDATED_SECONDS);
    }

    @Test
    @Transactional
    public void getAllBookmarksBySecondsIsInShouldWork() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where seconds in DEFAULT_SECONDS or UPDATED_SECONDS
        defaultBookmarkShouldBeFound("seconds.in=" + DEFAULT_SECONDS + "," + UPDATED_SECONDS);

        // Get all the bookmarkList where seconds equals to UPDATED_SECONDS
        defaultBookmarkShouldNotBeFound("seconds.in=" + UPDATED_SECONDS);
    }

    @Test
    @Transactional
    public void getAllBookmarksBySecondsIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where seconds is not null
        defaultBookmarkShouldBeFound("seconds.specified=true");

        // Get all the bookmarkList where seconds is null
        defaultBookmarkShouldNotBeFound("seconds.specified=false");
    }

    @Test
    @Transactional
    public void getAllBookmarksBySecondsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where seconds greater than or equals to DEFAULT_SECONDS
        defaultBookmarkShouldBeFound("seconds.greaterOrEqualThan=" + DEFAULT_SECONDS);

        // Get all the bookmarkList where seconds greater than or equals to UPDATED_SECONDS
        defaultBookmarkShouldNotBeFound("seconds.greaterOrEqualThan=" + UPDATED_SECONDS);
    }

    @Test
    @Transactional
    public void getAllBookmarksBySecondsIsLessThanSomething() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarkList where seconds less than or equals to DEFAULT_SECONDS
        defaultBookmarkShouldNotBeFound("seconds.lessThan=" + DEFAULT_SECONDS);

        // Get all the bookmarkList where seconds less than or equals to UPDATED_SECONDS
        defaultBookmarkShouldBeFound("seconds.lessThan=" + UPDATED_SECONDS);
    }


    @Test
    @Transactional
    public void getAllBookmarksBySectionIsEqualToSomething() throws Exception {
        // Initialize the database
        Section section = SectionResourceIntTest.createEntity(em);
        em.persist(section);
        em.flush();
        bookmark.setSection(section);
        bookmarkRepository.saveAndFlush(bookmark);
        Long sectionId = section.getId();

        // Get all the bookmarkList where section equals to sectionId
        defaultBookmarkShouldBeFound("sectionId.equals=" + sectionId);

        // Get all the bookmarkList where section equals to sectionId + 1
        defaultBookmarkShouldNotBeFound("sectionId.equals=" + (sectionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultBookmarkShouldBeFound(String filter) throws Exception {
        restBookmarkMockMvc.perform(get("/api/bookmarks?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookmark.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].slide").value(hasItem(DEFAULT_SLIDE)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)))
            .andExpect(jsonPath("$.[*].module").value(hasItem(DEFAULT_MODULE)))
            .andExpect(jsonPath("$.[*].seconds").value(hasItem(DEFAULT_SECONDS)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultBookmarkShouldNotBeFound(String filter) throws Exception {
        restBookmarkMockMvc.perform(get("/api/bookmarks?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingBookmark() throws Exception {
        // Get the bookmark
        restBookmarkMockMvc.perform(get("/api/bookmarks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBookmark() throws Exception {
        // Initialize the database
        bookmarkService.save(bookmark);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockBookmarkSearchRepository);

        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();

        // Update the bookmark
        Bookmark updatedBookmark = bookmarkRepository.findById(bookmark.getId()).get();
        // Disconnect from session so that the updates on updatedBookmark are not directly saved in db
        em.detach(updatedBookmark);
        updatedBookmark
            .text(UPDATED_TEXT)
            .slide(UPDATED_SLIDE)
            .timestamp(UPDATED_TIMESTAMP)
            .module(UPDATED_MODULE)
            .seconds(UPDATED_SECONDS);

        restBookmarkMockMvc.perform(put("/api/bookmarks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBookmark)))
            .andExpect(status().isOk());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
        Bookmark testBookmark = bookmarkList.get(bookmarkList.size() - 1);
        assertThat(testBookmark.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testBookmark.getSlide()).isEqualTo(UPDATED_SLIDE);
        assertThat(testBookmark.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testBookmark.getModule()).isEqualTo(UPDATED_MODULE);
        assertThat(testBookmark.getSeconds()).isEqualTo(UPDATED_SECONDS);

        // Validate the Bookmark in Elasticsearch
        verify(mockBookmarkSearchRepository, times(1)).save(testBookmark);
    }

    @Test
    @Transactional
    public void updateNonExistingBookmark() throws Exception {
        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();

        // Create the Bookmark

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restBookmarkMockMvc.perform(put("/api/bookmarks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmark)))
            .andExpect(status().isBadRequest());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Bookmark in Elasticsearch
        verify(mockBookmarkSearchRepository, times(0)).save(bookmark);
    }

    @Test
    @Transactional
    public void deleteBookmark() throws Exception {
        // Initialize the database
        bookmarkService.save(bookmark);

        int databaseSizeBeforeDelete = bookmarkRepository.findAll().size();

        // Get the bookmark
        restBookmarkMockMvc.perform(delete("/api/bookmarks/{id}", bookmark.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Bookmark in Elasticsearch
        verify(mockBookmarkSearchRepository, times(1)).deleteById(bookmark.getId());
    }

    @Test
    @Transactional
    public void searchBookmark() throws Exception {
        // Initialize the database
        bookmarkService.save(bookmark);
        when(mockBookmarkSearchRepository.search(queryStringQuery("id:" + bookmark.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(bookmark), PageRequest.of(0, 1), 1));
        // Search the bookmark
        restBookmarkMockMvc.perform(get("/api/_search/bookmarks?query=id:" + bookmark.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookmark.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].slide").value(hasItem(DEFAULT_SLIDE)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)))
            .andExpect(jsonPath("$.[*].module").value(hasItem(DEFAULT_MODULE)))
            .andExpect(jsonPath("$.[*].seconds").value(hasItem(DEFAULT_SECONDS)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bookmark.class);
        Bookmark bookmark1 = new Bookmark();
        bookmark1.setId(1L);
        Bookmark bookmark2 = new Bookmark();
        bookmark2.setId(bookmark1.getId());
        assertThat(bookmark1).isEqualTo(bookmark2);
        bookmark2.setId(2L);
        assertThat(bookmark1).isNotEqualTo(bookmark2);
        bookmark1.setId(null);
        assertThat(bookmark1).isNotEqualTo(bookmark2);
    }
}
