package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.service.CourseCartBridgeService;
import io.github.softech.dev.sgill.domain.CourseCartBridge;
import io.github.softech.dev.sgill.repository.CourseCartBridgeRepository;
import io.github.softech.dev.sgill.repository.search.CourseCartBridgeSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing CourseCartBridge.
 */
@Service
@Transactional
public class CourseCartBridgeServiceImpl implements CourseCartBridgeService {

    private final Logger log = LoggerFactory.getLogger(CourseCartBridgeServiceImpl.class);

    private final CourseCartBridgeRepository courseCartBridgeRepository;

    private final CourseCartBridgeSearchRepository courseCartBridgeSearchRepository;

    public CourseCartBridgeServiceImpl(CourseCartBridgeRepository courseCartBridgeRepository, CourseCartBridgeSearchRepository courseCartBridgeSearchRepository) {
        this.courseCartBridgeRepository = courseCartBridgeRepository;
        this.courseCartBridgeSearchRepository = courseCartBridgeSearchRepository;
    }

    /**
     * Save a courseCartBridge.
     *
     * @param courseCartBridge the entity to save
     * @return the persisted entity
     */
    @Override
    public CourseCartBridge save(CourseCartBridge courseCartBridge) {
        log.debug("Request to save CourseCartBridge : {}", courseCartBridge);        CourseCartBridge result = courseCartBridgeRepository.save(courseCartBridge);
        courseCartBridgeSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the courseCartBridges.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CourseCartBridge> findAll(Pageable pageable) {
        log.debug("Request to get all CourseCartBridges");
        return courseCartBridgeRepository.findAll(pageable);
    }


    /**
     * Get one courseCartBridge by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CourseCartBridge> findOne(Long id) {
        log.debug("Request to get CourseCartBridge : {}", id);
        return courseCartBridgeRepository.findById(id);
    }

    /**
     * Delete the courseCartBridge by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete CourseCartBridge : {}", id);
        courseCartBridgeRepository.deleteById(id);
        courseCartBridgeSearchRepository.deleteById(id);
    }

    /**
     * Search for the courseCartBridge corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CourseCartBridge> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of CourseCartBridges for query {}", query);
        return courseCartBridgeSearchRepository.search(queryStringQuery(query), pageable);    }
}
