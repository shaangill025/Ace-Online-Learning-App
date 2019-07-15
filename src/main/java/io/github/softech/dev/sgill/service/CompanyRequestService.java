package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.CompanyRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing CompanyRequest.
 */
public interface CompanyRequestService {

    /**
     * Save a companyRequest.
     *
     * @param companyRequest the entity to save
     * @return the persisted entity
     */
    CompanyRequest save(CompanyRequest companyRequest);

    /**
     * Get all the companyRequests.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CompanyRequest> findAll(Pageable pageable);


    /**
     * Get the "id" companyRequest.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<CompanyRequest> findOne(Long id);

    /**
     * Delete the "id" companyRequest.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the companyRequest corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CompanyRequest> search(String query, Pageable pageable);
}
