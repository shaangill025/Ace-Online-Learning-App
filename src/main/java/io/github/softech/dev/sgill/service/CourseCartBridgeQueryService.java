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

import io.github.softech.dev.sgill.domain.CourseCartBridge;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.CourseCartBridgeRepository;
import io.github.softech.dev.sgill.repository.search.CourseCartBridgeSearchRepository;
import io.github.softech.dev.sgill.service.dto.CourseCartBridgeCriteria;


/**
 * Service for executing complex queries for CourseCartBridge entities in the database.
 * The main input is a {@link CourseCartBridgeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CourseCartBridge} or a {@link Page} of {@link CourseCartBridge} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CourseCartBridgeQueryService extends QueryService<CourseCartBridge> {

    private final Logger log = LoggerFactory.getLogger(CourseCartBridgeQueryService.class);

    private final CourseCartBridgeRepository courseCartBridgeRepository;

    private final CourseCartBridgeSearchRepository courseCartBridgeSearchRepository;

    public CourseCartBridgeQueryService(CourseCartBridgeRepository courseCartBridgeRepository, CourseCartBridgeSearchRepository courseCartBridgeSearchRepository) {
        this.courseCartBridgeRepository = courseCartBridgeRepository;
        this.courseCartBridgeSearchRepository = courseCartBridgeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link CourseCartBridge} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CourseCartBridge> findByCriteria(CourseCartBridgeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<CourseCartBridge> specification = createSpecification(criteria);
        return courseCartBridgeRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link CourseCartBridge} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseCartBridge> findByCriteria(CourseCartBridgeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CourseCartBridge> specification = createSpecification(criteria);
        return courseCartBridgeRepository.findAll(specification, page);
    }

    /**
     * Function to convert CourseCartBridgeCriteria to a {@link Specification}
     */
    private Specification<CourseCartBridge> createSpecification(CourseCartBridgeCriteria criteria) {
        Specification<CourseCartBridge> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), CourseCartBridge_.id));
            }
            /**if (criteria.getTimestamp() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimestamp(), CourseCartBridge_.timestamp));
            }*/
            if (criteria.getCartId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCartId(), CourseCartBridge_.cart, Cart_.id));
            }
            if (criteria.getCourseId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCourseId(), CourseCartBridge_.course, Course_.id));
            }
        }
        return specification;
    }

}
