package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.QuizHistory;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.Quiz;
import io.github.softech.dev.sgill.repository.*;
import io.github.softech.dev.sgill.repository.search.QuizHistorySearchRepository;
import io.github.softech.dev.sgill.service.QuizHistoryService;
import io.github.softech.dev.sgill.service.SectionService;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.QuizHistoryCriteria;
import io.github.softech.dev.sgill.service.QuizHistoryQueryService;

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
 * Test class for the QuizHistoryResource REST controller.
 *
 * @see QuizHistoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class QuizHistoryResourceIntTest {

    private static final Instant DEFAULT_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_PASSED = false;
    private static final Boolean UPDATED_PASSED = true;

    @Autowired
    private QuizHistoryRepository quizHistoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizHistoryService quizHistoryService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.QuizHistorySearchRepositoryMockConfiguration
     */
    @Autowired
    private QuizHistorySearchRepository mockQuizHistorySearchRepository;

    @Autowired
    private QuizHistoryQueryService quizHistoryQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restQuizHistoryMockMvc;

    private QuizHistory quizHistory;

    @Autowired
    private SectionHistoryRepository sectionHistoryRepository;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private SectionRepository sectionRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final QuizHistoryResource quizHistoryResource = new QuizHistoryResource(quizHistoryService, quizHistoryQueryService, sectionHistoryRepository, quizHistoryRepository, sectionService, sectionRepository, customerRepository, quizRepository);
        this.restQuizHistoryMockMvc = MockMvcBuilders.standaloneSetup(quizHistoryResource)
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
    public static QuizHistory createEntity(EntityManager em) {
        QuizHistory quizHistory = new QuizHistory()
            .start(DEFAULT_START)
            .passed(DEFAULT_PASSED);
        return quizHistory;
    }

    @Before
    public void initTest() {
        quizHistory = createEntity(em);
    }

    @Test
    @Transactional
    public void createQuizHistory() throws Exception {
        int databaseSizeBeforeCreate = quizHistoryRepository.findAll().size();

        // Create the QuizHistory
        restQuizHistoryMockMvc.perform(post("/api/quiz-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quizHistory)))
            .andExpect(status().isCreated());

        // Validate the QuizHistory in the database
        List<QuizHistory> quizHistoryList = quizHistoryRepository.findAll();
        assertThat(quizHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        QuizHistory testQuizHistory = quizHistoryList.get(quizHistoryList.size() - 1);
        assertThat(testQuizHistory.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testQuizHistory.isPassed()).isEqualTo(DEFAULT_PASSED);

        // Validate the QuizHistory in Elasticsearch
        verify(mockQuizHistorySearchRepository, times(1)).save(testQuizHistory);
    }

    @Test
    @Transactional
    public void createQuizHistoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = quizHistoryRepository.findAll().size();

        // Create the QuizHistory with an existing ID
        quizHistory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuizHistoryMockMvc.perform(post("/api/quiz-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quizHistory)))
            .andExpect(status().isBadRequest());

        // Validate the QuizHistory in the database
        List<QuizHistory> quizHistoryList = quizHistoryRepository.findAll();
        assertThat(quizHistoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the QuizHistory in Elasticsearch
        verify(mockQuizHistorySearchRepository, times(0)).save(quizHistory);
    }

    @Test
    @Transactional
    public void getAllQuizHistories() throws Exception {
        // Initialize the database
        quizHistoryRepository.saveAndFlush(quizHistory);

        // Get all the quizHistoryList
        restQuizHistoryMockMvc.perform(get("/api/quiz-histories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizHistory.getId())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].passed").value(hasItem(DEFAULT_PASSED.booleanValue())));
    }
    

    @Test
    @Transactional
    public void getQuizHistory() throws Exception {
        // Initialize the database
        quizHistoryRepository.saveAndFlush(quizHistory);

        // Get the quizHistory
        restQuizHistoryMockMvc.perform(get("/api/quiz-histories/{id}", quizHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(quizHistory.getId()))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.passed").value(DEFAULT_PASSED.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllQuizHistoriesByStartIsEqualToSomething() throws Exception {
        // Initialize the database
        quizHistoryRepository.saveAndFlush(quizHistory);

        // Get all the quizHistoryList where start equals to DEFAULT_START
        defaultQuizHistoryShouldBeFound("start.equals=" + DEFAULT_START);

        // Get all the quizHistoryList where start equals to UPDATED_START
        defaultQuizHistoryShouldNotBeFound("start.equals=" + UPDATED_START);
    }

    @Test
    @Transactional
    public void getAllQuizHistoriesByStartIsInShouldWork() throws Exception {
        // Initialize the database
        quizHistoryRepository.saveAndFlush(quizHistory);

        // Get all the quizHistoryList where start in DEFAULT_START or UPDATED_START
        defaultQuizHistoryShouldBeFound("start.in=" + DEFAULT_START + "," + UPDATED_START);

        // Get all the quizHistoryList where start equals to UPDATED_START
        defaultQuizHistoryShouldNotBeFound("start.in=" + UPDATED_START);
    }

    @Test
    @Transactional
    public void getAllQuizHistoriesByStartIsNullOrNotNull() throws Exception {
        // Initialize the database
        quizHistoryRepository.saveAndFlush(quizHistory);

        // Get all the quizHistoryList where start is not null
        defaultQuizHistoryShouldBeFound("start.specified=true");

        // Get all the quizHistoryList where start is null
        defaultQuizHistoryShouldNotBeFound("start.specified=false");
    }

    @Test
    @Transactional
    public void getAllQuizHistoriesByPassedIsEqualToSomething() throws Exception {
        // Initialize the database
        quizHistoryRepository.saveAndFlush(quizHistory);

        // Get all the quizHistoryList where passed equals to DEFAULT_PASSED
        defaultQuizHistoryShouldBeFound("passed.equals=" + DEFAULT_PASSED);

        // Get all the quizHistoryList where passed equals to UPDATED_PASSED
        defaultQuizHistoryShouldNotBeFound("passed.equals=" + UPDATED_PASSED);
    }

    @Test
    @Transactional
    public void getAllQuizHistoriesByPassedIsInShouldWork() throws Exception {
        // Initialize the database
        quizHistoryRepository.saveAndFlush(quizHistory);

        // Get all the quizHistoryList where passed in DEFAULT_PASSED or UPDATED_PASSED
        defaultQuizHistoryShouldBeFound("passed.in=" + DEFAULT_PASSED + "," + UPDATED_PASSED);

        // Get all the quizHistoryList where passed equals to UPDATED_PASSED
        defaultQuizHistoryShouldNotBeFound("passed.in=" + UPDATED_PASSED);
    }

    @Test
    @Transactional
    public void getAllQuizHistoriesByPassedIsNullOrNotNull() throws Exception {
        // Initialize the database
        quizHistoryRepository.saveAndFlush(quizHistory);

        // Get all the quizHistoryList where passed is not null
        defaultQuizHistoryShouldBeFound("passed.specified=true");

        // Get all the quizHistoryList where passed is null
        defaultQuizHistoryShouldNotBeFound("passed.specified=false");
    }

    @Test
    @Transactional
    public void getAllQuizHistoriesByCustomerIsEqualToSomething() throws Exception {
        // Initialize the database
        Customer customer = CustomerResourceIntTest.createEntity(em);
        em.persist(customer);
        em.flush();
        quizHistory.setCustomer(customer);
        quizHistoryRepository.saveAndFlush(quizHistory);
        Long customerId = customer.getId();

        // Get all the quizHistoryList where customer equals to customerId
        defaultQuizHistoryShouldBeFound("customerId.equals=" + customerId);

        // Get all the quizHistoryList where customer equals to customerId + 1
        defaultQuizHistoryShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }


    @Test
    @Transactional
    public void getAllQuizHistoriesByQuizIsEqualToSomething() throws Exception {
        // Initialize the database
        Quiz quiz = QuizResourceIntTest.createEntity(em);
        em.persist(quiz);
        em.flush();
        quizHistory.setQuiz(quiz);
        quizHistoryRepository.saveAndFlush(quizHistory);
        Long quizId = quiz.getId();

        // Get all the quizHistoryList where quiz equals to quizId
        defaultQuizHistoryShouldBeFound("quizId.equals=" + quizId);

        // Get all the quizHistoryList where quiz equals to quizId + 1
        defaultQuizHistoryShouldNotBeFound("quizId.equals=" + (quizId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultQuizHistoryShouldBeFound(String filter) throws Exception {
        restQuizHistoryMockMvc.perform(get("/api/quiz-histories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizHistory.getId())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].passed").value(hasItem(DEFAULT_PASSED.booleanValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultQuizHistoryShouldNotBeFound(String filter) throws Exception {
        restQuizHistoryMockMvc.perform(get("/api/quiz-histories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingQuizHistory() throws Exception {
        // Get the quizHistory
        restQuizHistoryMockMvc.perform(get("/api/quiz-histories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQuizHistory() throws Exception {
        // Initialize the database
        quizHistoryService.save(quizHistory);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockQuizHistorySearchRepository);

        int databaseSizeBeforeUpdate = quizHistoryRepository.findAll().size();

        // Update the quizHistory
        QuizHistory updatedQuizHistory = quizHistoryRepository.findById(quizHistory.getId()).get();
        // Disconnect from session so that the updates on updatedQuizHistory are not directly saved in db
        em.detach(updatedQuizHistory);
        updatedQuizHistory
            .start(UPDATED_START)
            .passed(UPDATED_PASSED);

        restQuizHistoryMockMvc.perform(put("/api/quiz-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedQuizHistory)))
            .andExpect(status().isOk());

        // Validate the QuizHistory in the database
        List<QuizHistory> quizHistoryList = quizHistoryRepository.findAll();
        assertThat(quizHistoryList).hasSize(databaseSizeBeforeUpdate);
        QuizHistory testQuizHistory = quizHistoryList.get(quizHistoryList.size() - 1);
        assertThat(testQuizHistory.getStart()).isEqualTo(UPDATED_START);
        assertThat(testQuizHistory.isPassed()).isEqualTo(UPDATED_PASSED);

        // Validate the QuizHistory in Elasticsearch
        verify(mockQuizHistorySearchRepository, times(1)).save(testQuizHistory);
    }

    @Test
    @Transactional
    public void updateNonExistingQuizHistory() throws Exception {
        int databaseSizeBeforeUpdate = quizHistoryRepository.findAll().size();

        // Create the QuizHistory

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restQuizHistoryMockMvc.perform(put("/api/quiz-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quizHistory)))
            .andExpect(status().isBadRequest());

        // Validate the QuizHistory in the database
        List<QuizHistory> quizHistoryList = quizHistoryRepository.findAll();
        assertThat(quizHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the QuizHistory in Elasticsearch
        verify(mockQuizHistorySearchRepository, times(0)).save(quizHistory);
    }

    @Test
    @Transactional
    public void deleteQuizHistory() throws Exception {
        // Initialize the database
        quizHistoryService.save(quizHistory);

        int databaseSizeBeforeDelete = quizHistoryRepository.findAll().size();

        // Get the quizHistory
        restQuizHistoryMockMvc.perform(delete("/api/quiz-histories/{id}", quizHistory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<QuizHistory> quizHistoryList = quizHistoryRepository.findAll();
        assertThat(quizHistoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the QuizHistory in Elasticsearch
        verify(mockQuizHistorySearchRepository, times(1)).deleteById(quizHistory.getId());
    }

    @Test
    @Transactional
    public void searchQuizHistory() throws Exception {
        // Initialize the database
        quizHistoryService.save(quizHistory);
        when(mockQuizHistorySearchRepository.search(queryStringQuery("id:" + quizHistory.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(quizHistory), PageRequest.of(0, 1), 1));
        // Search the quizHistory
        restQuizHistoryMockMvc.perform(get("/api/_search/quiz-histories?query=id:" + quizHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizHistory.getId())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].passed").value(hasItem(DEFAULT_PASSED.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuizHistory.class);
        QuizHistory quizHistory1 = new QuizHistory();
        quizHistory1.setId(1L);
        QuizHistory quizHistory2 = new QuizHistory();
        quizHistory2.setId(quizHistory1.getId());
        assertThat(quizHistory1).isEqualTo(quizHistory2);
        quizHistory2.setId(2L);
        assertThat(quizHistory1).isNotEqualTo(quizHistory2);
        quizHistory1.setId(null);
        assertThat(quizHistory1).isNotEqualTo(quizHistory2);
    }
}
