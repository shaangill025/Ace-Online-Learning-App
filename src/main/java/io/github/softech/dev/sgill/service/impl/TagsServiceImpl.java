package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.service.TagsService;
import io.github.softech.dev.sgill.domain.Tags;
import io.github.softech.dev.sgill.repository.TagsRepository;
import io.github.softech.dev.sgill.repository.search.TagsSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Tags.
 */
@Service
@Transactional
public class TagsServiceImpl implements TagsService {

    private final Logger log = LoggerFactory.getLogger(TagsServiceImpl.class);

    private final TagsRepository tagsRepository;

    private final TagsSearchRepository tagsSearchRepository;

    public TagsServiceImpl(TagsRepository tagsRepository, TagsSearchRepository tagsSearchRepository) {
        this.tagsRepository = tagsRepository;
        this.tagsSearchRepository = tagsSearchRepository;
    }

    /**
     * Save a tags.
     *
     * @param tags the entity to save
     * @return the persisted entity
     */
    @Override
    public Tags save(Tags tags) {
        log.debug("Request to save Tags : {}", tags);        Tags result = tagsRepository.save(tags);
        tagsSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the tags.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Tags> findAll(Pageable pageable) {
        log.debug("Request to get all Tags");
        return tagsRepository.findAll(pageable);
    }


    /**
     * Get one tags by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Tags> findOne(Long id) {
        log.debug("Request to get Tags : {}", id);
        return tagsRepository.findById(id);
    }

    /**
     * Delete the tags by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Tags : {}", id);
        tagsRepository.deleteById(id);
        tagsSearchRepository.deleteById(id);
    }

    /**
     * Search for the tags corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Tags> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Tags for query {}", query);
        return tagsSearchRepository.search(queryStringQuery(query), pageable);    }
}
