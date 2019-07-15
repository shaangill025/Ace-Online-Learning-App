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

import io.github.softech.dev.sgill.domain.QuizHistory;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.QuizHistoryRepository;
import io.github.softech.dev.sgill.repository.search.QuizHistorySearchRepository;
import io.github.softech.dev.sgill.service.dto.QuizHistoryCriteria;


/**
 * Service for executing complex queries for QuizHistory entities in the database.
 * The main input is a {@link QuizHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link QuizHistory} or a {@link Page} of {@link QuizHistory} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuizHistoryQueryService extends QueryService<QuizHistory> {

    private final Logger log = LoggerFactory.getLogger(QuizHistoryQueryService.class);

    private final QuizHistoryRepository quizHistoryRepository;

    private final QuizHistorySearchRepository quizHistorySearchRepository;

    public QuizHistoryQueryService(QuizHistoryRepository quizHistoryRepository, QuizHistorySearchRepository quizHistorySearchRepository) {
        this.quizHistoryRepository = quizHistoryRepository;
        this.quizHistorySearchRepository = quizHistorySearchRepository;
    }

    /**
     * Return a {@link List} of {@link QuizHistory} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<QuizHistory> findByCriteria(QuizHistoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<QuizHistory> specification = createSpecification(criteria);
        return quizHistoryRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link QuizHistory} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<QuizHistory> findByCriteria(QuizHistoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<QuizHistory> specification = createSpecification(criteria);
        return quizHistoryRepository.findAll(specification, page);
    }

    /**
     * Function to convert QuizHistoryCriteria to a {@link Specification}
     */
    private Specification<QuizHistory> createSpecification(QuizHistoryCriteria criteria) {
        Specification<QuizHistory> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), QuizHistory_.id));
            }
            /**if (criteria.getStart() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStart(), QuizHistory_.start));
            }*/
            if (criteria.getPassed() != null) {
                specification = specification.and(buildSpecification(criteria.getPassed(), QuizHistory_.passed));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCustomerId(), QuizHistory_.customer, Customer_.id));
            }
            if (criteria.getQuizId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getQuizId(), QuizHistory_.quiz, Quiz_.id));
            }
        }
        return specification;
    }

}
