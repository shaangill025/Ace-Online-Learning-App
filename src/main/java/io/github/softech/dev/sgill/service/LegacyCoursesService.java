package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.LegacyCourses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing LegacyCourses.
 */
public interface LegacyCoursesService {

    /**
     * Save a legacyCourses.
     *
     * @param legacyCourses the entity to save
     * @return the persisted entity
     */
    LegacyCourses save(LegacyCourses legacyCourses);

    /**
     * Get all the legacyCourses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<LegacyCourses> findAll(Pageable pageable);


    /**
     * Get the "id" legacyCourses.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<LegacyCourses> findOne(Long id);

    /**
     * Delete the "id" legacyCourses.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the legacyCourses corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<LegacyCourses> search(String query, Pageable pageable);
}
