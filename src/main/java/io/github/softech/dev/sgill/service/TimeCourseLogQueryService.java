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

import io.github.softech.dev.sgill.domain.TimeCourseLog;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.TimeCourseLogRepository;
import io.github.softech.dev.sgill.repository.search.TimeCourseLogSearchRepository;
import io.github.softech.dev.sgill.service.dto.TimeCourseLogCriteria;


/**
 * Service for executing complex queries for TimeCourseLog entities in the database.
 * The main input is a {@link TimeCourseLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TimeCourseLog} or a {@link Page} of {@link TimeCourseLog} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TimeCourseLogQueryService extends QueryService<TimeCourseLog> {

    private final Logger log = LoggerFactory.getLogger(TimeCourseLogQueryService.class);

    private final TimeCourseLogRepository timeCourseLogRepository;

    private final TimeCourseLogSearchRepository timeCourseLogSearchRepository;

    public TimeCourseLogQueryService(TimeCourseLogRepository timeCourseLogRepository, TimeCourseLogSearchRepository timeCourseLogSearchRepository) {
        this.timeCourseLogRepository = timeCourseLogRepository;
        this.timeCourseLogSearchRepository = timeCourseLogSearchRepository;
    }

    /**
     * Return a {@link List} of {@link TimeCourseLog} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TimeCourseLog> findByCriteria(TimeCourseLogCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TimeCourseLog> specification = createSpecification(criteria);
        return timeCourseLogRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link TimeCourseLog} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TimeCourseLog> findByCriteria(TimeCourseLogCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TimeCourseLog> specification = createSpecification(criteria);
        return timeCourseLogRepository.findAll(specification, page);
    }

    /**
     * Function to convert TimeCourseLogCriteria to a {@link Specification}
     */
    private Specification<TimeCourseLog> createSpecification(TimeCourseLogCriteria criteria) {
        Specification<TimeCourseLog> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), TimeCourseLog_.id));
            }
            if (criteria.getTimespent() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimespent(), TimeCourseLog_.timespent));
            }
            /**if (criteria.getRecorddate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRecorddate(), TimeCourseLog_.recorddate));
            }*/
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCustomerId(), TimeCourseLog_.customer, Customer_.id));
            }
        }
        return specification;
    }

}
