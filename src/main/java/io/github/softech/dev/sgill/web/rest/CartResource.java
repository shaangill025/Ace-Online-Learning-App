package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hazelcast.com.eclipsesource.json.Json;
import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Order;
import com.stripe.model.Source;
import io.github.softech.dev.sgill.domain.*;
import io.github.softech.dev.sgill.domain.enumeration.NOTIFICATIONS;
import io.github.softech.dev.sgill.domain.enumeration.PAYMENT;
import io.github.softech.dev.sgill.repository.*;
import io.github.softech.dev.sgill.service.*;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.CartCriteria;
import io.github.jhipster.web.util.ResponseUtil;
import org.apache.lucene.document.DoubleRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Console;
import java.net.URI;
import java.net.URISyntaxException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Cart.
 */
@RestController
@RequestMapping("/api")
public class CartResource {

    private final Logger log = LoggerFactory.getLogger(CartResource.class);

    private static final String ENTITY_NAME = "cart";

    private final CartService cartService;

    private final CartQueryService cartQueryService;

    private final CartRepository cartRepository;

    private final CustomerRepository customerRepository;

    private final CustomerService customerService;

    private final CourseRepository courseRepository;

    private final OrdersRepository ordersRepository;

    private final CourseHistoryService courseHistoryService;

    private final CourseHistoryRepository courseHistoryRepository;

    private final OrdersService ordersService;

    private StripeClient stripeClient;

    private String stripeJson = "";

    private final CourseCartBridgeRepository courseCartBridgeRepository;

    public CartResource(CartService cartService, CartQueryService cartQueryService, CartRepository cartRepository,
                        CustomerRepository customerRepository, CustomerService customerService
                        , StripeClient stripeClient, CourseRepository courseRepository, OrdersRepository ordersRepository,
                        OrdersService ordersService, CourseHistoryRepository courseHistoryRepository, CourseHistoryService courseHistoryService,
                        CourseCartBridgeRepository courseCartBridgeRepository) {
        this.cartService = cartService;
        this.cartQueryService = cartQueryService;
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.customerService = customerService;
        this.courseRepository = courseRepository;
        this.stripeClient = stripeClient;
        this.ordersRepository = ordersRepository;
        this.ordersService = ordersService;
        this.courseHistoryRepository = courseHistoryRepository;
        this.courseHistoryService = courseHistoryService;
        this.courseCartBridgeRepository = courseCartBridgeRepository;
    }

