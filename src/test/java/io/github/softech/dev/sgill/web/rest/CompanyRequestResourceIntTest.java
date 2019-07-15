package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.CompanyRequest;
import io.github.softech.dev.sgill.repository.CompanyRequestRepository;
import io.github.softech.dev.sgill.repository.search.CompanyRequestSearchRepository;
import io.github.softech.dev.sgill.service.CompanyRequestService;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.CompanyRequestCriteria;
import io.github.softech.dev.sgill.service.CompanyRequestQueryService;

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
 * Test class for the CompanyRequestResource REST controller.
 *
 * @see CompanyRequestResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class CompanyRequestResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "(997)7259089";
    private static final String UPDATED_PHONE = "786)-487-8958";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_STREET_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_STREET_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_POSTAL_CODE = "62853-5314";
    private static final String UPDATED_POSTAL_CODE = "11040";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_STATE_PROVINCE = "AAAAAAAAAA";
    private static final String UPDATED_STATE_PROVINCE = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_LICENCE_CYCLE = "AAAAAAAAAA";
    private static final String UPDATED_LICENCE_CYCLE = "BBBBBBBBBB";

    @Autowired
    private CompanyRequestRepository companyRequestRepository;

    @Autowired
    private CompanyRequestService companyRequestService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.CompanyRequestSearchRepositoryMockConfiguration
     */
    @Autowired
    private CompanyRequestSearchRepository mockCompanyRequestSearchRepository;

    @Autowired
    private CompanyRequestQueryService companyRequestQueryService;

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

    private MockMvc restCompanyRequestMockMvc;

    private CompanyRequest companyRequest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CompanyRequestResource companyRequestResource = new CompanyRequestResource(companyRequestService, companyRequestQueryService);
        this.restCompanyRequestMockMvc = MockMvcBuilders.standaloneSetup(companyRequestResource)
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
    public static CompanyRequest createEntity(EntityManager em) {
        CompanyRequest companyRequest = new CompanyRequest()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .phone(DEFAULT_PHONE)
            .email(DEFAULT_EMAIL)
            .streetAddress(DEFAULT_STREET_ADDRESS)
            .postalCode(DEFAULT_POSTAL_CODE)
            .city(DEFAULT_CITY)
            .stateProvince(DEFAULT_STATE_PROVINCE)
            .country(DEFAULT_COUNTRY)
            .url(DEFAULT_URL)
            .licenceCycle(DEFAULT_LICENCE_CYCLE);
        return companyRequest;
    }

    @Before
    public void initTest() {
        companyRequest = createEntity(em);
    }

    @Test
    @Transactional
    public void createCompanyRequest() throws Exception {
        int databaseSizeBeforeCreate = companyRequestRepository.findAll().size();

        // Create the CompanyRequest
        restCompanyRequestMockMvc.perform(post("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyRequest)))
            .andExpect(status().isCreated());

        // Validate the CompanyRequest in the database
        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeCreate + 1);
        CompanyRequest testCompanyRequest = companyRequestList.get(companyRequestList.size() - 1);
        assertThat(testCompanyRequest.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCompanyRequest.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCompanyRequest.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testCompanyRequest.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCompanyRequest.getStreetAddress()).isEqualTo(DEFAULT_STREET_ADDRESS);
        assertThat(testCompanyRequest.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testCompanyRequest.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testCompanyRequest.getStateProvince()).isEqualTo(DEFAULT_STATE_PROVINCE);
        assertThat(testCompanyRequest.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testCompanyRequest.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testCompanyRequest.getLicenceCycle()).isEqualTo(DEFAULT_LICENCE_CYCLE);

        // Validate the CompanyRequest in Elasticsearch
        verify(mockCompanyRequestSearchRepository, times(1)).save(testCompanyRequest);
    }

    @Test
    @Transactional
    public void createCompanyRequestWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = companyRequestRepository.findAll().size();

        // Create the CompanyRequest with an existing ID
        companyRequest.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompanyRequestMockMvc.perform(post("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyRequest)))
            .andExpect(status().isBadRequest());

        // Validate the CompanyRequest in the database
        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeCreate);

        // Validate the CompanyRequest in Elasticsearch
        verify(mockCompanyRequestSearchRepository, times(0)).save(companyRequest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRequestRepository.findAll().size();
        // set the field null
        companyRequest.setName(null);

        // Create the CompanyRequest, which fails.

        restCompanyRequestMockMvc.perform(post("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyRequest)))
            .andExpect(status().isBadRequest());

        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRequestRepository.findAll().size();
        // set the field null
        companyRequest.setDescription(null);

        // Create the CompanyRequest, which fails.

        restCompanyRequestMockMvc.perform(post("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyRequest)))
            .andExpect(status().isBadRequest());

        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRequestRepository.findAll().size();
        // set the field null
        companyRequest.setPhone(null);

        // Create the CompanyRequest, which fails.

        restCompanyRequestMockMvc.perform(post("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyRequest)))
            .andExpect(status().isBadRequest());

        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRequestRepository.findAll().size();
        // set the field null
        companyRequest.setEmail(null);

        // Create the CompanyRequest, which fails.

        restCompanyRequestMockMvc.perform(post("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyRequest)))
            .andExpect(status().isBadRequest());

        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStreetAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRequestRepository.findAll().size();
        // set the field null
        companyRequest.setStreetAddress(null);

        // Create the CompanyRequest, which fails.

        restCompanyRequestMockMvc.perform(post("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyRequest)))
            .andExpect(status().isBadRequest());

        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPostalCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRequestRepository.findAll().size();
        // set the field null
        companyRequest.setPostalCode(null);

        // Create the CompanyRequest, which fails.

        restCompanyRequestMockMvc.perform(post("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyRequest)))
            .andExpect(status().isBadRequest());

        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRequestRepository.findAll().size();
        // set the field null
        companyRequest.setCity(null);

        // Create the CompanyRequest, which fails.

        restCompanyRequestMockMvc.perform(post("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyRequest)))
            .andExpect(status().isBadRequest());

        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStateProvinceIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRequestRepository.findAll().size();
        // set the field null
        companyRequest.setStateProvince(null);

        // Create the CompanyRequest, which fails.

        restCompanyRequestMockMvc.perform(post("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyRequest)))
            .andExpect(status().isBadRequest());

        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCountryIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRequestRepository.findAll().size();
        // set the field null
        companyRequest.setCountry(null);

        // Create the CompanyRequest, which fails.

        restCompanyRequestMockMvc.perform(post("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyRequest)))
            .andExpect(status().isBadRequest());

        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLicenceCycleIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRequestRepository.findAll().size();
        // set the field null
        companyRequest.setLicenceCycle(null);

        // Create the CompanyRequest, which fails.

        restCompanyRequestMockMvc.perform(post("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyRequest)))
            .andExpect(status().isBadRequest());

        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCompanyRequests() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList
        restCompanyRequestMockMvc.perform(get("/api/company-requests?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(companyRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].stateProvince").value(hasItem(DEFAULT_STATE_PROVINCE)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].licenceCycle").value(hasItem(DEFAULT_LICENCE_CYCLE)));
    }
    
    @Test
    @Transactional
    public void getCompanyRequest() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get the companyRequest
        restCompanyRequestMockMvc.perform(get("/api/company-requests/{id}", companyRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(companyRequest.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.streetAddress").value(DEFAULT_STREET_ADDRESS))
            .andExpect(jsonPath("$.postalCode").value(DEFAULT_POSTAL_CODE))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.stateProvince").value(DEFAULT_STATE_PROVINCE))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.licenceCycle").value(DEFAULT_LICENCE_CYCLE));
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where name equals to DEFAULT_NAME
        defaultCompanyRequestShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the companyRequestList where name equals to UPDATED_NAME
        defaultCompanyRequestShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCompanyRequestShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the companyRequestList where name equals to UPDATED_NAME
        defaultCompanyRequestShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where name is not null
        defaultCompanyRequestShouldBeFound("name.specified=true");

        // Get all the companyRequestList where name is null
        defaultCompanyRequestShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where description equals to DEFAULT_DESCRIPTION
        defaultCompanyRequestShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the companyRequestList where description equals to UPDATED_DESCRIPTION
        defaultCompanyRequestShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultCompanyRequestShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the companyRequestList where description equals to UPDATED_DESCRIPTION
        defaultCompanyRequestShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where description is not null
        defaultCompanyRequestShouldBeFound("description.specified=true");

        // Get all the companyRequestList where description is null
        defaultCompanyRequestShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where phone equals to DEFAULT_PHONE
        defaultCompanyRequestShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the companyRequestList where phone equals to UPDATED_PHONE
        defaultCompanyRequestShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultCompanyRequestShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the companyRequestList where phone equals to UPDATED_PHONE
        defaultCompanyRequestShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where phone is not null
        defaultCompanyRequestShouldBeFound("phone.specified=true");

        // Get all the companyRequestList where phone is null
        defaultCompanyRequestShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where email equals to DEFAULT_EMAIL
        defaultCompanyRequestShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the companyRequestList where email equals to UPDATED_EMAIL
        defaultCompanyRequestShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultCompanyRequestShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the companyRequestList where email equals to UPDATED_EMAIL
        defaultCompanyRequestShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where email is not null
        defaultCompanyRequestShouldBeFound("email.specified=true");

        // Get all the companyRequestList where email is null
        defaultCompanyRequestShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByStreetAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where streetAddress equals to DEFAULT_STREET_ADDRESS
        defaultCompanyRequestShouldBeFound("streetAddress.equals=" + DEFAULT_STREET_ADDRESS);

        // Get all the companyRequestList where streetAddress equals to UPDATED_STREET_ADDRESS
        defaultCompanyRequestShouldNotBeFound("streetAddress.equals=" + UPDATED_STREET_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByStreetAddressIsInShouldWork() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where streetAddress in DEFAULT_STREET_ADDRESS or UPDATED_STREET_ADDRESS
        defaultCompanyRequestShouldBeFound("streetAddress.in=" + DEFAULT_STREET_ADDRESS + "," + UPDATED_STREET_ADDRESS);

        // Get all the companyRequestList where streetAddress equals to UPDATED_STREET_ADDRESS
        defaultCompanyRequestShouldNotBeFound("streetAddress.in=" + UPDATED_STREET_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByStreetAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where streetAddress is not null
        defaultCompanyRequestShouldBeFound("streetAddress.specified=true");

        // Get all the companyRequestList where streetAddress is null
        defaultCompanyRequestShouldNotBeFound("streetAddress.specified=false");
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByPostalCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where postalCode equals to DEFAULT_POSTAL_CODE
        defaultCompanyRequestShouldBeFound("postalCode.equals=" + DEFAULT_POSTAL_CODE);

        // Get all the companyRequestList where postalCode equals to UPDATED_POSTAL_CODE
        defaultCompanyRequestShouldNotBeFound("postalCode.equals=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByPostalCodeIsInShouldWork() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where postalCode in DEFAULT_POSTAL_CODE or UPDATED_POSTAL_CODE
        defaultCompanyRequestShouldBeFound("postalCode.in=" + DEFAULT_POSTAL_CODE + "," + UPDATED_POSTAL_CODE);

        // Get all the companyRequestList where postalCode equals to UPDATED_POSTAL_CODE
        defaultCompanyRequestShouldNotBeFound("postalCode.in=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByPostalCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where postalCode is not null
        defaultCompanyRequestShouldBeFound("postalCode.specified=true");

        // Get all the companyRequestList where postalCode is null
        defaultCompanyRequestShouldNotBeFound("postalCode.specified=false");
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where city equals to DEFAULT_CITY
        defaultCompanyRequestShouldBeFound("city.equals=" + DEFAULT_CITY);

        // Get all the companyRequestList where city equals to UPDATED_CITY
        defaultCompanyRequestShouldNotBeFound("city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByCityIsInShouldWork() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where city in DEFAULT_CITY or UPDATED_CITY
        defaultCompanyRequestShouldBeFound("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY);

        // Get all the companyRequestList where city equals to UPDATED_CITY
        defaultCompanyRequestShouldNotBeFound("city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where city is not null
        defaultCompanyRequestShouldBeFound("city.specified=true");

        // Get all the companyRequestList where city is null
        defaultCompanyRequestShouldNotBeFound("city.specified=false");
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByStateProvinceIsEqualToSomething() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where stateProvince equals to DEFAULT_STATE_PROVINCE
        defaultCompanyRequestShouldBeFound("stateProvince.equals=" + DEFAULT_STATE_PROVINCE);

        // Get all the companyRequestList where stateProvince equals to UPDATED_STATE_PROVINCE
        defaultCompanyRequestShouldNotBeFound("stateProvince.equals=" + UPDATED_STATE_PROVINCE);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByStateProvinceIsInShouldWork() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where stateProvince in DEFAULT_STATE_PROVINCE or UPDATED_STATE_PROVINCE
        defaultCompanyRequestShouldBeFound("stateProvince.in=" + DEFAULT_STATE_PROVINCE + "," + UPDATED_STATE_PROVINCE);

        // Get all the companyRequestList where stateProvince equals to UPDATED_STATE_PROVINCE
        defaultCompanyRequestShouldNotBeFound("stateProvince.in=" + UPDATED_STATE_PROVINCE);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByStateProvinceIsNullOrNotNull() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where stateProvince is not null
        defaultCompanyRequestShouldBeFound("stateProvince.specified=true");

        // Get all the companyRequestList where stateProvince is null
        defaultCompanyRequestShouldNotBeFound("stateProvince.specified=false");
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByCountryIsEqualToSomething() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where country equals to DEFAULT_COUNTRY
        defaultCompanyRequestShouldBeFound("country.equals=" + DEFAULT_COUNTRY);

        // Get all the companyRequestList where country equals to UPDATED_COUNTRY
        defaultCompanyRequestShouldNotBeFound("country.equals=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByCountryIsInShouldWork() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where country in DEFAULT_COUNTRY or UPDATED_COUNTRY
        defaultCompanyRequestShouldBeFound("country.in=" + DEFAULT_COUNTRY + "," + UPDATED_COUNTRY);

        // Get all the companyRequestList where country equals to UPDATED_COUNTRY
        defaultCompanyRequestShouldNotBeFound("country.in=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByCountryIsNullOrNotNull() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where country is not null
        defaultCompanyRequestShouldBeFound("country.specified=true");

        // Get all the companyRequestList where country is null
        defaultCompanyRequestShouldNotBeFound("country.specified=false");
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where url equals to DEFAULT_URL
        defaultCompanyRequestShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the companyRequestList where url equals to UPDATED_URL
        defaultCompanyRequestShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where url in DEFAULT_URL or UPDATED_URL
        defaultCompanyRequestShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the companyRequestList where url equals to UPDATED_URL
        defaultCompanyRequestShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where url is not null
        defaultCompanyRequestShouldBeFound("url.specified=true");

        // Get all the companyRequestList where url is null
        defaultCompanyRequestShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByLicenceCycleIsEqualToSomething() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where licenceCycle equals to DEFAULT_LICENCE_CYCLE
        defaultCompanyRequestShouldBeFound("licenceCycle.equals=" + DEFAULT_LICENCE_CYCLE);

        // Get all the companyRequestList where licenceCycle equals to UPDATED_LICENCE_CYCLE
        defaultCompanyRequestShouldNotBeFound("licenceCycle.equals=" + UPDATED_LICENCE_CYCLE);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByLicenceCycleIsInShouldWork() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where licenceCycle in DEFAULT_LICENCE_CYCLE or UPDATED_LICENCE_CYCLE
        defaultCompanyRequestShouldBeFound("licenceCycle.in=" + DEFAULT_LICENCE_CYCLE + "," + UPDATED_LICENCE_CYCLE);

        // Get all the companyRequestList where licenceCycle equals to UPDATED_LICENCE_CYCLE
        defaultCompanyRequestShouldNotBeFound("licenceCycle.in=" + UPDATED_LICENCE_CYCLE);
    }

    @Test
    @Transactional
    public void getAllCompanyRequestsByLicenceCycleIsNullOrNotNull() throws Exception {
        // Initialize the database
        companyRequestRepository.saveAndFlush(companyRequest);

        // Get all the companyRequestList where licenceCycle is not null
        defaultCompanyRequestShouldBeFound("licenceCycle.specified=true");

        // Get all the companyRequestList where licenceCycle is null
        defaultCompanyRequestShouldNotBeFound("licenceCycle.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultCompanyRequestShouldBeFound(String filter) throws Exception {
        restCompanyRequestMockMvc.perform(get("/api/company-requests?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(companyRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].stateProvince").value(hasItem(DEFAULT_STATE_PROVINCE)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].licenceCycle").value(hasItem(DEFAULT_LICENCE_CYCLE)));

        // Check, that the count call also returns 1
        restCompanyRequestMockMvc.perform(get("/api/company-requests/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultCompanyRequestShouldNotBeFound(String filter) throws Exception {
        restCompanyRequestMockMvc.perform(get("/api/company-requests?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCompanyRequestMockMvc.perform(get("/api/company-requests/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingCompanyRequest() throws Exception {
        // Get the companyRequest
        restCompanyRequestMockMvc.perform(get("/api/company-requests/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCompanyRequest() throws Exception {
        // Initialize the database
        companyRequestService.save(companyRequest);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockCompanyRequestSearchRepository);

        int databaseSizeBeforeUpdate = companyRequestRepository.findAll().size();

        // Update the companyRequest
        CompanyRequest updatedCompanyRequest = companyRequestRepository.findById(companyRequest.getId()).get();
        // Disconnect from session so that the updates on updatedCompanyRequest are not directly saved in db
        em.detach(updatedCompanyRequest);
        updatedCompanyRequest
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .streetAddress(UPDATED_STREET_ADDRESS)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .stateProvince(UPDATED_STATE_PROVINCE)
            .country(UPDATED_COUNTRY)
            .url(UPDATED_URL)
            .licenceCycle(UPDATED_LICENCE_CYCLE);

        restCompanyRequestMockMvc.perform(put("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCompanyRequest)))
            .andExpect(status().isOk());

        // Validate the CompanyRequest in the database
        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeUpdate);
        CompanyRequest testCompanyRequest = companyRequestList.get(companyRequestList.size() - 1);
        assertThat(testCompanyRequest.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCompanyRequest.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCompanyRequest.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testCompanyRequest.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCompanyRequest.getStreetAddress()).isEqualTo(UPDATED_STREET_ADDRESS);
        assertThat(testCompanyRequest.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testCompanyRequest.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testCompanyRequest.getStateProvince()).isEqualTo(UPDATED_STATE_PROVINCE);
        assertThat(testCompanyRequest.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testCompanyRequest.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testCompanyRequest.getLicenceCycle()).isEqualTo(UPDATED_LICENCE_CYCLE);

        // Validate the CompanyRequest in Elasticsearch
        verify(mockCompanyRequestSearchRepository, times(1)).save(testCompanyRequest);
    }

    @Test
    @Transactional
    public void updateNonExistingCompanyRequest() throws Exception {
        int databaseSizeBeforeUpdate = companyRequestRepository.findAll().size();

        // Create the CompanyRequest

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanyRequestMockMvc.perform(put("/api/company-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyRequest)))
            .andExpect(status().isBadRequest());

        // Validate the CompanyRequest in the database
        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CompanyRequest in Elasticsearch
        verify(mockCompanyRequestSearchRepository, times(0)).save(companyRequest);
    }

    @Test
    @Transactional
    public void deleteCompanyRequest() throws Exception {
        // Initialize the database
        companyRequestService.save(companyRequest);

        int databaseSizeBeforeDelete = companyRequestRepository.findAll().size();

        // Get the companyRequest
        restCompanyRequestMockMvc.perform(delete("/api/company-requests/{id}", companyRequest.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CompanyRequest> companyRequestList = companyRequestRepository.findAll();
        assertThat(companyRequestList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the CompanyRequest in Elasticsearch
        verify(mockCompanyRequestSearchRepository, times(1)).deleteById(companyRequest.getId());
    }

    @Test
    @Transactional
    public void searchCompanyRequest() throws Exception {
        // Initialize the database
        companyRequestService.save(companyRequest);
        when(mockCompanyRequestSearchRepository.search(queryStringQuery("id:" + companyRequest.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(companyRequest), PageRequest.of(0, 1), 1));
        // Search the companyRequest
        restCompanyRequestMockMvc.perform(get("/api/_search/company-requests?query=id:" + companyRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(companyRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].stateProvince").value(hasItem(DEFAULT_STATE_PROVINCE)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].licenceCycle").value(hasItem(DEFAULT_LICENCE_CYCLE)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompanyRequest.class);
        CompanyRequest companyRequest1 = new CompanyRequest();
        companyRequest1.setId(1L);
        CompanyRequest companyRequest2 = new CompanyRequest();
        companyRequest2.setId(companyRequest1.getId());
        assertThat(companyRequest1).isEqualTo(companyRequest2);
        companyRequest2.setId(2L);
        assertThat(companyRequest1).isNotEqualTo(companyRequest2);
        companyRequest1.setId(null);
        assertThat(companyRequest1).isNotEqualTo(companyRequest2);
    }
}
