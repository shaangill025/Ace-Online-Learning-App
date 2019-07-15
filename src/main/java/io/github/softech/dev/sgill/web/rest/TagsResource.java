package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.Tags;
import io.github.softech.dev.sgill.service.TagsService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.TagsCriteria;
import io.github.softech.dev.sgill.service.TagsQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Tags.
 */
@RestController
@RequestMapping("/api")
public class TagsResource {

    private final Logger log = LoggerFactory.getLogger(TagsResource.class);

    private static final String ENTITY_NAME = "tags";

    private final TagsService tagsService;

    private final TagsQueryService tagsQueryService;

    public TagsResource(TagsService tagsService, TagsQueryService tagsQueryService) {
        this.tagsService = tagsService;
        this.tagsQueryService = tagsQueryService;
    }

    /**
     * POST  /tags : Create a new tags.
     *
     * @param tags the tags to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tags, or with status 400 (Bad Request) if the tags has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tags")
    @Timed
    public ResponseEntity<Tags> createTags(@RequestBody Tags tags) throws URISyntaxException {
        log.debug("REST request to save Tags : {}", tags);
        if (tags.getId() != null) {
            throw new BadRequestAlertException("A new tags cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Tags result = tagsService.save(tags);
        return ResponseEntity.created(new URI("/api/tags/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tags : Updates an existing tags.
     *
     * @param tags the tags to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tags,
     * or with status 400 (Bad Request) if the tags is not valid,
     * or with status 500 (Internal Server Error) if the tags couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tags")
    @Timed
    public ResponseEntity<Tags> updateTags(@RequestBody Tags tags) throws URISyntaxException {
        log.debug("REST request to update Tags : {}", tags);
        if (tags.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Tags result = tagsService.save(tags);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, tags.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tags : get all the tags.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of tags in body
     */
    @GetMapping("/tags")
    @Timed
    public ResponseEntity<List<Tags>> getAllTags(TagsCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Tags by criteria: {}", criteria);
        Page<Tags> page = tagsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tags");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tags/:id : get the "id" tags.
     *
     * @param id the id of the tags to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tags, or with status 404 (Not Found)
     */
    @GetMapping("/tags/{id}")
    @Timed
    public ResponseEntity<Tags> getTags(@PathVariable Long id) {
        log.debug("REST request to get Tags : {}", id);
        Optional<Tags> tags = tagsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tags);
    }

    /**
     * DELETE  /tags/:id : delete the "id" tags.
     *
     * @param id the id of the tags to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tags/{id}")
    @Timed
    public ResponseEntity<Void> deleteTags(@PathVariable Long id) {
        log.debug("REST request to delete Tags : {}", id);
        tagsService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/tags?query=:query : search for the tags corresponding
     * to the query.
     *
     * @param query the query of the tags search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/tags")
    @Timed
    public ResponseEntity<List<Tags>> searchTags(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Tags for query {}", query);
        Page<Tags> page = tagsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/tags");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
