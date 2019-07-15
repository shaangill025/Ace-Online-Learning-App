package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.MergeFunction;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.repository.LegacyCoursesRepository;
import io.github.softech.dev.sgill.repository.MergeFunctionRepository;
import io.github.softech.dev.sgill.repository.ServicelistRepository;
import io.github.softech.dev.sgill.repository.search.MergeFunctionSearchRepository;
import io.github.softech.dev.sgill.service.*;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;

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
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Test class for the MergeFunctionResource REST controller.
 *
 * @see MergeFunctionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class MergeFunctionResourceIntTest {

    private static final LocalDate DEFAULT_DATETIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATETIME = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    @Autowired
    private MergeFunctionRepository mergeFunctionRepository;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.MergeFunctionSearchRepositoryMockConfiguration
     */
    @Autowired
    private MergeFunctionSearchRepository mockMergeFunctionSearchRepository;

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
    private CustomerResource customerResource;

    @Autowired
    private LegacyCoursesRepository legacyCoursesRepository;

    @Autowired
    private ServicelistRepository servicelistRepository;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private QuestionHistoryService questionHistoryService;

    @Autowired
    private QuizHistoryService quizHistoryService;

    @Autowired
    private CourseHistoryService courseHistoryService;

    @Autowired
    private CustomerService customerService;

    private MockMvc restMergeFunctionMockMvc;

    private MergeFunction mergeFunction;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MergeFunctionResource mergeFunctionResource = new MergeFunctionResource(mergeFunctionRepository, mockMergeFunctionSearchRepository, customerResource,
            legacyCoursesRepository, servicelistRepository, certificateService, questionHistoryService, quizHistoryService, courseHistoryService, customerService);
        this.restMergeFunctionMockMvc = MockMvcBuilders.standaloneSetup(mergeFunctionResource)
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
    public static MergeFunction createEntity(EntityManager em) {
        MergeFunction mergeFunction = new MergeFunction()
            .datetime(DEFAULT_DATETIME)
            .note(DEFAULT_NOTE);
        // Add required entity
        Customer customer = CustomerResourceIntTest.createEntity(em);
        em.persist(customer);
        em.flush();
        mergeFunction.setTobeRemoved(customer);
        // Add required entity
        mergeFunction.setReplacement(customer);
        return mergeFunction;
    }

    @Before
    public void initTest() {
        mergeFunction = createEntity(em);
    }

    @Test
    @Transactional
    public void createMergeFunction() throws Exception {
        int databaseSizeBeforeCreate = mergeFunctionRepository.findAll().size();

        // Create the MergeFunction
        restMergeFunctionMockMvc.perform(post("/api/merge-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mergeFunction)))
            .andExpect(status().isCreated());

        // Validate the MergeFunction in the database
        List<MergeFunction> mergeFunctionList = mergeFunctionRepository.findAll();
        assertThat(mergeFunctionList).hasSize(databaseSizeBeforeCreate + 1);
        MergeFunction testMergeFunction = mergeFunctionList.get(mergeFunctionList.size() - 1);
        assertThat(testMergeFunction.getDatetime()).isEqualTo(DEFAULT_DATETIME);
        assertThat(testMergeFunction.getNote()).isEqualTo(DEFAULT_NOTE);

        // Validate the MergeFunction in Elasticsearch
        verify(mockMergeFunctionSearchRepository, times(1)).save(testMergeFunction);
    }

    @Test
    @Transactional
    public void createMergeFunctionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = mergeFunctionRepository.findAll().size();

        // Create the MergeFunction with an existing ID
        mergeFunction.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMergeFunctionMockMvc.perform(post("/api/merge-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mergeFunction)))
            .andExpect(status().isBadRequest());

        // Validate the MergeFunction in the database
        List<MergeFunction> mergeFunctionList = mergeFunctionRepository.findAll();
        assertThat(mergeFunctionList).hasSize(databaseSizeBeforeCreate);

        // Validate the MergeFunction in Elasticsearch
        verify(mockMergeFunctionSearchRepository, times(0)).save(mergeFunction);
    }

    @Test
    @Transactional
    public void checkDatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = mergeFunctionRepository.findAll().size();
        // set the field null
        mergeFunction.setDatetime(null);

        // Create the MergeFunction, which fails.

        restMergeFunctionMockMvc.perform(post("/api/merge-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mergeFunction)))
            .andExpect(status().isBadRequest());

        List<MergeFunction> mergeFunctionList = mergeFunctionRepository.findAll();
        assertThat(mergeFunctionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNoteIsRequired() throws Exception {
        int databaseSizeBeforeTest = mergeFunctionRepository.findAll().size();
        // set the field null
        mergeFunction.setNote(null);

        // Create the MergeFunction, which fails.

        restMergeFunctionMockMvc.perform(post("/api/merge-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mergeFunction)))
            .andExpect(status().isBadRequest());

        List<MergeFunction> mergeFunctionList = mergeFunctionRepository.findAll();
        assertThat(mergeFunctionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMergeFunctions() throws Exception {
        // Initialize the database
        mergeFunctionRepository.saveAndFlush(mergeFunction);

        // Get all the mergeFunctionList
        restMergeFunctionMockMvc.perform(get("/api/merge-functions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mergeFunction.getId().intValue())))
            .andExpect(jsonPath("$.[*].datetime").value(hasItem(DEFAULT_DATETIME.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }
    
    @Test
    @Transactional
    public void getMergeFunction() throws Exception {
        // Initialize the database
        mergeFunctionRepository.saveAndFlush(mergeFunction);

        // Get the mergeFunction
        restMergeFunctionMockMvc.perform(get("/api/merge-functions/{id}", mergeFunction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(mergeFunction.getId().intValue()))
            .andExpect(jsonPath("$.datetime").value(DEFAULT_DATETIME.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    public void getNonExistingMergeFunction() throws Exception {
        // Get the mergeFunction
        restMergeFunctionMockMvc.perform(get("/api/merge-functions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMergeFunction() throws Exception {
        // Initialize the database
        mergeFunctionRepository.saveAndFlush(mergeFunction);

        int databaseSizeBeforeUpdate = mergeFunctionRepository.findAll().size();

        // Update the mergeFunction
        MergeFunction updatedMergeFunction = mergeFunctionRepository.findById(mergeFunction.getId()).get();
        // Disconnect from session so that the updates on updatedMergeFunction are not directly saved in db
        em.detach(updatedMergeFunction);
        updatedMergeFunction
            .datetime(UPDATED_DATETIME)
            .note(UPDATED_NOTE);

        restMergeFunctionMockMvc.perform(put("/api/merge-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMergeFunction)))
            .andExpect(status().isOk());

        // Validate the MergeFunction in the database
        List<MergeFunction> mergeFunctionList = mergeFunctionRepository.findAll();
        assertThat(mergeFunctionList).hasSize(databaseSizeBeforeUpdate);
        MergeFunction testMergeFunction = mergeFunctionList.get(mergeFunctionList.size() - 1);
        assertThat(testMergeFunction.getDatetime()).isEqualTo(UPDATED_DATETIME);
        assertThat(testMergeFunction.getNote()).isEqualTo(UPDATED_NOTE);

        // Validate the MergeFunction in Elasticsearch
        verify(mockMergeFunctionSearchRepository, times(1)).save(testMergeFunction);
    }

    @Test
    @Transactional
    public void updateNonExistingMergeFunction() throws Exception {
        int databaseSizeBeforeUpdate = mergeFunctionRepository.findAll().size();

        // Create the MergeFunction

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMergeFunctionMockMvc.perform(put("/api/merge-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mergeFunction)))
            .andExpect(status().isBadRequest());

        // Validate the MergeFunction in the database
        List<MergeFunction> mergeFunctionList = mergeFunctionRepository.findAll();
        assertThat(mergeFunctionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the MergeFunction in Elasticsearch
        verify(mockMergeFunctionSearchRepository, times(0)).save(mergeFunction);
    }

    @Test
    @Transactional
    public void deleteMergeFunction() throws Exception {
        // Initialize the database
        mergeFunctionRepository.saveAndFlush(mergeFunction);

        int databaseSizeBeforeDelete = mergeFunctionRepository.findAll().size();

        // Get the mergeFunction
        restMergeFunctionMockMvc.perform(delete("/api/merge-functions/{id}", mergeFunction.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<MergeFunction> mergeFunctionList = mergeFunctionRepository.findAll();
        assertThat(mergeFunctionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the MergeFunction in Elasticsearch
        verify(mockMergeFunctionSearchRepository, times(1)).deleteById(mergeFunction.getId());
    }

    @Test
    @Transactional
    public void searchMergeFunction() throws Exception {
        // Initialize the database
        mergeFunctionRepository.saveAndFlush(mergeFunction);
        when(mockMergeFunctionSearchRepository.search(queryStringQuery("id:" + mergeFunction.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(mergeFunction), PageRequest.of(0, 1), 1));
        // Search the mergeFunction
        restMergeFunctionMockMvc.perform(get("/api/_search/merge-functions?query=id:" + mergeFunction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mergeFunction.getId().intValue())))
            .andExpect(jsonPath("$.[*].datetime").value(hasItem(DEFAULT_DATETIME.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MergeFunction.class);
        MergeFunction mergeFunction1 = new MergeFunction();
        mergeFunction1.setId(1L);
        MergeFunction mergeFunction2 = new MergeFunction();
        mergeFunction2.setId(mergeFunction1.getId());
        assertThat(mergeFunction1).isEqualTo(mergeFunction2);
        mergeFunction2.setId(2L);
        assertThat(mergeFunction1).isNotEqualTo(mergeFunction2);
        mergeFunction1.setId(null);
        assertThat(mergeFunction1).isNotEqualTo(mergeFunction2);
    }
}
