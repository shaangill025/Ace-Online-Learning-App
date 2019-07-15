package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.Servicelist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Servicelist.
 */
public interface ServicelistService {

    /**
     * Save a servicelist.
     *
     * @param servicelist the entity to save
     * @return the persisted entity
     */
    Servicelist save(Servicelist servicelist);

    /**
     * Get all the servicelists.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Servicelist> findAll(Pageable pageable);


    /**
     * Get the "id" servicelist.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Servicelist> findOne(Long id);

    /**
     * Delete the "id" servicelist.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the servicelist corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Servicelist> search(String query, Pageable pageable);
}
