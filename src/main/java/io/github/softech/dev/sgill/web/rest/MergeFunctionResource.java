package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.*;
import io.github.softech.dev.sgill.repository.LegacyCoursesRepository;
import io.github.softech.dev.sgill.repository.MergeFunctionRepository;
import io.github.softech.dev.sgill.repository.ServicelistRepository;
import io.github.softech.dev.sgill.repository.search.MergeFunctionSearchRepository;
import io.github.softech.dev.sgill.service.*;
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
 * REST controller for managing MergeFunction.
 */
@RestController
@RequestMapping("/api")
public class MergeFunctionResource {

    private final Logger log = LoggerFactory.getLogger(MergeFunctionResource.class);

    private static final String ENTITY_NAME = "mergeFunction";

    private final MergeFunctionRepository mergeFunctionRepository;

    private final MergeFunctionSearchRepository mergeFunctionSearchRepository;

    private final CustomerResource customerResource;

    private final LegacyCoursesRepository legacyCoursesRepository;

    private final ServicelistRepository servicelistRepository;

    private final CertificateService certificateService;

    private final QuestionHistoryService questionHistoryService;

    private final QuizHistoryService quizHistoryService;

    private final CourseHistoryService courseHistoryService;

    private final CustomerService customerService;

    public MergeFunctionResource(MergeFunctionRepository mergeFunctionRepository, MergeFunctionSearchRepository mergeFunctionSearchRepository, CustomerResource customerResource,
                                 LegacyCoursesRepository legacyCoursesRepository, ServicelistRepository servicelistRepository, CertificateService certificateService,
                                 QuestionHistoryService questionHistoryService, QuizHistoryService quizHistoryService, CourseHistoryService courseHistoryService,
                                 CustomerService customerService) {
        this.mergeFunctionRepository = mergeFunctionRepository;
        this.mergeFunctionSearchRepository = mergeFunctionSearchRepository;
        this.customerResource = customerResource;
        this.legacyCoursesRepository = legacyCoursesRepository;
        this.servicelistRepository = servicelistRepository;
        this.questionHistoryService = questionHistoryService;
        this.quizHistoryService = quizHistoryService;
        this.certificateService = certificateService;
        this.customerService = customerService;
        this.courseHistoryService = courseHistoryService;
    }

    /**
     * POST  /merge-functions : Create a new mergeFunction.
     *
     * @param mergeFunction the mergeFunction to create
     * @return the ResponseEntity with status 201 (Created) and with body the new mergeFunction, or with status 400 (Bad Request) if the mergeFunction has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/merge-functions")
    @Timed
    public ResponseEntity<MergeFunction> createMergeFunction(@Valid @RequestBody MergeFunction mergeFunction) throws URISyntaxException {
        log.debug("REST request to save MergeFunction : {}", mergeFunction);
        if (mergeFunction.getId() != null) {
            throw new BadRequestAlertException("A new mergeFunction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Customer oldCustomer, newCustomer;
        oldCustomer = customerService.findOne(mergeFunction.getTobeRemoved().getId()).orElse(null);
        newCustomer = customerService.findOne(mergeFunction.getReplacement().getId()).orElse(null);
        if (oldCustomer == null || newCustomer == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        MergeFunction result = mergeFunctionRepository.save(mergeFunction);
        mergeFunctionSearchRepository.save(result);
        List<CourseHistory> courseHistoryList = courseHistoryService.getCourseHistoriesByCustomer(oldCustomer).orElse(null);
        List<Certificate> certificateList = certificateService.getCertificatesByCustomer(oldCustomer).orElse(null);
        List<QuestionHistory> questionHistoryList = questionHistoryService.getQuestionHistoriesByCustomer(oldCustomer).orElse(null);
        List<QuizHistory> quizHistoryList = quizHistoryService.getQuizHistoriesByCustomer(oldCustomer).orElse(null);
        List<LegacyCourses> leagacyCoursesList = legacyCoursesRepository.findLegacyCoursesByCustomer(oldCustomer).orElse(null);
        Servicelist serviceList = servicelistRepository.findServicelistByCustomer(oldCustomer);

        if(courseHistoryList != null) {
            for (CourseHistory courseHistory: courseHistoryList){
                courseHistory.setCustomer(newCustomer);
                courseHistoryService.save(courseHistory);
            }
        }
        if(certificateList != null) {
            for (Certificate certificate: certificateList){
                certificate.setCustomer(newCustomer);
                certificateService.save(certificate);
            }
        }
        if(questionHistoryList != null) {
            for (QuestionHistory questionHistory: questionHistoryList){
                questionHistory.setCustomer(newCustomer);
                questionHistoryService.save(questionHistory);
            }
        }
        if(quizHistoryList != null) {
            for (QuizHistory quizHistory: quizHistoryList){
                quizHistory.setCustomer(newCustomer);
                quizHistoryService.save(quizHistory);
            }
        }
        if(serviceList != null) {
            serviceList.setCustomer(oldCustomer);
            servicelistRepository.save(serviceList);
        }

        if(leagacyCoursesList != null) {
            for (LegacyCourses legacyCourses: leagacyCoursesList){
                legacyCourses.setCustomer(newCustomer);
                legacyCoursesRepository.save(legacyCourses);
            }
        }
        return ResponseEntity.created(new URI("/api/merge-functions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /merge-functions : Updates an existing mergeFunction.
     *
     * @param mergeFunction the mergeFunction to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated mergeFunction,
     * or with status 400 (Bad Request) if the mergeFunction is not valid,
     * or with status 500 (Internal Server Error) if the mergeFunction couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/merge-functions")
    @Timed
    public ResponseEntity<MergeFunction> updateMergeFunction(@Valid @RequestBody MergeFunction mergeFunction) throws URISyntaxException {
        log.debug("REST request to update MergeFunction : {}", mergeFunction);
        if (mergeFunction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        MergeFunction result = mergeFunctionRepository.save(mergeFunction);
        mergeFunctionSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, mergeFunction.getId().toString()))
            .body(result);
    }

    /**
     * GET  /merge-functions : get all the mergeFunctions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of mergeFunctions in body
     */
    @GetMapping("/merge-functions")
    @Timed
    public ResponseEntity<List<MergeFunction>> getAllMergeFunctions(Pageable pageable) {
        log.debug("REST request to get a page of MergeFunctions");
        Page<MergeFunction> page = mergeFunctionRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/merge-functions");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /merge-functions/:id : get the "id" mergeFunction.
     *
     * @param id the id of the mergeFunction to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the mergeFunction, or with status 404 (Not Found)
     */
    @GetMapping("/merge-functions/{id}")
    @Timed
    public ResponseEntity<MergeFunction> getMergeFunction(@PathVariable Long id) {
        log.debug("REST request to get MergeFunction : {}", id);
        Optional<MergeFunction> mergeFunction = mergeFunctionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(mergeFunction);
    }

    /**
     * DELETE  /merge-functions/:id : delete the "id" mergeFunction.
     *
     * @param id the id of the mergeFunction to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/merge-functions/{id}")
    @Timed
    public ResponseEntity<Void> deleteMergeFunction(@PathVariable Long id) {
        log.debug("REST request to delete MergeFunction : {}", id);

        mergeFunctionRepository.deleteById(id);
        mergeFunctionSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/merge-functions?query=:query : search for the mergeFunction corresponding
     * to the query.
     *
     * @param query the query of the mergeFunction search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/merge-functions")
    @Timed
    public ResponseEntity<List<MergeFunction>> searchMergeFunctions(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of MergeFunctions for query {}", query);
        Page<MergeFunction> page = mergeFunctionSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/merge-functions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