    /**
     * POST  /carts : Create a new cart.
     *
     * @param cart the cart to create
     * @return the ResponseEntity with status 201 (Created) and with body the new cart, or with status 400 (Bad Request) if the cart has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/carts")
    @Timed
    public ResponseEntity<Cart> createCart(@RequestBody Cart cart) throws URISyntaxException {
        log.debug("REST request to save Cart : {}", cart);
        if (cart.getId() != null) {
            throw new BadRequestAlertException("A new cart cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (cartRepository.findCartsByCustomerIdAndCheckout(cart.getCustomer().getId(), false) != null) {
            throw new BadRequestAlertException("Cart requistion error, there is already an assigned cart resource for this customer", ENTITY_NAME, "idexists");
        }
        Cart result = cartService.save(cart);
        return ResponseEntity.created(new URI("/api/carts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PostMapping("/carts/order/change")
    @Timed
    public String chargeCard(HttpServletRequest request) throws Exception {
        Long cartId = Long.parseLong(request.getHeader("cartId"));
        Long orderId = Long.parseLong(request.getHeader("orderId"));
        Cart tempCart = this.cartService.findOne(cartId).orElse(null);
        Orders order = this.ordersService.findOne(orderId).orElse(null);
        order.setSeller_status("succeeded");
        order.setStatus(NOTIFICATIONS.COMPLETE);
        List<CourseCartBridge> tempCartCourses = this.courseCartBridgeRepository.findCourseCartBridgesByCartId(tempCart.getId());
        for (CourseCartBridge tempLoopVar : tempCartCourses) {
                CourseHistory tempHistory = courseHistoryRepository.findCourseHistoryByCourseIdAndCustomer_Id(tempLoopVar.getCourse().getId(), tempCart.getCustomer().getId()).orElse(null);
                if (tempHistory == null) {
                    tempHistory = new CourseHistory();
                    tempHistory.setAccess(true);
                    tempHistory.setCourse(tempLoopVar.getCourse());
                    tempHistory.setCustomer(tempCart.getCustomer());
                    tempHistory.setIscompleted(false);
                    tempHistory.setIsactive(true);
                    tempHistory.setStartdate(Instant.now());
                } else {
                    tempHistory.setAccess(true);
                    tempHistory.setIsactive(true);
                    tempHistory.setStartdate(Instant.now());
                }
                courseHistoryService.save(tempHistory);
        }
        ordersService.save(order);
        return "The payment status for this order has been updated";
    }

    @PostMapping("/carts/charge")
    @Timed
    public Orders chargeCard(HttpServletRequest request, @RequestBody Cart cart) throws Exception {
        String token = request.getHeader("token");
        double amount = Double.parseDouble(request.getHeader("amount"));
        String country = request.getHeader("currency");
        String dollar = "USD";
        if (country.equals("canada")) {
            dollar = "CAD";
        } else {
            dollar = "USD";
        }
        Long cartId = Long.parseLong(request.getHeader("cartId"));
        int redeem = Integer.parseInt(request.getHeader("redeem"));
        int points = Integer.parseInt(request.getHeader("buypoints"));
        log.debug("Rest Request - Token : {}", token);
        log.debug("REST request - Amount : {}", request.getHeader("amount"));
        log.debug("REST request - Amount (double) : {}", amount);
        Cart tempCart = this.cartService.findOne(cartId).orElse(null);
        Charge temp = this.stripeClient.chargeNewCard(token, ((amount - redeem)*100), dollar);
        //this.stripeJson = temp.toJson();
        /*GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        this.stripeJson = gson.toJson(temp);
        Charge pojo = gson.fromJson(this.stripeJson, Charge.class);*/
        log.debug("Stripe Status" + temp);
        Orders newOrder = new Orders();
        String returnJson = temp.toJson();
        JsonObject jsonObject = (JsonObject) Json.parse(returnJson);
        if((temp.getStatus().equals("succeeded"))&&temp.getPaid()) {
            Customer tempCustomer = customerService.findOne(tempCart.getCustomer().getId()).orElse(null);
            int tempPts = tempCustomer.getPoints();
            tempCustomer.setPoints(tempPts + points - redeem);
            this.customerService.save(tempCustomer);
            tempCart.setCheckout(true);
            tempCart.setAmount(amount);
            cartService.save(tempCart);
            //Source tempSource = Source.retrieve(temp.getSource().getId());
            newOrder.setAmount(tempCart.getAmount());
            newOrder.setCart(tempCart);
            newOrder.setCreateddate(Instant.now());
            newOrder.setGateway_amt(Long.toString(temp.getAmount()/100));
            newOrder.setGateway_id(temp.getId());
            newOrder.setPayment(PAYMENT.STRIPE);
            newOrder.setSeller_status("succeeded");
            newOrder.setSeller_message(temp.getOutcome().getSellerMessage());
            newOrder.setCard_type(temp.getOutcome().getType());
            newOrder.setNetwork_status(temp.getOutcome().getNetworkStatus());
            newOrder.setStatus(NOTIFICATIONS.COMPLETE);
            List<CourseCartBridge> tempCartCourses = this.courseCartBridgeRepository.findCourseCartBridgesByCartId(tempCart.getId());
            for (CourseCartBridge tempLoopVar : tempCartCourses) {
                CourseHistory tempHistory = courseHistoryRepository.findCourseHistoryByCourseIdAndCustomer_Id(tempLoopVar.getCourse().getId(), tempCart.getCustomer().getId()).orElse(null);
                if (tempHistory == null) {
                    tempHistory = new CourseHistory();
                    tempHistory.setAccess(true);
                    tempHistory.setCourse(tempLoopVar.getCourse());
                    tempHistory.setCustomer(tempCart.getCustomer());
                    tempHistory.setIscompleted(false);
                    tempHistory.setIsactive(true);
                    tempHistory.setStartdate(Instant.now());
                } else {
                    tempHistory.setAccess(true);
                    tempHistory.setIsactive(true);
                    tempHistory.setStartdate(Instant.now());
                }
                courseHistoryRepository.save(tempHistory);
            }
        } else if (temp.getStatus().equals("pending")) {
            //Source tempSource = Source.retrieve(temp.getSource().getId());
            newOrder.setAmount(tempCart.getAmount());
            newOrder.setCart(tempCart);
            newOrder.setCreateddate(Instant.now());
            newOrder.setGateway_amt(Long.toString(temp.getAmount()/100));
            newOrder.setGateway_id(temp.getId());
            newOrder.setPayment(PAYMENT.STRIPE);
            newOrder.setSeller_status("pending");
            newOrder.setStatus(NOTIFICATIONS.ORDERPROCESSING);
            newOrder.setSeller_message(temp.getOutcome().getSellerMessage());
            newOrder.setCard_type(temp.getOutcome().getType());
            newOrder.setNetwork_status(temp.getOutcome().getNetworkStatus());
        } else {
            //Source tempSource = Source.retrieve(temp.getSource().getId());
            newOrder.setAmount(tempCart.getAmount());
            newOrder.setCart(tempCart);
            newOrder.setCreateddate(Instant.now());
            newOrder.setGateway_amt(Long.toString(temp.getAmount()/100));
            newOrder.setGateway_id(temp.getId());
            newOrder.setPayment(PAYMENT.STRIPE);
            newOrder.setSeller_status("on-hold");
            newOrder.setStatus(NOTIFICATIONS.ONHOLD);
            newOrder.setSeller_message(temp.getOutcome().getSellerMessage());
            newOrder.setCard_type(temp.getOutcome().getType());
            newOrder.setNetwork_status(temp.getOutcome().getNetworkStatus());
        }
            //this.ordersService.save(newOrder);
        ordersService.save(newOrder);
            /*Customer tempCustomer = cart.getCustomer();
            int tempCustPts = tempCustomer.getPoints();
            tempCustomer.setPoints(tempCustPts + cart.getPoints() - redeem);
            this.customerService.save(tempCustomer);*/
        return /*(newOrder.getId().toString()) + */newOrder;
    }



    @GetMapping("/check/carts/{customerId}")
    @Timed
    public Cart checkCart(@PathVariable Long customerId) throws URISyntaxException {
        Customer reqdCustomer = customerService.findOne(customerId).orElse(null);
        log.debug("REST request to create a new customer specific Cart : {}", reqdCustomer);
        List<Cart> reqd = cartRepository.findCartsByCustomerIdAndCheckout(customerId, false).orElse(null);
        Instant moment = Instant.now();
        if(reqd == null) {
                Cart newCart = new Cart();
                newCart.setAmount((double) 0);
                newCart.setCheckout(false);
                newCart.setCustomer(reqdCustomer);
                newCart.setCreateddate(moment);
                newCart.setLastactivedate(moment);
                newCart.setNormCart("Cart for " + reqdCustomer.getNormalized());
                cartService.save(newCart);
            /*return ResponseEntity.created(new URI("/api/carts/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);*/
                return newCart;
        } else{
            log.debug("Customer Cart List Items: {}", reqd.get(0));
            /*return ResponseEntity.created(new URI("/api/carts/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);*/
            return reqd.get(0);
        }
    }

    @GetMapping("/all/carts/{customerId}")
    @Timed
    public ResponseEntity<List<Cart>> getCustomerCarts(@PathVariable Long customerId) throws URISyntaxException {
        //Customer reqdCustomer = customerService.findOne(customerId).orElse(null);
        //log.debug("REST request to get all carts for a customer : {}", reqdCustomer);
        //List<Cart> reqd = cartRepository.getCartsByCustomer(reqdCustomer);
        return ResponseUtil.wrapOrNotFound(cartRepository.findCartsByCustomerIdAndCheckout(customerId, true));
    }

    /**
     * PUT  /carts : Updates an existing cart.
     *
     * @param cart the cart to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated cart,
     * or with status 400 (Bad Request) if the cart is not valid,
     * or with status 500 (Internal Server Error) if the cart couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/carts")
    @Timed
    public ResponseEntity<Cart> updateCart(@RequestBody Cart cart) throws URISyntaxException {
        log.debug("REST request to update Cart : {}", cart);
        if (cart.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Cart result = cartService.save(cart);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, cart.getId().toString()))
            .body(result);
    }

    @PutMapping("/change/carts/{id}")
    @Timed
    public void subAmountCart(@PathVariable Long id, @RequestParam Long identifier) throws URISyntaxException {
        log.debug("REST request to update amount and points for the Cart by course ID: ", identifier);
        Cart cart = cartRepository.findById(id).orElse(null);
        Course course = courseRepository.findById(identifier).orElse(null);
        Double initAmount = cart.getAmount();
        int initPoints = cart.getPoints();
        if (initAmount - course.getAmount()>=0) {
            cart.setAmount(initAmount - course.getAmount());
        } else {
            cart.setAmount(0.0);
        }
        if (((int) (initPoints - course.getPoint()))>=0) {
            cart.setPoints((int) (initPoints - course.getPoint()));
        } else {
            cart.setPoints(0);
        }
        cartService.save(cart);
    }

    @GetMapping("/checkout/carts/{id}")
    @Timed
    public void checkoutCart(@PathVariable Long id) throws URISyntaxException {
        Cart cart = cartRepository.findById(id).orElse(null);
        cart.setCheckout(true);
        cartService.save(cart);
    }

    /**
     * GET  /carts : get all the carts.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of carts in body
     */
    @GetMapping("/carts")
    @Timed
    public ResponseEntity<List<Cart>> getAllCarts(CartCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Carts by criteria: {}", criteria);
        Page<Cart> page = cartQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/carts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /carts/:id : get the "id" cart.
     *
     * @param id the id of the cart to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the cart, or with status 404 (Not Found)
     */
    @GetMapping("/carts/{id}")
    @Timed
    public ResponseEntity<Cart> getCart(@PathVariable Long id) {
        log.debug("REST request to get Cart : {}", id);
        Optional<Cart> cart = cartService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cart);
    }

    /**
     * DELETE  /carts/:id : delete the "id" cart.
     *
     * @param id the id of the cart to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/carts/{id}")
    @Timed
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        log.debug("REST request to delete Cart : {}", id);
        cartService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/carts?query=:query : search for the cart corresponding
     * to the query.
     *
     * @param query the query of the cart search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/carts")
    @Timed
    public ResponseEntity<List<Cart>> searchCarts(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Carts for query {}", query);
        Page<Cart> page = cartService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/carts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
