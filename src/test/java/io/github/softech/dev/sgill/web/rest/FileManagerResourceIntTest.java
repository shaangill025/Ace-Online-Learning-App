package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.FileManager;
import io.github.softech.dev.sgill.repository.FileManagerRepository;
import io.github.softech.dev.sgill.repository.SectionRepository;
import io.github.softech.dev.sgill.repository.search.FileManagerSearchRepository;
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
 * Test class for the FileManagerResource REST controller.
 *
 * @see FileManagerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class FileManagerResourceIntTest {

    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_TYPE = "image/png";

    @Autowired
    private FileManagerRepository fileManagerRepository;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.FileManagerSearchRepositoryMockConfiguration
     */
    @Autowired
    private FileManagerSearchRepository mockFileManagerSearchRepository;

    @Autowired
    private SectionRepository sectionRepository;

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

    private MockMvc restFileManagerMockMvc;

    private FileManager fileManager;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FileManagerResource fileManagerResource = new FileManagerResource(fileManagerRepository, mockFileManagerSearchRepository, sectionRepository);
        this.restFileManagerMockMvc = MockMvcBuilders.standaloneSetup(fileManagerResource)
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
    public static FileManager createEntity(EntityManager em) {
        FileManager fileManager = new FileManager()
            .file(DEFAULT_FILE)
            .fileContentType(DEFAULT_FILE_CONTENT_TYPE);
        return fileManager;
    }

    @Before
    public void initTest() {
        fileManager = createEntity(em);
    }

    @Test
    @Transactional
    public void createFileManager() throws Exception {
        int databaseSizeBeforeCreate = fileManagerRepository.findAll().size();

        // Create the FileManager
        restFileManagerMockMvc.perform(post("/api/file-managers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileManager)))
            .andExpect(status().isCreated());

        // Validate the FileManager in the database
        List<FileManager> fileManagerList = fileManagerRepository.findAll();
        assertThat(fileManagerList).hasSize(databaseSizeBeforeCreate + 1);
        FileManager testFileManager = fileManagerList.get(fileManagerList.size() - 1);
        assertThat(testFileManager.getFile()).isEqualTo(DEFAULT_FILE);
        assertThat(testFileManager.getFileContentType()).isEqualTo(DEFAULT_FILE_CONTENT_TYPE);

        // Validate the FileManager in Elasticsearch
        verify(mockFileManagerSearchRepository, times(1)).save(testFileManager);
    }

    @Test
    @Transactional
    public void createFileManagerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = fileManagerRepository.findAll().size();

        // Create the FileManager with an existing ID
        fileManager.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFileManagerMockMvc.perform(post("/api/file-managers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileManager)))
            .andExpect(status().isBadRequest());

        // Validate the FileManager in the database
        List<FileManager> fileManagerList = fileManagerRepository.findAll();
        assertThat(fileManagerList).hasSize(databaseSizeBeforeCreate);

        // Validate the FileManager in Elasticsearch
        verify(mockFileManagerSearchRepository, times(0)).save(fileManager);
    }

    @Test
    @Transactional
    public void getAllFileManagers() throws Exception {
        // Initialize the database
        fileManagerRepository.saveAndFlush(fileManager);

        // Get all the fileManagerList
        restFileManagerMockMvc.perform(get("/api/file-managers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fileManager.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))));
    }
    
    @Test
    @Transactional
    public void getFileManager() throws Exception {
        // Initialize the database
        fileManagerRepository.saveAndFlush(fileManager);

        // Get the fileManager
        restFileManagerMockMvc.perform(get("/api/file-managers/{id}", fileManager.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(fileManager.getId().intValue()))
            .andExpect(jsonPath("$.fileContentType").value(DEFAULT_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.file").value(Base64Utils.encodeToString(DEFAULT_FILE)));
    }

    @Test
    @Transactional
    public void getNonExistingFileManager() throws Exception {
        // Get the fileManager
        restFileManagerMockMvc.perform(get("/api/file-managers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFileManager() throws Exception {
        // Initialize the database
        fileManagerRepository.saveAndFlush(fileManager);

        int databaseSizeBeforeUpdate = fileManagerRepository.findAll().size();

        // Update the fileManager
        FileManager updatedFileManager = fileManagerRepository.findById(fileManager.getId()).get();
        // Disconnect from session so that the updates on updatedFileManager are not directly saved in db
        em.detach(updatedFileManager);
        updatedFileManager
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE);

        restFileManagerMockMvc.perform(put("/api/file-managers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedFileManager)))
            .andExpect(status().isOk());

        // Validate the FileManager in the database
        List<FileManager> fileManagerList = fileManagerRepository.findAll();
        assertThat(fileManagerList).hasSize(databaseSizeBeforeUpdate);
        FileManager testFileManager = fileManagerList.get(fileManagerList.size() - 1);
        assertThat(testFileManager.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testFileManager.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);

        // Validate the FileManager in Elasticsearch
        verify(mockFileManagerSearchRepository, times(1)).save(testFileManager);
    }

    @Test
    @Transactional
    public void updateNonExistingFileManager() throws Exception {
        int databaseSizeBeforeUpdate = fileManagerRepository.findAll().size();

        // Create the FileManager

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileManagerMockMvc.perform(put("/api/file-managers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileManager)))
            .andExpect(status().isBadRequest());

        // Validate the FileManager in the database
        List<FileManager> fileManagerList = fileManagerRepository.findAll();
        assertThat(fileManagerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FileManager in Elasticsearch
        verify(mockFileManagerSearchRepository, times(0)).save(fileManager);
    }

    @Test
    @Transactional
    public void deleteFileManager() throws Exception {
        // Initialize the database
        fileManagerRepository.saveAndFlush(fileManager);

        int databaseSizeBeforeDelete = fileManagerRepository.findAll().size();

        // Get the fileManager
        restFileManagerMockMvc.perform(delete("/api/file-managers/{id}", fileManager.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<FileManager> fileManagerList = fileManagerRepository.findAll();
        assertThat(fileManagerList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the FileManager in Elasticsearch
        verify(mockFileManagerSearchRepository, times(1)).deleteById(fileManager.getId());
    }

    @Test
    @Transactional
    public void searchFileManager() throws Exception {
        // Initialize the database
        fileManagerRepository.saveAndFlush(fileManager);
        when(mockFileManagerSearchRepository.search(queryStringQuery("id:" + fileManager.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(fileManager), PageRequest.of(0, 1), 1));
        // Search the fileManager
        restFileManagerMockMvc.perform(get("/api/_search/file-managers?query=id:" + fileManager.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fileManager.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileManager.class);
        FileManager fileManager1 = new FileManager();
        fileManager1.setId(1L);
        FileManager fileManager2 = new FileManager();
        fileManager2.setId(fileManager1.getId());
        assertThat(fileManager1).isEqualTo(fileManager2);
        fileManager2.setId(2L);
        assertThat(fileManager1).isNotEqualTo(fileManager2);
        fileManager1.setId(null);
        assertThat(fileManager1).isNotEqualTo(fileManager2);
    }
}
