package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.service.LegacyCoursesService;
import io.github.softech.dev.sgill.domain.LegacyCourses;
import io.github.softech.dev.sgill.repository.LegacyCoursesRepository;
import io.github.softech.dev.sgill.repository.search.LegacyCoursesSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing LegacyCourses.
 */
@Service
@Transactional
public class LegacyCoursesServiceImpl implements LegacyCoursesService {

    private final Logger log = LoggerFactory.getLogger(LegacyCoursesServiceImpl.class);

    private final LegacyCoursesRepository legacyCoursesRepository;

    private final LegacyCoursesSearchRepository legacyCoursesSearchRepository;

    public LegacyCoursesServiceImpl(LegacyCoursesRepository legacyCoursesRepository, LegacyCoursesSearchRepository legacyCoursesSearchRepository) {
        this.legacyCoursesRepository = legacyCoursesRepository;
        this.legacyCoursesSearchRepository = legacyCoursesSearchRepository;
    }

    /**
     * Save a legacyCourses.
     *
     * @param legacyCourses the entity to save
     * @return the persisted entity
     */
    @Override
    public LegacyCourses save(LegacyCourses legacyCourses) {
        log.debug("Request to save LegacyCourses : {}", legacyCourses);
        LegacyCourses result = legacyCoursesRepository.save(legacyCourses);
        legacyCoursesSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the legacyCourses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LegacyCourses> findAll(Pageable pageable) {
        log.debug("Request to get all LegacyCourses");
        return legacyCoursesRepository.findAll(pageable);
    }


    /**
     * Get one legacyCourses by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<LegacyCourses> findOne(Long id) {
        log.debug("Request to get LegacyCourses : {}", id);
        return legacyCoursesRepository.findById(id);
    }

    /**
     * Delete the legacyCourses by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete LegacyCourses : {}", id);
        legacyCoursesRepository.deleteById(id);
        legacyCoursesSearchRepository.deleteById(id);
    }

    /**
     * Search for the legacyCourses corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LegacyCourses> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of LegacyCourses for query {}", query);
        return legacyCoursesSearchRepository.search(queryStringQuery(query), pageable);    }
}
