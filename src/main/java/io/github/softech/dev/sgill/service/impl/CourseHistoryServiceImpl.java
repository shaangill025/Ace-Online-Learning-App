package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.User;
import io.github.softech.dev.sgill.service.CourseHistoryService;
import io.github.softech.dev.sgill.domain.CourseHistory;
import io.github.softech.dev.sgill.repository.CourseHistoryRepository;
import io.github.softech.dev.sgill.repository.search.CourseHistorySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing CourseHistory.
 */
@Service
@Transactional
public class CourseHistoryServiceImpl implements CourseHistoryService {

    private final Logger log = LoggerFactory.getLogger(CourseHistoryServiceImpl.class);

    private final CourseHistoryRepository courseHistoryRepository;

    private final CourseHistorySearchRepository courseHistorySearchRepository;

    public CourseHistoryServiceImpl(CourseHistoryRepository courseHistoryRepository, CourseHistorySearchRepository courseHistorySearchRepository) {
        this.courseHistoryRepository = courseHistoryRepository;
        this.courseHistorySearchRepository = courseHistorySearchRepository;
    }

    /**
     * Save a courseHistory.
     *
     * @param courseHistory the entity to save
     * @return the persisted entity
     */
    @Override
    public CourseHistory save(CourseHistory courseHistory) {
        log.debug("Request to save CourseHistory : {}", courseHistory);
        CourseHistory result = courseHistoryRepository.save(courseHistory);
        courseHistorySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the courseHistories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CourseHistory> findAll(Pageable pageable) {
        log.debug("Request to get all CourseHistories");
        return courseHistoryRepository.findAll(pageable);
    }


    /**
     * Get one courseHistory by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CourseHistory> findOne(Long id) {
        log.debug("Request to get CourseHistory : {}", id);
        return courseHistoryRepository.findById(id);
    }

    /**
     * Delete the courseHistory by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete CourseHistory : {}", id);
        courseHistoryRepository.deleteById(id);
        courseHistorySearchRepository.deleteById(id);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void turnAccessWhen90Days() {
        List<CourseHistory> courseHistories = courseHistoryRepository.findAll();
        Instant nowMomenet = Instant.now();
        for (CourseHistory hist : courseHistories) {
            if (hist.isIsactive() && (Duration.between(hist.getStartdate(), nowMomenet).toDays() >= 90)) {
                hist.setIsactive(false);
                this.save(hist);
            }
        }
    }


    /**
     * Search for the courseHistory corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CourseHistory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of CourseHistories for query {}", query);
        return courseHistorySearchRepository.search(queryStringQuery(query), pageable);    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<CourseHistory>> getCourseHistoriesByCustomer(Customer customer) {
        log.debug("Request to get CourseHistories for a customer {}", customer);
        return courseHistoryRepository.findCourseHistoriesByCustomerOrderByIdDesc(customer);    }
}
