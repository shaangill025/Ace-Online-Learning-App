package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.service.TimeCourseLogService;
import io.github.softech.dev.sgill.domain.TimeCourseLog;
import io.github.softech.dev.sgill.repository.TimeCourseLogRepository;
import io.github.softech.dev.sgill.repository.search.TimeCourseLogSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing TimeCourseLog.
 */
@Service
@Transactional
public class TimeCourseLogServiceImpl implements TimeCourseLogService {

    private final Logger log = LoggerFactory.getLogger(TimeCourseLogServiceImpl.class);

    private final TimeCourseLogRepository timeCourseLogRepository;

    private final TimeCourseLogSearchRepository timeCourseLogSearchRepository;

    public TimeCourseLogServiceImpl(TimeCourseLogRepository timeCourseLogRepository, TimeCourseLogSearchRepository timeCourseLogSearchRepository) {
        this.timeCourseLogRepository = timeCourseLogRepository;
        this.timeCourseLogSearchRepository = timeCourseLogSearchRepository;
    }

    /**
     * Save a timeCourseLog.
     *
     * @param timeCourseLog the entity to save
     * @return the persisted entity
     */
    @Override
    public TimeCourseLog save(TimeCourseLog timeCourseLog) {
        log.debug("Request to save TimeCourseLog : {}", timeCourseLog);        TimeCourseLog result = timeCourseLogRepository.save(timeCourseLog);
        timeCourseLogSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the timeCourseLogs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TimeCourseLog> findAll(Pageable pageable) {
        log.debug("Request to get all TimeCourseLogs");
        return timeCourseLogRepository.findAll(pageable);
    }


    /**
     * Get one timeCourseLog by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TimeCourseLog> findOne(Long id) {
        log.debug("Request to get TimeCourseLog : {}", id);
        return timeCourseLogRepository.findById(id);
    }

    /**
     * Delete the timeCourseLog by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete TimeCourseLog : {}", id);
        timeCourseLogRepository.deleteById(id);
        timeCourseLogSearchRepository.deleteById(id);
    }

    /**
     * Search for the timeCourseLog corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TimeCourseLog> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TimeCourseLogs for query {}", query);
        return timeCourseLogSearchRepository.search(queryStringQuery(query), pageable);    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<TimeCourseLog>> getTimeCourseLogsByCustomer(Customer customer) {
        log.debug("Request to get all TimeCourseLogs for customer {}", customer);
        return timeCourseLogRepository.findTimeCourseLogsByCustomer(customer);    }
}
