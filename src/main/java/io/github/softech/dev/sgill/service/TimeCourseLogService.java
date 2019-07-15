package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.TimeCourseLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing TimeCourseLog.
 */
public interface TimeCourseLogService {

    /**
     * Save a timeCourseLog.
     *
     * @param timeCourseLog the entity to save
     * @return the persisted entity
     */
    TimeCourseLog save(TimeCourseLog timeCourseLog);

    /**
     * Get all the timeCourseLogs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TimeCourseLog> findAll(Pageable pageable);


    /**
     * Get the "id" timeCourseLog.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<TimeCourseLog> findOne(Long id);

    /**
     * Delete the "id" timeCourseLog.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the timeCourseLog corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TimeCourseLog> search(String query, Pageable pageable);

    Optional<List<TimeCourseLog>> getTimeCourseLogsByCustomer(Customer customer);
}
