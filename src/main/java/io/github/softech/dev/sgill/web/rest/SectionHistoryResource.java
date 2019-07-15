package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.*;
import io.github.softech.dev.sgill.repository.*;
import io.github.softech.dev.sgill.service.*;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.SectionHistoryCriteria;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing SectionHistory.
 */
@RestController
@RequestMapping("/api")
public class SectionHistoryResource {

    private final Logger log = LoggerFactory.getLogger(SectionHistoryResource.class);

    private static final String ENTITY_NAME = "sectionHistory";

    private final SectionHistoryService sectionHistoryService;

    private final SectionHistoryQueryService sectionHistoryQueryService;

    private final SectionHistoryRepository sectionHistoryRepository;

    private final CustomerService customerService;

    private final CourseService courseService;

    private final SectionService sectionService;

    private final SectionRepository sectionRepository;

    private final QuizHistoryRepository quizHistoryRepository;

    private final QuizRepository quizRepository;

    private final CourseHistoryRepository courseHistoryRepository;

    public SectionHistoryResource(SectionHistoryService sectionHistoryService, SectionHistoryQueryService sectionHistoryQueryService,
                                  SectionHistoryRepository sectionHistoryRepository, CustomerService customerService, SectionService sectionService,
                                  SectionRepository sectionRepository, QuizHistoryRepository quizHistoryRepository, CourseService courseService, QuizRepository quizRepository,
                                  CourseHistoryRepository courseHistoryRepository) {
        this.sectionHistoryService = sectionHistoryService;
        this.sectionHistoryQueryService = sectionHistoryQueryService;
        this.sectionHistoryRepository = sectionHistoryRepository;
        this.customerService = customerService;
        this.sectionService = sectionService;
        this.sectionRepository = sectionRepository;
        this.quizHistoryRepository = quizHistoryRepository;
        this.courseService = courseService;
        this.quizRepository = quizRepository;
        this.courseHistoryRepository = courseHistoryRepository;
    }

