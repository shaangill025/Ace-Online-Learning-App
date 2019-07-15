package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.CompanyRequest;
import io.github.softech.dev.sgill.service.CompanyRequestService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.CompanyRequestCriteria;
import io.github.softech.dev.sgill.service.CompanyRequestQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing CompanyRequest.
 */
@RestController
@RequestMapping("/api")
public class CompanyRequestResource {

    private final Logger log = LoggerFactory.getLogger(CompanyRequestResource.class);

    private static final String ENTITY_NAME = "companyRequest";

    private final CompanyRequestService companyRequestService;

    private final CompanyRequestQueryService companyRequestQueryService;

    public CompanyRequestResource(CompanyRequestService companyRequestService, CompanyRequestQueryService companyRequestQueryService) {
        this.companyRequestService = companyRequestService;
        this.companyRequestQueryService = companyRequestQueryService;
    }

    /**
     * POST  /company-requests : Create a new companyRequest.
     *
     * @param companyRequest the companyRequest to create
     * @return the ResponseEntity with status 201 (Created) and with body the new companyRequest, or with status 400 (Bad Request) if the companyRequest has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/company-requests")
    @Timed
    public ResponseEntity<CompanyRequest> createCompanyRequest(@Valid @RequestBody CompanyRequest companyRequest) throws URISyntaxException {
        log.debug("REST request to save CompanyRequest : {}", companyRequest);
        if (companyRequest.getId() != null) {
            throw new BadRequestAlertException("A new companyRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CompanyRequest result = companyRequestService.save(companyRequest);
        return ResponseEntity.created(new URI("/api/company-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /company-requests : Updates an existing companyRequest.
     *
     * @param companyRequest the companyRequest to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated companyRequest,
     * or with status 400 (Bad Request) if the companyRequest is not valid,
     * or with status 500 (Internal Server Error) if the companyRequest couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/company-requests")
    @Timed
    public ResponseEntity<CompanyRequest> updateCompanyRequest(@Valid @RequestBody CompanyRequest companyRequest) throws URISyntaxException {
        log.debug("REST request to update CompanyRequest : {}", companyRequest);
        if (companyRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CompanyRequest result = companyRequestService.save(companyRequest);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, companyRequest.getId().toString()))
            .body(result);
    }

    /**
     * GET  /company-requests : get all the companyRequests.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of companyRequests in body
     */
    @GetMapping("/company-requests")
    @Timed
    public ResponseEntity<List<CompanyRequest>> getAllCompanyRequests(CompanyRequestCriteria criteria, Pageable pageable) {
        log.debug("REST request to get CompanyRequests by criteria: {}", criteria);
        Page<CompanyRequest> page = companyRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/company-requests");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /company-requests/count : count all the companyRequests.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/company-requests/count")
    @Timed
    public ResponseEntity<Long> countCompanyRequests(CompanyRequestCriteria criteria) {
        log.debug("REST request to count CompanyRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(companyRequestQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /company-requests/:id : get the "id" companyRequest.
     *
     * @param id the id of the companyRequest to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the companyRequest, or with status 404 (Not Found)
     */
    @GetMapping("/company-requests/{id}")
    @Timed
    public ResponseEntity<CompanyRequest> getCompanyRequest(@PathVariable Long id) {
        log.debug("REST request to get CompanyRequest : {}", id);
        Optional<CompanyRequest> companyRequest = companyRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(companyRequest);
    }

    /**
     * DELETE  /company-requests/:id : delete the "id" companyRequest.
     *
     * @param id the id of the companyRequest to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/company-requests/{id}")
    @Timed
    public ResponseEntity<Void> deleteCompanyRequest(@PathVariable Long id) {
        log.debug("REST request to delete CompanyRequest : {}", id);
        companyRequestService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/company-requests?query=:query : search for the companyRequest corresponding
     * to the query.
     *
     * @param query the query of the companyRequest search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/company-requests")
    @Timed
    public ResponseEntity<List<CompanyRequest>> searchCompanyRequests(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of CompanyRequests for query {}", query);
        Page<CompanyRequest> page = companyRequestService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/company-requests");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
