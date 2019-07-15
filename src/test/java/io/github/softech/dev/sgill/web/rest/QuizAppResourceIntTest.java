package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.QuizApp;
import io.github.softech.dev.sgill.domain.Quiz;
import io.github.softech.dev.sgill.domain.Section;
import io.github.softech.dev.sgill.domain.Section;
import io.github.softech.dev.sgill.domain.Question;
import io.github.softech.dev.sgill.repository.QuizAppRepository;
import io.github.softech.dev.sgill.repository.search.QuizAppSearchRepository;
import io.github.softech.dev.sgill.service.QuizAppService;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.QuizAppCriteria;
import io.github.softech.dev.sgill.service.QuizAppQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import java.util.ArrayList;
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
 * Test class for the QuizAppResource REST controller.
 *
 * @see QuizAppResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class QuizAppResourceIntTest {

    @Autowired
    private QuizAppRepository quizAppRepository;
    @Mock
    private QuizAppRepository quizAppRepositoryMock;
    
    @Mock
    private QuizAppService quizAppServiceMock;

    @Autowired
    private QuizAppService quizAppService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.QuizAppSearchRepositoryMockConfiguration
     */
    @Autowired
    private QuizAppSearchRepository mockQuizAppSearchRepository;

    @Autowired
    private QuizAppQueryService quizAppQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restQuizAppMockMvc;

    private QuizApp quizApp;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final QuizAppResource quizAppResource = new QuizAppResource(quizAppService, quizAppQueryService);
        this.restQuizAppMockMvc = MockMvcBuilders.standaloneSetup(quizAppResource)
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
    public static QuizApp createEntity(EntityManager em) {
        QuizApp quizApp = new QuizApp();
        return quizApp;
    }

    @Before
    public void initTest() {
        quizApp = createEntity(em);
    }

    @Test
    @Transactional
    public void createQuizApp() throws Exception {
        int databaseSizeBeforeCreate = quizAppRepository.findAll().size();

        // Create the QuizApp
        restQuizAppMockMvc.perform(post("/api/quiz-apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quizApp)))
            .andExpect(status().isCreated());

        // Validate the QuizApp in the database
        List<QuizApp> quizAppList = quizAppRepository.findAll();
        assertThat(quizAppList).hasSize(databaseSizeBeforeCreate + 1);
        QuizApp testQuizApp = quizAppList.get(quizAppList.size() - 1);

        // Validate the QuizApp in Elasticsearch
        verify(mockQuizAppSearchRepository, times(1)).save(testQuizApp);
    }

    @Test
    @Transactional
    public void createQuizAppWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = quizAppRepository.findAll().size();

        // Create the QuizApp with an existing ID
        quizApp.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuizAppMockMvc.perform(post("/api/quiz-apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quizApp)))
            .andExpect(status().isBadRequest());

        // Validate the QuizApp in the database
        List<QuizApp> quizAppList = quizAppRepository.findAll();
        assertThat(quizAppList).hasSize(databaseSizeBeforeCreate);

        // Validate the QuizApp in Elasticsearch
        verify(mockQuizAppSearchRepository, times(0)).save(quizApp);
    }

    @Test
    @Transactional
    public void getAllQuizApps() throws Exception {
        // Initialize the database
        quizAppRepository.saveAndFlush(quizApp);

        // Get all the quizAppList
        restQuizAppMockMvc.perform(get("/api/quiz-apps?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizApp.getId())));
    }
    
    /**public void getAllQuizAppsWithEagerRelationshipsIsEnabled() throws Exception {
        QuizAppResource quizAppResource = new QuizAppResource(quizAppServiceMock, quizAppQueryService);
        when(quizAppServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restQuizAppMockMvc = MockMvcBuilders.standaloneSetup(quizAppResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restQuizAppMockMvc.perform(get("/api/quiz-apps?eagerload=true"))
        .andExpect(status().isOk());

        verify(quizAppServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    public void getAllQuizAppsWithEagerRelationshipsIsNotEnabled() throws Exception {
        QuizAppResource quizAppResource = new QuizAppResource(quizAppServiceMock, quizAppQueryService);
            when(quizAppServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restQuizAppMockMvc = MockMvcBuilders.standaloneSetup(quizAppResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restQuizAppMockMvc.perform(get("/api/quiz-apps?eagerload=true"))
        .andExpect(status().isOk());

            verify(quizAppServiceMock, times(1)).findAllWithEagerRelationships(any());
    }*/

    @Test
    @Transactional
    public void getQuizApp() throws Exception {
        // Initialize the database
        quizAppRepository.saveAndFlush(quizApp);

        // Get the quizApp
        restQuizAppMockMvc.perform(get("/api/quiz-apps/{id}", quizApp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(quizApp.getId()));
    }

    @Test
    @Transactional
    public void getAllQuizAppsByQuizIsEqualToSomething() throws Exception {
        // Initialize the database
        Quiz quiz = QuizResourceIntTest.createEntity(em);
        em.persist(quiz);
        em.flush();
        quizApp.setQuiz(quiz);
        quizAppRepository.saveAndFlush(quizApp);
        Long quizId = quiz.getId();

        // Get all the quizAppList where quiz equals to quizId
        defaultQuizAppShouldBeFound("quizId.equals=" + quizId);

        // Get all the quizAppList where quiz equals to quizId + 1
        defaultQuizAppShouldNotBeFound("quizId.equals=" + (quizId + 1));
    }


    @Test
    @Transactional
    public void getAllQuizAppsByCurrSectionIsEqualToSomething() throws Exception {
        // Initialize the database
        Section currSection = SectionResourceIntTest.createEntity(em);
        em.persist(currSection);
        em.flush();
        quizApp.setCurrSection(currSection);
        quizAppRepository.saveAndFlush(quizApp);
        Long currSectionId = currSection.getId();

        // Get all the quizAppList where currSection equals to currSectionId
        defaultQuizAppShouldBeFound("currSectionId.equals=" + currSectionId);

        // Get all the quizAppList where currSection equals to currSectionId + 1
        defaultQuizAppShouldNotBeFound("currSectionId.equals=" + (currSectionId + 1));
    }


    @Test
    @Transactional
    public void getAllQuizAppsByNewSectionIsEqualToSomething() throws Exception {
        // Initialize the database
        Section newSection = SectionResourceIntTest.createEntity(em);
        em.persist(newSection);
        em.flush();
        quizApp.setNewSection(newSection);
        quizAppRepository.saveAndFlush(quizApp);
        Long newSectionId = newSection.getId();

        // Get all the quizAppList where newSection equals to newSectionId
        defaultQuizAppShouldBeFound("newSectionId.equals=" + newSectionId);

        // Get all the quizAppList where newSection equals to newSectionId + 1
        defaultQuizAppShouldNotBeFound("newSectionId.equals=" + (newSectionId + 1));
    }


    /**@Test
    @Transactional
    public void getAllQuizAppsByQuestionsIsEqualToSomething() throws Exception {
        // Initialize the database
        Question questions = QuestionResourceIntTest.createEntity(em);
        em.persist(questions);
        em.flush();
        quizApp.addQuestions(questions);
        quizAppRepository.saveAndFlush(quizApp);
        Long questionsId = questions.getId();

        // Get all the quizAppList where questions equals to questionsId
        defaultQuizAppShouldBeFound("questionsId.equals=" + questionsId);

        // Get all the quizAppList where questions equals to questionsId + 1
        defaultQuizAppShouldNotBeFound("questionsId.equals=" + (questionsId + 1));
    }*/

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultQuizAppShouldBeFound(String filter) throws Exception {
        restQuizAppMockMvc.perform(get("/api/quiz-apps?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizApp.getId())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultQuizAppShouldNotBeFound(String filter) throws Exception {
        restQuizAppMockMvc.perform(get("/api/quiz-apps?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingQuizApp() throws Exception {
        // Get the quizApp
        restQuizAppMockMvc.perform(get("/api/quiz-apps/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQuizApp() throws Exception {
        // Initialize the database
        quizAppService.save(quizApp);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockQuizAppSearchRepository);

        int databaseSizeBeforeUpdate = quizAppRepository.findAll().size();

        // Update the quizApp
        QuizApp updatedQuizApp = quizAppRepository.findById(quizApp.getId()).get();
        // Disconnect from session so that the updates on updatedQuizApp are not directly saved in db
        em.detach(updatedQuizApp);

        restQuizAppMockMvc.perform(put("/api/quiz-apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedQuizApp)))
            .andExpect(status().isOk());

        // Validate the QuizApp in the database
        List<QuizApp> quizAppList = quizAppRepository.findAll();
        assertThat(quizAppList).hasSize(databaseSizeBeforeUpdate);
        QuizApp testQuizApp = quizAppList.get(quizAppList.size() - 1);

        // Validate the QuizApp in Elasticsearch
        verify(mockQuizAppSearchRepository, times(1)).save(testQuizApp);
    }

    @Test
    @Transactional
    public void updateNonExistingQuizApp() throws Exception {
        int databaseSizeBeforeUpdate = quizAppRepository.findAll().size();

        // Create the QuizApp

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restQuizAppMockMvc.perform(put("/api/quiz-apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quizApp)))
            .andExpect(status().isBadRequest());

        // Validate the QuizApp in the database
        List<QuizApp> quizAppList = quizAppRepository.findAll();
        assertThat(quizAppList).hasSize(databaseSizeBeforeUpdate);

        // Validate the QuizApp in Elasticsearch
        verify(mockQuizAppSearchRepository, times(0)).save(quizApp);
    }

    @Test
    @Transactional
    public void deleteQuizApp() throws Exception {
        // Initialize the database
        quizAppService.save(quizApp);

        int databaseSizeBeforeDelete = quizAppRepository.findAll().size();

        // Get the quizApp
        restQuizAppMockMvc.perform(delete("/api/quiz-apps/{id}", quizApp.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<QuizApp> quizAppList = quizAppRepository.findAll();
        assertThat(quizAppList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the QuizApp in Elasticsearch
        verify(mockQuizAppSearchRepository, times(1)).deleteById(quizApp.getId());
    }

    @Test
    @Transactional
    public void searchQuizApp() throws Exception {
        // Initialize the database
        quizAppService.save(quizApp);
        when(mockQuizAppSearchRepository.search(queryStringQuery("id:" + quizApp.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(quizApp), PageRequest.of(0, 1), 1));
        // Search the quizApp
        restQuizAppMockMvc.perform(get("/api/_search/quiz-apps?query=id:" + quizApp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizApp.getId())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuizApp.class);
        QuizApp quizApp1 = new QuizApp();
        quizApp1.setId(1L);
        QuizApp quizApp2 = new QuizApp();
        quizApp2.setId(quizApp1.getId());
        assertThat(quizApp1).isEqualTo(quizApp2);
        quizApp2.setId(2L);
        assertThat(quizApp1).isNotEqualTo(quizApp2);
        quizApp1.setId(null);
        assertThat(quizApp1).isNotEqualTo(quizApp2);
    }
}