    /**
     * POST  /section-histories : Create a new sectionHistory.
     *
     * @param sectionHistory the sectionHistory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new sectionHistory, or with status 400 (Bad Request) if the sectionHistory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/section-histories")
    @Timed
    public ResponseEntity<SectionHistory> createSectionHistory(@RequestBody SectionHistory sectionHistory) throws URISyntaxException {
        log.debug("REST request to save SectionHistory : {}", sectionHistory);
        if (sectionHistory.getId() != null) {
            throw new BadRequestAlertException("A new sectionHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SectionHistory result = sectionHistoryService.save(sectionHistory);
        List<SectionHistory> tempList = sectionHistoryRepository.findSectionHistoriesByCustomer(null).orElse(null);
        if (tempList != null) {
            for (SectionHistory temp: tempList) {
                sectionHistoryService.delete(temp.getId());
            }
        }
        return ResponseEntity.created(new URI("/api/section-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /section-histories : Updates an existing sectionHistory.
     *
     * @param sectionHistory the sectionHistory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated sectionHistory,
     * or with status 400 (Bad Request) if the sectionHistory is not valid,
     * or with status 500 (Internal Server Error) if the sectionHistory couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/section-histories")
    @Timed
    public ResponseEntity<SectionHistory> updateSectionHistory(@RequestBody SectionHistory sectionHistory) throws URISyntaxException {
        log.debug("REST request to update SectionHistory : {}", sectionHistory);
        if (sectionHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SectionHistory result = sectionHistoryService.save(sectionHistory);
        List<SectionHistory> tempList = sectionHistoryRepository.findSectionHistoriesByCustomer(null).orElse(null);
        if (tempList != null) {
            for (SectionHistory temp: tempList) {
                sectionHistoryService.delete(temp.getId());
            }
        }
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, sectionHistory.getId().toString()))
            .body(result);
    }

    @PutMapping("update/{stamp}/section-histories/{sectionHistId}")
    @Timed
    public ResponseEntity<SectionHistory> updateSectionHistory(@PathVariable int stamp, @PathVariable Long sectionHistId) throws URISyntaxException {
        SectionHistory temp = sectionHistoryService.findOne(sectionHistId).get();
        temp.setStamp(stamp);
        temp.setLastactivedate(Instant.now());
        SectionHistory result = sectionHistoryService.save(temp);
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * GET  /section-histories : get all the sectionHistories.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of sectionHistories in body
     */
    @GetMapping("/section-histories")
    @Timed
    public ResponseEntity<List<SectionHistory>> getAllSectionHistories(SectionHistoryCriteria criteria, Pageable pageable) {
        log.debug("REST request to get SectionHistories by criteria: {}", criteria);
        Page<SectionHistory> page = sectionHistoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/section-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{courseId}/section-histories/{customerId}")
    @Timed
    public SectionHistory getLastCourseSectionHistories(@PathVariable Long courseId, @PathVariable Long customerId) {
        log.debug("REST request to get most recent Section by Course ID: {}", courseId);
        log.debug("REST request to get most recent Section by Customer ID: {}", customerId);
        Customer customer = customerService.findOne(customerId).get();
        Course course = courseService.findOne(courseId).get();
        Optional<SectionHistory> tempOpt = sectionHistoryRepository.getTopByCustomerAndSection_CourseOrderByIdDesc(customer, course);
        SectionHistory tempSecHist;
        if(tempOpt.orElse(null) == null) {
            tempSecHist = new SectionHistory();
            tempSecHist.setLastactivedate(Instant.now());
            tempSecHist.setSection(sectionRepository.findSectionsByCourseOrderByIdAsc(course).get().get(0));
            tempSecHist.setStartdate(Instant.now());
            tempSecHist.setWatched(false);
            tempSecHist.setStamp(0);
            tempSecHist.setCustomer(customerService.findOne(customerId).get());
            return sectionHistoryService.save(tempSecHist);
        }
        return tempOpt.get();
    }

    @GetMapping("/{sectionId}/section-history/{customerId}")
    @Timed
    public SectionHistory getByCustomerSectionHistories(@PathVariable Long sectionId, @PathVariable Long customerId) {
        log.debug("REST request to get most recent Section by Course ID: {}", sectionId);
        log.debug("REST request to get most recent Section by Customer ID: {}", customerId);
        Customer customer = customerService.findOne(customerId).get();
        Section section = sectionService.findOne(sectionId).get();
        Optional<SectionHistory> tempOpt = sectionHistoryRepository.findTopByCustomerAndSectionOrderByIdDesc(customer, section);
        SectionHistory tempSecHist;
        if(tempOpt.orElse(null) == null) {
            tempSecHist = new SectionHistory();
            tempSecHist.setLastactivedate(Instant.now());
            tempSecHist.setSection(section);
            tempSecHist.setStartdate(Instant.now());
            tempSecHist.setWatched(false);
            tempSecHist.setStamp(0);
            tempSecHist.setCustomer(customer);
            return sectionHistoryService.save(tempSecHist);
        }
        List<SectionHistory> tempList = sectionHistoryRepository.findSectionHistoriesByCustomer(null).orElse(null);
        if (tempList != null) {
            for (SectionHistory temp: tempList) {
                sectionHistoryService.delete(temp.getId());
            }
        }
        return tempOpt.get();
    }

    /**
     * GET  /section-histories/:id : get the "id" sectionHistory.
     *
     * @param id the id of the sectionHistory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the sectionHistory, or with status 404 (Not Found)
     */
    @GetMapping("/section-histories/{id}")
    @Timed
    public ResponseEntity<SectionHistory> getSectionHistory(@PathVariable Long id) {
        log.debug("REST request to get SectionHistory : {}", id);
        Optional<SectionHistory> sectionHistory = sectionHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sectionHistory);
    }

    @GetMapping("/customer/section-histories/{customerid}")
    @Timed
    public List<SectionHistory> getCustomerSectionHistories(@PathVariable Long customerid) {
        log.debug("REST request to get SectionHistories by customer : {}", customerid);
        Customer reqdCustomer = customerService.findOne(customerid).orElse(null);
        List<SectionHistory> tempOpt = sectionHistoryRepository.getSectionHistoriesByCustomer(reqdCustomer).orElse(null);
        SectionHistory tempSecHist;
        if(tempOpt == null) {
            List<SectionHistory> temp = new ArrayList<SectionHistory>();
            tempSecHist = new SectionHistory();
            tempSecHist.setId(-1L);
            temp.add(tempSecHist);
            return temp;
        }
        return tempOpt;
    }

    @GetMapping("/recent/section-history/{customerid}")
    @Timed
    public Section getRecentSectionHistory(@PathVariable Long customerid) {
        log.debug("REST request to get recent Section by customer : {}", customerid);
        Customer customer = customerService.findOne(customerid).get();
        SectionHistory temp = sectionHistoryRepository.getTopByCustomerOrderByIdDesc(customer).orElse(null);
        if (temp == null) {
            Section tempSection = new Section();
            tempSection.setId(-1L);
            return tempSection;
        }
        return temp.getSection();
    }

