package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.Certificate;

import io.github.softech.dev.sgill.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Certificate.
 */
public interface CertificateService {

    /**
     * Save a certificate.
     *
     * @param certificate the entity to save
     * @return the persisted entity
     */
    Certificate save(Certificate certificate);

    /**
     * Get all the certificates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Certificate> findAll(Pageable pageable);


    /**
     * Get the "id" certificate.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Certificate> findOne(Long id);

    /**
     * Delete the "id" certificate.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the certificate corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Certificate> search(String query, Pageable pageable);

    Optional<List<Certificate>> getCertificatesByCustomer(Customer customer);
}
