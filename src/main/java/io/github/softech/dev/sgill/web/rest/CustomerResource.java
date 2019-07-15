package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import io.github.softech.dev.sgill.domain.*;
import io.github.softech.dev.sgill.repository.*;
import io.github.softech.dev.sgill.service.*;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.CustomerCriteria;
import io.github.softech.dev.sgill.service.CustomerQueryService;
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

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Customer.
 */
@RestController
@RequestMapping("/api")
public class CustomerResource {

    private final Logger log = LoggerFactory.getLogger(CustomerResource.class);

    private static final String ENTITY_NAME = "customer";

    private final CustomerService customerService;

    private final CustomerQueryService customerQueryService;

    private final CustomerRepository customerRepository;

    private final CompanyService companyService;

    private final CourseHistoryService courseHistoryService;

    private final SectionHistoryService sectionHistoryService;

    private final TimeCourseLogService timeCourseLogService;

    private final UserRepository userRepository;

    private final CartService cartService;

    private final UserService userService;

    private final CourseHistoryRepository courseHistoryRepository;

    private final CourseService courseService;

    public CustomerResource(CustomerService customerService, CustomerQueryService customerQueryService, CustomerRepository customerRepository,
                            CompanyService companyService, CourseHistoryService courseHistoryService, TimeCourseLogService timeCourseLogService, CartService cartService, UserRepository userRepository,
                            SectionHistoryService sectionHistoryService, UserService userService,
                            CourseHistoryRepository courseHistoryRepository, CourseService courseService) {
        this.customerService = customerService;
        this.customerQueryService = customerQueryService;
        this.customerRepository = customerRepository;
        this.companyService = companyService;
        this.courseHistoryService = courseHistoryService;
        this.timeCourseLogService = timeCourseLogService;
        this.cartService = cartService;
        this.sectionHistoryService = sectionHistoryService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.courseHistoryRepository = courseHistoryRepository;
        this.courseService = courseService;
    }

