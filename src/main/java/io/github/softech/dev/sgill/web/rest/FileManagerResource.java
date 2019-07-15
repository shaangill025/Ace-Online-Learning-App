package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.FileManager;
import io.github.softech.dev.sgill.domain.Section;
import io.github.softech.dev.sgill.repository.FileManagerRepository;
import io.github.softech.dev.sgill.repository.SectionRepository;
import io.github.softech.dev.sgill.repository.search.FileManagerSearchRepository;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing FileManager.
 */
@RestController
@RequestMapping("/api")
public class FileManagerResource {

    private final Logger log = LoggerFactory.getLogger(FileManagerResource.class);

    private static final String ENTITY_NAME = "fileManager";

    private final FileManagerRepository fileManagerRepository;

    private final FileManagerSearchRepository fileManagerSearchRepository;

    private final SectionRepository sectionRepository;

    public FileManagerResource(FileManagerRepository fileManagerRepository, FileManagerSearchRepository fileManagerSearchRepository, SectionRepository sectionRepository) {
        this.fileManagerRepository = fileManagerRepository;
        this.sectionRepository = sectionRepository;
        this.fileManagerSearchRepository = fileManagerSearchRepository;
    }

    /**
     * POST  /file-managers : Create a new fileManager.
     *
     * @param fileManager the fileManager to create
     * @return the ResponseEntity with status 201 (Created) and with body the new fileManager, or with status 400 (Bad Request) if the fileManager has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/file-managers")
    @Timed
    public ResponseEntity<FileManager> createFileManager(@Valid @RequestBody FileManager fileManager) throws URISyntaxException {
        log.debug("REST request to save FileManager : {}", fileManager);
        if (fileManager.getId() != null) {
            throw new BadRequestAlertException("A new fileManager cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FileManager result = fileManagerRepository.save(fileManager);
        fileManagerSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/file-managers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @GetMapping("/file-managers/file/{sectionid}")
    @Timed
    public byte[] getFileFromSection(@Valid @PathVariable Long sectionid){
        Section tempSection = sectionRepository.findById(sectionid).get();
        List<FileManager> tempFile = fileManagerRepository.findFileManagersBySection(tempSection).orElse(null);
        if (tempFile == null) {
            log.error("No file found for this section or module");
            return null;
        } else if (tempFile.size()>=2) {
            log.error("There are 2 or more file found for this section");
            return null;
        } else {
            return tempFile.get(0).getFile();
        }
    }

    /**
     * PUT  /file-managers : Updates an existing fileManager.
     *
     * @param fileManager the fileManager to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated fileManager,
     * or with status 400 (Bad Request) if the fileManager is not valid,
     * or with status 500 (Internal Server Error) if the fileManager couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/file-managers")
    @Timed
    public ResponseEntity<FileManager> updateFileManager(@Valid @RequestBody FileManager fileManager) throws URISyntaxException {
        log.debug("REST request to update FileManager : {}", fileManager);
        if (fileManager.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        FileManager result = fileManagerRepository.save(fileManager);
        fileManagerSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, fileManager.getId().toString()))
            .body(result);
    }

    /**
     * GET  /file-managers : get all the fileManagers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of fileManagers in body
     */
    @GetMapping("/file-managers")
    @Timed
    public ResponseEntity<List<FileManager>> getAllFileManagers(Pageable pageable) {
        log.debug("REST request to get a page of FileManagers");
        Page<FileManager> page = fileManagerRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/file-managers");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /file-managers/:id : get the "id" fileManager.
     *
     * @param id the id of the fileManager to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the fileManager, or with status 404 (Not Found)
     */
    @GetMapping("/file-managers/{id}")
    @Timed
    public ResponseEntity<FileManager> getFileManager(@PathVariable Long id) {
        log.debug("REST request to get FileManager : {}", id);
        Optional<FileManager> fileManager = fileManagerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(fileManager);
    }

    /**
     * DELETE  /file-managers/:id : delete the "id" fileManager.
     *
     * @param id the id of the fileManager to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/file-managers/{id}")
    @Timed
    public ResponseEntity<Void> deleteFileManager(@PathVariable Long id) {
        log.debug("REST request to delete FileManager : {}", id);

        fileManagerRepository.deleteById(id);
        fileManagerSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/file-managers?query=:query : search for the fileManager corresponding
     * to the query.
     *
     * @param query the query of the fileManager search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/file-managers")
    @Timed
    public ResponseEntity<List<FileManager>> searchFileManagers(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of FileManagers for query {}", query);
        Page<FileManager> page = fileManagerSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/file-managers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
