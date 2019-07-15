package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.QuestionHistory;
import io.github.softech.dev.sgill.service.QuestionHistoryService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.QuestionHistoryCriteria;
import io.github.softech.dev.sgill.service.QuestionHistoryQueryService;
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
 * REST controller for managing QuestionHistory.
 */
@RestController
@RequestMapping("/api")
public class QuestionHistoryResource {

    private final Logger log = LoggerFactory.getLogger(QuestionHistoryResource.class);

    private static final String ENTITY_NAME = "questionHistory";

    private final QuestionHistoryService questionHistoryService;

    private final QuestionHistoryQueryService questionHistoryQueryService;

    public QuestionHistoryResource(QuestionHistoryService questionHistoryService, QuestionHistoryQueryService questionHistoryQueryService) {
        this.questionHistoryService = questionHistoryService;
        this.questionHistoryQueryService = questionHistoryQueryService;
    }

    /**
     * POST  /question-histories : Create a new questionHistory.
     *
     * @param questionHistory the questionHistory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new questionHistory, or with status 400 (Bad Request) if the questionHistory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/question-histories")
    @Timed
    public ResponseEntity<QuestionHistory> createQuestionHistory(@RequestBody QuestionHistory questionHistory) throws URISyntaxException {
        log.debug("REST request to save QuestionHistory : {}", questionHistory);
        if (questionHistory.getId() != null) {
            throw new BadRequestAlertException("A new questionHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        QuestionHistory result = questionHistoryService.save(questionHistory);
        return ResponseEntity.created(new URI("/api/question-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /question-histories : Updates an existing questionHistory.
     *
     * @param questionHistory the questionHistory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated questionHistory,
     * or with status 400 (Bad Request) if the questionHistory is not valid,
     * or with status 500 (Internal Server Error) if the questionHistory couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/question-histories")
    @Timed
    public ResponseEntity<QuestionHistory> updateQuestionHistory(@RequestBody QuestionHistory questionHistory) throws URISyntaxException {
        log.debug("REST request to update QuestionHistory : {}", questionHistory);
        if (questionHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        QuestionHistory result = questionHistoryService.save(questionHistory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, questionHistory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /question-histories : get all the questionHistories.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of questionHistories in body
     */
    @GetMapping("/question-histories")
    @Timed
    public ResponseEntity<List<QuestionHistory>> getAllQuestionHistories(QuestionHistoryCriteria criteria, Pageable pageable) {
        log.debug("REST request to get QuestionHistories by criteria: {}", criteria);
        Page<QuestionHistory> page = questionHistoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/question-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /question-histories/:id : get the "id" questionHistory.
     *
     * @param id the id of the questionHistory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the questionHistory, or with status 404 (Not Found)
     */
    @GetMapping("/question-histories/{id}")
    @Timed
    public ResponseEntity<QuestionHistory> getQuestionHistory(@PathVariable Long id) {
        log.debug("REST request to get QuestionHistory : {}", id);
        Optional<QuestionHistory> questionHistory = questionHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(questionHistory);
    }

    /**
     * DELETE  /question-histories/:id : delete the "id" questionHistory.
     *
     * @param id the id of the questionHistory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/question-histories/{id}")
    @Timed
    public ResponseEntity<Void> deleteQuestionHistory(@PathVariable Long id) {
        log.debug("REST request to delete QuestionHistory : {}", id);
        questionHistoryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/question-histories?query=:query : search for the questionHistory corresponding
     * to the query.
     *
     * @param query the query of the questionHistory search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/question-histories")
    @Timed
    public ResponseEntity<List<QuestionHistory>> searchQuestionHistories(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of QuestionHistories for query {}", query);
        Page<QuestionHistory> page = questionHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/question-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
