package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.service.ServicelistService;
import io.github.softech.dev.sgill.domain.Servicelist;
import io.github.softech.dev.sgill.repository.ServicelistRepository;
import io.github.softech.dev.sgill.repository.search.ServicelistSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Servicelist.
 */
@Service
@Transactional
public class ServicelistServiceImpl implements ServicelistService {

    private final Logger log = LoggerFactory.getLogger(ServicelistServiceImpl.class);

    private final ServicelistRepository servicelistRepository;

    private final ServicelistSearchRepository servicelistSearchRepository;

    public ServicelistServiceImpl(ServicelistRepository servicelistRepository, ServicelistSearchRepository servicelistSearchRepository) {
        this.servicelistRepository = servicelistRepository;
        this.servicelistSearchRepository = servicelistSearchRepository;
    }

    /**
     * Save a servicelist.
     *
     * @param servicelist the entity to save
     * @return the persisted entity
     */
    @Override
    public Servicelist save(Servicelist servicelist) {
        log.debug("Request to save Servicelist : {}", servicelist);        Servicelist result = servicelistRepository.save(servicelist);
        servicelistSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the servicelists.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Servicelist> findAll(Pageable pageable) {
        log.debug("Request to get all Servicelists");
        return servicelistRepository.findAll(pageable);
    }


    /**
     * Get one servicelist by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Servicelist> findOne(Long id) {
        log.debug("Request to get Servicelist : {}", id);
        return servicelistRepository.findById(id);
    }

    /**
     * Delete the servicelist by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Servicelist : {}", id);
        servicelistRepository.deleteById(id);
        servicelistSearchRepository.deleteById(id);
    }

    /**
     * Search for the servicelist corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Servicelist> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Servicelists for query {}", query);
        return servicelistSearchRepository.search(queryStringQuery(query), pageable);    }
}
