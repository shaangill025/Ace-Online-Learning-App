package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.CourseCartBridge;
import io.github.softech.dev.sgill.domain.Cart;
import io.github.softech.dev.sgill.domain.Course;
import io.github.softech.dev.sgill.repository.CartRepository;
import io.github.softech.dev.sgill.repository.CourseCartBridgeRepository;
import io.github.softech.dev.sgill.repository.search.CourseCartBridgeSearchRepository;
import io.github.softech.dev.sgill.service.CartService;
import io.github.softech.dev.sgill.service.CourseCartBridgeService;
import io.github.softech.dev.sgill.service.CustomerService;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.CourseCartBridgeCriteria;
import io.github.softech.dev.sgill.service.CourseCartBridgeQueryService;

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
 * Test class for the CourseCartBridgeResource REST controller.
 *
 * @see CourseCartBridgeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class CourseCartBridgeResourceIntTest {

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private CourseCartBridgeRepository courseCartBridgeRepository;

    

    @Autowired
    private CourseCartBridgeService courseCartBridgeService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.CourseCartBridgeSearchRepositoryMockConfiguration
     */
    @Autowired
    private CourseCartBridgeSearchRepository mockCourseCartBridgeSearchRepository;

    @Autowired
    private CourseCartBridgeQueryService courseCartBridgeQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private CartService cartService;

    @Autowired
    private EntityManager em;

    private MockMvc restCourseCartBridgeMockMvc;

    private CourseCartBridge courseCartBridge;

    private CartRepository cartRepository;

    private CustomerService customerService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CourseCartBridgeResource courseCartBridgeResource = new CourseCartBridgeResource(courseCartBridgeService, courseCartBridgeQueryService, courseCartBridgeRepository, cartService,
            cartRepository, customerService);
        this.restCourseCartBridgeMockMvc = MockMvcBuilders.standaloneSetup(courseCartBridgeResource)
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
    public static CourseCartBridge createEntity(EntityManager em) {
        CourseCartBridge courseCartBridge = new CourseCartBridge()
            .timestamp(DEFAULT_TIMESTAMP);
        return courseCartBridge;
    }

    @Before
    public void initTest() {
        courseCartBridge = createEntity(em);
    }

    @Test
    @Transactional
    public void createCourseCartBridge() throws Exception {
        int databaseSizeBeforeCreate = courseCartBridgeRepository.findAll().size();

        // Create the CourseCartBridge
        restCourseCartBridgeMockMvc.perform(post("/api/course-cart-bridges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(courseCartBridge)))
            .andExpect(status().isCreated());

        // Validate the CourseCartBridge in the database
        List<CourseCartBridge> courseCartBridgeList = courseCartBridgeRepository.findAll();
        assertThat(courseCartBridgeList).hasSize(databaseSizeBeforeCreate + 1);
        CourseCartBridge testCourseCartBridge = courseCartBridgeList.get(courseCartBridgeList.size() - 1);
        assertThat(testCourseCartBridge.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);

        // Validate the CourseCartBridge in Elasticsearch
        verify(mockCourseCartBridgeSearchRepository, times(1)).save(testCourseCartBridge);
    }

    @Test
    @Transactional
    public void createCourseCartBridgeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = courseCartBridgeRepository.findAll().size();

        // Create the CourseCartBridge with an existing ID
        courseCartBridge.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseCartBridgeMockMvc.perform(post("/api/course-cart-bridges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(courseCartBridge)))
            .andExpect(status().isBadRequest());

        // Validate the CourseCartBridge in the database
        List<CourseCartBridge> courseCartBridgeList = courseCartBridgeRepository.findAll();
        assertThat(courseCartBridgeList).hasSize(databaseSizeBeforeCreate);

        // Validate the CourseCartBridge in Elasticsearch
        verify(mockCourseCartBridgeSearchRepository, times(0)).save(courseCartBridge);
    }

    @Test
    @Transactional
    public void getAllCourseCartBridges() throws Exception {
        // Initialize the database
        courseCartBridgeRepository.saveAndFlush(courseCartBridge);

        // Get all the courseCartBridgeList
        restCourseCartBridgeMockMvc.perform(get("/api/course-cart-bridges?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseCartBridge.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())));
    }
    

    @Test
    @Transactional
    public void getCourseCartBridge() throws Exception {
        // Initialize the database
        courseCartBridgeRepository.saveAndFlush(courseCartBridge);

        // Get the courseCartBridge
        restCourseCartBridgeMockMvc.perform(get("/api/course-cart-bridges/{id}", courseCartBridge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(courseCartBridge.getId().intValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()));
    }

    @Test
    @Transactional
    public void getAllCourseCartBridgesByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        courseCartBridgeRepository.saveAndFlush(courseCartBridge);

        // Get all the courseCartBridgeList where timestamp equals to DEFAULT_TIMESTAMP
        defaultCourseCartBridgeShouldBeFound("timestamp.equals=" + DEFAULT_TIMESTAMP);

        // Get all the courseCartBridgeList where timestamp equals to UPDATED_TIMESTAMP
        defaultCourseCartBridgeShouldNotBeFound("timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllCourseCartBridgesByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        courseCartBridgeRepository.saveAndFlush(courseCartBridge);

        // Get all the courseCartBridgeList where timestamp in DEFAULT_TIMESTAMP or UPDATED_TIMESTAMP
        defaultCourseCartBridgeShouldBeFound("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP);

        // Get all the courseCartBridgeList where timestamp equals to UPDATED_TIMESTAMP
        defaultCourseCartBridgeShouldNotBeFound("timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllCourseCartBridgesByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseCartBridgeRepository.saveAndFlush(courseCartBridge);

        // Get all the courseCartBridgeList where timestamp is not null
        defaultCourseCartBridgeShouldBeFound("timestamp.specified=true");

        // Get all the courseCartBridgeList where timestamp is null
        defaultCourseCartBridgeShouldNotBeFound("timestamp.specified=false");
    }

    @Test
    @Transactional
    public void getAllCourseCartBridgesByCartIsEqualToSomething() throws Exception {
        // Initialize the database
        Cart cart = CartResourceIntTest.createEntity(em);
        em.persist(cart);
        em.flush();
        courseCartBridge.setCart(cart);
        courseCartBridgeRepository.saveAndFlush(courseCartBridge);
        Long cartId = cart.getId();

        // Get all the courseCartBridgeList where cart equals to cartId
        defaultCourseCartBridgeShouldBeFound("cartId.equals=" + cartId);

        // Get all the courseCartBridgeList where cart equals to cartId + 1
        defaultCourseCartBridgeShouldNotBeFound("cartId.equals=" + (cartId + 1));
    }


    @Test
    @Transactional
    public void getAllCourseCartBridgesByCourseIsEqualToSomething() throws Exception {
        // Initialize the database
        Course course = CourseResourceIntTest.createEntity(em);
        em.persist(course);
        em.flush();
        courseCartBridge.setCourse(course);
        courseCartBridgeRepository.saveAndFlush(courseCartBridge);
        Long courseId = course.getId();

        // Get all the courseCartBridgeList where course equals to courseId
        defaultCourseCartBridgeShouldBeFound("courseId.equals=" + courseId);

        // Get all the courseCartBridgeList where course equals to courseId + 1
        defaultCourseCartBridgeShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultCourseCartBridgeShouldBeFound(String filter) throws Exception {
        restCourseCartBridgeMockMvc.perform(get("/api/course-cart-bridges?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseCartBridge.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultCourseCartBridgeShouldNotBeFound(String filter) throws Exception {
        restCourseCartBridgeMockMvc.perform(get("/api/course-cart-bridges?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingCourseCartBridge() throws Exception {
        // Get the courseCartBridge
        restCourseCartBridgeMockMvc.perform(get("/api/course-cart-bridges/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCourseCartBridge() throws Exception {
        // Initialize the database
        courseCartBridgeService.save(courseCartBridge);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockCourseCartBridgeSearchRepository);

        int databaseSizeBeforeUpdate = courseCartBridgeRepository.findAll().size();

        // Update the courseCartBridge
        CourseCartBridge updatedCourseCartBridge = courseCartBridgeRepository.findById(courseCartBridge.getId()).get();
        // Disconnect from session so that the updates on updatedCourseCartBridge are not directly saved in db
        em.detach(updatedCourseCartBridge);
        updatedCourseCartBridge
            .timestamp(UPDATED_TIMESTAMP);

        restCourseCartBridgeMockMvc.perform(put("/api/course-cart-bridges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCourseCartBridge)))
            .andExpect(status().isOk());

        // Validate the CourseCartBridge in the database
        List<CourseCartBridge> courseCartBridgeList = courseCartBridgeRepository.findAll();
        assertThat(courseCartBridgeList).hasSize(databaseSizeBeforeUpdate);
        CourseCartBridge testCourseCartBridge = courseCartBridgeList.get(courseCartBridgeList.size() - 1);
        assertThat(testCourseCartBridge.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);

        // Validate the CourseCartBridge in Elasticsearch
        verify(mockCourseCartBridgeSearchRepository, times(1)).save(testCourseCartBridge);
    }

    @Test
    @Transactional
    public void updateNonExistingCourseCartBridge() throws Exception {
        int databaseSizeBeforeUpdate = courseCartBridgeRepository.findAll().size();

        // Create the CourseCartBridge

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restCourseCartBridgeMockMvc.perform(put("/api/course-cart-bridges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(courseCartBridge)))
            .andExpect(status().isBadRequest());

        // Validate the CourseCartBridge in the database
        List<CourseCartBridge> courseCartBridgeList = courseCartBridgeRepository.findAll();
        assertThat(courseCartBridgeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CourseCartBridge in Elasticsearch
        verify(mockCourseCartBridgeSearchRepository, times(0)).save(courseCartBridge);
    }

    @Test
    @Transactional
    public void deleteCourseCartBridge() throws Exception {
        // Initialize the database
        courseCartBridgeService.save(courseCartBridge);

        int databaseSizeBeforeDelete = courseCartBridgeRepository.findAll().size();

        // Get the courseCartBridge
        restCourseCartBridgeMockMvc.perform(delete("/api/course-cart-bridges/{id}", courseCartBridge.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CourseCartBridge> courseCartBridgeList = courseCartBridgeRepository.findAll();
        assertThat(courseCartBridgeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the CourseCartBridge in Elasticsearch
        verify(mockCourseCartBridgeSearchRepository, times(1)).deleteById(courseCartBridge.getId());
    }

    @Test
    @Transactional
    public void searchCourseCartBridge() throws Exception {
        // Initialize the database
        courseCartBridgeService.save(courseCartBridge);
        when(mockCourseCartBridgeSearchRepository.search(queryStringQuery("id:" + courseCartBridge.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(courseCartBridge), PageRequest.of(0, 1), 1));
        // Search the courseCartBridge
        restCourseCartBridgeMockMvc.perform(get("/api/_search/course-cart-bridges?query=id:" + courseCartBridge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseCartBridge.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseCartBridge.class);
        CourseCartBridge courseCartBridge1 = new CourseCartBridge();
        courseCartBridge1.setId(1L);
        CourseCartBridge courseCartBridge2 = new CourseCartBridge();
        courseCartBridge2.setId(courseCartBridge1.getId());
        assertThat(courseCartBridge1).isEqualTo(courseCartBridge2);
        courseCartBridge2.setId(2L);
        assertThat(courseCartBridge1).isNotEqualTo(courseCartBridge2);
        courseCartBridge1.setId(null);
        assertThat(courseCartBridge1).isNotEqualTo(courseCartBridge2);
    }
}
