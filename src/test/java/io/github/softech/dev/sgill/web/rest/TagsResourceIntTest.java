package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.Tags;
import io.github.softech.dev.sgill.repository.TagsRepository;
import io.github.softech.dev.sgill.repository.search.TagsSearchRepository;
import io.github.softech.dev.sgill.service.TagsService;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.TagsCriteria;
import io.github.softech.dev.sgill.service.TagsQueryService;

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
 * Test class for the TagsResource REST controller.
 *
 * @see TagsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class TagsResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private TagsRepository tagsRepository;

    

    @Autowired
    private TagsService tagsService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.TagsSearchRepositoryMockConfiguration
     */
    @Autowired
    private TagsSearchRepository mockTagsSearchRepository;

    @Autowired
    private TagsQueryService tagsQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTagsMockMvc;

    private Tags tags;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TagsResource tagsResource = new TagsResource(tagsService, tagsQueryService);
        this.restTagsMockMvc = MockMvcBuilders.standaloneSetup(tagsResource)
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
    public static Tags createEntity(EntityManager em) {
        Tags tags = new Tags()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION);
        return tags;
    }

    @Before
    public void initTest() {
        tags = createEntity(em);
    }

    @Test
    @Transactional
    public void createTags() throws Exception {
        int databaseSizeBeforeCreate = tagsRepository.findAll().size();

        // Create the Tags
        restTagsMockMvc.perform(post("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tags)))
            .andExpect(status().isCreated());

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll();
        assertThat(tagsList).hasSize(databaseSizeBeforeCreate + 1);
        Tags testTags = tagsList.get(tagsList.size() - 1);
        assertThat(testTags.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTags.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Tags in Elasticsearch
        verify(mockTagsSearchRepository, times(1)).save(testTags);
    }

    @Test
    @Transactional
    public void createTagsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tagsRepository.findAll().size();

        // Create the Tags with an existing ID
        tags.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTagsMockMvc.perform(post("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tags)))
            .andExpect(status().isBadRequest());

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll();
        assertThat(tagsList).hasSize(databaseSizeBeforeCreate);

        // Validate the Tags in Elasticsearch
        verify(mockTagsSearchRepository, times(0)).save(tags);
    }

    @Test
    @Transactional
    public void getAllTags() throws Exception {
        // Initialize the database
        tagsRepository.saveAndFlush(tags);

        // Get all the tagsList
        restTagsMockMvc.perform(get("/api/tags?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tags.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
    

    @Test
    @Transactional
    public void getTags() throws Exception {
        // Initialize the database
        tagsRepository.saveAndFlush(tags);

        // Get the tags
        restTagsMockMvc.perform(get("/api/tags/{id}", tags.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tags.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    public void getAllTagsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        tagsRepository.saveAndFlush(tags);

        // Get all the tagsList where name equals to DEFAULT_NAME
        defaultTagsShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the tagsList where name equals to UPDATED_NAME
        defaultTagsShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllTagsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        tagsRepository.saveAndFlush(tags);

        // Get all the tagsList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTagsShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the tagsList where name equals to UPDATED_NAME
        defaultTagsShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllTagsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        tagsRepository.saveAndFlush(tags);

        // Get all the tagsList where name is not null
        defaultTagsShouldBeFound("name.specified=true");

        // Get all the tagsList where name is null
        defaultTagsShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllTagsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        tagsRepository.saveAndFlush(tags);

        // Get all the tagsList where description equals to DEFAULT_DESCRIPTION
        defaultTagsShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the tagsList where description equals to UPDATED_DESCRIPTION
        defaultTagsShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllTagsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        tagsRepository.saveAndFlush(tags);

        // Get all the tagsList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTagsShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the tagsList where description equals to UPDATED_DESCRIPTION
        defaultTagsShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllTagsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        tagsRepository.saveAndFlush(tags);

        // Get all the tagsList where description is not null
        defaultTagsShouldBeFound("description.specified=true");

        // Get all the tagsList where description is null
        defaultTagsShouldNotBeFound("description.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultTagsShouldBeFound(String filter) throws Exception {
        restTagsMockMvc.perform(get("/api/tags?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tags.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultTagsShouldNotBeFound(String filter) throws Exception {
        restTagsMockMvc.perform(get("/api/tags?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingTags() throws Exception {
        // Get the tags
        restTagsMockMvc.perform(get("/api/tags/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTags() throws Exception {
        // Initialize the database
        tagsService.save(tags);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockTagsSearchRepository);

        int databaseSizeBeforeUpdate = tagsRepository.findAll().size();

        // Update the tags
        Tags updatedTags = tagsRepository.findById(tags.getId()).get();
        // Disconnect from session so that the updates on updatedTags are not directly saved in db
        em.detach(updatedTags);
        updatedTags
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION);

        restTagsMockMvc.perform(put("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTags)))
            .andExpect(status().isOk());

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll();
        assertThat(tagsList).hasSize(databaseSizeBeforeUpdate);
        Tags testTags = tagsList.get(tagsList.size() - 1);
        assertThat(testTags.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTags.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Tags in Elasticsearch
        verify(mockTagsSearchRepository, times(1)).save(testTags);
    }

    @Test
    @Transactional
    public void updateNonExistingTags() throws Exception {
        int databaseSizeBeforeUpdate = tagsRepository.findAll().size();

        // Create the Tags

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restTagsMockMvc.perform(put("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tags)))
            .andExpect(status().isBadRequest());

        // Validate the Tags in the database
        List<Tags> tagsList = tagsRepository.findAll();
        assertThat(tagsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tags in Elasticsearch
        verify(mockTagsSearchRepository, times(0)).save(tags);
    }

    @Test
    @Transactional
    public void deleteTags() throws Exception {
        // Initialize the database
        tagsService.save(tags);

        int databaseSizeBeforeDelete = tagsRepository.findAll().size();

        // Get the tags
        restTagsMockMvc.perform(delete("/api/tags/{id}", tags.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Tags> tagsList = tagsRepository.findAll();
        assertThat(tagsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Tags in Elasticsearch
        verify(mockTagsSearchRepository, times(1)).deleteById(tags.getId());
    }

    @Test
    @Transactional
    public void searchTags() throws Exception {
        // Initialize the database
        tagsService.save(tags);
        when(mockTagsSearchRepository.search(queryStringQuery("id:" + tags.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(tags), PageRequest.of(0, 1), 1));
        // Search the tags
        restTagsMockMvc.perform(get("/api/_search/tags?query=id:" + tags.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tags.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tags.class);
        Tags tags1 = new Tags();
        tags1.setId(1L);
        Tags tags2 = new Tags();
        tags2.setId(tags1.getId());
        assertThat(tags1).isEqualTo(tags2);
        tags2.setId(2L);
        assertThat(tags1).isNotEqualTo(tags2);
        tags1.setId(null);
        assertThat(tags1).isNotEqualTo(tags2);
    }
}
