package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.CourseCartBridge;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing CourseCartBridge.
 */
public interface CourseCartBridgeService {

    /**
     * Save a courseCartBridge.
     *
     * @param courseCartBridge the entity to save
     * @return the persisted entity
     */
    CourseCartBridge save(CourseCartBridge courseCartBridge);

    /**
     * Get all the courseCartBridges.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CourseCartBridge> findAll(Pageable pageable);


    /**
     * Get the "id" courseCartBridge.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<CourseCartBridge> findOne(Long id);

    /**
     * Delete the "id" courseCartBridge.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the courseCartBridge corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CourseCartBridge> search(String query, Pageable pageable);
}