    /**
     * POST  /customers : Create a new customer.
     *
     * @param customer the customer to create
     * @return the ResponseEntity with status 201 (Created) and with body the new customer, or with status 400 (Bad Request) if the customer has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/customers")
    @Timed
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) throws URISyntaxException {
        log.debug("REST request to save Customer : {}", customer);
        if (customer.getId() != null) {
            throw new BadRequestAlertException("A new customer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Customer result = customerService.save(customer);
        return ResponseEntity.created(new URI("/api/customers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /customers : Updates an existing customer.
     *
     * @param customer the customer to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated customer,
     * or with status 400 (Bad Request) if the customer is not valid,
     * or with status 500 (Internal Server Error) if the customer couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/customers")
    @Timed
    public ResponseEntity<Customer> updateCustomer(@Valid @RequestBody Customer customer) throws URISyntaxException {
        log.debug("REST request to update Customer : {}", customer);
        if (customer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Customer result = customerService.save(customer);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, customer.getId().toString()))
            .body(result);
    }

    /**
     * GET  /customers : get all the customers.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of customers in body
     */
    @GetMapping("/customers")
    @Timed
    public ResponseEntity<List<Customer>> getAllCustomers(CustomerCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Customers by criteria: {}", criteria);
        Page<Customer> page = customerQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/customers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
    * GET  /customers/count : count all the customers.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/customers/count")
    @Timed
    public ResponseEntity<Long> countCustomers(CustomerCriteria criteria) {
        log.debug("REST request to count Customers by criteria: {}", criteria);
        return ResponseEntity.ok().body(customerQueryService.countByCriteria(criteria));
    }

    @GetMapping("/check/account/customer")
    @Timed
    public Long getConstraintCustomer(@RequestParam(value = "hidden") String hidden) {
        log.debug("REST request to verify hidden parameter for Customer Registration process: {}", hidden);
        List<Customer> customer = customerRepository.findCustomersByHidden(hidden).orElse(null);
        if (customer == null) {
            return 0L;
        } else {
            return (long) customer.size();
        }
    }

    /**
     * GET  /customers/:id : get the "id" customer.
     *
     * @param id the id of the customer to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the customer, or with status 404 (Not Found)
     */
    @GetMapping("/customers/{id}")
    @Timed
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        log.debug("REST request to get Customer : {}", id);
        Optional<Customer> customer = customerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customer);
    }

    @GetMapping("/user/customers/{userId}")
    @Timed
    public Customer getUserCustomer(@PathVariable Long userId) {
        log.debug("REST request to get Customer by UserID: {}", userId);
        return customerRepository.findCustomerByUserId(userId);
    }

    @GetMapping("/area/customers/{area}")
    @Timed
    public List<Customer> getAreaCustomers(@PathVariable String area) {
        log.debug("REST request to get Customer by area: {}", area);
        return customerRepository.findCustomersByAreaserviced(area);
    }

    @GetMapping("/city/customers/{userId}")
    @Timed
    public List<Customer> getCityCustomers(@PathVariable String city) {
        log.debug("REST request to get Customer by city: {}", city);
        return customerRepository.findCustomersByCity(city);
    }

    @GetMapping("/company/customers/{userId}")
    @Timed
    public List<Customer> getCompanyCustomers(@PathVariable Long companyId) {
        log.debug("REST request to get Customer by companyID: {}", companyId);
        Company reqdCompany = companyService.findOne(companyId).get();
        return customerRepository.findCustomersByCompany(reqdCompany);
    }

    @PutMapping("/merge/customers/old/{oldId}/new/{newId}")
    @Timed
    public String mergeCustomer(@PathVariable Long oldId, @PathVariable Long newId) {
        log.debug("REST request to merge Customer records : old ID is " + oldId + ", and new ID is " + newId);
        Customer oldCustomer, newCustomer;
        oldCustomer = customerService.findOne(oldId).orElse(null);
        newCustomer = customerService.findOne(newId).orElse(null);
        if (oldCustomer == null || newCustomer == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        List<SectionHistory> sectionHistoryList = sectionHistoryService.getSectionHistoriesByCustomer(oldCustomer).orElse(null);
        List<TimeCourseLog> timeCourseLogList = timeCourseLogService.getTimeCourseLogsByCustomer(oldCustomer).orElse(null);
        List<Cart> cartList = cartService.getCartsByCustomer(oldCustomer).orElse(null);
        if(sectionHistoryList != null) {
            for (SectionHistory sectionHistory: sectionHistoryList){
                sectionHistory.setCustomer(newCustomer);
                sectionHistoryService.save(sectionHistory);
            }
        }
        if(timeCourseLogList != null) {
            for (TimeCourseLog timeCourseLog: timeCourseLogList){
                timeCourseLog.setCustomer(newCustomer);
                timeCourseLogService.save(timeCourseLog);
            }
        }
        if(cartList != null) {
            for (Cart cart: cartList){
                cart.setCustomer(newCustomer);
                cartService.save(cart);
            }
        }
        List<CourseHistory> courseHistoryList1 = courseHistoryService.getCourseHistoriesByCustomer(newCustomer).orElse(null);
        if (courseHistoryList1 != null) {
            HashMap<Long, Integer> frequency = new HashMap<>();
            for (CourseHistory temp : courseHistoryList1) {
                if (temp.isIsactive() && temp.isAccess()) {
                    if (!frequency.containsKey(temp.getCourse().getId())) {
                        frequency.put(temp.getCourse().getId(), 1);
                    } else {
                        Integer tempCnt = frequency.get(temp.getCourse().getId());
                        frequency.put(temp.getCourse().getId(), tempCnt++);
                    }
                }
            }
            List<Long> courseIdList = new ArrayList<>();
            for (HashMap.Entry<Long, Integer> tempEntry: frequency.entrySet()) {
                if (tempEntry.getValue() > 1) {
                    courseIdList.add(tempEntry.getKey());
                }
            }
            for (Long tempKey: courseIdList) {
                Course courseTemp = courseService.findOne(tempKey).get();
                List<CourseHistory> courseHistoryListLoop = courseHistoryRepository.findCourseHistoriesByCustomerAndCourseAndAccessAndIsactiveOrderByIdDesc(newCustomer, courseTemp, true, true).get();
                for (int h=0;h<courseHistoryListLoop.size();h++) {
                    if (h!=0) {
                        courseHistoryListLoop.get(h).setIsactive(false);
                        courseHistoryListLoop.get(h).setAccess(false);
                        courseHistoryListLoop.get(h).setLastactivedate(Instant.now());
                        courseHistoryService.save(courseHistoryListLoop.get(h));
                    }
                }
            }
        }
        // MergeFunction mergeFunction = mergeFunctionRepository.findMergeFunctionByTobeRemovedAndReplacement(oldCustomer, newCustomer).get();
        // mergeFunctionRepository.delete(mergeFunction);
        User deletedUser = oldCustomer.getUser();
        deletedUser.setActivated(false);
        userRepository.save(deletedUser);

        return "Customers records have been merged successfully";
    }

    /**
     * DELETE  /customers/:id : delete the "id" customer.
     *
     * @param id the id of the customer to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/customers/{id}")
    @Timed
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.debug("REST request to delete Customer : {}", id);
        customerService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/customers?query=:query : search for the customer corresponding
     * to the query.
     *
     * @param query the query of the customer search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/customers")
    @Timed
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Customers for query {}", query);
        Page<Customer> page = customerService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/customers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
