package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.Servicelist;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.repository.ServicelistRepository;
import io.github.softech.dev.sgill.repository.search.ServicelistSearchRepository;
import io.github.softech.dev.sgill.service.ServicelistService;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.ServicelistCriteria;
import io.github.softech.dev.sgill.service.ServicelistQueryService;

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
 * Test class for the ServicelistResource REST controller.
 *
 * @see ServicelistResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class ServicelistResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMPANY = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_AREAS = "AAAAAAAAAA";
    private static final String UPDATED_AREAS = "BBBBBBBBBB";

    private static final String DEFAULT_SPECIALITY = "AAAAAAAAAA";
    private static final String UPDATED_SPECIALITY = "BBBBBBBBBB";

    private static final String DEFAULT_TRADES = "AAAAAAAAAA";
    private static final String UPDATED_TRADES = "BBBBBBBBBB";

    @Autowired
    private ServicelistRepository servicelistRepository;

    

    @Autowired
    private ServicelistService servicelistService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.ServicelistSearchRepositoryMockConfiguration
     */
    @Autowired
    private ServicelistSearchRepository mockServicelistSearchRepository;

    @Autowired
    private ServicelistQueryService servicelistQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restServicelistMockMvc;

    private Servicelist servicelist;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ServicelistResource servicelistResource = new ServicelistResource(servicelistService, servicelistQueryService);
        this.restServicelistMockMvc = MockMvcBuilders.standaloneSetup(servicelistResource)
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
    public static Servicelist createEntity(EntityManager em) {
        Servicelist servicelist = new Servicelist()
            .name(DEFAULT_NAME)
            .company(DEFAULT_COMPANY)
            .url(DEFAULT_URL)
            .phone(DEFAULT_PHONE)
            .email(DEFAULT_EMAIL)
            .areas(DEFAULT_AREAS)
            .speciality(DEFAULT_SPECIALITY)
            .trades(DEFAULT_TRADES);
        // Add required entity
        Customer customer = CustomerResourceIntTest.createEntity(em);
        em.persist(customer);
        em.flush();
        servicelist.setCustomer(customer);
        return servicelist;
    }

    @Before
    public void initTest() {
        servicelist = createEntity(em);
    }

    @Test
    @Transactional
    public void createServicelist() throws Exception {
        int databaseSizeBeforeCreate = servicelistRepository.findAll().size();

        // Create the Servicelist
        restServicelistMockMvc.perform(post("/api/servicelists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(servicelist)))
            .andExpect(status().isCreated());

        // Validate the Servicelist in the database
        List<Servicelist> servicelistList = servicelistRepository.findAll();
        assertThat(servicelistList).hasSize(databaseSizeBeforeCreate + 1);
        Servicelist testServicelist = servicelistList.get(servicelistList.size() - 1);
        assertThat(testServicelist.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testServicelist.getCompany()).isEqualTo(DEFAULT_COMPANY);
        assertThat(testServicelist.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testServicelist.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testServicelist.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testServicelist.getAreas()).isEqualTo(DEFAULT_AREAS);
        assertThat(testServicelist.getSpeciality()).isEqualTo(DEFAULT_SPECIALITY);
        assertThat(testServicelist.getTrades()).isEqualTo(DEFAULT_TRADES);

        // Validate the Servicelist in Elasticsearch
        verify(mockServicelistSearchRepository, times(1)).save(testServicelist);
    }

    @Test
    @Transactional
    public void createServicelistWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = servicelistRepository.findAll().size();

        // Create the Servicelist with an existing ID
        servicelist.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restServicelistMockMvc.perform(post("/api/servicelists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(servicelist)))
            .andExpect(status().isBadRequest());

        // Validate the Servicelist in the database
        List<Servicelist> servicelistList = servicelistRepository.findAll();
        assertThat(servicelistList).hasSize(databaseSizeBeforeCreate);

        // Validate the Servicelist in Elasticsearch
        verify(mockServicelistSearchRepository, times(0)).save(servicelist);
    }

    @Test
    @Transactional
    public void getAllServicelists() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList
        restServicelistMockMvc.perform(get("/api/servicelists?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(servicelist.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].company").value(hasItem(DEFAULT_COMPANY)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].areas").value(hasItem(DEFAULT_AREAS)))
            .andExpect(jsonPath("$.[*].speciality").value(hasItem(DEFAULT_SPECIALITY)))
            .andExpect(jsonPath("$.[*].trades").value(hasItem(DEFAULT_TRADES)));
    }
    

    @Test
    @Transactional
    public void getServicelist() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get the servicelist
        restServicelistMockMvc.perform(get("/api/servicelists/{id}", servicelist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(servicelist.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.company").value(DEFAULT_COMPANY))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.areas").value(DEFAULT_AREAS))
            .andExpect(jsonPath("$.speciality").value(DEFAULT_SPECIALITY))
            .andExpect(jsonPath("$.trades").value(DEFAULT_TRADES));
    }

    @Test
    @Transactional
    public void getAllServicelistsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where name equals to DEFAULT_NAME
        defaultServicelistShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the servicelistList where name equals to UPDATED_NAME
        defaultServicelistShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllServicelistsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where name in DEFAULT_NAME or UPDATED_NAME
        defaultServicelistShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the servicelistList where name equals to UPDATED_NAME
        defaultServicelistShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllServicelistsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where name is not null
        defaultServicelistShouldBeFound("name.specified=true");

        // Get all the servicelistList where name is null
        defaultServicelistShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllServicelistsByCompanyIsEqualToSomething() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where company equals to DEFAULT_COMPANY
        defaultServicelistShouldBeFound("company.equals=" + DEFAULT_COMPANY);

        // Get all the servicelistList where company equals to UPDATED_COMPANY
        defaultServicelistShouldNotBeFound("company.equals=" + UPDATED_COMPANY);
    }

    @Test
    @Transactional
    public void getAllServicelistsByCompanyIsInShouldWork() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where company in DEFAULT_COMPANY or UPDATED_COMPANY
        defaultServicelistShouldBeFound("company.in=" + DEFAULT_COMPANY + "," + UPDATED_COMPANY);

        // Get all the servicelistList where company equals to UPDATED_COMPANY
        defaultServicelistShouldNotBeFound("company.in=" + UPDATED_COMPANY);
    }

    @Test
    @Transactional
    public void getAllServicelistsByCompanyIsNullOrNotNull() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where company is not null
        defaultServicelistShouldBeFound("company.specified=true");

        // Get all the servicelistList where company is null
        defaultServicelistShouldNotBeFound("company.specified=false");
    }

    @Test
    @Transactional
    public void getAllServicelistsByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where url equals to DEFAULT_URL
        defaultServicelistShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the servicelistList where url equals to UPDATED_URL
        defaultServicelistShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllServicelistsByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where url in DEFAULT_URL or UPDATED_URL
        defaultServicelistShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the servicelistList where url equals to UPDATED_URL
        defaultServicelistShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllServicelistsByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where url is not null
        defaultServicelistShouldBeFound("url.specified=true");

        // Get all the servicelistList where url is null
        defaultServicelistShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    public void getAllServicelistsByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where phone equals to DEFAULT_PHONE
        defaultServicelistShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the servicelistList where phone equals to UPDATED_PHONE
        defaultServicelistShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllServicelistsByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultServicelistShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the servicelistList where phone equals to UPDATED_PHONE
        defaultServicelistShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllServicelistsByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where phone is not null
        defaultServicelistShouldBeFound("phone.specified=true");

        // Get all the servicelistList where phone is null
        defaultServicelistShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    public void getAllServicelistsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where email equals to DEFAULT_EMAIL
        defaultServicelistShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the servicelistList where email equals to UPDATED_EMAIL
        defaultServicelistShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllServicelistsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultServicelistShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the servicelistList where email equals to UPDATED_EMAIL
        defaultServicelistShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllServicelistsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where email is not null
        defaultServicelistShouldBeFound("email.specified=true");

        // Get all the servicelistList where email is null
        defaultServicelistShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    public void getAllServicelistsByAreasIsEqualToSomething() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where areas equals to DEFAULT_AREAS
        defaultServicelistShouldBeFound("areas.equals=" + DEFAULT_AREAS);

        // Get all the servicelistList where areas equals to UPDATED_AREAS
        defaultServicelistShouldNotBeFound("areas.equals=" + UPDATED_AREAS);
    }

    @Test
    @Transactional
    public void getAllServicelistsByAreasIsInShouldWork() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where areas in DEFAULT_AREAS or UPDATED_AREAS
        defaultServicelistShouldBeFound("areas.in=" + DEFAULT_AREAS + "," + UPDATED_AREAS);

        // Get all the servicelistList where areas equals to UPDATED_AREAS
        defaultServicelistShouldNotBeFound("areas.in=" + UPDATED_AREAS);
    }

    @Test
    @Transactional
    public void getAllServicelistsByAreasIsNullOrNotNull() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where areas is not null
        defaultServicelistShouldBeFound("areas.specified=true");

        // Get all the servicelistList where areas is null
        defaultServicelistShouldNotBeFound("areas.specified=false");
    }

    @Test
    @Transactional
    public void getAllServicelistsBySpecialityIsEqualToSomething() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where speciality equals to DEFAULT_SPECIALITY
        defaultServicelistShouldBeFound("speciality.equals=" + DEFAULT_SPECIALITY);

        // Get all the servicelistList where speciality equals to UPDATED_SPECIALITY
        defaultServicelistShouldNotBeFound("speciality.equals=" + UPDATED_SPECIALITY);
    }

    @Test
    @Transactional
    public void getAllServicelistsBySpecialityIsInShouldWork() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where speciality in DEFAULT_SPECIALITY or UPDATED_SPECIALITY
        defaultServicelistShouldBeFound("speciality.in=" + DEFAULT_SPECIALITY + "," + UPDATED_SPECIALITY);

        // Get all the servicelistList where speciality equals to UPDATED_SPECIALITY
        defaultServicelistShouldNotBeFound("speciality.in=" + UPDATED_SPECIALITY);
    }

    @Test
    @Transactional
    public void getAllServicelistsBySpecialityIsNullOrNotNull() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where speciality is not null
        defaultServicelistShouldBeFound("speciality.specified=true");

        // Get all the servicelistList where speciality is null
        defaultServicelistShouldNotBeFound("speciality.specified=false");
    }

    @Test
    @Transactional
    public void getAllServicelistsByTradesIsEqualToSomething() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where trades equals to DEFAULT_TRADES
        defaultServicelistShouldBeFound("trades.equals=" + DEFAULT_TRADES);

        // Get all the servicelistList where trades equals to UPDATED_TRADES
        defaultServicelistShouldNotBeFound("trades.equals=" + UPDATED_TRADES);
    }

    @Test
    @Transactional
    public void getAllServicelistsByTradesIsInShouldWork() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where trades in DEFAULT_TRADES or UPDATED_TRADES
        defaultServicelistShouldBeFound("trades.in=" + DEFAULT_TRADES + "," + UPDATED_TRADES);

        // Get all the servicelistList where trades equals to UPDATED_TRADES
        defaultServicelistShouldNotBeFound("trades.in=" + UPDATED_TRADES);
    }

    @Test
    @Transactional
    public void getAllServicelistsByTradesIsNullOrNotNull() throws Exception {
        // Initialize the database
        servicelistRepository.saveAndFlush(servicelist);

        // Get all the servicelistList where trades is not null
        defaultServicelistShouldBeFound("trades.specified=true");

        // Get all the servicelistList where trades is null
        defaultServicelistShouldNotBeFound("trades.specified=false");
    }

    @Test
    @Transactional
    public void getAllServicelistsByCustomerIsEqualToSomething() throws Exception {
        // Initialize the database
        Customer customer = CustomerResourceIntTest.createEntity(em);
        em.persist(customer);
        em.flush();
        servicelist.setCustomer(customer);
        servicelistRepository.saveAndFlush(servicelist);
        Long customerId = customer.getId();

        // Get all the servicelistList where customer equals to customerId
        defaultServicelistShouldBeFound("customerId.equals=" + customerId);

        // Get all the servicelistList where customer equals to customerId + 1
        defaultServicelistShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultServicelistShouldBeFound(String filter) throws Exception {
        restServicelistMockMvc.perform(get("/api/servicelists?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(servicelist.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].company").value(hasItem(DEFAULT_COMPANY)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].areas").value(hasItem(DEFAULT_AREAS)))
            .andExpect(jsonPath("$.[*].speciality").value(hasItem(DEFAULT_SPECIALITY)))
            .andExpect(jsonPath("$.[*].trades").value(hasItem(DEFAULT_TRADES)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultServicelistShouldNotBeFound(String filter) throws Exception {
        restServicelistMockMvc.perform(get("/api/servicelists?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingServicelist() throws Exception {
        // Get the servicelist
        restServicelistMockMvc.perform(get("/api/servicelists/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateServicelist() throws Exception {
        // Initialize the database
        servicelistService.save(servicelist);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockServicelistSearchRepository);

        int databaseSizeBeforeUpdate = servicelistRepository.findAll().size();

        // Update the servicelist
        Servicelist updatedServicelist = servicelistRepository.findById(servicelist.getId()).get();
        // Disconnect from session so that the updates on updatedServicelist are not directly saved in db
        em.detach(updatedServicelist);
        updatedServicelist
            .name(UPDATED_NAME)
            .company(UPDATED_COMPANY)
            .url(UPDATED_URL)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .areas(UPDATED_AREAS)
            .speciality(UPDATED_SPECIALITY)
            .trades(UPDATED_TRADES);

        restServicelistMockMvc.perform(put("/api/servicelists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedServicelist)))
            .andExpect(status().isOk());

        // Validate the Servicelist in the database
        List<Servicelist> servicelistList = servicelistRepository.findAll();
        assertThat(servicelistList).hasSize(databaseSizeBeforeUpdate);
        Servicelist testServicelist = servicelistList.get(servicelistList.size() - 1);
        assertThat(testServicelist.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testServicelist.getCompany()).isEqualTo(UPDATED_COMPANY);
        assertThat(testServicelist.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testServicelist.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testServicelist.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testServicelist.getAreas()).isEqualTo(UPDATED_AREAS);
        assertThat(testServicelist.getSpeciality()).isEqualTo(UPDATED_SPECIALITY);
        assertThat(testServicelist.getTrades()).isEqualTo(UPDATED_TRADES);

        // Validate the Servicelist in Elasticsearch
        verify(mockServicelistSearchRepository, times(1)).save(testServicelist);
    }

    @Test
    @Transactional
    public void updateNonExistingServicelist() throws Exception {
        int databaseSizeBeforeUpdate = servicelistRepository.findAll().size();

        // Create the Servicelist

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restServicelistMockMvc.perform(put("/api/servicelists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(servicelist)))
            .andExpect(status().isBadRequest());

        // Validate the Servicelist in the database
        List<Servicelist> servicelistList = servicelistRepository.findAll();
        assertThat(servicelistList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Servicelist in Elasticsearch
        verify(mockServicelistSearchRepository, times(0)).save(servicelist);
    }

    @Test
    @Transactional
    public void deleteServicelist() throws Exception {
        // Initialize the database
        servicelistService.save(servicelist);

        int databaseSizeBeforeDelete = servicelistRepository.findAll().size();

        // Get the servicelist
        restServicelistMockMvc.perform(delete("/api/servicelists/{id}", servicelist.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Servicelist> servicelistList = servicelistRepository.findAll();
        assertThat(servicelistList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Servicelist in Elasticsearch
        verify(mockServicelistSearchRepository, times(1)).deleteById(servicelist.getId());
    }

    @Test
    @Transactional
    public void searchServicelist() throws Exception {
        // Initialize the database
        servicelistService.save(servicelist);
        when(mockServicelistSearchRepository.search(queryStringQuery("id:" + servicelist.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(servicelist), PageRequest.of(0, 1), 1));
        // Search the servicelist
        restServicelistMockMvc.perform(get("/api/_search/servicelists?query=id:" + servicelist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(servicelist.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].company").value(hasItem(DEFAULT_COMPANY)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].areas").value(hasItem(DEFAULT_AREAS)))
            .andExpect(jsonPath("$.[*].speciality").value(hasItem(DEFAULT_SPECIALITY)))
            .andExpect(jsonPath("$.[*].trades").value(hasItem(DEFAULT_TRADES)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Servicelist.class);
        Servicelist servicelist1 = new Servicelist();
        servicelist1.setId(1L);
        Servicelist servicelist2 = new Servicelist();
        servicelist2.setId(servicelist1.getId());
        assertThat(servicelist1).isEqualTo(servicelist2);
        servicelist2.setId(2L);
        assertThat(servicelist1).isNotEqualTo(servicelist2);
        servicelist1.setId(null);
        assertThat(servicelist1).isNotEqualTo(servicelist2);
    }
}
