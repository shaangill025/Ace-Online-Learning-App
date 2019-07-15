package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.Section;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Section.
 */
public interface SectionService {

    /**
     * Save a section.
     *
     * @param section the entity to save
     * @return the persisted entity
     */
    Section save(Section section);

    /**
     * Get all the sections.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Section> findAll(Pageable pageable);

    /**
     * Get all the Section with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    Page<Section> findAllWithEagerRelationships(Pageable pageable);
    
    /**
     * Get the "id" section.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Section> findOne(Long id);

    /**
     * Delete the "id" section.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the section corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Section> search(String query, Pageable pageable);
}
