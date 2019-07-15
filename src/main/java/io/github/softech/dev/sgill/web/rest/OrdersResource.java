package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.*;
import io.github.softech.dev.sgill.domain.enumeration.NOTIFICATIONS;
import io.github.softech.dev.sgill.repository.*;
import io.github.softech.dev.sgill.service.*;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.OrdersCriteria;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Orders.
 */
@RestController
@RequestMapping("/api")
public class OrdersResource {

    private final Logger log = LoggerFactory.getLogger(OrdersResource.class);

    private static final String ENTITY_NAME = "orders";

    private final OrdersService ordersService;

    private final OrdersQueryService ordersQueryService;

    private final CourseHistoryRepository courseHistoryRepository;

    private final CourseHistoryService courseHistoryService;

    private final CourseCartBridgeRepository courseCartBridgeRepository;

    private final CourseRepository courseRepository;

    private final CustomerRepository customerRepository;

    private final CustomerService customerService;

    private final CartService cartService;

    private final OrdersRepository ordersRepository;

    public OrdersResource(OrdersService ordersService, OrdersQueryService ordersQueryService, CourseHistoryRepository courseHistoryRepository,
                          CourseCartBridgeRepository courseCartBridgeRepository, CourseHistoryService courseHistoryService, CourseRepository courseRepository,
                          CustomerRepository customerRepository, CustomerService customerService, CartService cartService, OrdersRepository ordersRepository) {
        this.ordersService = ordersService;
        this.ordersQueryService = ordersQueryService;
        this.courseHistoryRepository = courseHistoryRepository;
        this.courseCartBridgeRepository = courseCartBridgeRepository;
        this.courseHistoryService = courseHistoryService;
        this.customerRepository = customerRepository;
        this.courseRepository = courseRepository;
        this.customerService = customerService;
        this.cartService = cartService;
        this.ordersRepository = ordersRepository;
    }

    /**
     * POST  /orders : Create a new orders.
     *
     * @param orders the orders to create
     * @return the ResponseEntity with status 201 (Created) and with body the new orders, or with status 400 (Bad Request) if the orders has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/orders")
    @Timed
    public ResponseEntity<Orders> createOrders(@RequestBody Orders orders) throws URISyntaxException {
        log.debug("REST request to save Orders : {}", orders);
        if (orders.getId() != null) {
            throw new BadRequestAlertException("A new orders cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Orders result = ordersService.save(orders);
        return ResponseEntity.created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /orders : Updates an existing orders.
     *
     * @param orders the orders to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated orders,
     * or with status 400 (Bad Request) if the orders is not valid,
     * or with status 500 (Internal Server Error) if the orders couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/orders")
    @Timed
    public ResponseEntity<Orders> updateOrders(@RequestBody Orders orders) throws URISyntaxException {
        log.debug("REST request to update Orders : {}", orders);
        List<CourseCartBridge> listTemp = courseCartBridgeRepository.findCourseCartBridgesByCartId(orders.getCart().getId());
        if (orders.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (orders.getStatus() == NOTIFICATIONS.COMPLETE) {
            Customer tempCustomer = orders.getCart().getCustomer();
            tempCustomer.setPoints(orders.getCart().getPoints());
            customerService.save(tempCustomer);
            for (CourseCartBridge aListTemp : listTemp) {
                CourseHistory temp = new CourseHistory();
                temp.setAccess(false);
                temp.setCourse(aListTemp.getCourse());
                temp.setCustomer(orders.getCart().getCustomer());
                temp.setIsactive(true);
                temp.setIscompleted(false);
                temp.setStartdate(Instant.now());
                temp.setLastactivedate(Instant.now());
                courseHistoryRepository.save(temp);
                log.debug("REST request to create Course History Instance : {}", temp);
            }
        }
        Orders result = ordersService.save(orders);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, orders.getId().toString()))
            .body(result);
    }

    /**
     * GET  /orders : get all the orders.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of orders in body
     */
    @GetMapping("/orders")
    @Timed
    public ResponseEntity<List<Orders>> getAllOrders(OrdersCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Orders by criteria: {}", criteria);
        Page<Orders> page = ordersQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/orders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /orders/:id : get the "id" orders.
     *
     * @param id the id of the orders to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the orders, or with status 404 (Not Found)
     */
    @GetMapping("/orders/{id}")
    @Timed
    public ResponseEntity<Orders> getOrders(@PathVariable Long id) {
        log.debug("REST request to get Orders : {}", id);
        Optional<Orders> orders = ordersService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orders);
    }

    @GetMapping("/cart/orders/{cartid}")
    @Timed
    public List<Orders> getCartOrders(@PathVariable Long cartid) {
        log.debug("REST request to get Orders by Cart ID : {}", cartid);
        Cart tempCart = cartService.findOne(cartid).get();
        return ordersRepository.getOrdersByCart(tempCart);
    }

    @GetMapping("/cart/order/optional/{cartid}")
    @Timed
    public ResponseEntity<Orders> getCartOptionalOrders(@PathVariable Long cartid) {
        log.debug("REST request to get Orders by Cart ID : {}", cartid);
        //Cart tempCart = cartService.findOne(cartid).get();
        return ResponseUtil.wrapOrNotFound(ordersRepository.getOrdersByCartId(cartid));
    }

    @GetMapping("/cart/order/{cartid}")
    @Timed
    public Orders getSingleCartOrders(@PathVariable Long cartid) {
        log.debug("REST request to get Orders by Cart ID : {}", cartid);
        return ordersRepository.getOrdersByCartId(cartid).orElse(null);
    }

    /**
     * DELETE  /orders/:id : delete the "id" orders.
     *
     * @param id the id of the orders to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/orders/{id}")
    @Timed
    public ResponseEntity<Void> deleteOrders(@PathVariable Long id) {
        log.debug("REST request to delete Orders : {}", id);
        ordersService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/orders?query=:query : search for the orders corresponding
     * to the query.
     *
     * @param query the query of the orders search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/orders")
    @Timed
    public ResponseEntity<List<Orders>> searchOrders(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Orders for query {}", query);
        Page<Orders> page = ordersService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/orders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
