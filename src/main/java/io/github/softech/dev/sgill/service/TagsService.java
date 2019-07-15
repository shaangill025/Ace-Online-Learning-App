package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.Tags;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Tags.
 */
public interface TagsService {

    /**
     * Save a tags.
     *
     * @param tags the entity to save
     * @return the persisted entity
     */
    Tags save(Tags tags);

    /**
     * Get all the tags.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Tags> findAll(Pageable pageable);


    /**
     * Get the "id" tags.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Tags> findOne(Long id);

    /**
     * Delete the "id" tags.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the tags corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Tags> search(String query, Pageable pageable);
}
