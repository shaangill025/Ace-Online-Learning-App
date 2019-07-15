package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.*;
import io.github.softech.dev.sgill.repository.*;
import io.github.softech.dev.sgill.service.*;
import io.github.softech.dev.sgill.domain.Course;
import io.github.softech.dev.sgill.service.CourseService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.service.dto.CourseCriteria;
import io.github.softech.dev.sgill.service.CourseQueryService;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link io.github.softech.dev.sgill.domain.Course}.
 */
@RestController
@RequestMapping("/api")
public class CourseResource {

    private final Logger log = LoggerFactory.getLogger(CourseResource.class);

    private static final String ENTITY_NAME = "course";

    private final CourseService courseService;

    private final CourseQueryService courseQueryService;

    private final CourseRepository courseRepository;

    private final CustomerRepository customerRepository;

    private final CourseHistoryRepository courseHistoryRepository;

    private final CustomerService customerService;

    private final CourseCartBridgeRepository courseCartBridgeRepository;

    private final CourseCartBridgeService courseCartBridgeService;

    private final CartService cartService;

    private final LegacyCoursesRepository legacyCoursesRepository;

    public CourseResource(CourseService courseService, CourseQueryService courseQueryService, CustomerRepository customerRepository,
                          CourseHistoryRepository courseHistoryRepository, CustomerService customerService, CourseCartBridgeRepository courseCartBridgeRepository,
                          CourseCartBridgeService courseCartBridgeService, CartService cartService, LegacyCoursesRepository legacyCoursesRepository, CourseRepository courseRepository) {
        this.courseService = courseService;
        this.courseQueryService = courseQueryService;
        this.customerRepository = customerRepository;
        this.courseHistoryRepository = courseHistoryRepository;
        this.customerService = customerService;
        this.courseCartBridgeRepository = courseCartBridgeRepository;
        this.courseCartBridgeService = courseCartBridgeService;
        this.cartService = cartService;
        this.legacyCoursesRepository = legacyCoursesRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * {@code POST  /courses} : Create a new course.
     *
     * @param course the course to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new course, or with status {@code 400 (Bad Request)} if the course has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) throws URISyntaxException {
        log.debug("REST request to save Course : {}", course);
        if (course.getId() != null) {
            throw new BadRequestAlertException("A new course cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Course result = courseService.save(course);
        return ResponseEntity.created(new URI("/api/courses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /courses} : Updates an existing course.
     *
     * @param course the course to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated course,
     * or with status {@code 400 (Bad Request)} if the course is not valid,
     * or with status {@code 500 (Internal Server Error)} if the course couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/courses")
    public ResponseEntity<Course> updateCourse(@Valid @RequestBody Course course) throws URISyntaxException {
        log.debug("REST request to update Course : {}", course);
        if (course.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Course result = courseService.save(course);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, course.getId().toString()))
            .body(result);
    }

    @GetMapping("/check/{customerId}/customer/{cartId}/cart/courses/{courseId}")
    @Timed
    public boolean checkCourses(@PathVariable Long customerId, @PathVariable Long cartId, @PathVariable Long courseId) throws URISyntaxException {
        Course course = courseService.findOne(courseId).orElse(null);
        Customer customer = customerService.findOne(customerId).orElse(null);
        Cart tempCart = cartService.findOne(cartId).orElse(null);
        List<CourseHistory> listHistory = courseHistoryRepository.findCourseHistoriesByCustomerAndCourseAndAccessAndIsactiveOrderByIdDesc(customer, course, true, true).orElse(null);
        List<LegacyCourses> listLegacy = legacyCoursesRepository.findLegacyCoursesByCourseAndCustomer(course, customer).orElse(null);
        CourseHistory temp;
        if (listHistory == null) {
            if (listLegacy != null) {
                log.debug("Inside checkCourses function, length of Legacy course check : {}", listLegacy.size());
                return listLegacy.size() < 1;
            }
            return true;
        } else {
            temp =  listHistory.get(0);
        }
        log.debug("Inside checkCourses function, length of CourseHistory check : {}", listHistory.size());
        if (listLegacy != null) {
            log.debug("Inside checkCourses function, length of Legacy course check : {}", listLegacy.size());
            if (!listLegacy.isEmpty()) {
                return false;
            }
        }
        if(!temp.isIsactive() && temp.isAccess()){
            CourseCartBridge tempCartBridge = courseCartBridgeRepository.findCourseCartBridgeByCartIdAndCourseId(cartId, courseId).orElse(null);
            if (tempCartBridge == null) {
                tempCartBridge = new CourseCartBridge();
            }
            tempCartBridge.setCart(tempCart);
            tempCartBridge.setCourse(course);
            tempCartBridge.setTimestamp(Instant.now());
            courseCartBridgeService.save(tempCartBridge);
            return true;
        }
        return false;
    }

    /**
     * {@code GET  /courses} : get all the courses.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of courses in body.
     */
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses(CourseCriteria criteria, Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get Courses by criteria: {}", criteria);
        Page<Course> page = courseQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/courses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/courses/country/{country}")
    public ResponseEntity<List<Course>> getAllCoursesbyCountry(@PathVariable String country) {
        log.debug("REST request to get Courses by country: {}", country);
        List<Course> courseList = courseRepository.findCoursesByCountryAndShow(country, true).orElse(null);
        return ResponseEntity.ok().body(courseList);
    }

    /**
     * {@code GET  /courses/count} : count all the courses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/courses/count")
    public ResponseEntity<Long> countCourses(CourseCriteria criteria) {
        log.debug("REST request to count Courses by criteria: {}", criteria);
        return ResponseEntity.ok().body(courseQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /courses/:id} : get the "id" course.
     *
     * @param id the id of the course to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the course, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable Long id) {
        log.debug("REST request to get Course : {}", id);
        Optional<Course> course = courseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(course);
    }

    /**
     * {@code DELETE  /courses/:id} : delete the "id" course.
     *
     * @param id the id of the course to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        log.debug("REST request to delete Course : {}", id);
        courseService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/courses?query=:query} : search for the course corresponding
     * to the query.
     *
     * @param query the query of the course search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/courses")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String query, Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to search for a page of Courses for query {}", query);
        Page<Course> page = courseService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/_search/courses");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
