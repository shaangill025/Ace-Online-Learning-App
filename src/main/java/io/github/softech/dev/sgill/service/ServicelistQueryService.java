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

import io.github.softech.dev.sgill.domain.Servicelist;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.ServicelistRepository;
import io.github.softech.dev.sgill.repository.search.ServicelistSearchRepository;
import io.github.softech.dev.sgill.service.dto.ServicelistCriteria;


/**
 * Service for executing complex queries for Servicelist entities in the database.
 * The main input is a {@link ServicelistCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Servicelist} or a {@link Page} of {@link Servicelist} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ServicelistQueryService extends QueryService<Servicelist> {

    private final Logger log = LoggerFactory.getLogger(ServicelistQueryService.class);

    private final ServicelistRepository servicelistRepository;

    private final ServicelistSearchRepository servicelistSearchRepository;

    public ServicelistQueryService(ServicelistRepository servicelistRepository, ServicelistSearchRepository servicelistSearchRepository) {
        this.servicelistRepository = servicelistRepository;
        this.servicelistSearchRepository = servicelistSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Servicelist} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Servicelist> findByCriteria(ServicelistCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Servicelist> specification = createSpecification(criteria);
        return servicelistRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Servicelist} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Servicelist> findByCriteria(ServicelistCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Servicelist> specification = createSpecification(criteria);
        return servicelistRepository.findAll(specification, page);
    }

    /**
     * Function to convert ServicelistCriteria to a {@link Specification}
     */
    private Specification<Servicelist> createSpecification(ServicelistCriteria criteria) {
        Specification<Servicelist> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Servicelist_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Servicelist_.name));
            }
            if (criteria.getCompany() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCompany(), Servicelist_.company));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), Servicelist_.url));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), Servicelist_.phone));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Servicelist_.email));
            }
            if (criteria.getAreas() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAreas(), Servicelist_.areas));
            }
            if (criteria.getSpeciality() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSpeciality(), Servicelist_.speciality));
            }
            if (criteria.getTrades() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTrades(), Servicelist_.trades));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCustomerId(), Servicelist_.customer, Customer_.id));
            }
        }
        return specification;
    }

}
