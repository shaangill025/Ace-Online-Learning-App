package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.Quiz;
import io.github.softech.dev.sgill.domain.Section;
import io.github.softech.dev.sgill.repository.QuizRepository;
import io.github.softech.dev.sgill.repository.search.QuizSearchRepository;
import io.github.softech.dev.sgill.service.QuizService;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.QuizCriteria;
import io.github.softech.dev.sgill.service.QuizQueryService;

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
 * Test class for the QuizResource REST controller.
 *
 * @see QuizResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class QuizResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DIFFICULTY = "AAAAAAAAAA";
    private static final String UPDATED_DIFFICULTY = "BBBBBBBBBB";

    private static final Integer DEFAULT_PASSINGSCORE = 1;
    private static final Integer UPDATED_PASSINGSCORE = 2;

    @Autowired
    private QuizRepository quizRepository;

    

    @Autowired
    private QuizService quizService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.QuizSearchRepositoryMockConfiguration
     */
    @Autowired
    private QuizSearchRepository mockQuizSearchRepository;

    @Autowired
    private QuizQueryService quizQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restQuizMockMvc;

    private Quiz quiz;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final QuizResource quizResource = new QuizResource(quizService, quizQueryService);
        this.restQuizMockMvc = MockMvcBuilders.standaloneSetup(quizResource)
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
    public static Quiz createEntity(EntityManager em) {
        Quiz quiz = new Quiz()
            .name(DEFAULT_NAME)
            .difficulty(DEFAULT_DIFFICULTY)
            .passingscore(DEFAULT_PASSINGSCORE);
        return quiz;
    }

    @Before
    public void initTest() {
        quiz = createEntity(em);
    }

    @Test
    @Transactional
    public void createQuiz() throws Exception {
        int databaseSizeBeforeCreate = quizRepository.findAll().size();

        // Create the Quiz
        restQuizMockMvc.perform(post("/api/quizzes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quiz)))
            .andExpect(status().isCreated());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeCreate + 1);
        Quiz testQuiz = quizList.get(quizList.size() - 1);
        assertThat(testQuiz.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testQuiz.getDifficulty()).isEqualTo(DEFAULT_DIFFICULTY);
        assertThat(testQuiz.getPassingscore()).isEqualTo(DEFAULT_PASSINGSCORE);

        // Validate the Quiz in Elasticsearch
        verify(mockQuizSearchRepository, times(1)).save(testQuiz);
    }

    @Test
    @Transactional
    public void createQuizWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = quizRepository.findAll().size();

        // Create the Quiz with an existing ID
        quiz.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuizMockMvc.perform(post("/api/quizzes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quiz)))
            .andExpect(status().isBadRequest());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeCreate);

        // Validate the Quiz in Elasticsearch
        verify(mockQuizSearchRepository, times(0)).save(quiz);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = quizRepository.findAll().size();
        // set the field null
        quiz.setName(null);

        // Create the Quiz, which fails.

        restQuizMockMvc.perform(post("/api/quizzes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quiz)))
            .andExpect(status().isBadRequest());

        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllQuizzes() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizList
        restQuizMockMvc.perform(get("/api/quizzes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quiz.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].difficulty").value(hasItem(DEFAULT_DIFFICULTY)))
            .andExpect(jsonPath("$.[*].passingscore").value(hasItem(DEFAULT_PASSINGSCORE)));
    }
    

    @Test
    @Transactional
    public void getQuiz() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get the quiz
        restQuizMockMvc.perform(get("/api/quizzes/{id}", quiz.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(quiz.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.difficulty").value(DEFAULT_DIFFICULTY))
            .andExpect(jsonPath("$.passingscore").value(DEFAULT_PASSINGSCORE));
    }

    @Test
    @Transactional
    public void getAllQuizzesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizList where name equals to DEFAULT_NAME
        defaultQuizShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the quizList where name equals to UPDATED_NAME
        defaultQuizShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllQuizzesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizList where name in DEFAULT_NAME or UPDATED_NAME
        defaultQuizShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the quizList where name equals to UPDATED_NAME
        defaultQuizShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllQuizzesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizList where name is not null
        defaultQuizShouldBeFound("name.specified=true");

        // Get all the quizList where name is null
        defaultQuizShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllQuizzesByDifficultyIsEqualToSomething() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizList where difficulty equals to DEFAULT_DIFFICULTY
        defaultQuizShouldBeFound("difficulty.equals=" + DEFAULT_DIFFICULTY);

        // Get all the quizList where difficulty equals to UPDATED_DIFFICULTY
        defaultQuizShouldNotBeFound("difficulty.equals=" + UPDATED_DIFFICULTY);
    }

    @Test
    @Transactional
    public void getAllQuizzesByDifficultyIsInShouldWork() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizList where difficulty in DEFAULT_DIFFICULTY or UPDATED_DIFFICULTY
        defaultQuizShouldBeFound("difficulty.in=" + DEFAULT_DIFFICULTY + "," + UPDATED_DIFFICULTY);

        // Get all the quizList where difficulty equals to UPDATED_DIFFICULTY
        defaultQuizShouldNotBeFound("difficulty.in=" + UPDATED_DIFFICULTY);
    }

    @Test
    @Transactional
    public void getAllQuizzesByDifficultyIsNullOrNotNull() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizList where difficulty is not null
        defaultQuizShouldBeFound("difficulty.specified=true");

        // Get all the quizList where difficulty is null
        defaultQuizShouldNotBeFound("difficulty.specified=false");
    }

    @Test
    @Transactional
    public void getAllQuizzesByPassingscoreIsEqualToSomething() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizList where passingscore equals to DEFAULT_PASSINGSCORE
        defaultQuizShouldBeFound("passingscore.equals=" + DEFAULT_PASSINGSCORE);

        // Get all the quizList where passingscore equals to UPDATED_PASSINGSCORE
        defaultQuizShouldNotBeFound("passingscore.equals=" + UPDATED_PASSINGSCORE);
    }

    @Test
    @Transactional
    public void getAllQuizzesByPassingscoreIsInShouldWork() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizList where passingscore in DEFAULT_PASSINGSCORE or UPDATED_PASSINGSCORE
        defaultQuizShouldBeFound("passingscore.in=" + DEFAULT_PASSINGSCORE + "," + UPDATED_PASSINGSCORE);

        // Get all the quizList where passingscore equals to UPDATED_PASSINGSCORE
        defaultQuizShouldNotBeFound("passingscore.in=" + UPDATED_PASSINGSCORE);
    }

    @Test
    @Transactional
    public void getAllQuizzesByPassingscoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizList where passingscore is not null
        defaultQuizShouldBeFound("passingscore.specified=true");

        // Get all the quizList where passingscore is null
        defaultQuizShouldNotBeFound("passingscore.specified=false");
    }

    @Test
    @Transactional
    public void getAllQuizzesByPassingscoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizList where passingscore greater than or equals to DEFAULT_PASSINGSCORE
        defaultQuizShouldBeFound("passingscore.greaterOrEqualThan=" + DEFAULT_PASSINGSCORE);

        // Get all the quizList where passingscore greater than or equals to UPDATED_PASSINGSCORE
        defaultQuizShouldNotBeFound("passingscore.greaterOrEqualThan=" + UPDATED_PASSINGSCORE);
    }

    @Test
    @Transactional
    public void getAllQuizzesByPassingscoreIsLessThanSomething() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizList where passingscore less than or equals to DEFAULT_PASSINGSCORE
        defaultQuizShouldNotBeFound("passingscore.lessThan=" + DEFAULT_PASSINGSCORE);

        // Get all the quizList where passingscore less than or equals to UPDATED_PASSINGSCORE
        defaultQuizShouldBeFound("passingscore.lessThan=" + UPDATED_PASSINGSCORE);
    }


    @Test
    @Transactional
    public void getAllQuizzesByNewSectionIsEqualToSomething() throws Exception {
        // Initialize the database
        Section newSection = SectionResourceIntTest.createEntity(em);
        em.persist(newSection);
        em.flush();
        quiz.setNewSection(newSection);
        quizRepository.saveAndFlush(quiz);
        Long newSectionId = newSection.getId();

        // Get all the quizList where newSection equals to newSectionId
        defaultQuizShouldBeFound("newSectionId.equals=" + newSectionId);

        // Get all the quizList where newSection equals to newSectionId + 1
        defaultQuizShouldNotBeFound("newSectionId.equals=" + (newSectionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultQuizShouldBeFound(String filter) throws Exception {
        restQuizMockMvc.perform(get("/api/quizzes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quiz.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].difficulty").value(hasItem(DEFAULT_DIFFICULTY)))
            .andExpect(jsonPath("$.[*].passingscore").value(hasItem(DEFAULT_PASSINGSCORE)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultQuizShouldNotBeFound(String filter) throws Exception {
        restQuizMockMvc.perform(get("/api/quizzes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingQuiz() throws Exception {
        // Get the quiz
        restQuizMockMvc.perform(get("/api/quizzes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQuiz() throws Exception {
        // Initialize the database
        quizService.save(quiz);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockQuizSearchRepository);

        int databaseSizeBeforeUpdate = quizRepository.findAll().size();

        // Update the quiz
        Quiz updatedQuiz = quizRepository.findById(quiz.getId()).get();
        // Disconnect from session so that the updates on updatedQuiz are not directly saved in db
        em.detach(updatedQuiz);
        updatedQuiz
            .name(UPDATED_NAME)
            .difficulty(UPDATED_DIFFICULTY)
            .passingscore(UPDATED_PASSINGSCORE);

        restQuizMockMvc.perform(put("/api/quizzes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedQuiz)))
            .andExpect(status().isOk());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeUpdate);
        Quiz testQuiz = quizList.get(quizList.size() - 1);
        assertThat(testQuiz.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testQuiz.getDifficulty()).isEqualTo(UPDATED_DIFFICULTY);
        assertThat(testQuiz.getPassingscore()).isEqualTo(UPDATED_PASSINGSCORE);

        // Validate the Quiz in Elasticsearch
        verify(mockQuizSearchRepository, times(1)).save(testQuiz);
    }

    @Test
    @Transactional
    public void updateNonExistingQuiz() throws Exception {
        int databaseSizeBeforeUpdate = quizRepository.findAll().size();

        // Create the Quiz

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restQuizMockMvc.perform(put("/api/quizzes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quiz)))
            .andExpect(status().isBadRequest());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Quiz in Elasticsearch
        verify(mockQuizSearchRepository, times(0)).save(quiz);
    }

    @Test
    @Transactional
    public void deleteQuiz() throws Exception {
        // Initialize the database
        quizService.save(quiz);

        int databaseSizeBeforeDelete = quizRepository.findAll().size();

        // Get the quiz
        restQuizMockMvc.perform(delete("/api/quizzes/{id}", quiz.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Quiz in Elasticsearch
        verify(mockQuizSearchRepository, times(1)).deleteById(quiz.getId());
    }

    @Test
    @Transactional
    public void searchQuiz() throws Exception {
        // Initialize the database
        quizService.save(quiz);
        when(mockQuizSearchRepository.search(queryStringQuery("id:" + quiz.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(quiz), PageRequest.of(0, 1), 1));
        // Search the quiz
        restQuizMockMvc.perform(get("/api/_search/quizzes?query=id:" + quiz.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quiz.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].difficulty").value(hasItem(DEFAULT_DIFFICULTY)))
            .andExpect(jsonPath("$.[*].passingscore").value(hasItem(DEFAULT_PASSINGSCORE)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Quiz.class);
        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        Quiz quiz2 = new Quiz();
        quiz2.setId(quiz1.getId());
        assertThat(quiz1).isEqualTo(quiz2);
        quiz2.setId(2L);
        assertThat(quiz1).isNotEqualTo(quiz2);
        quiz1.setId(null);
        assertThat(quiz1).isNotEqualTo(quiz2);
    }
}
