package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.Choice;
import io.github.softech.dev.sgill.domain.Question;
import io.github.softech.dev.sgill.repository.ChoiceRepository;
import io.github.softech.dev.sgill.repository.search.ChoiceSearchRepository;
import io.github.softech.dev.sgill.service.ChoiceService;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.ChoiceCriteria;
import io.github.softech.dev.sgill.service.ChoiceQueryService;

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
 * Test class for the ChoiceResource REST controller.
 *
 * @see ChoiceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class ChoiceResourceIntTest {

    private static final String DEFAULT_TEXT_CHOICE = "AAAAAAAAAA";
    private static final String UPDATED_TEXT_CHOICE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ISANSWER = false;
    private static final Boolean UPDATED_ISANSWER = true;

    @Autowired
    private ChoiceRepository choiceRepository;

    

    @Autowired
    private ChoiceService choiceService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.ChoiceSearchRepositoryMockConfiguration
     */
    @Autowired
    private ChoiceSearchRepository mockChoiceSearchRepository;

    @Autowired
    private ChoiceQueryService choiceQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restChoiceMockMvc;

    private Choice choice;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ChoiceResource choiceResource = new ChoiceResource(choiceService, choiceQueryService);
        this.restChoiceMockMvc = MockMvcBuilders.standaloneSetup(choiceResource)
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
    public static Choice createEntity(EntityManager em) {
        Choice choice = new Choice()
            .textChoice(DEFAULT_TEXT_CHOICE)
            .isanswer(DEFAULT_ISANSWER);
        return choice;
    }

    @Before
    public void initTest() {
        choice = createEntity(em);
    }

    @Test
    @Transactional
    public void createChoice() throws Exception {
        int databaseSizeBeforeCreate = choiceRepository.findAll().size();

        // Create the Choice
        restChoiceMockMvc.perform(post("/api/choices")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(choice)))
            .andExpect(status().isCreated());

        // Validate the Choice in the database
        List<Choice> choiceList = choiceRepository.findAll();
        assertThat(choiceList).hasSize(databaseSizeBeforeCreate + 1);
        Choice testChoice = choiceList.get(choiceList.size() - 1);
        assertThat(testChoice.getTextChoice()).isEqualTo(DEFAULT_TEXT_CHOICE);
        assertThat(testChoice.isIsanswer()).isEqualTo(DEFAULT_ISANSWER);

        // Validate the Choice in Elasticsearch
        verify(mockChoiceSearchRepository, times(1)).save(testChoice);
    }

    @Test
    @Transactional
    public void createChoiceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = choiceRepository.findAll().size();

        // Create the Choice with an existing ID
        choice.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restChoiceMockMvc.perform(post("/api/choices")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(choice)))
            .andExpect(status().isBadRequest());

        // Validate the Choice in the database
        List<Choice> choiceList = choiceRepository.findAll();
        assertThat(choiceList).hasSize(databaseSizeBeforeCreate);

        // Validate the Choice in Elasticsearch
        verify(mockChoiceSearchRepository, times(0)).save(choice);
    }

    @Test
    @Transactional
    public void checkTextChoiceIsRequired() throws Exception {
        int databaseSizeBeforeTest = choiceRepository.findAll().size();
        // set the field null
        choice.setTextChoice(null);

        // Create the Choice, which fails.

        restChoiceMockMvc.perform(post("/api/choices")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(choice)))
            .andExpect(status().isBadRequest());

        List<Choice> choiceList = choiceRepository.findAll();
        assertThat(choiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllChoices() throws Exception {
        // Initialize the database
        choiceRepository.saveAndFlush(choice);

        // Get all the choiceList
        restChoiceMockMvc.perform(get("/api/choices?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(choice.getId())))
            .andExpect(jsonPath("$.[*].textChoice").value(hasItem(DEFAULT_TEXT_CHOICE)))
            .andExpect(jsonPath("$.[*].isanswer").value(hasItem(DEFAULT_ISANSWER.booleanValue())));
    }
    

    @Test
    @Transactional
    public void getChoice() throws Exception {
        // Initialize the database
        choiceRepository.saveAndFlush(choice);

        // Get the choice
        restChoiceMockMvc.perform(get("/api/choices/{id}", choice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(choice.getId()))
            .andExpect(jsonPath("$.textChoice").value(DEFAULT_TEXT_CHOICE))
            .andExpect(jsonPath("$.isanswer").value(DEFAULT_ISANSWER.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllChoicesByTextChoiceIsEqualToSomething() throws Exception {
        // Initialize the database
        choiceRepository.saveAndFlush(choice);

        // Get all the choiceList where textChoice equals to DEFAULT_TEXT_CHOICE
        defaultChoiceShouldBeFound("textChoice.equals=" + DEFAULT_TEXT_CHOICE);

        // Get all the choiceList where textChoice equals to UPDATED_TEXT_CHOICE
        defaultChoiceShouldNotBeFound("textChoice.equals=" + UPDATED_TEXT_CHOICE);
    }

    @Test
    @Transactional
    public void getAllChoicesByTextChoiceIsInShouldWork() throws Exception {
        // Initialize the database
        choiceRepository.saveAndFlush(choice);

        // Get all the choiceList where textChoice in DEFAULT_TEXT_CHOICE or UPDATED_TEXT_CHOICE
        defaultChoiceShouldBeFound("textChoice.in=" + DEFAULT_TEXT_CHOICE + "," + UPDATED_TEXT_CHOICE);

        // Get all the choiceList where textChoice equals to UPDATED_TEXT_CHOICE
        defaultChoiceShouldNotBeFound("textChoice.in=" + UPDATED_TEXT_CHOICE);
    }

    @Test
    @Transactional
    public void getAllChoicesByTextChoiceIsNullOrNotNull() throws Exception {
        // Initialize the database
        choiceRepository.saveAndFlush(choice);

        // Get all the choiceList where textChoice is not null
        defaultChoiceShouldBeFound("textChoice.specified=true");

        // Get all the choiceList where textChoice is null
        defaultChoiceShouldNotBeFound("textChoice.specified=false");
    }

    @Test
    @Transactional
    public void getAllChoicesByIsanswerIsEqualToSomething() throws Exception {
        // Initialize the database
        choiceRepository.saveAndFlush(choice);

        // Get all the choiceList where isanswer equals to DEFAULT_ISANSWER
        defaultChoiceShouldBeFound("isanswer.equals=" + DEFAULT_ISANSWER);

        // Get all the choiceList where isanswer equals to UPDATED_ISANSWER
        defaultChoiceShouldNotBeFound("isanswer.equals=" + UPDATED_ISANSWER);
    }

    @Test
    @Transactional
    public void getAllChoicesByIsanswerIsInShouldWork() throws Exception {
        // Initialize the database
        choiceRepository.saveAndFlush(choice);

        // Get all the choiceList where isanswer in DEFAULT_ISANSWER or UPDATED_ISANSWER
        defaultChoiceShouldBeFound("isanswer.in=" + DEFAULT_ISANSWER + "," + UPDATED_ISANSWER);

        // Get all the choiceList where isanswer equals to UPDATED_ISANSWER
        defaultChoiceShouldNotBeFound("isanswer.in=" + UPDATED_ISANSWER);
    }

    @Test
    @Transactional
    public void getAllChoicesByIsanswerIsNullOrNotNull() throws Exception {
        // Initialize the database
        choiceRepository.saveAndFlush(choice);

        // Get all the choiceList where isanswer is not null
        defaultChoiceShouldBeFound("isanswer.specified=true");

        // Get all the choiceList where isanswer is null
        defaultChoiceShouldNotBeFound("isanswer.specified=false");
    }

    @Test
    @Transactional
    public void getAllChoicesByQuestionIsEqualToSomething() throws Exception {
        // Initialize the database
        Question question = QuestionResourceIntTest.createEntity(em);
        em.persist(question);
        em.flush();
        choice.setQuestion(question);
        choiceRepository.saveAndFlush(choice);
        Long questionId = question.getId();

        // Get all the choiceList where question equals to questionId
        defaultChoiceShouldBeFound("questionId.equals=" + questionId);

        // Get all the choiceList where question equals to questionId + 1
        defaultChoiceShouldNotBeFound("questionId.equals=" + (questionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultChoiceShouldBeFound(String filter) throws Exception {
        restChoiceMockMvc.perform(get("/api/choices?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(choice.getId())))
            .andExpect(jsonPath("$.[*].textChoice").value(hasItem(DEFAULT_TEXT_CHOICE)))
            .andExpect(jsonPath("$.[*].isanswer").value(hasItem(DEFAULT_ISANSWER.booleanValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultChoiceShouldNotBeFound(String filter) throws Exception {
        restChoiceMockMvc.perform(get("/api/choices?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingChoice() throws Exception {
        // Get the choice
        restChoiceMockMvc.perform(get("/api/choices/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateChoice() throws Exception {
        // Initialize the database
        choiceService.save(choice);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockChoiceSearchRepository);

        int databaseSizeBeforeUpdate = choiceRepository.findAll().size();

        // Update the choice
        Choice updatedChoice = choiceRepository.findById(choice.getId()).get();
        // Disconnect from session so that the updates on updatedChoice are not directly saved in db
        em.detach(updatedChoice);
        updatedChoice
            .textChoice(UPDATED_TEXT_CHOICE)
            .isanswer(UPDATED_ISANSWER);

        restChoiceMockMvc.perform(put("/api/choices")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedChoice)))
            .andExpect(status().isOk());

        // Validate the Choice in the database
        List<Choice> choiceList = choiceRepository.findAll();
        assertThat(choiceList).hasSize(databaseSizeBeforeUpdate);
        Choice testChoice = choiceList.get(choiceList.size() - 1);
        assertThat(testChoice.getTextChoice()).isEqualTo(UPDATED_TEXT_CHOICE);
        assertThat(testChoice.isIsanswer()).isEqualTo(UPDATED_ISANSWER);

        // Validate the Choice in Elasticsearch
        verify(mockChoiceSearchRepository, times(1)).save(testChoice);
    }

    @Test
    @Transactional
    public void updateNonExistingChoice() throws Exception {
        int databaseSizeBeforeUpdate = choiceRepository.findAll().size();

        // Create the Choice

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restChoiceMockMvc.perform(put("/api/choices")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(choice)))
            .andExpect(status().isBadRequest());

        // Validate the Choice in the database
        List<Choice> choiceList = choiceRepository.findAll();
        assertThat(choiceList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Choice in Elasticsearch
        verify(mockChoiceSearchRepository, times(0)).save(choice);
    }

    @Test
    @Transactional
    public void deleteChoice() throws Exception {
        // Initialize the database
        choiceService.save(choice);

        int databaseSizeBeforeDelete = choiceRepository.findAll().size();

        // Get the choice
        restChoiceMockMvc.perform(delete("/api/choices/{id}", choice.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Choice> choiceList = choiceRepository.findAll();
        assertThat(choiceList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Choice in Elasticsearch
        verify(mockChoiceSearchRepository, times(1)).deleteById(choice.getId());
    }

    @Test
    @Transactional
    public void searchChoice() throws Exception {
        // Initialize the database
        choiceService.save(choice);
        when(mockChoiceSearchRepository.search(queryStringQuery("id:" + choice.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(choice), PageRequest.of(0, 1), 1));
        // Search the choice
        restChoiceMockMvc.perform(get("/api/_search/choices?query=id:" + choice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(choice.getId())))
            .andExpect(jsonPath("$.[*].textChoice").value(hasItem(DEFAULT_TEXT_CHOICE)))
            .andExpect(jsonPath("$.[*].isanswer").value(hasItem(DEFAULT_ISANSWER.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Choice.class);
        Choice choice1 = new Choice();
        choice1.setId(1L);
        Choice choice2 = new Choice();
        choice2.setId(choice1.getId());
        assertThat(choice1).isEqualTo(choice2);
        choice2.setId(2L);
        assertThat(choice1).isNotEqualTo(choice2);
        choice1.setId(null);
        assertThat(choice1).isNotEqualTo(choice2);
    }
}
