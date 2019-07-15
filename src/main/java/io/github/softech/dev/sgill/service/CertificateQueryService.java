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

import io.github.softech.dev.sgill.domain.Certificate;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.CertificateRepository;
import io.github.softech.dev.sgill.repository.search.CertificateSearchRepository;
import io.github.softech.dev.sgill.service.dto.CertificateCriteria;


/**
 * Service for executing complex queries for Certificate entities in the database.
 * The main input is a {@link CertificateCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Certificate} or a {@link Page} of {@link Certificate} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CertificateQueryService extends QueryService<Certificate> {

    private final Logger log = LoggerFactory.getLogger(CertificateQueryService.class);

    private final CertificateRepository certificateRepository;

    private final CertificateSearchRepository certificateSearchRepository;

    public CertificateQueryService(CertificateRepository certificateRepository, CertificateSearchRepository certificateSearchRepository) {
        this.certificateRepository = certificateRepository;
        this.certificateSearchRepository = certificateSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Certificate} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Certificate> findByCriteria(CertificateCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Certificate> specification = createSpecification(criteria);
        return certificateRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Certificate} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Certificate> findByCriteria(CertificateCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Certificate> specification = createSpecification(criteria);
        return certificateRepository.findAll(specification, page);
    }

    /**
     * Function to convert CertificateCriteria to a {@link Specification}
     */
    private Specification<Certificate> createSpecification(CertificateCriteria criteria) {
        Specification<Certificate> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Certificate_.id));
            }
            if (criteria.getTimestamp() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimestamp(), Certificate_.timestamp));
            }
            if (criteria.getIsEmailed() != null) {
                specification = specification.and(buildSpecification(criteria.getIsEmailed(), Certificate_.isEmailed));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCustomerId(), Certificate_.customer, Customer_.id));
            }
        }
        return specification;
    }

}
