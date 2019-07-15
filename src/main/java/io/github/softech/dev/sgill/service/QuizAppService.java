package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.QuizApp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing QuizApp.
 */
public interface QuizAppService {

    /**
     * Save a quizApp.
     *
     * @param quizApp the entity to save
     * @return the persisted entity
     */
    QuizApp save(QuizApp quizApp);

    /**
     * Get all the quizApps.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<QuizApp> findAll(Pageable pageable);

    /**
     * Get all the QuizApp with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    // Page<QuizApp> findAllWithEagerRelationships(Pageable pageable);
    
    /**
     * Get the "id" quizApp.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<QuizApp> findOne(Long id);

    /**
     * Delete the "id" quizApp.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the quizApp corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<QuizApp> search(String query, Pageable pageable);
}
