package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.domain.Servicelist;
import io.github.softech.dev.sgill.domain.User;
import io.github.softech.dev.sgill.repository.ServicelistRepository;
import io.github.softech.dev.sgill.repository.UserRepository;
import io.github.softech.dev.sgill.repository.search.ServicelistSearchRepository;
import io.github.softech.dev.sgill.repository.search.UserSearchRepository;
import io.github.softech.dev.sgill.service.CustomerService;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.repository.CustomerRepository;
import io.github.softech.dev.sgill.repository.search.CustomerSearchRepository;
import io.github.softech.dev.sgill.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Customer.
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    private final CustomerSearchRepository customerSearchRepository;

    private final UserRepository userRepository;

    private final UserSearchRepository userSearchRepository;

    private final UserService userService;

    private final ServicelistRepository servicelistRepository;

    private final ServicelistSearchRepository servicelistSearchRepository;

    private final CacheManager cacheManager;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerSearchRepository customerSearchRepository,
                               UserRepository userRepository, UserSearchRepository userSearchRepository, CacheManager cacheManager, UserService userService,
                               ServicelistRepository servicelistRepository, ServicelistSearchRepository servicelistSearchRepository) {
        this.customerRepository = customerRepository;
        this.customerSearchRepository = customerSearchRepository;
        this.userRepository = userRepository;
        this.userSearchRepository = userSearchRepository;
        this.cacheManager = cacheManager;
        this.userService = userService;
        this.servicelistRepository = servicelistRepository;
        this.servicelistSearchRepository = servicelistSearchRepository;
    }

    /**
     * Save a customer.
     *
     * @param customer the entity to save
     * @return the persisted entity
     */
    @Override
    public Customer save(Customer customer) {
        log.debug("Request to save Customer : {}", customer);
        Customer result = customerRepository.save(customer);
        customerSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the customers.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Customer> findAll(Pageable pageable) {
        log.debug("Request to get all Customers");
        return customerRepository.findAll(pageable);
    }


    /**
     * Get one customer by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findOne(Long id) {
        log.debug("Request to get Customer : {}", id);
        return customerRepository.findById(id);
    }

    /**
     * Delete the customer by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Customer : {}", id);
        Customer customer = customerRepository.findById(id).get();
        Servicelist listing = servicelistRepository.findServicelistByCustomer(customer);
        User user = customer.getUser();

        customerRepository.delete(customer);
        customerSearchRepository.delete(customer);

        servicelistRepository.delete(listing);
        servicelistSearchRepository.delete(listing);

        userRepository.delete(user);
        userSearchRepository.delete(user);
        userService.clearUserCaches(user);
    }

    @Override
    public Customer findbyUserId(Long id){
        return customerRepository.findCustomerByUserId(id);
    }

    /**
     * Search for the customer corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Customer> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Customers for query {}", query);
        return customerSearchRepository.search(queryStringQuery(query), pageable);    }
}
