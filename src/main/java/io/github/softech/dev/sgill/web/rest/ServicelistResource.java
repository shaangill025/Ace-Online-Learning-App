package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.Servicelist;
import io.github.softech.dev.sgill.service.ServicelistService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.ServicelistCriteria;
import io.github.softech.dev.sgill.service.ServicelistQueryService;
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
 * REST controller for managing Servicelist.
 */
@RestController
@RequestMapping("/api")
public class ServicelistResource {

    private final Logger log = LoggerFactory.getLogger(ServicelistResource.class);

    private static final String ENTITY_NAME = "servicelist";

    private final ServicelistService servicelistService;

    private final ServicelistQueryService servicelistQueryService;

    public ServicelistResource(ServicelistService servicelistService, ServicelistQueryService servicelistQueryService) {
        this.servicelistService = servicelistService;
        this.servicelistQueryService = servicelistQueryService;
    }

    /**
     * POST  /servicelists : Create a new servicelist.
     *
     * @param servicelist the servicelist to create
     * @return the ResponseEntity with status 201 (Created) and with body the new servicelist, or with status 400 (Bad Request) if the servicelist has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/servicelists")
    @Timed
    public ResponseEntity<Servicelist> createServicelist(@Valid @RequestBody Servicelist servicelist) throws URISyntaxException {
        log.debug("REST request to save Servicelist : {}", servicelist);
        if (servicelist.getId() != null) {
            throw new BadRequestAlertException("A new servicelist cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Servicelist result = servicelistService.save(servicelist);
        return ResponseEntity.created(new URI("/api/servicelists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /servicelists : Updates an existing servicelist.
     *
     * @param servicelist the servicelist to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated servicelist,
     * or with status 400 (Bad Request) if the servicelist is not valid,
     * or with status 500 (Internal Server Error) if the servicelist couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/servicelists")
    @Timed
    public ResponseEntity<Servicelist> updateServicelist(@Valid @RequestBody Servicelist servicelist) throws URISyntaxException {
        log.debug("REST request to update Servicelist : {}", servicelist);
        if (servicelist.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Servicelist result = servicelistService.save(servicelist);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, servicelist.getId().toString()))
            .body(result);
    }

    /**
     * GET  /servicelists : get all the servicelists.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of servicelists in body
     */
    @GetMapping("/servicelists")
    @Timed
    public ResponseEntity<List<Servicelist>> getAllServicelists(ServicelistCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Servicelists by criteria: {}", criteria);
        Page<Servicelist> page = servicelistQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/servicelists");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /servicelists/:id : get the "id" servicelist.
     *
     * @param id the id of the servicelist to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the servicelist, or with status 404 (Not Found)
     */
    @GetMapping("/servicelists/{id}")
    @Timed
    public ResponseEntity<Servicelist> getServicelist(@PathVariable Long id) {
        log.debug("REST request to get Servicelist : {}", id);
        Optional<Servicelist> servicelist = servicelistService.findOne(id);
        return ResponseUtil.wrapOrNotFound(servicelist);
    }

    /**
     * DELETE  /servicelists/:id : delete the "id" servicelist.
     *
     * @param id the id of the servicelist to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/servicelists/{id}")
    @Timed
    public ResponseEntity<Void> deleteServicelist(@PathVariable Long id) {
        log.debug("REST request to delete Servicelist : {}", id);
        servicelistService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/servicelists?query=:query : search for the servicelist corresponding
     * to the query.
     *
     * @param query the query of the servicelist search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/servicelists")
    @Timed
    public ResponseEntity<List<Servicelist>> searchServicelists(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Servicelists for query {}", query);
        Page<Servicelist> page = servicelistService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/servicelists");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
