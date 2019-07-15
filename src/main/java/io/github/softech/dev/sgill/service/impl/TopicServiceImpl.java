package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.service.TopicService;
import io.github.softech.dev.sgill.domain.Topic;
import io.github.softech.dev.sgill.repository.TopicRepository;
import io.github.softech.dev.sgill.repository.search.TopicSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Topic.
 */
@Service
@Transactional
public class TopicServiceImpl implements TopicService {

    private final Logger log = LoggerFactory.getLogger(TopicServiceImpl.class);

    private final TopicRepository topicRepository;

    private final TopicSearchRepository topicSearchRepository;

    public TopicServiceImpl(TopicRepository topicRepository, TopicSearchRepository topicSearchRepository) {
        this.topicRepository = topicRepository;
        this.topicSearchRepository = topicSearchRepository;
    }

    /**
     * Save a topic.
     *
     * @param topic the entity to save
     * @return the persisted entity
     */
    @Override
    public Topic save(Topic topic) {
        log.debug("Request to save Topic : {}", topic);        Topic result = topicRepository.save(topic);
        topicSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the topics.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Topic> findAll(Pageable pageable) {
        log.debug("Request to get all Topics");
        return topicRepository.findAll(pageable);
    }


    /**
     * Get one topic by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Topic> findOne(Long id) {
        log.debug("Request to get Topic : {}", id);
        return topicRepository.findById(id);
    }

    /**
     * Delete the topic by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Topic : {}", id);
        topicRepository.deleteById(id);
        topicSearchRepository.deleteById(id);
    }

    /**
     * Search for the topic corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Topic> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Topics for query {}", query);
        return topicSearchRepository.search(queryStringQuery(query), pageable);    }
}
