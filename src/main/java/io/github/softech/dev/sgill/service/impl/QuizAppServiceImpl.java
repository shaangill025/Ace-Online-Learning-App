package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.service.QuizAppService;
import io.github.softech.dev.sgill.domain.QuizApp;
import io.github.softech.dev.sgill.repository.QuizAppRepository;
import io.github.softech.dev.sgill.repository.search.QuizAppSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing QuizApp.
 */
@Service
@Transactional
public class QuizAppServiceImpl implements QuizAppService {

    private final Logger log = LoggerFactory.getLogger(QuizAppServiceImpl.class);

    private final QuizAppRepository quizAppRepository;

    private final QuizAppSearchRepository quizAppSearchRepository;

    public QuizAppServiceImpl(QuizAppRepository quizAppRepository, QuizAppSearchRepository quizAppSearchRepository) {
        this.quizAppRepository = quizAppRepository;
        this.quizAppSearchRepository = quizAppSearchRepository;
    }

    /**
     * Save a quizApp.
     *
     * @param quizApp the entity to save
     * @return the persisted entity
     */
    @Override
    public QuizApp save(QuizApp quizApp) {
        log.debug("Request to save QuizApp : {}", quizApp);        QuizApp result = quizAppRepository.save(quizApp);
        quizAppSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the quizApps.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<QuizApp> findAll(Pageable pageable) {
        log.debug("Request to get all QuizApps");
        return quizAppRepository.findAll(pageable);
    }

    /**
     * Get all the QuizApp with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    /*public Page<QuizApp> findAllWithEagerRelationships(Pageable pageable) {
        return quizAppRepository.findAllWithEagerRelationships(pageable);
    }*/
    

    /**
     * Get one quizApp by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<QuizApp> findOne(Long id) {
        log.debug("Request to get QuizApp : {}", id);
        return quizAppRepository.findQuizAppById(id);
    }

    /**
     * Delete the quizApp by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete QuizApp : {}", id);
        quizAppRepository.deleteById(id);
        quizAppSearchRepository.deleteById(id);
    }

    /**
     * Search for the quizApp corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<QuizApp> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of QuizApps for query {}", query);
        return quizAppSearchRepository.search(queryStringQuery(query), pageable);    }
}
