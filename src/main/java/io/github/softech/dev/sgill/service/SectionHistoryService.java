package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.SectionHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing SectionHistory.
 */
public interface SectionHistoryService {

    /**
     * Save a sectionHistory.
     *
     * @param sectionHistory the entity to save
     * @return the persisted entity
     */
    SectionHistory save(SectionHistory sectionHistory);

    /**
     * Get all the sectionHistories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<SectionHistory> findAll(Pageable pageable);


    /**
     * Get the "id" sectionHistory.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<SectionHistory> findOne(Long id);

    /**
     * Delete the "id" sectionHistory.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the sectionHistory corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<SectionHistory> search(String query, Pageable pageable);

    Optional<List<SectionHistory>> getSectionHistoriesByCustomer(Customer customer);
}
