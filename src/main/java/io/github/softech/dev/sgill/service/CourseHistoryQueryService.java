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

import io.github.softech.dev.sgill.domain.CourseHistory;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.CourseHistoryRepository;
import io.github.softech.dev.sgill.repository.search.CourseHistorySearchRepository;
import io.github.softech.dev.sgill.service.dto.CourseHistoryCriteria;


/**
 * Service for executing complex queries for CourseHistory entities in the database.
 * The main input is a {@link CourseHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CourseHistory} or a {@link Page} of {@link CourseHistory} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CourseHistoryQueryService extends QueryService<CourseHistory> {

    private final Logger log = LoggerFactory.getLogger(CourseHistoryQueryService.class);

    private final CourseHistoryRepository courseHistoryRepository;

    private final CourseHistorySearchRepository courseHistorySearchRepository;

    public CourseHistoryQueryService(CourseHistoryRepository courseHistoryRepository, CourseHistorySearchRepository courseHistorySearchRepository) {
        this.courseHistoryRepository = courseHistoryRepository;
        this.courseHistorySearchRepository = courseHistorySearchRepository;
    }

    /**
     * Return a {@link List} of {@link CourseHistory} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CourseHistory> findByCriteria(CourseHistoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<CourseHistory> specification = createSpecification(criteria);
        return courseHistoryRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link CourseHistory} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseHistory> findByCriteria(CourseHistoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CourseHistory> specification = createSpecification(criteria);
        return courseHistoryRepository.findAll(specification, page);
    }

    /**
     * Function to convert CourseHistoryCriteria to a {@link Specification}
     */
    private Specification<CourseHistory> createSpecification(CourseHistoryCriteria criteria) {
        Specification<CourseHistory> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), CourseHistory_.id));
            }
            if (criteria.getStartdate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartdate(), CourseHistory_.startdate));
            }
            if (criteria.getLastactivedate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastactivedate(), CourseHistory_.lastactivedate));
            }
            if (criteria.getIsactive() != null) {
                specification = specification.and(buildSpecification(criteria.getIsactive(), CourseHistory_.isactive));
            }
            if (criteria.getIscompleted() != null) {
                specification = specification.and(buildSpecification(criteria.getIscompleted(), CourseHistory_.iscompleted));
            }
            if (criteria.getAccess() != null) {
                specification = specification.and(buildSpecification(criteria.getAccess(), CourseHistory_.access));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCustomerId(), CourseHistory_.customer, Customer_.id));
            }
            if (criteria.getCourseId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCourseId(), CourseHistory_.course, Course_.id));
            }
        }
        return specification;
    }

}
