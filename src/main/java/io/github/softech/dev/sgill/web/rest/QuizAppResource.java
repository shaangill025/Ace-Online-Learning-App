package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.QuizApp;
import io.github.softech.dev.sgill.service.QuizAppService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.QuizAppCriteria;
import io.github.softech.dev.sgill.service.QuizAppQueryService;
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
 * REST controller for managing QuizApp.
 */
@RestController
@RequestMapping("/api")
public class QuizAppResource {

    private final Logger log = LoggerFactory.getLogger(QuizAppResource.class);

    private static final String ENTITY_NAME = "quizApp";

    private final QuizAppService quizAppService;

    private final QuizAppQueryService quizAppQueryService;

    public QuizAppResource(QuizAppService quizAppService, QuizAppQueryService quizAppQueryService) {
        this.quizAppService = quizAppService;
        this.quizAppQueryService = quizAppQueryService;
    }

    /**
     * POST  /quiz-apps : Create a new quizApp.
     *
     * @param quizApp the quizApp to create
     * @return the ResponseEntity with status 201 (Created) and with body the new quizApp, or with status 400 (Bad Request) if the quizApp has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/quiz-apps")
    @Timed
    public ResponseEntity<QuizApp> createQuizApp(@RequestBody QuizApp quizApp) throws URISyntaxException {
        log.debug("REST request to save QuizApp : {}", quizApp);
        if (quizApp.getId() != null) {
            throw new BadRequestAlertException("A new quizApp cannot already have an ID", ENTITY_NAME, "idexists");
        }
        QuizApp result = quizAppService.save(quizApp);
        return ResponseEntity.created(new URI("/api/quiz-apps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /quiz-apps : Updates an existing quizApp.
     *
     * @param quizApp the quizApp to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated quizApp,
     * or with status 400 (Bad Request) if the quizApp is not valid,
     * or with status 500 (Internal Server Error) if the quizApp couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/quiz-apps")
    @Timed
    public ResponseEntity<QuizApp> updateQuizApp(@RequestBody QuizApp quizApp) throws URISyntaxException {
        log.debug("REST request to update QuizApp : {}", quizApp);
        if (quizApp.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        QuizApp result = quizAppService.save(quizApp);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, quizApp.getId().toString()))
            .body(result);
    }

    /**
     * GET  /quiz-apps : get all the quizApps.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of quizApps in body
     */
    @GetMapping("/quiz-apps")
    @Timed
    public ResponseEntity<List<QuizApp>> getAllQuizApps(QuizAppCriteria criteria, Pageable pageable) {
        log.debug("REST request to get QuizApps by criteria: {}", criteria);
        Page<QuizApp> page = quizAppQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/quiz-apps");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /quiz-apps/:id : get the "id" quizApp.
     *
     * @param id the id of the quizApp to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the quizApp, or with status 404 (Not Found)
     */
    @GetMapping("/quiz-apps/{id}")
    @Timed
    public ResponseEntity<QuizApp> getQuizApp(@PathVariable Long id) {
        log.debug("REST request to get QuizApp : {}", id);
        Optional<QuizApp> quizApp = quizAppService.findOne(id);
        return ResponseUtil.wrapOrNotFound(quizApp);
    }

    /**
     * DELETE  /quiz-apps/:id : delete the "id" quizApp.
     *
     * @param id the id of the quizApp to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/quiz-apps/{id}")
    @Timed
    public ResponseEntity<Void> deleteQuizApp(@PathVariable Long id) {
        log.debug("REST request to delete QuizApp : {}", id);
        quizAppService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/quiz-apps?query=:query : search for the quizApp corresponding
     * to the query.
     *
     * @param query the query of the quizApp search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/quiz-apps")
    @Timed
    public ResponseEntity<List<QuizApp>> searchQuizApps(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of QuizApps for query {}", query);
        Page<QuizApp> page = quizAppService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/quiz-apps");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
