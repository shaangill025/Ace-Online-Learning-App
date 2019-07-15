package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.Quiz;
import io.github.softech.dev.sgill.service.QuizService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.QuizCriteria;
import io.github.softech.dev.sgill.service.QuizQueryService;
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
 * REST controller for managing Quiz.
 */
@RestController
@RequestMapping("/api")
public class QuizResource {

    private final Logger log = LoggerFactory.getLogger(QuizResource.class);

    private static final String ENTITY_NAME = "quiz";

    private final QuizService quizService;

    private final QuizQueryService quizQueryService;

    public QuizResource(QuizService quizService, QuizQueryService quizQueryService) {
        this.quizService = quizService;
        this.quizQueryService = quizQueryService;
    }

    /**
     * POST  /quizzes : Create a new quiz.
     *
     * @param quiz the quiz to create
     * @return the ResponseEntity with status 201 (Created) and with body the new quiz, or with status 400 (Bad Request) if the quiz has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/quizzes")
    @Timed
    public ResponseEntity<Quiz> createQuiz(@Valid @RequestBody Quiz quiz) throws URISyntaxException {
        log.debug("REST request to save Quiz : {}", quiz);
        if (quiz.getId() != null) {
            throw new BadRequestAlertException("A new quiz cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Quiz result = quizService.save(quiz);
        return ResponseEntity.created(new URI("/api/quizzes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /quizzes : Updates an existing quiz.
     *
     * @param quiz the quiz to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated quiz,
     * or with status 400 (Bad Request) if the quiz is not valid,
     * or with status 500 (Internal Server Error) if the quiz couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/quizzes")
    @Timed
    public ResponseEntity<Quiz> updateQuiz(@Valid @RequestBody Quiz quiz) throws URISyntaxException {
        log.debug("REST request to update Quiz : {}", quiz);
        if (quiz.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Quiz result = quizService.save(quiz);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, quiz.getId().toString()))
            .body(result);
    }

    /**
     * GET  /quizzes : get all the quizzes.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of quizzes in body
     */
    @GetMapping("/quizzes")
    @Timed
    public ResponseEntity<List<Quiz>> getAllQuizzes(QuizCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Quizzes by criteria: {}", criteria);
        Page<Quiz> page = quizQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/quizzes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /quizzes/:id : get the "id" quiz.
     *
     * @param id the id of the quiz to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the quiz, or with status 404 (Not Found)
     */
    @GetMapping("/quizzes/{id}")
    @Timed
    public ResponseEntity<Quiz> getQuiz(@PathVariable Long id) {
        log.debug("REST request to get Quiz : {}", id);
        Optional<Quiz> quiz = quizService.findOne(id);
        return ResponseUtil.wrapOrNotFound(quiz);
    }

    /**
     * DELETE  /quizzes/:id : delete the "id" quiz.
     *
     * @param id the id of the quiz to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/quizzes/{id}")
    @Timed
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        log.debug("REST request to delete Quiz : {}", id);
        quizService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/quizzes?query=:query : search for the quiz corresponding
     * to the query.
     *
     * @param query the query of the quiz search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/quizzes")
    @Timed
    public ResponseEntity<List<Quiz>> searchQuizzes(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Quizzes for query {}", query);
        Page<Quiz> page = quizService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/quizzes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
