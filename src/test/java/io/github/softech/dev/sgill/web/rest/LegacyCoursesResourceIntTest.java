package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.LegacyCourses;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.Course;
import io.github.softech.dev.sgill.repository.LegacyCoursesRepository;
import io.github.softech.dev.sgill.repository.search.LegacyCoursesSearchRepository;
import io.github.softech.dev.sgill.service.LegacyCoursesService;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.LegacyCoursesCriteria;
import io.github.softech.dev.sgill.service.LegacyCoursesQueryService;

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
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

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
 * Test class for the LegacyCoursesResource REST controller.
 *
 * @see LegacyCoursesResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class LegacyCoursesResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_AMOUNT = 1D;
    private static final Double UPDATED_AMOUNT = 2D;

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_PROVINCE = "AAAAAAAAAA";
    private static final String UPDATED_PROVINCE = "BBBBBBBBBB";

    @Autowired
    private LegacyCoursesRepository legacyCoursesRepository;

    @Autowired
    private LegacyCoursesService legacyCoursesService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.LegacyCoursesSearchRepositoryMockConfiguration
     */
    @Autowired
    private LegacyCoursesSearchRepository mockLegacyCoursesSearchRepository;

    @Autowired
    private LegacyCoursesQueryService legacyCoursesQueryService;

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

    private MockMvc restLegacyCoursesMockMvc;

    private LegacyCourses legacyCourses;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LegacyCoursesResource legacyCoursesResource = new LegacyCoursesResource(legacyCoursesService, legacyCoursesQueryService);
        this.restLegacyCoursesMockMvc = MockMvcBuilders.standaloneSetup(legacyCoursesResource)
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
    public static LegacyCourses createEntity(EntityManager em) {
        LegacyCourses legacyCourses = new LegacyCourses()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .amount(DEFAULT_AMOUNT)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .province(DEFAULT_PROVINCE);
        return legacyCourses;
    }

    @Before
    public void initTest() {
        legacyCourses = createEntity(em);
    }

    @Test
    @Transactional
    public void createLegacyCourses() throws Exception {
        int databaseSizeBeforeCreate = legacyCoursesRepository.findAll().size();

        // Create the LegacyCourses
        restLegacyCoursesMockMvc.perform(post("/api/legacy-courses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(legacyCourses)))
            .andExpect(status().isCreated());

        // Validate the LegacyCourses in the database
        List<LegacyCourses> legacyCoursesList = legacyCoursesRepository.findAll();
        assertThat(legacyCoursesList).hasSize(databaseSizeBeforeCreate + 1);
        LegacyCourses testLegacyCourses = legacyCoursesList.get(legacyCoursesList.size() - 1);
        assertThat(testLegacyCourses.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testLegacyCourses.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testLegacyCourses.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testLegacyCourses.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testLegacyCourses.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testLegacyCourses.getProvince()).isEqualTo(DEFAULT_PROVINCE);

        // Validate the LegacyCourses in Elasticsearch
        verify(mockLegacyCoursesSearchRepository, times(1)).save(testLegacyCourses);
    }

    @Test
    @Transactional
    public void createLegacyCoursesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = legacyCoursesRepository.findAll().size();

        // Create the LegacyCourses with an existing ID
        legacyCourses.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLegacyCoursesMockMvc.perform(post("/api/legacy-courses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(legacyCourses)))
            .andExpect(status().isBadRequest());

        // Validate the LegacyCourses in the database
        List<LegacyCourses> legacyCoursesList = legacyCoursesRepository.findAll();
        assertThat(legacyCoursesList).hasSize(databaseSizeBeforeCreate);

        // Validate the LegacyCourses in Elasticsearch
        verify(mockLegacyCoursesSearchRepository, times(0)).save(legacyCourses);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = legacyCoursesRepository.findAll().size();
        // set the field null
        legacyCourses.setTitle(null);

        // Create the LegacyCourses, which fails.

        restLegacyCoursesMockMvc.perform(post("/api/legacy-courses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(legacyCourses)))
            .andExpect(status().isBadRequest());

        List<LegacyCourses> legacyCoursesList = legacyCoursesRepository.findAll();
        assertThat(legacyCoursesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = legacyCoursesRepository.findAll().size();
        // set the field null
        legacyCourses.setDescription(null);

        // Create the LegacyCourses, which fails.

        restLegacyCoursesMockMvc.perform(post("/api/legacy-courses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(legacyCourses)))
            .andExpect(status().isBadRequest());

        List<LegacyCourses> legacyCoursesList = legacyCoursesRepository.findAll();
        assertThat(legacyCoursesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkProvinceIsRequired() throws Exception {
        int databaseSizeBeforeTest = legacyCoursesRepository.findAll().size();
        // set the field null
        legacyCourses.setProvince(null);

        // Create the LegacyCourses, which fails.

        restLegacyCoursesMockMvc.perform(post("/api/legacy-courses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(legacyCourses)))
            .andExpect(status().isBadRequest());

        List<LegacyCourses> legacyCoursesList = legacyCoursesRepository.findAll();
        assertThat(legacyCoursesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLegacyCourses() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get all the legacyCoursesList
        restLegacyCoursesMockMvc.perform(get("/api/legacy-courses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(legacyCourses.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].province").value(hasItem(DEFAULT_PROVINCE)));
    }
    
    @Test
    @Transactional
    public void getLegacyCourses() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get the legacyCourses
        restLegacyCoursesMockMvc.perform(get("/api/legacy-courses/{id}", legacyCourses.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(legacyCourses.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.province").value(DEFAULT_PROVINCE));
    }

    @Test
    @Transactional
    public void getAllLegacyCoursesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get all the legacyCoursesList where title equals to DEFAULT_TITLE
        defaultLegacyCoursesShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the legacyCoursesList where title equals to UPDATED_TITLE
        defaultLegacyCoursesShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllLegacyCoursesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get all the legacyCoursesList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultLegacyCoursesShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the legacyCoursesList where title equals to UPDATED_TITLE
        defaultLegacyCoursesShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllLegacyCoursesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get all the legacyCoursesList where title is not null
        defaultLegacyCoursesShouldBeFound("title.specified=true");

        // Get all the legacyCoursesList where title is null
        defaultLegacyCoursesShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    public void getAllLegacyCoursesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get all the legacyCoursesList where description equals to DEFAULT_DESCRIPTION
        defaultLegacyCoursesShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the legacyCoursesList where description equals to UPDATED_DESCRIPTION
        defaultLegacyCoursesShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllLegacyCoursesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get all the legacyCoursesList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultLegacyCoursesShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the legacyCoursesList where description equals to UPDATED_DESCRIPTION
        defaultLegacyCoursesShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllLegacyCoursesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get all the legacyCoursesList where description is not null
        defaultLegacyCoursesShouldBeFound("description.specified=true");

        // Get all the legacyCoursesList where description is null
        defaultLegacyCoursesShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllLegacyCoursesByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get all the legacyCoursesList where amount equals to DEFAULT_AMOUNT
        defaultLegacyCoursesShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the legacyCoursesList where amount equals to UPDATED_AMOUNT
        defaultLegacyCoursesShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllLegacyCoursesByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get all the legacyCoursesList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultLegacyCoursesShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the legacyCoursesList where amount equals to UPDATED_AMOUNT
        defaultLegacyCoursesShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllLegacyCoursesByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get all the legacyCoursesList where amount is not null
        defaultLegacyCoursesShouldBeFound("amount.specified=true");

        // Get all the legacyCoursesList where amount is null
        defaultLegacyCoursesShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    public void getAllLegacyCoursesByProvinceIsEqualToSomething() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get all the legacyCoursesList where province equals to DEFAULT_PROVINCE
        defaultLegacyCoursesShouldBeFound("province.equals=" + DEFAULT_PROVINCE);

        // Get all the legacyCoursesList where province equals to UPDATED_PROVINCE
        defaultLegacyCoursesShouldNotBeFound("province.equals=" + UPDATED_PROVINCE);
    }

    @Test
    @Transactional
    public void getAllLegacyCoursesByProvinceIsInShouldWork() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get all the legacyCoursesList where province in DEFAULT_PROVINCE or UPDATED_PROVINCE
        defaultLegacyCoursesShouldBeFound("province.in=" + DEFAULT_PROVINCE + "," + UPDATED_PROVINCE);

        // Get all the legacyCoursesList where province equals to UPDATED_PROVINCE
        defaultLegacyCoursesShouldNotBeFound("province.in=" + UPDATED_PROVINCE);
    }

    @Test
    @Transactional
    public void getAllLegacyCoursesByProvinceIsNullOrNotNull() throws Exception {
        // Initialize the database
        legacyCoursesRepository.saveAndFlush(legacyCourses);

        // Get all the legacyCoursesList where province is not null
        defaultLegacyCoursesShouldBeFound("province.specified=true");

        // Get all the legacyCoursesList where province is null
        defaultLegacyCoursesShouldNotBeFound("province.specified=false");
    }

    @Test
    @Transactional
    public void getAllLegacyCoursesByCustomerIsEqualToSomething() throws Exception {
        // Initialize the database
        Customer customer = CustomerResourceIntTest.createEntity(em);
        em.persist(customer);
        em.flush();
        legacyCourses.setCustomer(customer);
        legacyCoursesRepository.saveAndFlush(legacyCourses);
        Long customerId = customer.getId();

        // Get all the legacyCoursesList where customer equals to customerId
        defaultLegacyCoursesShouldBeFound("customerId.equals=" + customerId);

        // Get all the legacyCoursesList where customer equals to customerId + 1
        defaultLegacyCoursesShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }


    @Test
    @Transactional
    public void getAllLegacyCoursesByCourseIsEqualToSomething() throws Exception {
        // Initialize the database
        Course course = CourseResourceIntTest.createEntity(em);
        em.persist(course);
        em.flush();
        legacyCourses.setCourse(course);
        legacyCoursesRepository.saveAndFlush(legacyCourses);
        Long courseId = course.getId();

        // Get all the legacyCoursesList where course equals to courseId
        defaultLegacyCoursesShouldBeFound("courseId.equals=" + courseId);

        // Get all the legacyCoursesList where course equals to courseId + 1
        defaultLegacyCoursesShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultLegacyCoursesShouldBeFound(String filter) throws Exception {
        restLegacyCoursesMockMvc.perform(get("/api/legacy-courses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(legacyCourses.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].province").value(hasItem(DEFAULT_PROVINCE)));

        // Check, that the count call also returns 1
        restLegacyCoursesMockMvc.perform(get("/api/legacy-courses/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultLegacyCoursesShouldNotBeFound(String filter) throws Exception {
        restLegacyCoursesMockMvc.perform(get("/api/legacy-courses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLegacyCoursesMockMvc.perform(get("/api/legacy-courses/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingLegacyCourses() throws Exception {
        // Get the legacyCourses
        restLegacyCoursesMockMvc.perform(get("/api/legacy-courses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLegacyCourses() throws Exception {
        // Initialize the database
        legacyCoursesService.save(legacyCourses);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockLegacyCoursesSearchRepository);

        int databaseSizeBeforeUpdate = legacyCoursesRepository.findAll().size();

        // Update the legacyCourses
        LegacyCourses updatedLegacyCourses = legacyCoursesRepository.findById(legacyCourses.getId()).get();
        // Disconnect from session so that the updates on updatedLegacyCourses are not directly saved in db
        em.detach(updatedLegacyCourses);
        updatedLegacyCourses
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .province(UPDATED_PROVINCE);

        restLegacyCoursesMockMvc.perform(put("/api/legacy-courses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLegacyCourses)))
            .andExpect(status().isOk());

        // Validate the LegacyCourses in the database
        List<LegacyCourses> legacyCoursesList = legacyCoursesRepository.findAll();
        assertThat(legacyCoursesList).hasSize(databaseSizeBeforeUpdate);
        LegacyCourses testLegacyCourses = legacyCoursesList.get(legacyCoursesList.size() - 1);
        assertThat(testLegacyCourses.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testLegacyCourses.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testLegacyCourses.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testLegacyCourses.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testLegacyCourses.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testLegacyCourses.getProvince()).isEqualTo(UPDATED_PROVINCE);

        // Validate the LegacyCourses in Elasticsearch
        verify(mockLegacyCoursesSearchRepository, times(1)).save(testLegacyCourses);
    }

    @Test
    @Transactional
    public void updateNonExistingLegacyCourses() throws Exception {
        int databaseSizeBeforeUpdate = legacyCoursesRepository.findAll().size();

        // Create the LegacyCourses

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLegacyCoursesMockMvc.perform(put("/api/legacy-courses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(legacyCourses)))
            .andExpect(status().isBadRequest());

        // Validate the LegacyCourses in the database
        List<LegacyCourses> legacyCoursesList = legacyCoursesRepository.findAll();
        assertThat(legacyCoursesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the LegacyCourses in Elasticsearch
        verify(mockLegacyCoursesSearchRepository, times(0)).save(legacyCourses);
    }

    @Test
    @Transactional
    public void deleteLegacyCourses() throws Exception {
        // Initialize the database
        legacyCoursesService.save(legacyCourses);

        int databaseSizeBeforeDelete = legacyCoursesRepository.findAll().size();

        // Get the legacyCourses
        restLegacyCoursesMockMvc.perform(delete("/api/legacy-courses/{id}", legacyCourses.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<LegacyCourses> legacyCoursesList = legacyCoursesRepository.findAll();
        assertThat(legacyCoursesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the LegacyCourses in Elasticsearch
        verify(mockLegacyCoursesSearchRepository, times(1)).deleteById(legacyCourses.getId());
    }

    @Test
    @Transactional
    public void searchLegacyCourses() throws Exception {
        // Initialize the database
        legacyCoursesService.save(legacyCourses);
        when(mockLegacyCoursesSearchRepository.search(queryStringQuery("id:" + legacyCourses.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(legacyCourses), PageRequest.of(0, 1), 1));
        // Search the legacyCourses
        restLegacyCoursesMockMvc.perform(get("/api/_search/legacy-courses?query=id:" + legacyCourses.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(legacyCourses.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].province").value(hasItem(DEFAULT_PROVINCE)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LegacyCourses.class);
        LegacyCourses legacyCourses1 = new LegacyCourses();
        legacyCourses1.setId(1L);
        LegacyCourses legacyCourses2 = new LegacyCourses();
        legacyCourses2.setId(legacyCourses1.getId());
        assertThat(legacyCourses1).isEqualTo(legacyCourses2);
        legacyCourses2.setId(2L);
        assertThat(legacyCourses1).isNotEqualTo(legacyCourses2);
        legacyCourses1.setId(null);
        assertThat(legacyCourses1).isNotEqualTo(legacyCourses2);
    }
}
