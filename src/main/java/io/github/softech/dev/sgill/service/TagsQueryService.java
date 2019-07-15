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

import io.github.softech.dev.sgill.domain.Tags;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.TagsRepository;
import io.github.softech.dev.sgill.repository.search.TagsSearchRepository;
import io.github.softech.dev.sgill.service.dto.TagsCriteria;


/**
 * Service for executing complex queries for Tags entities in the database.
 * The main input is a {@link TagsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Tags} or a {@link Page} of {@link Tags} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TagsQueryService extends QueryService<Tags> {

    private final Logger log = LoggerFactory.getLogger(TagsQueryService.class);

    private final TagsRepository tagsRepository;

    private final TagsSearchRepository tagsSearchRepository;

    public TagsQueryService(TagsRepository tagsRepository, TagsSearchRepository tagsSearchRepository) {
        this.tagsRepository = tagsRepository;
        this.tagsSearchRepository = tagsSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Tags} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Tags> findByCriteria(TagsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Tags> specification = createSpecification(criteria);
        return tagsRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Tags} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Tags> findByCriteria(TagsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Tags> specification = createSpecification(criteria);
        return tagsRepository.findAll(specification, page);
    }

    /**
     * Function to convert TagsCriteria to a {@link Specification}
     */
    private Specification<Tags> createSpecification(TagsCriteria criteria) {
        Specification<Tags> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Tags_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Tags_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Tags_.description));
            }
        }
        return specification;
    }

}
