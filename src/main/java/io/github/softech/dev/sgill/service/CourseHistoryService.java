package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.CourseHistory;

import io.github.softech.dev.sgill.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing CourseHistory.
 */
public interface CourseHistoryService {

    /**
     * Save a courseHistory.
     *
     * @param courseHistory the entity to save
     * @return the persisted entity
     */
    CourseHistory save(CourseHistory courseHistory);

    /**
     * Get all the courseHistories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CourseHistory> findAll(Pageable pageable);


    /**
     * Get the "id" courseHistory.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<CourseHistory> findOne(Long id);

    /**
     * Delete the "id" courseHistory.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the courseHistory corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CourseHistory> search(String query, Pageable pageable);

    Optional<List<CourseHistory>> getCourseHistoriesByCustomer(Customer customer);
}
