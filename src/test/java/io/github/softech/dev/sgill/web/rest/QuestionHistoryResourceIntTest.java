package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.QuestionHistory;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.Question;
import io.github.softech.dev.sgill.repository.QuestionHistoryRepository;
import io.github.softech.dev.sgill.repository.search.QuestionHistorySearchRepository;
import io.github.softech.dev.sgill.service.QuestionHistoryService;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.QuestionHistoryCriteria;
import io.github.softech.dev.sgill.service.QuestionHistoryQueryService;

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
 * Test class for the QuestionHistoryResource REST controller.
 *
 * @see QuestionHistoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class QuestionHistoryResourceIntTest {

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_CORRECT = false;
    private static final Boolean UPDATED_CORRECT = true;

    @Autowired
    private QuestionHistoryRepository questionHistoryRepository;

    

    @Autowired
    private QuestionHistoryService questionHistoryService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.QuestionHistorySearchRepositoryMockConfiguration
     */
    @Autowired
    private QuestionHistorySearchRepository mockQuestionHistorySearchRepository;

    @Autowired
    private QuestionHistoryQueryService questionHistoryQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restQuestionHistoryMockMvc;

    private QuestionHistory questionHistory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final QuestionHistoryResource questionHistoryResource = new QuestionHistoryResource(questionHistoryService, questionHistoryQueryService);
        this.restQuestionHistoryMockMvc = MockMvcBuilders.standaloneSetup(questionHistoryResource)
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
    public static QuestionHistory createEntity(EntityManager em) {
        QuestionHistory questionHistory = new QuestionHistory()
            .timestamp(DEFAULT_TIMESTAMP)
            .correct(DEFAULT_CORRECT);
        return questionHistory;
    }

    @Before
    public void initTest() {
        questionHistory = createEntity(em);
    }

    @Test
    @Transactional
    public void createQuestionHistory() throws Exception {
        int databaseSizeBeforeCreate = questionHistoryRepository.findAll().size();

        // Create the QuestionHistory
        restQuestionHistoryMockMvc.perform(post("/api/question-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(questionHistory)))
            .andExpect(status().isCreated());

        // Validate the QuestionHistory in the database
        List<QuestionHistory> questionHistoryList = questionHistoryRepository.findAll();
        assertThat(questionHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        QuestionHistory testQuestionHistory = questionHistoryList.get(questionHistoryList.size() - 1);
        assertThat(testQuestionHistory.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testQuestionHistory.isCorrect()).isEqualTo(DEFAULT_CORRECT);

        // Validate the QuestionHistory in Elasticsearch
        verify(mockQuestionHistorySearchRepository, times(1)).save(testQuestionHistory);
    }

    @Test
    @Transactional
    public void createQuestionHistoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = questionHistoryRepository.findAll().size();

        // Create the QuestionHistory with an existing ID
        questionHistory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionHistoryMockMvc.perform(post("/api/question-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(questionHistory)))
            .andExpect(status().isBadRequest());

        // Validate the QuestionHistory in the database
        List<QuestionHistory> questionHistoryList = questionHistoryRepository.findAll();
        assertThat(questionHistoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the QuestionHistory in Elasticsearch
        verify(mockQuestionHistorySearchRepository, times(0)).save(questionHistory);
    }

    @Test
    @Transactional
    public void getAllQuestionHistories() throws Exception {
        // Initialize the database
        questionHistoryRepository.saveAndFlush(questionHistory);

        // Get all the questionHistoryList
        restQuestionHistoryMockMvc.perform(get("/api/question-histories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(questionHistory.getId())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].correct").value(hasItem(DEFAULT_CORRECT.booleanValue())));
    }
    

    @Test
    @Transactional
    public void getQuestionHistory() throws Exception {
        // Initialize the database
        questionHistoryRepository.saveAndFlush(questionHistory);

        // Get the questionHistory
        restQuestionHistoryMockMvc.perform(get("/api/question-histories/{id}", questionHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(questionHistory.getId()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.correct").value(DEFAULT_CORRECT.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllQuestionHistoriesByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        questionHistoryRepository.saveAndFlush(questionHistory);

        // Get all the questionHistoryList where timestamp equals to DEFAULT_TIMESTAMP
        defaultQuestionHistoryShouldBeFound("timestamp.equals=" + DEFAULT_TIMESTAMP);

        // Get all the questionHistoryList where timestamp equals to UPDATED_TIMESTAMP
        defaultQuestionHistoryShouldNotBeFound("timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllQuestionHistoriesByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        questionHistoryRepository.saveAndFlush(questionHistory);

        // Get all the questionHistoryList where timestamp in DEFAULT_TIMESTAMP or UPDATED_TIMESTAMP
        defaultQuestionHistoryShouldBeFound("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP);

        // Get all the questionHistoryList where timestamp equals to UPDATED_TIMESTAMP
        defaultQuestionHistoryShouldNotBeFound("timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllQuestionHistoriesByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        questionHistoryRepository.saveAndFlush(questionHistory);

        // Get all the questionHistoryList where timestamp is not null
        defaultQuestionHistoryShouldBeFound("timestamp.specified=true");

        // Get all the questionHistoryList where timestamp is null
        defaultQuestionHistoryShouldNotBeFound("timestamp.specified=false");
    }

    @Test
    @Transactional
    public void getAllQuestionHistoriesByCorrectIsEqualToSomething() throws Exception {
        // Initialize the database
        questionHistoryRepository.saveAndFlush(questionHistory);

        // Get all the questionHistoryList where correct equals to DEFAULT_CORRECT
        defaultQuestionHistoryShouldBeFound("correct.equals=" + DEFAULT_CORRECT);

        // Get all the questionHistoryList where correct equals to UPDATED_CORRECT
        defaultQuestionHistoryShouldNotBeFound("correct.equals=" + UPDATED_CORRECT);
    }

    @Test
    @Transactional
    public void getAllQuestionHistoriesByCorrectIsInShouldWork() throws Exception {
        // Initialize the database
        questionHistoryRepository.saveAndFlush(questionHistory);

        // Get all the questionHistoryList where correct in DEFAULT_CORRECT or UPDATED_CORRECT
        defaultQuestionHistoryShouldBeFound("correct.in=" + DEFAULT_CORRECT + "," + UPDATED_CORRECT);

        // Get all the questionHistoryList where correct equals to UPDATED_CORRECT
        defaultQuestionHistoryShouldNotBeFound("correct.in=" + UPDATED_CORRECT);
    }

    @Test
    @Transactional
    public void getAllQuestionHistoriesByCorrectIsNullOrNotNull() throws Exception {
        // Initialize the database
        questionHistoryRepository.saveAndFlush(questionHistory);

        // Get all the questionHistoryList where correct is not null
        defaultQuestionHistoryShouldBeFound("correct.specified=true");

        // Get all the questionHistoryList where correct is null
        defaultQuestionHistoryShouldNotBeFound("correct.specified=false");
    }

    @Test
    @Transactional
    public void getAllQuestionHistoriesByCustomerIsEqualToSomething() throws Exception {
        // Initialize the database
        Customer customer = CustomerResourceIntTest.createEntity(em);
        em.persist(customer);
        em.flush();
        questionHistory.setCustomer(customer);
        questionHistoryRepository.saveAndFlush(questionHistory);
        Long customerId = customer.getId();

        // Get all the questionHistoryList where customer equals to customerId
        defaultQuestionHistoryShouldBeFound("customerId.equals=" + customerId);

        // Get all the questionHistoryList where customer equals to customerId + 1
        defaultQuestionHistoryShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }


    @Test
    @Transactional
    public void getAllQuestionHistoriesByQuestionIsEqualToSomething() throws Exception {
        // Initialize the database
        Question question = QuestionResourceIntTest.createEntity(em);
        em.persist(question);
        em.flush();
        questionHistory.setQuestion(question);
        questionHistoryRepository.saveAndFlush(questionHistory);
        Long questionId = question.getId();

        // Get all the questionHistoryList where question equals to questionId
        defaultQuestionHistoryShouldBeFound("questionId.equals=" + questionId);

        // Get all the questionHistoryList where question equals to questionId + 1
        defaultQuestionHistoryShouldNotBeFound("questionId.equals=" + (questionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultQuestionHistoryShouldBeFound(String filter) throws Exception {
        restQuestionHistoryMockMvc.perform(get("/api/question-histories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(questionHistory.getId())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].correct").value(hasItem(DEFAULT_CORRECT.booleanValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultQuestionHistoryShouldNotBeFound(String filter) throws Exception {
        restQuestionHistoryMockMvc.perform(get("/api/question-histories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingQuestionHistory() throws Exception {
        // Get the questionHistory
        restQuestionHistoryMockMvc.perform(get("/api/question-histories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQuestionHistory() throws Exception {
        // Initialize the database
        questionHistoryService.save(questionHistory);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockQuestionHistorySearchRepository);

        int databaseSizeBeforeUpdate = questionHistoryRepository.findAll().size();

        // Update the questionHistory
        QuestionHistory updatedQuestionHistory = questionHistoryRepository.findById(questionHistory.getId()).get();
        // Disconnect from session so that the updates on updatedQuestionHistory are not directly saved in db
        em.detach(updatedQuestionHistory);
        updatedQuestionHistory
            .timestamp(UPDATED_TIMESTAMP)
            .correct(UPDATED_CORRECT);

        restQuestionHistoryMockMvc.perform(put("/api/question-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedQuestionHistory)))
            .andExpect(status().isOk());

        // Validate the QuestionHistory in the database
        List<QuestionHistory> questionHistoryList = questionHistoryRepository.findAll();
        assertThat(questionHistoryList).hasSize(databaseSizeBeforeUpdate);
        QuestionHistory testQuestionHistory = questionHistoryList.get(questionHistoryList.size() - 1);
        assertThat(testQuestionHistory.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testQuestionHistory.isCorrect()).isEqualTo(UPDATED_CORRECT);

        // Validate the QuestionHistory in Elasticsearch
        verify(mockQuestionHistorySearchRepository, times(1)).save(testQuestionHistory);
    }

    @Test
    @Transactional
    public void updateNonExistingQuestionHistory() throws Exception {
        int databaseSizeBeforeUpdate = questionHistoryRepository.findAll().size();

        // Create the QuestionHistory

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restQuestionHistoryMockMvc.perform(put("/api/question-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(questionHistory)))
            .andExpect(status().isBadRequest());

        // Validate the QuestionHistory in the database
        List<QuestionHistory> questionHistoryList = questionHistoryRepository.findAll();
        assertThat(questionHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the QuestionHistory in Elasticsearch
        verify(mockQuestionHistorySearchRepository, times(0)).save(questionHistory);
    }

    @Test
    @Transactional
    public void deleteQuestionHistory() throws Exception {
        // Initialize the database
        questionHistoryService.save(questionHistory);

        int databaseSizeBeforeDelete = questionHistoryRepository.findAll().size();

        // Get the questionHistory
        restQuestionHistoryMockMvc.perform(delete("/api/question-histories/{id}", questionHistory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<QuestionHistory> questionHistoryList = questionHistoryRepository.findAll();
        assertThat(questionHistoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the QuestionHistory in Elasticsearch
        verify(mockQuestionHistorySearchRepository, times(1)).deleteById(questionHistory.getId());
    }

    @Test
    @Transactional
    public void searchQuestionHistory() throws Exception {
        // Initialize the database
        questionHistoryService.save(questionHistory);
        when(mockQuestionHistorySearchRepository.search(queryStringQuery("id:" + questionHistory.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(questionHistory), PageRequest.of(0, 1), 1));
        // Search the questionHistory
        restQuestionHistoryMockMvc.perform(get("/api/_search/question-histories?query=id:" + questionHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(questionHistory.getId())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].correct").value(hasItem(DEFAULT_CORRECT.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuestionHistory.class);
        QuestionHistory questionHistory1 = new QuestionHistory();
        questionHistory1.setId(1L);
        QuestionHistory questionHistory2 = new QuestionHistory();
        questionHistory2.setId(questionHistory1.getId());
        assertThat(questionHistory1).isEqualTo(questionHistory2);
        questionHistory2.setId(2L);
        assertThat(questionHistory1).isNotEqualTo(questionHistory2);
        questionHistory1.setId(null);
        assertThat(questionHistory1).isNotEqualTo(questionHistory2);
    }
}
