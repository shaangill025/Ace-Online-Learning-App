package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.Section;
import io.github.softech.dev.sgill.repository.SectionRepository;
import io.github.softech.dev.sgill.service.SectionService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.SectionCriteria;
import io.github.softech.dev.sgill.service.SectionQueryService;
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
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Section.
 */
@RestController
@RequestMapping("/api")
public class SectionResource {

    private final Logger log = LoggerFactory.getLogger(SectionResource.class);

    private static final String ENTITY_NAME = "section";

    private final SectionService sectionService;

    private final SectionQueryService sectionQueryService;

    private final SectionRepository sectionRepository;

    public SectionResource(SectionService sectionService, SectionQueryService sectionQueryService, SectionRepository sectionRepository) {
        this.sectionService = sectionService;
        this.sectionQueryService = sectionQueryService;
        this.sectionRepository = sectionRepository;
    }

    /**
     * POST  /sections : Create a new section.
     *
     * @param section the section to create
     * @return the ResponseEntity with status 201 (Created) and with body the new section, or with status 400 (Bad Request) if the section has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/sections")
    @Timed
    public ResponseEntity<Section> createSection(@Valid @RequestBody Section section) throws URISyntaxException {
        log.debug("REST request to save Section : {}", section);
        if (section.getId() != null) {
            throw new BadRequestAlertException("A new section cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Section result = sectionService.save(section);
        return ResponseEntity.created(new URI("/api/sections/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sections : Updates an existing section.
     *
     * @param section the section to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated section,
     * or with status 400 (Bad Request) if the section is not valid,
     * or with status 500 (Internal Server Error) if the section couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/sections")
    @Timed
    public ResponseEntity<Section> updateSection(@Valid @RequestBody Section section) throws URISyntaxException {
        log.debug("REST request to update Section : {}", section);
        if (section.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Section result = sectionService.save(section);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, section.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sections : get all the sections.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of sections in body
     */
    @GetMapping("/sections")
    @Timed
    public ResponseEntity<List<Section>> getAllSections(SectionCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Sections by criteria: {}", criteria);
        Page<Section> page = sectionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sections");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /sections/:id : get the "id" section.
     *
     * @param id the id of the section to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the section, or with status 404 (Not Found)
     */
    @GetMapping("/sections/{id}")
    @Timed
    public ResponseEntity<Section> getSection(@PathVariable Long id) {
        log.debug("REST request to get Section : {}", id);
        Optional<Section> section = sectionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(section);
    }

    @GetMapping("bycourse/sections/{id}")
    @Timed
    public ResponseEntity<Section> getbyCourseSection(@PathVariable Long id) {
        log.debug("REST request to get Section by Course : {}", id);
        Optional<Section> section = sectionRepository.getSectionByCourse(id);
        return ResponseUtil.wrapOrNotFound(section);
    }

    @GetMapping("count/bycourse/sections/{id}")
    @Timed
    public ResponseEntity<List<Section>> getCountbyCourseSection(@PathVariable Long id) {
        log.debug("REST request to get Section by Course : {}", id);
        Optional<List<Section>> section = sectionRepository.getSectionsByCourse(id);
        return ResponseUtil.wrapOrNotFound(section);
    }

    /**
     * DELETE  /sections/:id : delete the "id" section.
     *
     * @param id the id of the section to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/sections/{id}")
    @Timed
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        log.debug("REST request to delete Section : {}", id);
        sectionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/sections?query=:query : search for the section corresponding
     * to the query.
     *
     * @param query the query of the section search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/sections")
    @Timed
    public ResponseEntity<List<Section>> searchSections(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Sections for query {}", query);
        Page<Section> page = sectionService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/sections");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
