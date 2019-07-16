package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sendgrid.*;
import io.github.softech.dev.sgill.domain.*;
import io.github.softech.dev.sgill.repository.CertificateRepository;
import io.github.softech.dev.sgill.service.*;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.CertificateCriteria;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Certificate.
 */
@RestController
@RequestMapping("/api")
public class CertificateResource {

    private final Logger log = LoggerFactory.getLogger(CertificateResource.class);

    private static final String ENTITY_NAME = "certificate";

    private final CertificateService certificateService;

    private final CertificateQueryService certificateQueryService;

    private final CustomerService customerService;

    private final CourseService courseService;

    private final CourseHistoryService courseHistoryService;

    private final CertificateRepository certificateRepository;

    private final MailService mailService;

    public CertificateResource(CertificateService certificateService, CertificateQueryService certificateQueryService, CustomerService customerService, CertificateRepository certificateRepository,
                               CourseService courseService, MailService mailService, CourseHistoryService courseHistoryService) {
        this.certificateService = certificateService;
        this.certificateQueryService = certificateQueryService;
        this.customerService = customerService;
        this.certificateRepository = certificateRepository;
        this.courseService = courseService;
        this.mailService = mailService;
        this.courseHistoryService = courseHistoryService;
    }

    /**
     * POST  /certificates : Create a new certificate.
     *
     * @param certificate the certificate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new certificate, or with status 400 (Bad Request) if the certificate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/certificates")
    @Timed
    public ResponseEntity<Certificate> createCertificate(@RequestBody Certificate certificate) throws URISyntaxException {
        log.debug("REST request to save Certificate : {}", certificate);
        if (certificate.getId() != null) {
            throw new BadRequestAlertException("A new certificate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Certificate temp = certificate;
        certificate.setTimestamp(Instant.now());
        Certificate result = certificateService.save(certificate);
        certificate.setIsEmailed(true);
        Long idNum = result.getId();
        //log.debug("REST request to email Certificate : {}", idNum);
        //mailService.sendEmail(result.getCustomer().getUser().getEmail(), "Your Certificate for " + result.getCourses().getNormCourses(), "Click http://localhost:9000/certificates/" + idNum, true, true);


        /**
         *
        Email from = new Email(result.getCustomer().getUser().getEmail());
        String subject = "Your Certificate for " + result.getCourses().getNormCourses();
        Email to = new Email(result.getCustomer().getUser().getEmail());
        Content content = new Content("text/plain", "Click http://localhost:9000/certificates/" + idNum);
        Mail mail = new Mail(from, subject, to, content);
        //mail.addAttachments(attachment);

        SendGrid sg = new SendGrid("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");
        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return ResponseEntity.created(new URI("/api/certificates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /certificates : Updates an existing certificate.
     *
     * @param certificate the certificate to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated certificate,
     * or with status 400 (Bad Request) if the certificate is not valid,
     * or with status 500 (Internal Server Error) if the certificate couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/certificates")
    @Timed
    public ResponseEntity<Certificate> updateCertificate(@RequestBody Certificate certificate) throws URISyntaxException {
        log.debug("REST request to update Certificate : {}", certificate);
        if (certificate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Certificate result = certificateService.save(certificate);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, certificate.getId().toString()))
            .body(result);
    }

    /**
     * GET  /certificates : get all the certificates.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of certificates in body
     */
    @GetMapping("/certificates")
    @Timed
    public ResponseEntity<List<Certificate>> getAllCertificates(CertificateCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Certificates by criteria: {}", criteria);
        Page<Certificate> page = certificateQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/certificates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}/certificates/{coursehistoryId}")
    @Timed
    public Certificate getCustomerCourseCertificate(@PathVariable Long customerId, @PathVariable Long coursehistoryId) throws URISyntaxException {
        Customer reqdCustomer = customerService.findOne(customerId).get();
        CourseHistory reqdCourseHist = courseHistoryService.findOne(coursehistoryId).get();
        List<Certificate> tempList = certificateRepository.findCertificatesByCustomerAndCourseHistoryOrderByIdDesc(reqdCustomer, reqdCourseHist).orElse(null);
        if (tempList == null) {
            Certificate temp = new Certificate();
            temp.setCourseHistory(reqdCourseHist);
            temp.setCustomer(reqdCustomer);
            temp.setId(-1L);
            temp.setIsEmailed(false);
            temp.setTimestamp(Instant.now());
            return temp;
        }
        return tempList.get(0);
    }

    @GetMapping("/count/certificates/{customerId}")
    @Timed
    public Long getCountCertificates(@PathVariable Long customerId) throws URISyntaxException {
        Customer reqdCustomer = customerService.findOne(customerId).get();
        log.debug("REST request to get all certificates for a customer : {}", reqdCustomer);
        List<Certificate> tempList = certificateRepository.getCertificatesByCustomer(reqdCustomer).orElse(null);
        if (tempList==null) return 0L;
        else return (long) tempList.size();
    }

    @GetMapping("/all/certificates/{customerId}")
    @Timed
    public ResponseEntity<List<Certificate>> getCustomerCertificates(@PathVariable Long customerId) throws URISyntaxException {
        Customer reqdCustomer = customerService.findOne(customerId).get();
        log.debug("REST request to get all certificates for a customer : {}", reqdCustomer);
        return ResponseUtil.wrapOrNotFound(certificateRepository.getCertificatesByCustomer(reqdCustomer));
    }

    @GetMapping("/{coursehistoryId}/certificates/{customerId}")
    @Timed
    public Certificate getByCourseCertificates(@PathVariable Long customerId, @PathVariable Long coursehistoryId) throws URISyntaxException {
        Customer reqdCustomer = customerService.findOne(customerId).get();
        CourseHistory reqdCourseHist = courseHistoryService.findOne(coursehistoryId).get();
        log.debug("REST request to get a certificate for a customer : {}", reqdCustomer);
        log.debug("REST request to get a certificate for a course : {}", reqdCourseHist);
        return certificateRepository.getCertificateByCourseHistoryAndCustomer(reqdCourseHist,reqdCustomer);
    }

    /**
     * GET  /certificates/:id : get the "id" certificate.
     *
     * @param id the id of the certificate to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the certificate, or with status 404 (Not Found)
     */
    @GetMapping("/certificates/{id}")
    @Timed
    public ResponseEntity<Certificate> getCertificate(@PathVariable Long id) {
        log.debug("REST request to get Certificate : {}", id);
        Optional<Certificate> certificate = certificateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(certificate);
    }

    /**
     * DELETE  /certificates/:id : delete the "id" certificate.
     *
     * @param id the id of the certificate to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/certificates/{id}")
    @Timed
    public ResponseEntity<Void> deleteCertificate(@PathVariable Long id) {
        log.debug("REST request to delete Certificate : {}", id);
        certificateService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/certificates?query=:query : search for the certificate corresponding
     * to the query.
     *
     * @param query the query of the certificate search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/certificates")
    @Timed
    public ResponseEntity<List<Certificate>> searchCertificates(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Certificates for query {}", query);
        Page<Certificate> page = certificateService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/certificates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
