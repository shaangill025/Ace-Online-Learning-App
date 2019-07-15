package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.LegacyCourses;
import io.github.softech.dev.sgill.service.LegacyCoursesService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.LegacyCoursesCriteria;
import io.github.softech.dev.sgill.service.LegacyCoursesQueryService;
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
 * REST controller for managing LegacyCourses.
 */
@RestController
@RequestMapping("/api")
public class LegacyCoursesResource {

    private final Logger log = LoggerFactory.getLogger(LegacyCoursesResource.class);

    private static final String ENTITY_NAME = "legacyCourses";

    private final LegacyCoursesService legacyCoursesService;

    private final LegacyCoursesQueryService legacyCoursesQueryService;

    public LegacyCoursesResource(LegacyCoursesService legacyCoursesService, LegacyCoursesQueryService legacyCoursesQueryService) {
        this.legacyCoursesService = legacyCoursesService;
        this.legacyCoursesQueryService = legacyCoursesQueryService;
    }

    /**
     * POST  /legacy-courses : Create a new legacyCourses.
     *
     * @param legacyCourses the legacyCourses to create
     * @return the ResponseEntity with status 201 (Created) and with body the new legacyCourses, or with status 400 (Bad Request) if the legacyCourses has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/legacy-courses")
    @Timed
    public ResponseEntity<LegacyCourses> createLegacyCourses(@Valid @RequestBody LegacyCourses legacyCourses) throws URISyntaxException {
        log.debug("REST request to save LegacyCourses : {}", legacyCourses);
        if (legacyCourses.getId() != null) {
            throw new BadRequestAlertException("A new legacyCourses cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LegacyCourses result = legacyCoursesService.save(legacyCourses);
        return ResponseEntity.created(new URI("/api/legacy-courses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /legacy-courses : Updates an existing legacyCourses.
     *
     * @param legacyCourses the legacyCourses to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated legacyCourses,
     * or with status 400 (Bad Request) if the legacyCourses is not valid,
     * or with status 500 (Internal Server Error) if the legacyCourses couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/legacy-courses")
    @Timed
    public ResponseEntity<LegacyCourses> updateLegacyCourses(@Valid @RequestBody LegacyCourses legacyCourses) throws URISyntaxException {
        log.debug("REST request to update LegacyCourses : {}", legacyCourses);
        if (legacyCourses.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LegacyCourses result = legacyCoursesService.save(legacyCourses);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, legacyCourses.getId().toString()))
            .body(result);
    }

    /**
     * GET  /legacy-courses : get all the legacyCourses.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of legacyCourses in body
     */
    @GetMapping("/legacy-courses")
    @Timed
    public ResponseEntity<List<LegacyCourses>> getAllLegacyCourses(LegacyCoursesCriteria criteria, Pageable pageable) {
        log.debug("REST request to get LegacyCourses by criteria: {}", criteria);
        Page<LegacyCourses> page = legacyCoursesQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/legacy-courses");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /legacy-courses/count : count all the legacyCourses.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/legacy-courses/count")
    @Timed
    public ResponseEntity<Long> countLegacyCourses(LegacyCoursesCriteria criteria) {
        log.debug("REST request to count LegacyCourses by criteria: {}", criteria);
        return ResponseEntity.ok().body(legacyCoursesQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /legacy-courses/:id : get the "id" legacyCourses.
     *
     * @param id the id of the legacyCourses to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the legacyCourses, or with status 404 (Not Found)
     */
    @GetMapping("/legacy-courses/{id}")
    @Timed
    public ResponseEntity<LegacyCourses> getLegacyCourses(@PathVariable Long id) {
        log.debug("REST request to get LegacyCourses : {}", id);
        Optional<LegacyCourses> legacyCourses = legacyCoursesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(legacyCourses);
    }

    /**
     * DELETE  /legacy-courses/:id : delete the "id" legacyCourses.
     *
     * @param id the id of the legacyCourses to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/legacy-courses/{id}")
    @Timed
    public ResponseEntity<Void> deleteLegacyCourses(@PathVariable Long id) {
        log.debug("REST request to delete LegacyCourses : {}", id);
        legacyCoursesService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/legacy-courses?query=:query : search for the legacyCourses corresponding
     * to the query.
     *
     * @param query the query of the legacyCourses search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/legacy-courses")
    @Timed
    public ResponseEntity<List<LegacyCourses>> searchLegacyCourses(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of LegacyCourses for query {}", query);
        Page<LegacyCourses> page = legacyCoursesService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/legacy-courses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
