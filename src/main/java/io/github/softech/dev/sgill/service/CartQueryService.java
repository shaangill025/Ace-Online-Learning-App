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

import io.github.softech.dev.sgill.domain.Cart;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.CartRepository;
import io.github.softech.dev.sgill.repository.search.CartSearchRepository;
import io.github.softech.dev.sgill.service.dto.CartCriteria;


/**
 * Service for executing complex queries for Cart entities in the database.
 * The main input is a {@link CartCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Cart} or a {@link Page} of {@link Cart} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CartQueryService extends QueryService<Cart> {

    private final Logger log = LoggerFactory.getLogger(CartQueryService.class);

    private final CartRepository cartRepository;

    private final CartSearchRepository cartSearchRepository;

    public CartQueryService(CartRepository cartRepository, CartSearchRepository cartSearchRepository) {
        this.cartRepository = cartRepository;
        this.cartSearchRepository = cartSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Cart} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Cart> findByCriteria(CartCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Cart> specification = createSpecification(criteria);
        return cartRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Cart} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Cart> findByCriteria(CartCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Cart> specification = createSpecification(criteria);
        return cartRepository.findAll(specification, page);
    }

    /**
     * Function to convert CartCriteria to a {@link Specification}
     */
    private Specification<Cart> createSpecification(CartCriteria criteria) {
        Specification<Cart> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Cart_.id));
            }
            if (criteria.getNormCart() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNormCart(), Cart_.normCart));
            }
            if (criteria.getCreateddate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreateddate(), Cart_.createddate));
            }
            if (criteria.getLastactivedate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastactivedate(), Cart_.lastactivedate));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), Cart_.amount));
            }
            if (criteria.getCheckout() != null) {
                specification = specification.and(buildSpecification(criteria.getCheckout(), Cart_.checkout));
            }
            if (criteria.getPoints() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPoints(), Cart_.points));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCustomerId(), Cart_.customer, Customer_.id));
            }
        }
        return specification;
    }

}
