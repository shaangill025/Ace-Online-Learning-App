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

import io.github.softech.dev.sgill.domain.Choice;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.ChoiceRepository;
import io.github.softech.dev.sgill.repository.search.ChoiceSearchRepository;
import io.github.softech.dev.sgill.service.dto.ChoiceCriteria;


/**
 * Service for executing complex queries for Choice entities in the database.
 * The main input is a {@link ChoiceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Choice} or a {@link Page} of {@link Choice} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ChoiceQueryService extends QueryService<Choice> {

    private final Logger log = LoggerFactory.getLogger(ChoiceQueryService.class);

    private final ChoiceRepository choiceRepository;

    private final ChoiceSearchRepository choiceSearchRepository;

    public ChoiceQueryService(ChoiceRepository choiceRepository, ChoiceSearchRepository choiceSearchRepository) {
        this.choiceRepository = choiceRepository;
        this.choiceSearchRepository = choiceSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Choice} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Choice> findByCriteria(ChoiceCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Choice> specification = createSpecification(criteria);
        return choiceRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Choice} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Choice> findByCriteria(ChoiceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Choice> specification = createSpecification(criteria);
        return choiceRepository.findAll(specification, page);
    }

    /**
     * Function to convert ChoiceCriteria to a {@link Specification}
     */
    private Specification<Choice> createSpecification(ChoiceCriteria criteria) {
        Specification<Choice> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Choice_.id));
            }
            if (criteria.getTextChoice() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTextChoice(), Choice_.textChoice));
            }
            if (criteria.getIsanswer() != null) {
                specification = specification.and(buildSpecification(criteria.getIsanswer(), Choice_.isanswer));
            }
            if (criteria.getQuestionId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getQuestionId(), Choice_.question, Question_.id));
            }
        }
        return specification;
    }

}
