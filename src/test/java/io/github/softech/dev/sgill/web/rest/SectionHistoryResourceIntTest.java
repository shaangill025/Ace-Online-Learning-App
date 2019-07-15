package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.SectionHistory;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.Section;
import io.github.softech.dev.sgill.repository.*;
import io.github.softech.dev.sgill.repository.search.SectionHistorySearchRepository;
import io.github.softech.dev.sgill.service.*;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.SectionHistoryCriteria;

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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Test class for the SectionHistoryResource REST controller.
 *
 * @see SectionHistoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class SectionHistoryResourceIntTest {

    private static final Instant DEFAULT_STARTDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LASTACTIVEDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LASTACTIVEDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_WATCHED = false;
    private static final Boolean UPDATED_WATCHED = true;

    @Autowired
    private SectionHistoryRepository sectionHistoryRepository;

    

    @Autowired
    private SectionHistoryService sectionHistoryService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.SectionHistorySearchRepositoryMockConfiguration
     */
    @Autowired
    private SectionHistorySearchRepository mockSectionHistorySearchRepository;

    @Autowired
    private SectionHistoryQueryService sectionHistoryQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSectionHistoryMockMvc;

    private SectionHistory sectionHistory;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private CourseHistoryRepository courseHistoryRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuizHistoryRepository quizHistoryRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SectionHistoryResource sectionHistoryResource = new SectionHistoryResource(sectionHistoryService, sectionHistoryQueryService,
            sectionHistoryRepository, customerService, sectionService, sectionRepository, quizHistoryRepository, courseService, quizRepository,
            courseHistoryRepository);
        this.restSectionHistoryMockMvc = MockMvcBuilders.standaloneSetup(sectionHistoryResource)
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
    public static SectionHistory createEntity(EntityManager em) {
        SectionHistory sectionHistory = new SectionHistory()
            .startdate(DEFAULT_STARTDATE)
            .lastactivedate(DEFAULT_LASTACTIVEDATE)
            .watched(DEFAULT_WATCHED);
        return sectionHistory;
    }

    @Before
    public void initTest() {
        sectionHistory = createEntity(em);
    }

    @Test
    @Transactional
    public void createSectionHistory() throws Exception {
        int databaseSizeBeforeCreate = sectionHistoryRepository.findAll().size();

        // Create the SectionHistory
        restSectionHistoryMockMvc.perform(post("/api/section-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sectionHistory)))
            .andExpect(status().isCreated());

        // Validate the SectionHistory in the database
        List<SectionHistory> sectionHistoryList = sectionHistoryRepository.findAll();
        assertThat(sectionHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        SectionHistory testSectionHistory = sectionHistoryList.get(sectionHistoryList.size() - 1);
        assertThat(testSectionHistory.getStartdate()).isEqualTo(DEFAULT_STARTDATE);
        assertThat(testSectionHistory.getLastactivedate()).isEqualTo(DEFAULT_LASTACTIVEDATE);
        assertThat(testSectionHistory.isWatched()).isEqualTo(DEFAULT_WATCHED);

        // Validate the SectionHistory in Elasticsearch
        verify(mockSectionHistorySearchRepository, times(1)).save(testSectionHistory);
    }

    @Test
    @Transactional
    public void createSectionHistoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = sectionHistoryRepository.findAll().size();

        // Create the SectionHistory with an existing ID
        sectionHistory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSectionHistoryMockMvc.perform(post("/api/section-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sectionHistory)))
            .andExpect(status().isBadRequest());

        // Validate the SectionHistory in the database
        List<SectionHistory> sectionHistoryList = sectionHistoryRepository.findAll();
        assertThat(sectionHistoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the SectionHistory in Elasticsearch
        verify(mockSectionHistorySearchRepository, times(0)).save(sectionHistory);
    }

    @Test
    @Transactional
    public void getAllSectionHistories() throws Exception {
        // Initialize the database
        sectionHistoryRepository.saveAndFlush(sectionHistory);

        // Get all the sectionHistoryList
        restSectionHistoryMockMvc.perform(get("/api/section-histories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sectionHistory.getId())))
            .andExpect(jsonPath("$.[*].startdate").value(hasItem(DEFAULT_STARTDATE.toString())))
            .andExpect(jsonPath("$.[*].lastactivedate").value(hasItem(DEFAULT_LASTACTIVEDATE.toString())))
            .andExpect(jsonPath("$.[*].watched").value(hasItem(DEFAULT_WATCHED.booleanValue())));
    }
    

    @Test
    @Transactional
    public void getSectionHistory() throws Exception {
        // Initialize the database
        sectionHistoryRepository.saveAndFlush(sectionHistory);

        // Get the sectionHistory
        restSectionHistoryMockMvc.perform(get("/api/section-histories/{id}", sectionHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(sectionHistory.getId()))
            .andExpect(jsonPath("$.startdate").value(DEFAULT_STARTDATE.toString()))
            .andExpect(jsonPath("$.lastactivedate").value(DEFAULT_LASTACTIVEDATE.toString()))
            .andExpect(jsonPath("$.watched").value(DEFAULT_WATCHED.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllSectionHistoriesByStartdateIsEqualToSomething() throws Exception {
        // Initialize the database
        sectionHistoryRepository.saveAndFlush(sectionHistory);

        // Get all the sectionHistoryList where startdate equals to DEFAULT_STARTDATE
        defaultSectionHistoryShouldBeFound("startdate.equals=" + DEFAULT_STARTDATE);

        // Get all the sectionHistoryList where startdate equals to UPDATED_STARTDATE
        defaultSectionHistoryShouldNotBeFound("startdate.equals=" + UPDATED_STARTDATE);
    }

    @Test
    @Transactional
    public void getAllSectionHistoriesByStartdateIsInShouldWork() throws Exception {
        // Initialize the database
        sectionHistoryRepository.saveAndFlush(sectionHistory);

        // Get all the sectionHistoryList where startdate in DEFAULT_STARTDATE or UPDATED_STARTDATE
        defaultSectionHistoryShouldBeFound("startdate.in=" + DEFAULT_STARTDATE + "," + UPDATED_STARTDATE);

        // Get all the sectionHistoryList where startdate equals to UPDATED_STARTDATE
        defaultSectionHistoryShouldNotBeFound("startdate.in=" + UPDATED_STARTDATE);
    }

    @Test
    @Transactional
    public void getAllSectionHistoriesByStartdateIsNullOrNotNull() throws Exception {
        // Initialize the database
        sectionHistoryRepository.saveAndFlush(sectionHistory);

        // Get all the sectionHistoryList where startdate is not null
        defaultSectionHistoryShouldBeFound("startdate.specified=true");

        // Get all the sectionHistoryList where startdate is null
        defaultSectionHistoryShouldNotBeFound("startdate.specified=false");
    }

    @Test
    @Transactional
    public void getAllSectionHistoriesByLastactivedateIsEqualToSomething() throws Exception {
        // Initialize the database
        sectionHistoryRepository.saveAndFlush(sectionHistory);

        // Get all the sectionHistoryList where lastactivedate equals to DEFAULT_LASTACTIVEDATE
        defaultSectionHistoryShouldBeFound("lastactivedate.equals=" + DEFAULT_LASTACTIVEDATE);

        // Get all the sectionHistoryList where lastactivedate equals to UPDATED_LASTACTIVEDATE
        defaultSectionHistoryShouldNotBeFound("lastactivedate.equals=" + UPDATED_LASTACTIVEDATE);
    }

    @Test
    @Transactional
    public void getAllSectionHistoriesByLastactivedateIsInShouldWork() throws Exception {
        // Initialize the database
        sectionHistoryRepository.saveAndFlush(sectionHistory);

        // Get all the sectionHistoryList where lastactivedate in DEFAULT_LASTACTIVEDATE or UPDATED_LASTACTIVEDATE
        defaultSectionHistoryShouldBeFound("lastactivedate.in=" + DEFAULT_LASTACTIVEDATE + "," + UPDATED_LASTACTIVEDATE);

        // Get all the sectionHistoryList where lastactivedate equals to UPDATED_LASTACTIVEDATE
        defaultSectionHistoryShouldNotBeFound("lastactivedate.in=" + UPDATED_LASTACTIVEDATE);
    }

    @Test
    @Transactional
    public void getAllSectionHistoriesByLastactivedateIsNullOrNotNull() throws Exception {
        // Initialize the database
        sectionHistoryRepository.saveAndFlush(sectionHistory);

        // Get all the sectionHistoryList where lastactivedate is not null
        defaultSectionHistoryShouldBeFound("lastactivedate.specified=true");

        // Get all the sectionHistoryList where lastactivedate is null
        defaultSectionHistoryShouldNotBeFound("lastactivedate.specified=false");
    }

    @Test
    @Transactional
    public void getAllSectionHistoriesByWatchedIsEqualToSomething() throws Exception {
        // Initialize the database
        sectionHistoryRepository.saveAndFlush(sectionHistory);

        // Get all the sectionHistoryList where watched equals to DEFAULT_WATCHED
        defaultSectionHistoryShouldBeFound("watched.equals=" + DEFAULT_WATCHED);

        // Get all the sectionHistoryList where watched equals to UPDATED_WATCHED
        defaultSectionHistoryShouldNotBeFound("watched.equals=" + UPDATED_WATCHED);
    }

    @Test
    @Transactional
    public void getAllSectionHistoriesByWatchedIsInShouldWork() throws Exception {
        // Initialize the database
        sectionHistoryRepository.saveAndFlush(sectionHistory);

        // Get all the sectionHistoryList where watched in DEFAULT_WATCHED or UPDATED_WATCHED
        defaultSectionHistoryShouldBeFound("watched.in=" + DEFAULT_WATCHED + "," + UPDATED_WATCHED);

        // Get all the sectionHistoryList where watched equals to UPDATED_WATCHED
        defaultSectionHistoryShouldNotBeFound("watched.in=" + UPDATED_WATCHED);
    }

    @Test
    @Transactional
    public void getAllSectionHistoriesByWatchedIsNullOrNotNull() throws Exception {
        // Initialize the database
        sectionHistoryRepository.saveAndFlush(sectionHistory);

        // Get all the sectionHistoryList where watched is not null
        defaultSectionHistoryShouldBeFound("watched.specified=true");

        // Get all the sectionHistoryList where watched is null
        defaultSectionHistoryShouldNotBeFound("watched.specified=false");
    }

    @Test
    @Transactional
    public void getAllSectionHistoriesByCustomerIsEqualToSomething() throws Exception {
        // Initialize the database
        Customer customer = CustomerResourceIntTest.createEntity(em);
        em.persist(customer);
        em.flush();
        sectionHistory.setCustomer(customer);
        sectionHistoryRepository.saveAndFlush(sectionHistory);
        Long customerId = customer.getId();

        // Get all the sectionHistoryList where customer equals to customerId
        defaultSectionHistoryShouldBeFound("customerId.equals=" + customerId);

        // Get all the sectionHistoryList where customer equals to customerId + 1
        defaultSectionHistoryShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }


    @Test
    @Transactional
    public void getAllSectionHistoriesBySectionIsEqualToSomething() throws Exception {
        // Initialize the database
        Section section = SectionResourceIntTest.createEntity(em);
        em.persist(section);
        em.flush();
        sectionHistory.setSection(section);
        sectionHistoryRepository.saveAndFlush(sectionHistory);
        Long sectionId = section.getId();

        // Get all the sectionHistoryList where section equals to sectionId
        defaultSectionHistoryShouldBeFound("sectionId.equals=" + sectionId);

        // Get all the sectionHistoryList where section equals to sectionId + 1
        defaultSectionHistoryShouldNotBeFound("sectionId.equals=" + (sectionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultSectionHistoryShouldBeFound(String filter) throws Exception {
        restSectionHistoryMockMvc.perform(get("/api/section-histories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sectionHistory.getId())))
            .andExpect(jsonPath("$.[*].startdate").value(hasItem(DEFAULT_STARTDATE.toString())))
            .andExpect(jsonPath("$.[*].lastactivedate").value(hasItem(DEFAULT_LASTACTIVEDATE.toString())))
            .andExpect(jsonPath("$.[*].watched").value(hasItem(DEFAULT_WATCHED.booleanValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultSectionHistoryShouldNotBeFound(String filter) throws Exception {
        restSectionHistoryMockMvc.perform(get("/api/section-histories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingSectionHistory() throws Exception {
        // Get the sectionHistory
        restSectionHistoryMockMvc.perform(get("/api/section-histories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSectionHistory() throws Exception {
        // Initialize the database
        sectionHistoryService.save(sectionHistory);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockSectionHistorySearchRepository);

        int databaseSizeBeforeUpdate = sectionHistoryRepository.findAll().size();

        // Update the sectionHistory
        SectionHistory updatedSectionHistory = sectionHistoryRepository.findById(sectionHistory.getId()).get();
        // Disconnect from session so that the updates on updatedSectionHistory are not directly saved in db
        em.detach(updatedSectionHistory);
        updatedSectionHistory
            .startdate(UPDATED_STARTDATE)
            .lastactivedate(UPDATED_LASTACTIVEDATE)
            .watched(UPDATED_WATCHED);

        restSectionHistoryMockMvc.perform(put("/api/section-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSectionHistory)))
            .andExpect(status().isOk());

        // Validate the SectionHistory in the database
        List<SectionHistory> sectionHistoryList = sectionHistoryRepository.findAll();
        assertThat(sectionHistoryList).hasSize(databaseSizeBeforeUpdate);
        SectionHistory testSectionHistory = sectionHistoryList.get(sectionHistoryList.size() - 1);
        assertThat(testSectionHistory.getStartdate()).isEqualTo(UPDATED_STARTDATE);
        assertThat(testSectionHistory.getLastactivedate()).isEqualTo(UPDATED_LASTACTIVEDATE);
        assertThat(testSectionHistory.isWatched()).isEqualTo(UPDATED_WATCHED);

        // Validate the SectionHistory in Elasticsearch
        verify(mockSectionHistorySearchRepository, times(1)).save(testSectionHistory);
    }

    @Test
    @Transactional
    public void updateNonExistingSectionHistory() throws Exception {
        int databaseSizeBeforeUpdate = sectionHistoryRepository.findAll().size();

        // Create the SectionHistory

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restSectionHistoryMockMvc.perform(put("/api/section-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sectionHistory)))
            .andExpect(status().isBadRequest());

        // Validate the SectionHistory in the database
        List<SectionHistory> sectionHistoryList = sectionHistoryRepository.findAll();
        assertThat(sectionHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SectionHistory in Elasticsearch
        verify(mockSectionHistorySearchRepository, times(0)).save(sectionHistory);
    }

    @Test
    @Transactional
    public void deleteSectionHistory() throws Exception {
        // Initialize the database
        sectionHistoryService.save(sectionHistory);

        int databaseSizeBeforeDelete = sectionHistoryRepository.findAll().size();

        // Get the sectionHistory
        restSectionHistoryMockMvc.perform(delete("/api/section-histories/{id}", sectionHistory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<SectionHistory> sectionHistoryList = sectionHistoryRepository.findAll();
        assertThat(sectionHistoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the SectionHistory in Elasticsearch
        verify(mockSectionHistorySearchRepository, times(1)).deleteById(sectionHistory.getId());
    }

    @Test
    @Transactional
    public void searchSectionHistory() throws Exception {
        // Initialize the database
        sectionHistoryService.save(sectionHistory);
        when(mockSectionHistorySearchRepository.search(queryStringQuery("id:" + sectionHistory.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(sectionHistory), PageRequest.of(0, 1), 1));
        // Search the sectionHistory
        restSectionHistoryMockMvc.perform(get("/api/_search/section-histories?query=id:" + sectionHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sectionHistory.getId())))
            .andExpect(jsonPath("$.[*].startdate").value(hasItem(DEFAULT_STARTDATE.toString())))
            .andExpect(jsonPath("$.[*].lastactivedate").value(hasItem(DEFAULT_LASTACTIVEDATE.toString())))
            .andExpect(jsonPath("$.[*].watched").value(hasItem(DEFAULT_WATCHED.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SectionHistory.class);
        SectionHistory sectionHistory1 = new SectionHistory();
        sectionHistory1.setId(1L);
        SectionHistory sectionHistory2 = new SectionHistory();
        sectionHistory2.setId(sectionHistory1.getId());
        assertThat(sectionHistory1).isEqualTo(sectionHistory2);
        sectionHistory2.setId(2L);
        assertThat(sectionHistory1).isNotEqualTo(sectionHistory2);
        sectionHistory1.setId(null);
        assertThat(sectionHistory1).isNotEqualTo(sectionHistory2);
    }
}
