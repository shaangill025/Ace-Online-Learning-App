package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.Quiz;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Quiz.
 */
public interface QuizService {

    /**
     * Save a quiz.
     *
     * @param quiz the entity to save
     * @return the persisted entity
     */
    Quiz save(Quiz quiz);

    /**
     * Get all the quizzes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Quiz> findAll(Pageable pageable);


    /**
     * Get the "id" quiz.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Quiz> findOne(Long id);

    /**
     * Delete the "id" quiz.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the quiz corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Quiz> search(String query, Pageable pageable);
}
