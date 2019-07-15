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

import io.github.softech.dev.sgill.domain.QuizApp;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.QuizAppRepository;
import io.github.softech.dev.sgill.repository.search.QuizAppSearchRepository;
import io.github.softech.dev.sgill.service.dto.QuizAppCriteria;


/**
 * Service for executing complex queries for QuizApp entities in the database.
 * The main input is a {@link QuizAppCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link QuizApp} or a {@link Page} of {@link QuizApp} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuizAppQueryService extends QueryService<QuizApp> {

    private final Logger log = LoggerFactory.getLogger(QuizAppQueryService.class);

    private final QuizAppRepository quizAppRepository;

    private final QuizAppSearchRepository quizAppSearchRepository;

    public QuizAppQueryService(QuizAppRepository quizAppRepository, QuizAppSearchRepository quizAppSearchRepository) {
        this.quizAppRepository = quizAppRepository;
        this.quizAppSearchRepository = quizAppSearchRepository;
    }

    /**
     * Return a {@link List} of {@link QuizApp} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<QuizApp> findByCriteria(QuizAppCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<QuizApp> specification = createSpecification(criteria);
        return quizAppRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link QuizApp} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<QuizApp> findByCriteria(QuizAppCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<QuizApp> specification = createSpecification(criteria);
        return quizAppRepository.findAll(specification, page);
    }

    /**
     * Function to convert QuizAppCriteria to a {@link Specification}
     */
    private Specification<QuizApp> createSpecification(QuizAppCriteria criteria) {
        Specification<QuizApp> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), QuizApp_.id));
            }
            if (criteria.getQuizId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getQuizId(), QuizApp_.quiz, Quiz_.id));
            }
            if (criteria.getCurrSectionId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCurrSectionId(), QuizApp_.currSection, Section_.id));
            }
            if (criteria.getNewSectionId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getNewSectionId(), QuizApp_.newSection, Section_.id));
            }
        }
        return specification;
    }

}
