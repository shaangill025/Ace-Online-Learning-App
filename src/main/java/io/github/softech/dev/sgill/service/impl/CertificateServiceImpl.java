package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.service.CertificateService;
import io.github.softech.dev.sgill.domain.Certificate;
import io.github.softech.dev.sgill.repository.CertificateRepository;
import io.github.softech.dev.sgill.repository.search.CertificateSearchRepository;
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
 * Service Implementation for managing Certificate.
 */
@Service
@Transactional
public class CertificateServiceImpl implements CertificateService {

    private final Logger log = LoggerFactory.getLogger(CertificateServiceImpl.class);

    private final CertificateRepository certificateRepository;

    private final CertificateSearchRepository certificateSearchRepository;

    public CertificateServiceImpl(CertificateRepository certificateRepository, CertificateSearchRepository certificateSearchRepository) {
        this.certificateRepository = certificateRepository;
        this.certificateSearchRepository = certificateSearchRepository;
    }

    /**
     * Save a certificate.
     *
     * @param certificate the entity to save
     * @return the persisted entity
     */
    @Override
    public Certificate save(Certificate certificate) {
        log.debug("Request to save Certificate : {}", certificate);        Certificate result = certificateRepository.save(certificate);
        certificateSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the certificates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Certificate> findAll(Pageable pageable) {
        log.debug("Request to get all Certificates");
        return certificateRepository.findAll(pageable);
    }


    /**
     * Get one certificate by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Certificate> findOne(Long id) {
        log.debug("Request to get Certificate : {}", id);
        return certificateRepository.findById(id);
    }

    /**
     * Delete the certificate by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Certificate : {}", id);
        certificateRepository.deleteById(id);
        certificateSearchRepository.deleteById(id);
    }

    /**
     * Search for the certificate corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Certificate> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Certificates for query {}", query);
        return certificateSearchRepository.search(queryStringQuery(query), pageable);    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<Certificate>> getCertificatesByCustomer(Customer customer) {
        log.debug("Request to get Certificates for Customer {}", customer);
        return certificateRepository.findCertificatesByCustomer(customer);    }
}
