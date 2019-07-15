package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.service.CompanyRequestService;
import io.github.softech.dev.sgill.domain.CompanyRequest;
import io.github.softech.dev.sgill.repository.CompanyRequestRepository;
import io.github.softech.dev.sgill.repository.search.CompanyRequestSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing CompanyRequest.
 */
@Service
@Transactional
public class CompanyRequestServiceImpl implements CompanyRequestService {

    private final Logger log = LoggerFactory.getLogger(CompanyRequestServiceImpl.class);

    private final CompanyRequestRepository companyRequestRepository;

    private final CompanyRequestSearchRepository companyRequestSearchRepository;

    public CompanyRequestServiceImpl(CompanyRequestRepository companyRequestRepository, CompanyRequestSearchRepository companyRequestSearchRepository) {
        this.companyRequestRepository = companyRequestRepository;
        this.companyRequestSearchRepository = companyRequestSearchRepository;
    }

    /**
     * Save a companyRequest.
     *
     * @param companyRequest the entity to save
     * @return the persisted entity
     */
    @Override
    public CompanyRequest save(CompanyRequest companyRequest) {
        log.debug("Request to save CompanyRequest : {}", companyRequest);
        CompanyRequest result = companyRequestRepository.save(companyRequest);
        companyRequestSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the companyRequests.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyRequest> findAll(Pageable pageable) {
        log.debug("Request to get all CompanyRequests");
        return companyRequestRepository.findAll(pageable);
    }


    /**
     * Get one companyRequest by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyRequest> findOne(Long id) {
        log.debug("Request to get CompanyRequest : {}", id);
        return companyRequestRepository.findById(id);
    }

    /**
     * Delete the companyRequest by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete CompanyRequest : {}", id);
        companyRequestRepository.deleteById(id);
        companyRequestSearchRepository.deleteById(id);
    }

    /**
     * Search for the companyRequest corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyRequest> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of CompanyRequests for query {}", query);
        return companyRequestSearchRepository.search(queryStringQuery(query), pageable);    }
}
