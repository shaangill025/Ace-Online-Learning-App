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

import io.github.softech.dev.sgill.domain.CompanyRequest;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.CompanyRequestRepository;
import io.github.softech.dev.sgill.repository.search.CompanyRequestSearchRepository;
import io.github.softech.dev.sgill.service.dto.CompanyRequestCriteria;

/**
 * Service for executing complex queries for CompanyRequest entities in the database.
 * The main input is a {@link CompanyRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CompanyRequest} or a {@link Page} of {@link CompanyRequest} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CompanyRequestQueryService extends QueryService<CompanyRequest> {

    private final Logger log = LoggerFactory.getLogger(CompanyRequestQueryService.class);

    private final CompanyRequestRepository companyRequestRepository;

    private final CompanyRequestSearchRepository companyRequestSearchRepository;

    public CompanyRequestQueryService(CompanyRequestRepository companyRequestRepository, CompanyRequestSearchRepository companyRequestSearchRepository) {
        this.companyRequestRepository = companyRequestRepository;
        this.companyRequestSearchRepository = companyRequestSearchRepository;
    }

    /**
     * Return a {@link List} of {@link CompanyRequest} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CompanyRequest> findByCriteria(CompanyRequestCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<CompanyRequest> specification = createSpecification(criteria);
        return companyRequestRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link CompanyRequest} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CompanyRequest> findByCriteria(CompanyRequestCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CompanyRequest> specification = createSpecification(criteria);
        return companyRequestRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CompanyRequestCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<CompanyRequest> specification = createSpecification(criteria);
        return companyRequestRepository.count(specification);
    }

    /**
     * Function to convert CompanyRequestCriteria to a {@link Specification}
     */
    private Specification<CompanyRequest> createSpecification(CompanyRequestCriteria criteria) {
        Specification<CompanyRequest> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), CompanyRequest_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), CompanyRequest_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), CompanyRequest_.description));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), CompanyRequest_.phone));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), CompanyRequest_.email));
            }
            if (criteria.getStreetAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStreetAddress(), CompanyRequest_.streetAddress));
            }
            if (criteria.getPostalCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPostalCode(), CompanyRequest_.postalCode));
            }
            if (criteria.getCity() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCity(), CompanyRequest_.city));
            }
            if (criteria.getStateProvince() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStateProvince(), CompanyRequest_.stateProvince));
            }
            if (criteria.getCountry() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCountry(), CompanyRequest_.country));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), CompanyRequest_.url));
            }
            if (criteria.getLicenceCycle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLicenceCycle(), CompanyRequest_.licenceCycle));
            }
        }
        return specification;
    }
}
