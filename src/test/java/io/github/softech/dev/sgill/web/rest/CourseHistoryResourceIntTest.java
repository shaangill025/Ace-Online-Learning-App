package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.CourseHistory;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.Course;
import io.github.softech.dev.sgill.repository.CourseHistoryRepository;
import io.github.softech.dev.sgill.repository.search.CourseHistorySearchRepository;
import io.github.softech.dev.sgill.service.CourseHistoryService;
import io.github.softech.dev.sgill.service.CourseService;
import io.github.softech.dev.sgill.service.CustomerService;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.CourseHistoryCriteria;
import io.github.softech.dev.sgill.service.CourseHistoryQueryService;

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
 * Test class for the CourseHistoryResource REST controller.
 *
 * @see CourseHistoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class CourseHistoryResourceIntTest {

    private static final Instant DEFAULT_STARTDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LASTACTIVEDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LASTACTIVEDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_ISACTIVE = false;
    private static final Boolean UPDATED_ISACTIVE = true;

    private static final Boolean DEFAULT_ISCOMPLETED = false;
    private static final Boolean UPDATED_ISCOMPLETED = true;

    private static final Boolean DEFAULT_ACCESS = false;
    private static final Boolean UPDATED_ACCESS = true;

    @Autowired
    private CourseHistoryRepository courseHistoryRepository;

    

    @Autowired
    private CourseHistoryService courseHistoryService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.CourseHistorySearchRepositoryMockConfiguration
     */
    @Autowired
    private CourseHistorySearchRepository mockCourseHistorySearchRepository;

    @Autowired
    private CourseHistoryQueryService courseHistoryQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCourseHistoryMockMvc;

    private CourseHistory courseHistory;

    private CustomerService customerService;

    private CourseService courseService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CourseHistoryResource courseHistoryResource = new CourseHistoryResource(courseHistoryService, courseHistoryQueryService, courseHistoryRepository,
            customerService, courseService);
        this.restCourseHistoryMockMvc = MockMvcBuilders.standaloneSetup(courseHistoryResource)
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
    public static CourseHistory createEntity(EntityManager em) {
        CourseHistory courseHistory = new CourseHistory()
            .startdate(DEFAULT_STARTDATE)
            .lastactivedate(DEFAULT_LASTACTIVEDATE)
            .isactive(DEFAULT_ISACTIVE)
            .iscompleted(DEFAULT_ISCOMPLETED)
            .access(DEFAULT_ACCESS);
        return courseHistory;
    }

    @Before
    public void initTest() {
        courseHistory = createEntity(em);
    }

    @Test
    @Transactional
    public void createCourseHistory() throws Exception {
        int databaseSizeBeforeCreate = courseHistoryRepository.findAll().size();

        // Create the CourseHistory
        restCourseHistoryMockMvc.perform(post("/api/course-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(courseHistory)))
            .andExpect(status().isCreated());

        // Validate the CourseHistory in the database
        List<CourseHistory> courseHistoryList = courseHistoryRepository.findAll();
        assertThat(courseHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        CourseHistory testCourseHistory = courseHistoryList.get(courseHistoryList.size() - 1);
        assertThat(testCourseHistory.getStartdate()).isEqualTo(DEFAULT_STARTDATE);
        assertThat(testCourseHistory.getLastactivedate()).isEqualTo(DEFAULT_LASTACTIVEDATE);
        assertThat(testCourseHistory.isIsactive()).isEqualTo(DEFAULT_ISACTIVE);
        assertThat(testCourseHistory.isIscompleted()).isEqualTo(DEFAULT_ISCOMPLETED);
        assertThat(testCourseHistory.isAccess()).isEqualTo(DEFAULT_ACCESS);

        // Validate the CourseHistory in Elasticsearch
        verify(mockCourseHistorySearchRepository, times(1)).save(testCourseHistory);
    }

    @Test
    @Transactional
    public void createCourseHistoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = courseHistoryRepository.findAll().size();

        // Create the CourseHistory with an existing ID
        courseHistory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseHistoryMockMvc.perform(post("/api/course-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(courseHistory)))
            .andExpect(status().isBadRequest());

        // Validate the CourseHistory in the database
        List<CourseHistory> courseHistoryList = courseHistoryRepository.findAll();
        assertThat(courseHistoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the CourseHistory in Elasticsearch
        verify(mockCourseHistorySearchRepository, times(0)).save(courseHistory);
    }

    @Test
    @Transactional
    public void getAllCourseHistories() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList
        restCourseHistoryMockMvc.perform(get("/api/course-histories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseHistory.getId())))
            .andExpect(jsonPath("$.[*].startdate").value(hasItem(DEFAULT_STARTDATE.toString())))
            .andExpect(jsonPath("$.[*].lastactivedate").value(hasItem(DEFAULT_LASTACTIVEDATE.toString())))
            .andExpect(jsonPath("$.[*].isactive").value(hasItem(DEFAULT_ISACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].iscompleted").value(hasItem(DEFAULT_ISCOMPLETED.booleanValue())))
            .andExpect(jsonPath("$.[*].access").value(hasItem(DEFAULT_ACCESS.booleanValue())));
    }
    

    @Test
    @Transactional
    public void getCourseHistory() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get the courseHistory
        restCourseHistoryMockMvc.perform(get("/api/course-histories/{id}", courseHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(courseHistory.getId()))
            .andExpect(jsonPath("$.startdate").value(DEFAULT_STARTDATE.toString()))
            .andExpect(jsonPath("$.lastactivedate").value(DEFAULT_LASTACTIVEDATE.toString()))
            .andExpect(jsonPath("$.isactive").value(DEFAULT_ISACTIVE.booleanValue()))
            .andExpect(jsonPath("$.iscompleted").value(DEFAULT_ISCOMPLETED.booleanValue()))
            .andExpect(jsonPath("$.access").value(DEFAULT_ACCESS.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByStartdateIsEqualToSomething() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where startdate equals to DEFAULT_STARTDATE
        defaultCourseHistoryShouldBeFound("startdate.equals=" + DEFAULT_STARTDATE);

        // Get all the courseHistoryList where startdate equals to UPDATED_STARTDATE
        defaultCourseHistoryShouldNotBeFound("startdate.equals=" + UPDATED_STARTDATE);
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByStartdateIsInShouldWork() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where startdate in DEFAULT_STARTDATE or UPDATED_STARTDATE
        defaultCourseHistoryShouldBeFound("startdate.in=" + DEFAULT_STARTDATE + "," + UPDATED_STARTDATE);

        // Get all the courseHistoryList where startdate equals to UPDATED_STARTDATE
        defaultCourseHistoryShouldNotBeFound("startdate.in=" + UPDATED_STARTDATE);
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByStartdateIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where startdate is not null
        defaultCourseHistoryShouldBeFound("startdate.specified=true");

        // Get all the courseHistoryList where startdate is null
        defaultCourseHistoryShouldNotBeFound("startdate.specified=false");
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByLastactivedateIsEqualToSomething() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where lastactivedate equals to DEFAULT_LASTACTIVEDATE
        defaultCourseHistoryShouldBeFound("lastactivedate.equals=" + DEFAULT_LASTACTIVEDATE);

        // Get all the courseHistoryList where lastactivedate equals to UPDATED_LASTACTIVEDATE
        defaultCourseHistoryShouldNotBeFound("lastactivedate.equals=" + UPDATED_LASTACTIVEDATE);
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByLastactivedateIsInShouldWork() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where lastactivedate in DEFAULT_LASTACTIVEDATE or UPDATED_LASTACTIVEDATE
        defaultCourseHistoryShouldBeFound("lastactivedate.in=" + DEFAULT_LASTACTIVEDATE + "," + UPDATED_LASTACTIVEDATE);

        // Get all the courseHistoryList where lastactivedate equals to UPDATED_LASTACTIVEDATE
        defaultCourseHistoryShouldNotBeFound("lastactivedate.in=" + UPDATED_LASTACTIVEDATE);
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByLastactivedateIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where lastactivedate is not null
        defaultCourseHistoryShouldBeFound("lastactivedate.specified=true");

        // Get all the courseHistoryList where lastactivedate is null
        defaultCourseHistoryShouldNotBeFound("lastactivedate.specified=false");
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByIsactiveIsEqualToSomething() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where isactive equals to DEFAULT_ISACTIVE
        defaultCourseHistoryShouldBeFound("isactive.equals=" + DEFAULT_ISACTIVE);

        // Get all the courseHistoryList where isactive equals to UPDATED_ISACTIVE
        defaultCourseHistoryShouldNotBeFound("isactive.equals=" + UPDATED_ISACTIVE);
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByIsactiveIsInShouldWork() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where isactive in DEFAULT_ISACTIVE or UPDATED_ISACTIVE
        defaultCourseHistoryShouldBeFound("isactive.in=" + DEFAULT_ISACTIVE + "," + UPDATED_ISACTIVE);

        // Get all the courseHistoryList where isactive equals to UPDATED_ISACTIVE
        defaultCourseHistoryShouldNotBeFound("isactive.in=" + UPDATED_ISACTIVE);
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByIsactiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where isactive is not null
        defaultCourseHistoryShouldBeFound("isactive.specified=true");

        // Get all the courseHistoryList where isactive is null
        defaultCourseHistoryShouldNotBeFound("isactive.specified=false");
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByIscompletedIsEqualToSomething() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where iscompleted equals to DEFAULT_ISCOMPLETED
        defaultCourseHistoryShouldBeFound("iscompleted.equals=" + DEFAULT_ISCOMPLETED);

        // Get all the courseHistoryList where iscompleted equals to UPDATED_ISCOMPLETED
        defaultCourseHistoryShouldNotBeFound("iscompleted.equals=" + UPDATED_ISCOMPLETED);
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByIscompletedIsInShouldWork() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where iscompleted in DEFAULT_ISCOMPLETED or UPDATED_ISCOMPLETED
        defaultCourseHistoryShouldBeFound("iscompleted.in=" + DEFAULT_ISCOMPLETED + "," + UPDATED_ISCOMPLETED);

        // Get all the courseHistoryList where iscompleted equals to UPDATED_ISCOMPLETED
        defaultCourseHistoryShouldNotBeFound("iscompleted.in=" + UPDATED_ISCOMPLETED);
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByIscompletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where iscompleted is not null
        defaultCourseHistoryShouldBeFound("iscompleted.specified=true");

        // Get all the courseHistoryList where iscompleted is null
        defaultCourseHistoryShouldNotBeFound("iscompleted.specified=false");
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByAccessIsEqualToSomething() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where access equals to DEFAULT_ACCESS
        defaultCourseHistoryShouldBeFound("access.equals=" + DEFAULT_ACCESS);

        // Get all the courseHistoryList where access equals to UPDATED_ACCESS
        defaultCourseHistoryShouldNotBeFound("access.equals=" + UPDATED_ACCESS);
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByAccessIsInShouldWork() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where access in DEFAULT_ACCESS or UPDATED_ACCESS
        defaultCourseHistoryShouldBeFound("access.in=" + DEFAULT_ACCESS + "," + UPDATED_ACCESS);

        // Get all the courseHistoryList where access equals to UPDATED_ACCESS
        defaultCourseHistoryShouldNotBeFound("access.in=" + UPDATED_ACCESS);
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByAccessIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList where access is not null
        defaultCourseHistoryShouldBeFound("access.specified=true");

        // Get all the courseHistoryList where access is null
        defaultCourseHistoryShouldNotBeFound("access.specified=false");
    }

    @Test
    @Transactional
    public void getAllCourseHistoriesByCustomerIsEqualToSomething() throws Exception {
        // Initialize the database
        Customer customer = CustomerResourceIntTest.createEntity(em);
        em.persist(customer);
        em.flush();
        courseHistory.setCustomer(customer);
        courseHistoryRepository.saveAndFlush(courseHistory);
        Long customerId = customer.getId();

        // Get all the courseHistoryList where customer equals to customerId
        defaultCourseHistoryShouldBeFound("customerId.equals=" + customerId);

        // Get all the courseHistoryList where customer equals to customerId + 1
        defaultCourseHistoryShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }


    @Test
    @Transactional
    public void getAllCourseHistoriesByCourseIsEqualToSomething() throws Exception {
        // Initialize the database
        Course course = CourseResourceIntTest.createEntity(em);
        em.persist(course);
        em.flush();
        courseHistory.setCourse(course);
        courseHistoryRepository.saveAndFlush(courseHistory);
        Long courseId = course.getId();

        // Get all the courseHistoryList where course equals to courseId
        defaultCourseHistoryShouldBeFound("courseId.equals=" + courseId);

        // Get all the courseHistoryList where course equals to courseId + 1
        defaultCourseHistoryShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultCourseHistoryShouldBeFound(String filter) throws Exception {
        restCourseHistoryMockMvc.perform(get("/api/course-histories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseHistory.getId())))
            .andExpect(jsonPath("$.[*].startdate").value(hasItem(DEFAULT_STARTDATE.toString())))
            .andExpect(jsonPath("$.[*].lastactivedate").value(hasItem(DEFAULT_LASTACTIVEDATE.toString())))
            .andExpect(jsonPath("$.[*].isactive").value(hasItem(DEFAULT_ISACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].iscompleted").value(hasItem(DEFAULT_ISCOMPLETED.booleanValue())))
            .andExpect(jsonPath("$.[*].access").value(hasItem(DEFAULT_ACCESS.booleanValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultCourseHistoryShouldNotBeFound(String filter) throws Exception {
        restCourseHistoryMockMvc.perform(get("/api/course-histories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingCourseHistory() throws Exception {
        // Get the courseHistory
        restCourseHistoryMockMvc.perform(get("/api/course-histories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCourseHistory() throws Exception {
        // Initialize the database
        courseHistoryService.save(courseHistory);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockCourseHistorySearchRepository);

        int databaseSizeBeforeUpdate = courseHistoryRepository.findAll().size();

        // Update the courseHistory
        CourseHistory updatedCourseHistory = courseHistoryRepository.findById(courseHistory.getId()).get();
        // Disconnect from session so that the updates on updatedCourseHistory are not directly saved in db
        em.detach(updatedCourseHistory);
        updatedCourseHistory
            .startdate(UPDATED_STARTDATE)
            .lastactivedate(UPDATED_LASTACTIVEDATE)
            .isactive(UPDATED_ISACTIVE)
            .iscompleted(UPDATED_ISCOMPLETED)
            .access(UPDATED_ACCESS);

        restCourseHistoryMockMvc.perform(put("/api/course-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCourseHistory)))
            .andExpect(status().isOk());

        // Validate the CourseHistory in the database
        List<CourseHistory> courseHistoryList = courseHistoryRepository.findAll();
        assertThat(courseHistoryList).hasSize(databaseSizeBeforeUpdate);
        CourseHistory testCourseHistory = courseHistoryList.get(courseHistoryList.size() - 1);
        assertThat(testCourseHistory.getStartdate()).isEqualTo(UPDATED_STARTDATE);
        assertThat(testCourseHistory.getLastactivedate()).isEqualTo(UPDATED_LASTACTIVEDATE);
        assertThat(testCourseHistory.isIsactive()).isEqualTo(UPDATED_ISACTIVE);
        assertThat(testCourseHistory.isIscompleted()).isEqualTo(UPDATED_ISCOMPLETED);
        assertThat(testCourseHistory.isAccess()).isEqualTo(UPDATED_ACCESS);

        // Validate the CourseHistory in Elasticsearch
        verify(mockCourseHistorySearchRepository, times(1)).save(testCourseHistory);
    }

    @Test
    @Transactional
    public void updateNonExistingCourseHistory() throws Exception {
        int databaseSizeBeforeUpdate = courseHistoryRepository.findAll().size();

        // Create the CourseHistory

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restCourseHistoryMockMvc.perform(put("/api/course-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(courseHistory)))
            .andExpect(status().isBadRequest());

        // Validate the CourseHistory in the database
        List<CourseHistory> courseHistoryList = courseHistoryRepository.findAll();
        assertThat(courseHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CourseHistory in Elasticsearch
        verify(mockCourseHistorySearchRepository, times(0)).save(courseHistory);
    }

    @Test
    @Transactional
    public void deleteCourseHistory() throws Exception {
        // Initialize the database
        courseHistoryService.save(courseHistory);

        int databaseSizeBeforeDelete = courseHistoryRepository.findAll().size();

        // Get the courseHistory
        restCourseHistoryMockMvc.perform(delete("/api/course-histories/{id}", courseHistory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CourseHistory> courseHistoryList = courseHistoryRepository.findAll();
        assertThat(courseHistoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the CourseHistory in Elasticsearch
        verify(mockCourseHistorySearchRepository, times(1)).deleteById(courseHistory.getId());
    }

    @Test
    @Transactional
    public void searchCourseHistory() throws Exception {
        // Initialize the database
        courseHistoryService.save(courseHistory);
        when(mockCourseHistorySearchRepository.search(queryStringQuery("id:" + courseHistory.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(courseHistory), PageRequest.of(0, 1), 1));
        // Search the courseHistory
        restCourseHistoryMockMvc.perform(get("/api/_search/course-histories?query=id:" + courseHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseHistory.getId())))
            .andExpect(jsonPath("$.[*].startdate").value(hasItem(DEFAULT_STARTDATE.toString())))
            .andExpect(jsonPath("$.[*].lastactivedate").value(hasItem(DEFAULT_LASTACTIVEDATE.toString())))
            .andExpect(jsonPath("$.[*].isactive").value(hasItem(DEFAULT_ISACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].iscompleted").value(hasItem(DEFAULT_ISCOMPLETED.booleanValue())))
            .andExpect(jsonPath("$.[*].access").value(hasItem(DEFAULT_ACCESS.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseHistory.class);
        CourseHistory courseHistory1 = new CourseHistory();
        courseHistory1.setId(1L);
        CourseHistory courseHistory2 = new CourseHistory();
        courseHistory2.setId(courseHistory1.getId());
        assertThat(courseHistory1).isEqualTo(courseHistory2);
        courseHistory2.setId(2L);
        assertThat(courseHistory1).isNotEqualTo(courseHistory2);
        courseHistory1.setId(null);
        assertThat(courseHistory1).isNotEqualTo(courseHistory2);
    }
}
