package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.*;
import io.github.softech.dev.sgill.repository.*;
import io.github.softech.dev.sgill.service.QuizHistoryService;
import io.github.softech.dev.sgill.service.SectionService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.QuizHistoryCriteria;
import io.github.softech.dev.sgill.service.QuizHistoryQueryService;
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
 * REST controller for managing QuizHistory.
 */
@RestController
@RequestMapping("/api")
public class QuizHistoryResource {

    private final Logger log = LoggerFactory.getLogger(QuizHistoryResource.class);

    private static final String ENTITY_NAME = "quizHistory";

    private final QuizHistoryService quizHistoryService;

    private final QuizHistoryQueryService quizHistoryQueryService;

    private final QuizHistoryRepository quizHistoryRepository;

    private SectionHistoryRepository sectionHistoryRepository;

    private final SectionService sectionService;

    private final SectionRepository sectionRepository;

    private final CustomerRepository customerRepository;

    private final QuizRepository quizRepository;

    public QuizHistoryResource(QuizHistoryService quizHistoryService, QuizHistoryQueryService quizHistoryQueryService, SectionHistoryRepository
        sectionHistoryRepository,QuizHistoryRepository quizHistoryRepository, SectionService sectionService, SectionRepository sectionRepository,
                               CustomerRepository customerRepository, QuizRepository quizRepository) {
        this.quizHistoryService = quizHistoryService;
        this.quizHistoryQueryService = quizHistoryQueryService;
        this.quizHistoryRepository = quizHistoryRepository;
        this.sectionService = sectionService;
        this.sectionRepository = sectionRepository;
        this.customerRepository= customerRepository;
        this.quizRepository = quizRepository;
    }

    /**
     * POST  /quiz-histories : Create a new quizHistory.
     *
     * @param quizHistory the quizHistory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new quizHistory, or with status 400 (Bad Request) if the quizHistory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/quiz-histories")
    @Timed
    public ResponseEntity<QuizHistory> createQuizHistory(@RequestBody QuizHistory quizHistory) throws URISyntaxException {
        log.debug("REST request to save QuizHistory : {}", quizHistory);
        if (quizHistory.getId() != null) {
            throw new BadRequestAlertException("A new quizHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        QuizHistory result = quizHistoryService.save(quizHistory);
        return ResponseEntity.created(new URI("/api/quiz-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /quiz-histories : Updates an existing quizHistory.
     *
     * @param quizHistory the quizHistory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated quizHistory,
     * or with status 400 (Bad Request) if the quizHistory is not valid,
     * or with status 500 (Internal Server Error) if the quizHistory couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/quiz-histories")
    @Timed
    public ResponseEntity<QuizHistory> updateQuizHistory(@RequestBody QuizHistory quizHistory) throws URISyntaxException {
        log.debug("REST request to update QuizHistory : {}", quizHistory);
        if (quizHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        QuizHistory result = quizHistoryService.save(quizHistory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, quizHistory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /quiz-histories : get all the quizHistories.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of quizHistories in body
     */
    @GetMapping("/quiz-histories")
    @Timed
    public ResponseEntity<List<QuizHistory>> getAllQuizHistories(QuizHistoryCriteria criteria, Pageable pageable) {
        log.debug("REST request to get QuizHistories by criteria: {}", criteria);
        Page<QuizHistory> page = quizHistoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/quiz-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/attempt/{sectionid}/watched/quiz-history/{customerid}")
    @Timed
    public Boolean getLastAttemptQuizHistory(@PathVariable Long customerid, @PathVariable Long sectionid) {
        log.debug("REST request to get recent last Section watch history by customer : {}", customerid);
        Section tempSection = sectionService.findOne(sectionid).get();
        Customer tempCust = customerRepository.findById(customerid).get();
        List<SectionHistory> tempHistList = sectionHistoryRepository.findSectionHistoriesBySectionAndCustomerOrderByIdDesc(tempSection, tempCust).orElse(null);
        if (tempHistList == null) {
            return false;
        } else {
            SectionHistory tempHist = tempHistList.get(0);
            List<Quiz> tempQuizList = quizRepository.findQuizzesByNewSectionOrderByIdDesc(tempHist.getSection()).orElse(null);
            if (tempQuizList == null) {
                return tempHist.isWatched();
            } else {
                Quiz tempQuiz = tempQuizList.get(0);
                List<QuizHistory> tempQuizHistList = quizHistoryRepository.findQuizHistoriesByCustomerAndQuizOrderByIdDesc(tempCust, tempQuiz).orElse(null);
                if (tempQuizHistList == null) {
                    return false;
                } else {
                    QuizHistory temp = tempQuizHistList.get(0);
                    return temp.isPassed();
                }
            }
        }
    }

    /**
     * GET  /quiz-histories/:id : get the "id" quizHistory.
     *
     * @param id the id of the quizHistory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the quizHistory, or with status 404 (Not Found)
     */
    @GetMapping("/quiz-histories/{id}")
    @Timed
    public ResponseEntity<QuizHistory> getQuizHistory(@PathVariable Long id) {
        log.debug("REST request to get QuizHistory : {}", id);
        Optional<QuizHistory> quizHistory = quizHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(quizHistory);
    }

    /**
     * DELETE  /quiz-histories/:id : delete the "id" quizHistory.
     *
     * @param id the id of the quizHistory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/quiz-histories/{id}")
    @Timed
    public ResponseEntity<Void> deleteQuizHistory(@PathVariable Long id) {
        log.debug("REST request to delete QuizHistory : {}", id);
        quizHistoryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/quiz-histories?query=:query : search for the quizHistory corresponding
     * to the query.
     *
     * @param query the query of the quizHistory search
     * @param pageable the pagination information
     * @return the result of the search
     */

    @GetMapping("/_search/quiz-histories")
    @Timed
    public ResponseEntity<List<QuizHistory>> searchQuizHistories(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of QuizHistories for query {}", query);
        Page<QuizHistory> page = quizHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/quiz-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
