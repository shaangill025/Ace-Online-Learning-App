package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.service.QuestionHistoryService;
import io.github.softech.dev.sgill.domain.QuestionHistory;
import io.github.softech.dev.sgill.repository.QuestionHistoryRepository;
import io.github.softech.dev.sgill.repository.search.QuestionHistorySearchRepository;
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
 * Service Implementation for managing QuestionHistory.
 */
@Service
@Transactional
public class QuestionHistoryServiceImpl implements QuestionHistoryService {

    private final Logger log = LoggerFactory.getLogger(QuestionHistoryServiceImpl.class);

    private final QuestionHistoryRepository questionHistoryRepository;

    private final QuestionHistorySearchRepository questionHistorySearchRepository;

    public QuestionHistoryServiceImpl(QuestionHistoryRepository questionHistoryRepository, QuestionHistorySearchRepository questionHistorySearchRepository) {
        this.questionHistoryRepository = questionHistoryRepository;
        this.questionHistorySearchRepository = questionHistorySearchRepository;
    }

    /**
     * Save a questionHistory.
     *
     * @param questionHistory the entity to save
     * @return the persisted entity
     */
    @Override
    public QuestionHistory save(QuestionHistory questionHistory) {
        log.debug("Request to save QuestionHistory : {}", questionHistory);        QuestionHistory result = questionHistoryRepository.save(questionHistory);
        questionHistorySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the questionHistories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<QuestionHistory> findAll(Pageable pageable) {
        log.debug("Request to get all QuestionHistories");
        return questionHistoryRepository.findAll(pageable);
    }


    /**
     * Get one questionHistory by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<QuestionHistory> findOne(Long id) {
        log.debug("Request to get QuestionHistory : {}", id);
        return questionHistoryRepository.findById(id);
    }

    /**
     * Delete the questionHistory by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete QuestionHistory : {}", id);
        questionHistoryRepository.deleteById(id);
        questionHistorySearchRepository.deleteById(id);
    }

    /**
     * Search for the questionHistory corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<QuestionHistory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of QuestionHistories for query {}", query);
        return questionHistorySearchRepository.search(queryStringQuery(query), pageable);    }

    @Override
    public Optional<List<QuestionHistory>> getQuestionHistoriesByCustomer(Customer customer) {
        return questionHistoryRepository.findQuestionHistoriesByCustomer(customer);    }
}
