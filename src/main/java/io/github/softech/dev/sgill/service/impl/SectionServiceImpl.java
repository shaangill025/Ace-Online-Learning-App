package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.service.SectionService;
import io.github.softech.dev.sgill.domain.Section;
import io.github.softech.dev.sgill.repository.SectionRepository;
import io.github.softech.dev.sgill.repository.search.SectionSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Section.
 */
@Service
@Transactional
public class SectionServiceImpl implements SectionService {

    private final Logger log = LoggerFactory.getLogger(SectionServiceImpl.class);

    private final SectionRepository sectionRepository;

    private final SectionSearchRepository sectionSearchRepository;

    public SectionServiceImpl(SectionRepository sectionRepository, SectionSearchRepository sectionSearchRepository) {
        this.sectionRepository = sectionRepository;
        this.sectionSearchRepository = sectionSearchRepository;
    }

    /**
     * Save a section.
     *
     * @param section the entity to save
     * @return the persisted entity
     */
    @Override
    public Section save(Section section) {
        log.debug("Request to save Section : {}", section);        Section result = sectionRepository.save(section);
        sectionSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the sections.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Section> findAll(Pageable pageable) {
        log.debug("Request to get all Sections");
        return sectionRepository.findAll(pageable);
    }

    /**
     * Get all the Section with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    public Page<Section> findAllWithEagerRelationships(Pageable pageable) {
        return sectionRepository.findAllWithEagerRelationships(pageable);
    }
    

    /**
     * Get one section by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Section> findOne(Long id) {
        log.debug("Request to get Section : {}", id);
        return sectionRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the section by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Section : {}", id);
        sectionRepository.deleteById(id);
        sectionSearchRepository.deleteById(id);
    }

    /**
     * Search for the section corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Section> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Sections for query {}", query);
        return sectionSearchRepository.search(queryStringQuery(query), pageable);    }
}
