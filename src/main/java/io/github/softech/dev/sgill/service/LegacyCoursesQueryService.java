package io.github.softech.dev.sgill.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import io.github.softech.dev.sgill.domain.LegacyCourses;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.LegacyCoursesRepository;
import io.github.softech.dev.sgill.repository.search.LegacyCoursesSearchRepository;
import io.github.softech.dev.sgill.service.dto.LegacyCoursesCriteria;

/**
 * Service for executing complex queries for LegacyCourses entities in the database.
 * The main input is a {@link LegacyCoursesCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LegacyCourses} or a {@link Page} of {@link LegacyCourses} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LegacyCoursesQueryService extends QueryService<LegacyCourses> {

    private final Logger log = LoggerFactory.getLogger(LegacyCoursesQueryService.class);

    private final LegacyCoursesRepository legacyCoursesRepository;

    private final LegacyCoursesSearchRepository legacyCoursesSearchRepository;

    public LegacyCoursesQueryService(LegacyCoursesRepository legacyCoursesRepository, LegacyCoursesSearchRepository legacyCoursesSearchRepository) {
        this.legacyCoursesRepository = legacyCoursesRepository;
        this.legacyCoursesSearchRepository = legacyCoursesSearchRepository;
    }

    /**
     * Return a {@link List} of {@link LegacyCourses} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LegacyCourses> findByCriteria(LegacyCoursesCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<LegacyCourses> specification = createSpecification(criteria);
        return legacyCoursesRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link LegacyCourses} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LegacyCourses> findByCriteria(LegacyCoursesCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LegacyCourses> specification = createSpecification(criteria);
        return legacyCoursesRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LegacyCoursesCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<LegacyCourses> specification = createSpecification(criteria);
        return legacyCoursesRepository.count(specification);
    }

    /**
     * Function to convert LegacyCoursesCriteria to a {@link Specification}
     */
    private Specification<LegacyCourses> createSpecification(LegacyCoursesCriteria criteria) {
        Specification<LegacyCourses> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), LegacyCourses_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), LegacyCourses_.title));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), LegacyCourses_.description));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), LegacyCourses_.amount));
            }
            if (criteria.getProvince() != null) {
                specification = specification.and(buildStringSpecification(criteria.getProvince(), LegacyCourses_.province));
            }
        }
        return specification;
    }
}
