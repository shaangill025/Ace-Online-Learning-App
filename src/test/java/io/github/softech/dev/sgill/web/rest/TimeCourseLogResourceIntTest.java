package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.TimeCourseLog;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.CourseHistory;
import io.github.softech.dev.sgill.repository.TimeCourseLogRepository;
import io.github.softech.dev.sgill.repository.search.TimeCourseLogSearchRepository;
import io.github.softech.dev.sgill.service.*;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.TimeCourseLogCriteria;

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
import org.springframework.validation.Validator;

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
 * Test class for the TimeCourseLogResource REST controller.
 *
 * @see TimeCourseLogResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class TimeCourseLogResourceIntTest {

    private static final Long DEFAULT_TIMESPENT = 1L;
    private static final Long UPDATED_TIMESPENT = 2L;

    private static final Instant DEFAULT_RECORDDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RECORDDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private TimeCourseLogRepository timeCourseLogRepository;

    @Autowired
    private TimeCourseLogService timeCourseLogService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.TimeCourseLogSearchRepositoryMockConfiguration
     */
    @Autowired
    private TimeCourseLogSearchRepository mockTimeCourseLogSearchRepository;

    @Autowired
    private TimeCourseLogQueryService timeCourseLogQueryService;

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

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CourseHistoryService courseHistoryServiceService;

    private MockMvc restTimeCourseLogMockMvc;

    private TimeCourseLog timeCourseLog;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TimeCourseLogResource timeCourseLogResource = new TimeCourseLogResource(timeCourseLogService, timeCourseLogQueryService, timeCourseLogRepository, customerService, courseHistoryServiceService);
        this.restTimeCourseLogMockMvc = MockMvcBuilders.standaloneSetup(timeCourseLogResource)
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
    public static TimeCourseLog createEntity(EntityManager em) {
        TimeCourseLog timeCourseLog = new TimeCourseLog()
            .timespent(DEFAULT_TIMESPENT)
            .recorddate(DEFAULT_RECORDDATE);
        // Add required entity
        CourseHistory courseHistory = CourseHistoryResourceIntTest.createEntity(em);
        em.persist(courseHistory);
        em.flush();
        timeCourseLog.setCourseHistory(courseHistory);
        return timeCourseLog;
    }

    @Before
    public void initTest() {
        timeCourseLog = createEntity(em);
    }

    @Test
    @Transactional
    public void createTimeCourseLog() throws Exception {
        int databaseSizeBeforeCreate = timeCourseLogRepository.findAll().size();

        // Create the TimeCourseLog
        restTimeCourseLogMockMvc.perform(post("/api/time-course-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(timeCourseLog)))
            .andExpect(status().isCreated());

        // Validate the TimeCourseLog in the database
        List<TimeCourseLog> timeCourseLogList = timeCourseLogRepository.findAll();
        assertThat(timeCourseLogList).hasSize(databaseSizeBeforeCreate + 1);
        TimeCourseLog testTimeCourseLog = timeCourseLogList.get(timeCourseLogList.size() - 1);
        assertThat(testTimeCourseLog.getTimespent()).isEqualTo(DEFAULT_TIMESPENT);
        assertThat(testTimeCourseLog.getRecorddate()).isEqualTo(DEFAULT_RECORDDATE);

        // Validate the TimeCourseLog in Elasticsearch
        verify(mockTimeCourseLogSearchRepository, times(1)).save(testTimeCourseLog);
    }

    @Test
    @Transactional
    public void createTimeCourseLogWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = timeCourseLogRepository.findAll().size();

        // Create the TimeCourseLog with an existing ID
        timeCourseLog.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimeCourseLogMockMvc.perform(post("/api/time-course-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(timeCourseLog)))
            .andExpect(status().isBadRequest());

        // Validate the TimeCourseLog in the database
        List<TimeCourseLog> timeCourseLogList = timeCourseLogRepository.findAll();
        assertThat(timeCourseLogList).hasSize(databaseSizeBeforeCreate);

        // Validate the TimeCourseLog in Elasticsearch
        verify(mockTimeCourseLogSearchRepository, times(0)).save(timeCourseLog);
    }

    @Test
    @Transactional
    public void getAllTimeCourseLogs() throws Exception {
        // Initialize the database
        timeCourseLogRepository.saveAndFlush(timeCourseLog);

        // Get all the timeCourseLogList
        restTimeCourseLogMockMvc.perform(get("/api/time-course-logs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeCourseLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].timespent").value(hasItem(DEFAULT_TIMESPENT.intValue())))
            .andExpect(jsonPath("$.[*].recorddate").value(hasItem(DEFAULT_RECORDDATE.toString())));
    }
    
    @Test
    @Transactional
    public void getTimeCourseLog() throws Exception {
        // Initialize the database
        timeCourseLogRepository.saveAndFlush(timeCourseLog);

        // Get the timeCourseLog
        restTimeCourseLogMockMvc.perform(get("/api/time-course-logs/{id}", timeCourseLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(timeCourseLog.getId().intValue()))
            .andExpect(jsonPath("$.timespent").value(DEFAULT_TIMESPENT.intValue()))
            .andExpect(jsonPath("$.recorddate").value(DEFAULT_RECORDDATE.toString()));
    }

    @Test
    @Transactional
    public void getAllTimeCourseLogsByTimespentIsEqualToSomething() throws Exception {
        // Initialize the database
        timeCourseLogRepository.saveAndFlush(timeCourseLog);

        // Get all the timeCourseLogList where timespent equals to DEFAULT_TIMESPENT
        defaultTimeCourseLogShouldBeFound("timespent.equals=" + DEFAULT_TIMESPENT);

        // Get all the timeCourseLogList where timespent equals to UPDATED_TIMESPENT
        defaultTimeCourseLogShouldNotBeFound("timespent.equals=" + UPDATED_TIMESPENT);
    }

    @Test
    @Transactional
    public void getAllTimeCourseLogsByTimespentIsInShouldWork() throws Exception {
        // Initialize the database
        timeCourseLogRepository.saveAndFlush(timeCourseLog);

        // Get all the timeCourseLogList where timespent in DEFAULT_TIMESPENT or UPDATED_TIMESPENT
        defaultTimeCourseLogShouldBeFound("timespent.in=" + DEFAULT_TIMESPENT + "," + UPDATED_TIMESPENT);

        // Get all the timeCourseLogList where timespent equals to UPDATED_TIMESPENT
        defaultTimeCourseLogShouldNotBeFound("timespent.in=" + UPDATED_TIMESPENT);
    }

    @Test
    @Transactional
    public void getAllTimeCourseLogsByTimespentIsNullOrNotNull() throws Exception {
        // Initialize the database
        timeCourseLogRepository.saveAndFlush(timeCourseLog);

        // Get all the timeCourseLogList where timespent is not null
        defaultTimeCourseLogShouldBeFound("timespent.specified=true");

        // Get all the timeCourseLogList where timespent is null
        defaultTimeCourseLogShouldNotBeFound("timespent.specified=false");
    }

    @Test
    @Transactional
    public void getAllTimeCourseLogsByTimespentIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        timeCourseLogRepository.saveAndFlush(timeCourseLog);

        // Get all the timeCourseLogList where timespent greater than or equals to DEFAULT_TIMESPENT
        defaultTimeCourseLogShouldBeFound("timespent.greaterOrEqualThan=" + DEFAULT_TIMESPENT);

        // Get all the timeCourseLogList where timespent greater than or equals to UPDATED_TIMESPENT
        defaultTimeCourseLogShouldNotBeFound("timespent.greaterOrEqualThan=" + UPDATED_TIMESPENT);
    }

    @Test
    @Transactional
    public void getAllTimeCourseLogsByTimespentIsLessThanSomething() throws Exception {
        // Initialize the database
        timeCourseLogRepository.saveAndFlush(timeCourseLog);

        // Get all the timeCourseLogList where timespent less than or equals to DEFAULT_TIMESPENT
        defaultTimeCourseLogShouldNotBeFound("timespent.lessThan=" + DEFAULT_TIMESPENT);

        // Get all the timeCourseLogList where timespent less than or equals to UPDATED_TIMESPENT
        defaultTimeCourseLogShouldBeFound("timespent.lessThan=" + UPDATED_TIMESPENT);
    }


    @Test
    @Transactional
    public void getAllTimeCourseLogsByRecorddateIsEqualToSomething() throws Exception {
        // Initialize the database
        timeCourseLogRepository.saveAndFlush(timeCourseLog);

        // Get all the timeCourseLogList where recorddate equals to DEFAULT_RECORDDATE
        defaultTimeCourseLogShouldBeFound("recorddate.equals=" + DEFAULT_RECORDDATE);

        // Get all the timeCourseLogList where recorddate equals to UPDATED_RECORDDATE
        defaultTimeCourseLogShouldNotBeFound("recorddate.equals=" + UPDATED_RECORDDATE);
    }

    @Test
    @Transactional
    public void getAllTimeCourseLogsByRecorddateIsInShouldWork() throws Exception {
        // Initialize the database
        timeCourseLogRepository.saveAndFlush(timeCourseLog);

        // Get all the timeCourseLogList where recorddate in DEFAULT_RECORDDATE or UPDATED_RECORDDATE
        defaultTimeCourseLogShouldBeFound("recorddate.in=" + DEFAULT_RECORDDATE + "," + UPDATED_RECORDDATE);

        // Get all the timeCourseLogList where recorddate equals to UPDATED_RECORDDATE
        defaultTimeCourseLogShouldNotBeFound("recorddate.in=" + UPDATED_RECORDDATE);
    }

    @Test
    @Transactional
    public void getAllTimeCourseLogsByRecorddateIsNullOrNotNull() throws Exception {
        // Initialize the database
        timeCourseLogRepository.saveAndFlush(timeCourseLog);

        // Get all the timeCourseLogList where recorddate is not null
        defaultTimeCourseLogShouldBeFound("recorddate.specified=true");

        // Get all the timeCourseLogList where recorddate is null
        defaultTimeCourseLogShouldNotBeFound("recorddate.specified=false");
    }

    @Test
    @Transactional
    public void getAllTimeCourseLogsByCustomerIsEqualToSomething() throws Exception {
        // Initialize the database
        Customer customer = CustomerResourceIntTest.createEntity(em);
        em.persist(customer);
        em.flush();
        timeCourseLog.setCustomer(customer);
        timeCourseLogRepository.saveAndFlush(timeCourseLog);
        Long customerId = customer.getId();

        // Get all the timeCourseLogList where customer equals to customerId
        defaultTimeCourseLogShouldBeFound("customerId.equals=" + customerId);

        // Get all the timeCourseLogList where customer equals to customerId + 1
        defaultTimeCourseLogShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }


    @Test
    @Transactional
    public void getAllTimeCourseLogsByCourseHistoryIsEqualToSomething() throws Exception {
        // Initialize the database
        CourseHistory courseHistory = CourseHistoryResourceIntTest.createEntity(em);
        em.persist(courseHistory);
        em.flush();
        timeCourseLog.setCourseHistory(courseHistory);
        timeCourseLogRepository.saveAndFlush(timeCourseLog);
        Long courseHistoryId = courseHistory.getId();

        // Get all the timeCourseLogList where courseHistory equals to courseHistoryId
        defaultTimeCourseLogShouldBeFound("courseHistoryId.equals=" + courseHistoryId);

        // Get all the timeCourseLogList where courseHistory equals to courseHistoryId + 1
        defaultTimeCourseLogShouldNotBeFound("courseHistoryId.equals=" + (courseHistoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultTimeCourseLogShouldBeFound(String filter) throws Exception {
        restTimeCourseLogMockMvc.perform(get("/api/time-course-logs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeCourseLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].timespent").value(hasItem(DEFAULT_TIMESPENT.intValue())))
            .andExpect(jsonPath("$.[*].recorddate").value(hasItem(DEFAULT_RECORDDATE.toString())));

        // Check, that the count call also returns 1
        restTimeCourseLogMockMvc.perform(get("/api/time-course-logs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultTimeCourseLogShouldNotBeFound(String filter) throws Exception {
        restTimeCourseLogMockMvc.perform(get("/api/time-course-logs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTimeCourseLogMockMvc.perform(get("/api/time-course-logs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingTimeCourseLog() throws Exception {
        // Get the timeCourseLog
        restTimeCourseLogMockMvc.perform(get("/api/time-course-logs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTimeCourseLog() throws Exception {
        // Initialize the database
        timeCourseLogService.save(timeCourseLog);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockTimeCourseLogSearchRepository);

        int databaseSizeBeforeUpdate = timeCourseLogRepository.findAll().size();

        // Update the timeCourseLog
        TimeCourseLog updatedTimeCourseLog = timeCourseLogRepository.findById(timeCourseLog.getId()).get();
        // Disconnect from session so that the updates on updatedTimeCourseLog are not directly saved in db
        em.detach(updatedTimeCourseLog);
        updatedTimeCourseLog
            .timespent(UPDATED_TIMESPENT)
            .recorddate(UPDATED_RECORDDATE);

        restTimeCourseLogMockMvc.perform(put("/api/time-course-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTimeCourseLog)))
            .andExpect(status().isOk());

        // Validate the TimeCourseLog in the database
        List<TimeCourseLog> timeCourseLogList = timeCourseLogRepository.findAll();
        assertThat(timeCourseLogList).hasSize(databaseSizeBeforeUpdate);
        TimeCourseLog testTimeCourseLog = timeCourseLogList.get(timeCourseLogList.size() - 1);
        assertThat(testTimeCourseLog.getTimespent()).isEqualTo(UPDATED_TIMESPENT);
        assertThat(testTimeCourseLog.getRecorddate()).isEqualTo(UPDATED_RECORDDATE);

        // Validate the TimeCourseLog in Elasticsearch
        verify(mockTimeCourseLogSearchRepository, times(1)).save(testTimeCourseLog);
    }

    @Test
    @Transactional
    public void updateNonExistingTimeCourseLog() throws Exception {
        int databaseSizeBeforeUpdate = timeCourseLogRepository.findAll().size();

        // Create the TimeCourseLog

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeCourseLogMockMvc.perform(put("/api/time-course-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(timeCourseLog)))
            .andExpect(status().isBadRequest());

        // Validate the TimeCourseLog in the database
        List<TimeCourseLog> timeCourseLogList = timeCourseLogRepository.findAll();
        assertThat(timeCourseLogList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TimeCourseLog in Elasticsearch
        verify(mockTimeCourseLogSearchRepository, times(0)).save(timeCourseLog);
    }

    @Test
    @Transactional
    public void deleteTimeCourseLog() throws Exception {
        // Initialize the database
        timeCourseLogService.save(timeCourseLog);

        int databaseSizeBeforeDelete = timeCourseLogRepository.findAll().size();

        // Get the timeCourseLog
        restTimeCourseLogMockMvc.perform(delete("/api/time-course-logs/{id}", timeCourseLog.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<TimeCourseLog> timeCourseLogList = timeCourseLogRepository.findAll();
        assertThat(timeCourseLogList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TimeCourseLog in Elasticsearch
        verify(mockTimeCourseLogSearchRepository, times(1)).deleteById(timeCourseLog.getId());
    }

    @Test
    @Transactional
    public void searchTimeCourseLog() throws Exception {
        // Initialize the database
        timeCourseLogService.save(timeCourseLog);
        when(mockTimeCourseLogSearchRepository.search(queryStringQuery("id:" + timeCourseLog.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(timeCourseLog), PageRequest.of(0, 1), 1));
        // Search the timeCourseLog
        restTimeCourseLogMockMvc.perform(get("/api/_search/time-course-logs?query=id:" + timeCourseLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeCourseLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].timespent").value(hasItem(DEFAULT_TIMESPENT.intValue())))
            .andExpect(jsonPath("$.[*].recorddate").value(hasItem(DEFAULT_RECORDDATE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeCourseLog.class);
        TimeCourseLog timeCourseLog1 = new TimeCourseLog();
        timeCourseLog1.setId(1L);
        TimeCourseLog timeCourseLog2 = new TimeCourseLog();
        timeCourseLog2.setId(timeCourseLog1.getId());
        assertThat(timeCourseLog1).isEqualTo(timeCourseLog2);
        timeCourseLog2.setId(2L);
        assertThat(timeCourseLog1).isNotEqualTo(timeCourseLog2);
        timeCourseLog1.setId(null);
        assertThat(timeCourseLog1).isNotEqualTo(timeCourseLog2);
    }
}
