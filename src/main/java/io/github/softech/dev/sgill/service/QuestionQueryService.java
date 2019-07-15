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

import io.github.softech.dev.sgill.domain.Question;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.QuestionRepository;
import io.github.softech.dev.sgill.repository.search.QuestionSearchRepository;
import io.github.softech.dev.sgill.service.dto.QuestionCriteria;


/**
 * Service for executing complex queries for Question entities in the database.
 * The main input is a {@link QuestionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Question} or a {@link Page} of {@link Question} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuestionQueryService extends QueryService<Question> {

    private final Logger log = LoggerFactory.getLogger(QuestionQueryService.class);

    private final QuestionRepository questionRepository;

    private final QuestionSearchRepository questionSearchRepository;

    public QuestionQueryService(QuestionRepository questionRepository, QuestionSearchRepository questionSearchRepository) {
        this.questionRepository = questionRepository;
        this.questionSearchRepository = questionSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Question} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Question> findByCriteria(QuestionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Question> specification = createSpecification(criteria);
        return questionRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Question} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Question> findByCriteria(QuestionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Question> specification = createSpecification(criteria);
        return questionRepository.findAll(specification, page);
    }

    /**
     * Function to convert QuestionCriteria to a {@link Specification}
     */
    private Specification<Question> createSpecification(QuestionCriteria criteria) {
        Specification<Question> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Question_.id));
            }
            if (criteria.getTextQuestion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTextQuestion(), Question_.textQuestion));
            }
            if (criteria.getDifficulty() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDifficulty(), Question_.difficulty));
            }
            if (criteria.getRestudy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRestudy(), Question_.restudy));
            }
            if (criteria.getUsed() != null) {
                specification = specification.and(buildSpecification(criteria.getUsed(), Question_.used));
            }
            if (criteria.getChoiceId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getChoiceId(), Question_.choices, Choice_.id));
            }
            if (criteria.getQuizId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getQuizId(), Question_.quiz, Quiz_.id));
            }
        }
        return specification;
    }

}
