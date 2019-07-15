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

import io.github.softech.dev.sgill.domain.Orders;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.OrdersRepository;
import io.github.softech.dev.sgill.repository.search.OrdersSearchRepository;
import io.github.softech.dev.sgill.service.dto.OrdersCriteria;


/**
 * Service for executing complex queries for Orders entities in the database.
 * The main input is a {@link OrdersCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Orders} or a {@link Page} of {@link Orders} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrdersQueryService extends QueryService<Orders> {

    private final Logger log = LoggerFactory.getLogger(OrdersQueryService.class);

    private final OrdersRepository ordersRepository;

    private final OrdersSearchRepository ordersSearchRepository;

    public OrdersQueryService(OrdersRepository ordersRepository, OrdersSearchRepository ordersSearchRepository) {
        this.ordersRepository = ordersRepository;
        this.ordersSearchRepository = ordersSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Orders} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Orders> findByCriteria(OrdersCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Orders> specification = createSpecification(criteria);
        return ordersRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Orders} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Orders> findByCriteria(OrdersCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Orders> specification = createSpecification(criteria);
        return ordersRepository.findAll(specification, page);
    }

    /**
     * Function to convert OrdersCriteria to a {@link Specification}
     */
    private Specification<Orders> createSpecification(OrdersCriteria criteria) {
        Specification<Orders> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Orders_.id));
            }
            /**if (criteria.getCreateddate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreateddate(), Orders_.createddate));
            }*/
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), Orders_.amount));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Orders_.status));
            }
            if (criteria.getPayment() != null) {
                specification = specification.and(buildSpecification(criteria.getPayment(), Orders_.payment));
            }
            if (criteria.getGateway_id() != null) {
                specification = specification.and(buildStringSpecification(criteria.getGateway_id(), Orders_.gateway_id));
            }
            if (criteria.getSeller_message() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSeller_message(), Orders_.seller_message));
            }
            if (criteria.getNetwork_status() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNetwork_status(), Orders_.network_status));
            }
            if (criteria.getSeller_status() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSeller_status(), Orders_.seller_status));
            }
            if (criteria.getGateway_amt() != null) {
                specification = specification.and(buildStringSpecification(criteria.getGateway_amt(), Orders_.gateway_amt));
            }
            if (criteria.getSeller_type() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSeller_type(), Orders_.seller_type));
            }
            if (criteria.getCard_type() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCard_type(), Orders_.card_type));
            }
            if (criteria.getLast4() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLast4(), Orders_.last4));
            }
            if (criteria.getCartId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCartId(), Orders_.cart, Cart_.id));
            }
        }
        return specification;
    }

}
