package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.Topic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Topic.
 */
public interface TopicService {

    /**
     * Save a topic.
     *
     * @param topic the entity to save
     * @return the persisted entity
     */
    Topic save(Topic topic);

    /**
     * Get all the topics.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Topic> findAll(Pageable pageable);


    /**
     * Get the "id" topic.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Topic> findOne(Long id);

    /**
     * Delete the "id" topic.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the topic corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Topic> search(String query, Pageable pageable);
}
