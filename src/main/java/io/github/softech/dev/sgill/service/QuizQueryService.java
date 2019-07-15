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

import io.github.softech.dev.sgill.domain.Quiz;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.QuizRepository;
import io.github.softech.dev.sgill.repository.search.QuizSearchRepository;
import io.github.softech.dev.sgill.service.dto.QuizCriteria;


/**
 * Service for executing complex queries for Quiz entities in the database.
 * The main input is a {@link QuizCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Quiz} or a {@link Page} of {@link Quiz} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuizQueryService extends QueryService<Quiz> {

    private final Logger log = LoggerFactory.getLogger(QuizQueryService.class);

    private final QuizRepository quizRepository;

    private final QuizSearchRepository quizSearchRepository;

    public QuizQueryService(QuizRepository quizRepository, QuizSearchRepository quizSearchRepository) {
        this.quizRepository = quizRepository;
        this.quizSearchRepository = quizSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Quiz} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Quiz> findByCriteria(QuizCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Quiz> specification = createSpecification(criteria);
        return quizRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Quiz} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Quiz> findByCriteria(QuizCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Quiz> specification = createSpecification(criteria);
        return quizRepository.findAll(specification, page);
    }

    /**
     * Function to convert QuizCriteria to a {@link Specification}
     */
    private Specification<Quiz> createSpecification(QuizCriteria criteria) {
        Specification<Quiz> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Quiz_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Quiz_.name));
            }
            if (criteria.getDifficulty() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDifficulty(), Quiz_.difficulty));
            }
            if (criteria.getPassingscore() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPassingscore(), Quiz_.passingscore));
            }
            if (criteria.getNewSectionId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getNewSectionId(), Quiz_.newSection, Section_.id));
            }
        }
        return specification;
    }

}
