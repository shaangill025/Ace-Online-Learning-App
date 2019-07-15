package io.github.softech.dev.sgill.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import io.github.softech.dev.sgill.domain.QuestionHistory;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.QuestionHistoryRepository;
import io.github.softech.dev.sgill.repository.search.QuestionHistorySearchRepository;
import io.github.softech.dev.sgill.service.dto.QuestionHistoryCriteria;


/**
 * Service for executing complex queries for QuestionHistory entities in the database.
 * The main input is a {@link QuestionHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link QuestionHistory} or a {@link Page} of {@link QuestionHistory} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuestionHistoryQueryService extends QueryService<QuestionHistory> {

    private final Logger log = LoggerFactory.getLogger(QuestionHistoryQueryService.class);

    private final QuestionHistoryRepository questionHistoryRepository;

    private final QuestionHistorySearchRepository questionHistorySearchRepository;

    public QuestionHistoryQueryService(QuestionHistoryRepository questionHistoryRepository, QuestionHistorySearchRepository questionHistorySearchRepository) {
        this.questionHistoryRepository = questionHistoryRepository;
        this.questionHistorySearchRepository = questionHistorySearchRepository;
    }

    /**
     * Return a {@link List} of {@link QuestionHistory} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<QuestionHistory> findByCriteria(QuestionHistoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<QuestionHistory> specification = createSpecification(criteria);
        return questionHistoryRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link QuestionHistory} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<QuestionHistory> findByCriteria(QuestionHistoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<QuestionHistory> specification = createSpecification(criteria);
        return questionHistoryRepository.findAll(specification, page);
    }

    /**
     * Function to convert QuestionHistoryCriteria to a {@link Specification}
     */
    private Specification<QuestionHistory> createSpecification(QuestionHistoryCriteria criteria) {
        Specification<QuestionHistory> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), QuestionHistory_.id));
            }
            /**if (criteria.getTimestamp() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimestamp(), QuestionHistory_.timestamp));
            }*/
            if (criteria.getCorrect() != null) {
                specification = specification.and(buildSpecification(criteria.getCorrect(), QuestionHistory_.correct));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCustomerId(), QuestionHistory_.customer, Customer_.id));
            }
            if (criteria.getQuestionId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getQuestionId(), QuestionHistory_.question, Question_.id));
            }
        }
        return specification;
    }

}
