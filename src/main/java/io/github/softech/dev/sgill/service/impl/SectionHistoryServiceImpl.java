package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.service.SectionHistoryService;
import io.github.softech.dev.sgill.domain.SectionHistory;
import io.github.softech.dev.sgill.repository.SectionHistoryRepository;
import io.github.softech.dev.sgill.repository.search.SectionHistorySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing SectionHistory.
 */
@Service
@Transactional
public class SectionHistoryServiceImpl implements SectionHistoryService {

    private final Logger log = LoggerFactory.getLogger(SectionHistoryServiceImpl.class);

    private final SectionHistoryRepository sectionHistoryRepository;

    private final SectionHistorySearchRepository sectionHistorySearchRepository;

    public SectionHistoryServiceImpl(SectionHistoryRepository sectionHistoryRepository, SectionHistorySearchRepository sectionHistorySearchRepository) {
        this.sectionHistoryRepository = sectionHistoryRepository;
        this.sectionHistorySearchRepository = sectionHistorySearchRepository;
    }

    /**
     * Save a sectionHistory.
     *
     * @param sectionHistory the entity to save
     * @return the persisted entity
     */
    @Override
    public SectionHistory save(SectionHistory sectionHistory) {
        log.debug("Request to save SectionHistory : {}", sectionHistory);        SectionHistory result = sectionHistoryRepository.save(sectionHistory);
        sectionHistorySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the sectionHistories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SectionHistory> findAll(Pageable pageable) {
        log.debug("Request to get all SectionHistories");
        return sectionHistoryRepository.findAll(pageable);
    }


    /**
     * Get one sectionHistory by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SectionHistory> findOne(Long id) {
        log.debug("Request to get SectionHistory : {}", id);
        return sectionHistoryRepository.findById(id);
    }

    /**
     * Delete the sectionHistory by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete SectionHistory : {}", id);
        sectionHistoryRepository.deleteById(id);
        sectionHistorySearchRepository.deleteById(id);
    }

    /**
     * Search for the sectionHistory corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SectionHistory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SectionHistories for query {}", query);
        return sectionHistorySearchRepository.search(queryStringQuery(query), pageable);    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<SectionHistory>> getSectionHistoriesByCustomer(Customer customer) {
        log.debug("Request to get SectionHistories for customer {}", customer);
        return sectionHistoryRepository.findSectionHistoriesByCustomer(customer);    }
}
