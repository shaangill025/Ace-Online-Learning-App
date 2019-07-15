package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.Choice;
import io.github.softech.dev.sgill.service.ChoiceService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.ChoiceCriteria;
import io.github.softech.dev.sgill.service.ChoiceQueryService;
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
 * REST controller for managing Choice.
 */
@RestController
@RequestMapping("/api")
public class ChoiceResource {

    private final Logger log = LoggerFactory.getLogger(ChoiceResource.class);

    private static final String ENTITY_NAME = "choice";

    private final ChoiceService choiceService;

    private final ChoiceQueryService choiceQueryService;

    public ChoiceResource(ChoiceService choiceService, ChoiceQueryService choiceQueryService) {
        this.choiceService = choiceService;
        this.choiceQueryService = choiceQueryService;
    }

    /**
     * POST  /choices : Create a new choice.
     *
     * @param choice the choice to create
     * @return the ResponseEntity with status 201 (Created) and with body the new choice, or with status 400 (Bad Request) if the choice has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/choices")
    @Timed
    public ResponseEntity<Choice> createChoice(@Valid @RequestBody Choice choice) throws URISyntaxException {
        log.debug("REST request to save Choice : {}", choice);
        if (choice.getId() != null) {
            throw new BadRequestAlertException("A new choice cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Choice result = choiceService.save(choice);
        return ResponseEntity.created(new URI("/api/choices/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /choices : Updates an existing choice.
     *
     * @param choice the choice to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated choice,
     * or with status 400 (Bad Request) if the choice is not valid,
     * or with status 500 (Internal Server Error) if the choice couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/choices")
    @Timed
    public ResponseEntity<Choice> updateChoice(@Valid @RequestBody Choice choice) throws URISyntaxException {
        log.debug("REST request to update Choice : {}", choice);
        if (choice.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Choice result = choiceService.save(choice);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, choice.getId().toString()))
            .body(result);
    }

    /**
     * GET  /choices : get all the choices.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of choices in body
     */
    @GetMapping("/choices")
    @Timed
    public ResponseEntity<List<Choice>> getAllChoices(ChoiceCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Choices by criteria: {}", criteria);
        Page<Choice> page = choiceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/choices");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /choices/:id : get the "id" choice.
     *
     * @param id the id of the choice to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the choice, or with status 404 (Not Found)
     */
    @GetMapping("/choices/{id}")
    @Timed
    public ResponseEntity<Choice> getChoice(@PathVariable Long id) {
        log.debug("REST request to get Choice : {}", id);
        Optional<Choice> choice = choiceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(choice);
    }

    /**
     * DELETE  /choices/:id : delete the "id" choice.
     *
     * @param id the id of the choice to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/choices/{id}")
    @Timed
    public ResponseEntity<Void> deleteChoice(@PathVariable Long id) {
        log.debug("REST request to delete Choice : {}", id);
        choiceService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/choices?query=:query : search for the choice corresponding
     * to the query.
     *
     * @param query the query of the choice search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/choices")
    @Timed
    public ResponseEntity<List<Choice>> searchChoices(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Choices for query {}", query);
        Page<Choice> page = choiceService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/choices");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
