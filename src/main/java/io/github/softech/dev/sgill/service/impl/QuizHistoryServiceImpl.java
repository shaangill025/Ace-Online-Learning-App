package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.service.QuizHistoryService;
import io.github.softech.dev.sgill.domain.QuizHistory;
import io.github.softech.dev.sgill.repository.QuizHistoryRepository;
import io.github.softech.dev.sgill.repository.search.QuizHistorySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing QuizHistory.
 */
@Service
@Transactional
public class QuizHistoryServiceImpl implements QuizHistoryService {

    private final Logger log = LoggerFactory.getLogger(QuizHistoryServiceImpl.class);

    private final QuizHistoryRepository quizHistoryRepository;

    private final QuizHistorySearchRepository quizHistorySearchRepository;

    public QuizHistoryServiceImpl(QuizHistoryRepository quizHistoryRepository, QuizHistorySearchRepository quizHistorySearchRepository) {
        this.quizHistoryRepository = quizHistoryRepository;
        this.quizHistorySearchRepository = quizHistorySearchRepository;
    }

    /**
     * Save a quizHistory.
     *
     * @param quizHistory the entity to save
     * @return the persisted entity
     */
    @Override
    public QuizHistory save(QuizHistory quizHistory) {
        log.debug("Request to save QuizHistory : {}", quizHistory);        QuizHistory result = quizHistoryRepository.save(quizHistory);
        quizHistorySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the quizHistories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<QuizHistory> findAll(Pageable pageable) {
        log.debug("Request to get all QuizHistories");
        return quizHistoryRepository.findAll(pageable);
    }


    /**
     * Get one quizHistory by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<QuizHistory> findOne(Long id) {
        log.debug("Request to get QuizHistory : {}", id);
        return quizHistoryRepository.findById(id);
    }

    /**
     * Delete the quizHistory by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete QuizHistory : {}", id);
        quizHistoryRepository.deleteById(id);
        quizHistorySearchRepository.deleteById(id);
    }

    /**
     * Search for the quizHistory corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<QuizHistory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of QuizHistories for query {}", query);
        return quizHistorySearchRepository.search(queryStringQuery(query), pageable);    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<QuizHistory>> getQuizHistoriesByCustomer(Customer customer) {
        log.debug("Request to find QuizHistories for customer {}", customer);
        return quizHistoryRepository.findQuizHistoriesByCustomer(customer);    }
}
