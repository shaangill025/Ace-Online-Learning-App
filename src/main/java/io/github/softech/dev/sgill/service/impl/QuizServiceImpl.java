package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.service.QuizService;
import io.github.softech.dev.sgill.domain.Quiz;
import io.github.softech.dev.sgill.repository.QuizRepository;
import io.github.softech.dev.sgill.repository.search.QuizSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Quiz.
 */
@Service
@Transactional
public class QuizServiceImpl implements QuizService {

    private final Logger log = LoggerFactory.getLogger(QuizServiceImpl.class);

    private final QuizRepository quizRepository;

    private final QuizSearchRepository quizSearchRepository;

    public QuizServiceImpl(QuizRepository quizRepository, QuizSearchRepository quizSearchRepository) {
        this.quizRepository = quizRepository;
        this.quizSearchRepository = quizSearchRepository;
    }

    /**
     * Save a quiz.
     *
     * @param quiz the entity to save
     * @return the persisted entity
     */
    @Override
    public Quiz save(Quiz quiz) {
        log.debug("Request to save Quiz : {}", quiz);        Quiz result = quizRepository.save(quiz);
        quizSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the quizzes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Quiz> findAll(Pageable pageable) {
        log.debug("Request to get all Quizzes");
        return quizRepository.findAll(pageable);
    }


    /**
     * Get one quiz by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Quiz> findOne(Long id) {
        log.debug("Request to get Quiz : {}", id);
        return quizRepository.findById(id);
    }

    /**
     * Delete the quiz by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Quiz : {}", id);
        quizRepository.deleteById(id);
        quizSearchRepository.deleteById(id);
    }

    /**
     * Search for the quiz corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Quiz> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Quizzes for query {}", query);
        return quizSearchRepository.search(queryStringQuery(query), pageable);    }
}