    @GetMapping("/check/{sectionid}/watched/section-history/{customerid}")
    @Timed
    public Boolean getRecentWatchedSectionHistory(@PathVariable Long customerid, @PathVariable Long sectionid) {
        log.debug("REST request to get recent last Section watch history by customer : {}", customerid);
        Section tempSection = sectionService.findOne(sectionid).get();
        Customer tempCustomer = customerService.findOne(customerid).get();
        List<Quiz> quizList = quizRepository.findQuizzesByNewSectionOrderByIdDesc(tempSection).orElse(null);
        if (quizList == null) {
            Boolean flag_temp1 = checkInCourseHistory(tempSection, tempCustomer);
            return flag_temp1;
        } else {
            Quiz lastQuiz = quizList.get(0);
            List<QuizHistory> temp = quizHistoryRepository.findQuizHistoriesByCustomerAndQuizOrderByIdDesc(tempCustomer, lastQuiz).orElse(null);
            Section lastSection = sectionRepository.getSectionsByQuiz(lastQuiz).get();
            List<SectionHistory> tempSectionhist = sectionHistoryRepository.findSectionHistoriesBySectionAndCustomerOrderByIdDesc(lastSection, tempCustomer).orElse(null);
            if (temp == null) return false;
            if (tempSectionhist.get(0).isWatched() && temp.get(0).isPassed()) {
                Boolean flag_temp2 = checkInCourseHistory(tempSection, tempCustomer);
                return flag_temp2;
            }
            log.debug("Inside getRecentWatched, not caught returning false");
            return false;
        }
    }

    private boolean checkInCourseHistory(Section section, Customer customer) {
        Course tempCourse = section.getCourse();
        List<CourseHistory> courseHistory = courseHistoryRepository.findCourseHistoriesByCustomerAndCourseAndAccessAndIsactiveOrderByIdDesc(customer, tempCourse, true, true).orElse(null);
        if(courseHistory == null) return false;
        return courseHistory.size() == 1;
    }

    @GetMapping("/customer/{customerid}/section-history/{courseid}")
    @Timed
    public SectionHistory getPersistanceSectionHistory(@PathVariable Long customerid, @PathVariable Long courseid) {
        log.debug("REST request to get SectionHistory by customer and Section : {}", customerid);
        Customer customer = customerService.findOne(customerid).get();
        Course course = courseService.findOne(courseid).get();
        Optional<SectionHistory> temp = sectionHistoryRepository.getTopByCustomerAndSection_CourseOrderByIdDesc(customer, course);
        SectionHistory tempSecHist = temp.orElse(null);

        if(tempSecHist == null) {
            List<Section> tempSec= sectionRepository.findSectionsByCourse(course).get();
            SectionHistory temp2 = new SectionHistory();
            temp2.setLastactivedate(Instant.now());
            temp2.setSection(tempSec.get(0));
            temp2.setStamp(0);
            temp2.setStartdate(Instant.now());
            temp2.setWatched(false);
            return sectionHistoryService.save(temp2);
        }
        return sectionHistoryRepository.getTopByCustomerAndSection_CourseOrderByIdDesc(customer, course).orElse(null);
    }

    @GetMapping("/count/{customerid}/section-histories/{courseid}")
    @Timed
    public ResponseEntity<List<SectionHistory>> getCountSectionHistory(@PathVariable Long customerid, @PathVariable Long courseid) {
        log.debug("REST request to get SectionHistory by customer and Section : {}", customerid);
        return ResponseUtil.wrapOrNotFound(sectionHistoryRepository.getSectionCountByCourse(customerid, courseid));
    }

    /**
     * DELETE  /section-histories/:id : delete the "id" sectionHistory.
     *
     * @param id the id of the sectionHistory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/section-histories/{id}")
    @Timed
    public ResponseEntity<Void> deleteSectionHistory(@PathVariable Long id) {
        log.debug("REST request to delete SectionHistory : {}", id);
        sectionHistoryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/section-histories?query=:query : search for the sectionHistory corresponding
     * to the query.
     *
     * @param query the query of the sectionHistory search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/section-histories")
    @Timed
    public ResponseEntity<List<SectionHistory>> searchSectionHistories(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of SectionHistories for query {}", query);
        Page<SectionHistory> page = sectionHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/section-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
