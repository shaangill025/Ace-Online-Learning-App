package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.service.OrdersService;
import io.github.softech.dev.sgill.domain.Orders;
import io.github.softech.dev.sgill.repository.OrdersRepository;
import io.github.softech.dev.sgill.repository.search.OrdersSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Orders.
 */
@Service
@Transactional
public class OrdersServiceImpl implements OrdersService {

    private final Logger log = LoggerFactory.getLogger(OrdersServiceImpl.class);

    private final OrdersRepository ordersRepository;

    private final OrdersSearchRepository ordersSearchRepository;

    public OrdersServiceImpl(OrdersRepository ordersRepository, OrdersSearchRepository ordersSearchRepository) {
        this.ordersRepository = ordersRepository;
        this.ordersSearchRepository = ordersSearchRepository;
    }

    /**
     * Save a orders.
     *
     * @param orders the entity to save
     * @return the persisted entity
     */
    @Override
    public Orders save(Orders orders) {
        log.debug("Request to save Orders : {}", orders);        Orders result = ordersRepository.save(orders);
        ordersSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the orders.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Orders> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        return ordersRepository.findAll(pageable);
    }


    /**
     * Get one orders by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Orders> findOne(Long id) {
        log.debug("Request to get Orders : {}", id);
        return ordersRepository.findById(id);
    }

    /**
     * Delete the orders by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Orders : {}", id);
        ordersRepository.deleteById(id);
        ordersSearchRepository.deleteById(id);
    }

    /**
     * Search for the orders corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Orders> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Orders for query {}", query);
        return ordersSearchRepository.search(queryStringQuery(query), pageable);    }
}
