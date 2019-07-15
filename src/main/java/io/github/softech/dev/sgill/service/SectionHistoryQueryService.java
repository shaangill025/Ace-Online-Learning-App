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

import io.github.softech.dev.sgill.domain.SectionHistory;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.SectionHistoryRepository;
import io.github.softech.dev.sgill.repository.search.SectionHistorySearchRepository;
import io.github.softech.dev.sgill.service.dto.SectionHistoryCriteria;


/**
 * Service for executing complex queries for SectionHistory entities in the database.
 * The main input is a {@link SectionHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SectionHistory} or a {@link Page} of {@link SectionHistory} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SectionHistoryQueryService extends QueryService<SectionHistory> {

    private final Logger log = LoggerFactory.getLogger(SectionHistoryQueryService.class);

    private final SectionHistoryRepository sectionHistoryRepository;

    private final SectionHistorySearchRepository sectionHistorySearchRepository;

    public SectionHistoryQueryService(SectionHistoryRepository sectionHistoryRepository, SectionHistorySearchRepository sectionHistorySearchRepository) {
        this.sectionHistoryRepository = sectionHistoryRepository;
        this.sectionHistorySearchRepository = sectionHistorySearchRepository;
    }

    /**
     * Return a {@link List} of {@link SectionHistory} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SectionHistory> findByCriteria(SectionHistoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SectionHistory> specification = createSpecification(criteria);
        return sectionHistoryRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link SectionHistory} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SectionHistory> findByCriteria(SectionHistoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SectionHistory> specification = createSpecification(criteria);
        return sectionHistoryRepository.findAll(specification, page);
    }

    /**
     * Function to convert SectionHistoryCriteria to a {@link Specification}
     */
    private Specification<SectionHistory> createSpecification(SectionHistoryCriteria criteria) {
        Specification<SectionHistory> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), SectionHistory_.id));
            }
            /**if (criteria.getStartdate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartdate(), SectionHistory_.startdate));
            }*/
            /**if (criteria.getLastactivedate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastactivedate(), SectionHistory_.lastactivedate));
            }*/
            if (criteria.getWatched() != null) {
                specification = specification.and(buildSpecification(criteria.getWatched(), SectionHistory_.watched));
            }
            if (criteria.getStamp() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStamp(), SectionHistory_.stamp));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCustomerId(), SectionHistory_.customer, Customer_.id));
            }
            if (criteria.getSectionId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getSectionId(), SectionHistory_.section, Section_.id));
            }
        }
        return specification;
    }

}
