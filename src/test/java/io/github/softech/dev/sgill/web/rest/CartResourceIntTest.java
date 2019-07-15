package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.Cart;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.repository.*;
import io.github.softech.dev.sgill.repository.search.CartSearchRepository;
import io.github.softech.dev.sgill.service.*;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.CartCriteria;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;


import static io.github.softech.dev.sgill.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CartResource REST controller.
 *
 * @see CartResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class CartResourceIntTest {

    private static final String DEFAULT_NORM_CART = "AAAAAAAAAA";
    private static final String UPDATED_NORM_CART = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATEDDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATEDDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LASTACTIVEDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LASTACTIVEDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_AMOUNT = 1D;
    private static final Double UPDATED_AMOUNT = 2D;

    private static final Boolean DEFAULT_CHECKOUT = false;
    private static final Boolean UPDATED_CHECKOUT = true;

    private static final Integer DEFAULT_POINTS = 1;
    private static final Integer UPDATED_POINTS = 2;

    @Autowired
    private CartRepository cartRepository;

    

    @Autowired
    private CartService cartService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.CartSearchRepositoryMockConfiguration
     */
    @Autowired
    private CartSearchRepository mockCartSearchRepository;

    @Autowired
    private CartQueryService cartQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EntityManager em;

    private MockMvc restCartMockMvc;

    private Cart cart;

    private StripeClient stripeClient;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private CourseHistoryService courseHistoryService;

    @Autowired
    private CourseHistoryRepository courseHistoryRepository;

    @Autowired
    private CourseCartBridgeRepository courseCartBridgeRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CartResource cartResource = new CartResource(cartService, cartQueryService, cartRepository,
            customerRepository, customerService, stripeClient, courseRepository, ordersRepository, ordersService,
            courseHistoryRepository, courseHistoryService, courseCartBridgeRepository);
        this.restCartMockMvc = MockMvcBuilders.standaloneSetup(cartResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cart createEntity(EntityManager em) {
        Cart cart = new Cart()
            .normCart(DEFAULT_NORM_CART)
            .createddate(DEFAULT_CREATEDDATE)
            .lastactivedate(DEFAULT_LASTACTIVEDATE)
            .amount(DEFAULT_AMOUNT)
            .checkout(DEFAULT_CHECKOUT)
            .points(DEFAULT_POINTS);
        return cart;
    }

    @Before
    public void initTest() {
        cart = createEntity(em);
    }

    @Test
    @Transactional
    public void createCart() throws Exception {
        int databaseSizeBeforeCreate = cartRepository.findAll().size();

        // Create the Cart
        restCartMockMvc.perform(post("/api/carts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cart)))
            .andExpect(status().isCreated());

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll();
        assertThat(cartList).hasSize(databaseSizeBeforeCreate + 1);
        Cart testCart = cartList.get(cartList.size() - 1);
        assertThat(testCart.getNormCart()).isEqualTo(DEFAULT_NORM_CART);
        assertThat(testCart.getCreateddate()).isEqualTo(DEFAULT_CREATEDDATE);
        assertThat(testCart.getLastactivedate()).isEqualTo(DEFAULT_LASTACTIVEDATE);
        assertThat(testCart.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testCart.isCheckout()).isEqualTo(DEFAULT_CHECKOUT);
        assertThat(testCart.getPoints()).isEqualTo(DEFAULT_POINTS);

        // Validate the Cart in Elasticsearch
        verify(mockCartSearchRepository, times(1)).save(testCart);
    }

    @Test
    @Transactional
    public void createCartWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = cartRepository.findAll().size();

        // Create the Cart with an existing ID
        cart.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCartMockMvc.perform(post("/api/carts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cart)))
            .andExpect(status().isBadRequest());

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll();
        assertThat(cartList).hasSize(databaseSizeBeforeCreate);

        // Validate the Cart in Elasticsearch
        verify(mockCartSearchRepository, times(0)).save(cart);
    }

    @Test
    @Transactional
    public void getAllCarts() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList
        restCartMockMvc.perform(get("/api/carts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cart.getId())))
            .andExpect(jsonPath("$.[*].normCart").value(hasItem(DEFAULT_NORM_CART)))
            .andExpect(jsonPath("$.[*].createddate").value(hasItem(DEFAULT_CREATEDDATE.toString())))
            .andExpect(jsonPath("$.[*].lastactivedate").value(hasItem(DEFAULT_LASTACTIVEDATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].checkout").value(hasItem(DEFAULT_CHECKOUT.booleanValue())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS)));
    }
    

    @Test
    @Transactional
    public void getCart() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get the cart
        restCartMockMvc.perform(get("/api/carts/{id}", cart.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(cart.getId()))
            .andExpect(jsonPath("$.normCart").value(DEFAULT_NORM_CART))
            .andExpect(jsonPath("$.createddate").value(DEFAULT_CREATEDDATE.toString()))
            .andExpect(jsonPath("$.lastactivedate").value(DEFAULT_LASTACTIVEDATE.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.checkout").value(DEFAULT_CHECKOUT.booleanValue()))
            .andExpect(jsonPath("$.points").value(DEFAULT_POINTS));
    }

    @Test
    @Transactional
    public void getAllCartsByNormCartIsEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where normCart equals to DEFAULT_NORM_CART
        defaultCartShouldBeFound("normCart.equals=" + DEFAULT_NORM_CART);

        // Get all the cartList where normCart equals to UPDATED_NORM_CART
        defaultCartShouldNotBeFound("normCart.equals=" + UPDATED_NORM_CART);
    }

    @Test
    @Transactional
    public void getAllCartsByNormCartIsInShouldWork() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where normCart in DEFAULT_NORM_CART or UPDATED_NORM_CART
        defaultCartShouldBeFound("normCart.in=" + DEFAULT_NORM_CART + "," + UPDATED_NORM_CART);

        // Get all the cartList where normCart equals to UPDATED_NORM_CART
        defaultCartShouldNotBeFound("normCart.in=" + UPDATED_NORM_CART);
    }

    @Test
    @Transactional
    public void getAllCartsByNormCartIsNullOrNotNull() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where normCart is not null
        defaultCartShouldBeFound("normCart.specified=true");

        // Get all the cartList where normCart is null
        defaultCartShouldNotBeFound("normCart.specified=false");
    }

    @Test
    @Transactional
    public void getAllCartsByCreateddateIsEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where createddate equals to DEFAULT_CREATEDDATE
        defaultCartShouldBeFound("createddate.equals=" + DEFAULT_CREATEDDATE);

        // Get all the cartList where createddate equals to UPDATED_CREATEDDATE
        defaultCartShouldNotBeFound("createddate.equals=" + UPDATED_CREATEDDATE);
    }

    @Test
    @Transactional
    public void getAllCartsByCreateddateIsInShouldWork() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where createddate in DEFAULT_CREATEDDATE or UPDATED_CREATEDDATE
        defaultCartShouldBeFound("createddate.in=" + DEFAULT_CREATEDDATE + "," + UPDATED_CREATEDDATE);

        // Get all the cartList where createddate equals to UPDATED_CREATEDDATE
        defaultCartShouldNotBeFound("createddate.in=" + UPDATED_CREATEDDATE);
    }

    @Test
    @Transactional
    public void getAllCartsByCreateddateIsNullOrNotNull() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where createddate is not null
        defaultCartShouldBeFound("createddate.specified=true");

        // Get all the cartList where createddate is null
        defaultCartShouldNotBeFound("createddate.specified=false");
    }

    @Test
    @Transactional
    public void getAllCartsByLastactivedateIsEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where lastactivedate equals to DEFAULT_LASTACTIVEDATE
        defaultCartShouldBeFound("lastactivedate.equals=" + DEFAULT_LASTACTIVEDATE);

        // Get all the cartList where lastactivedate equals to UPDATED_LASTACTIVEDATE
        defaultCartShouldNotBeFound("lastactivedate.equals=" + UPDATED_LASTACTIVEDATE);
    }

    @Test
    @Transactional
    public void getAllCartsByLastactivedateIsInShouldWork() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where lastactivedate in DEFAULT_LASTACTIVEDATE or UPDATED_LASTACTIVEDATE
        defaultCartShouldBeFound("lastactivedate.in=" + DEFAULT_LASTACTIVEDATE + "," + UPDATED_LASTACTIVEDATE);

        // Get all the cartList where lastactivedate equals to UPDATED_LASTACTIVEDATE
        defaultCartShouldNotBeFound("lastactivedate.in=" + UPDATED_LASTACTIVEDATE);
    }

    @Test
    @Transactional
    public void getAllCartsByLastactivedateIsNullOrNotNull() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where lastactivedate is not null
        defaultCartShouldBeFound("lastactivedate.specified=true");

        // Get all the cartList where lastactivedate is null
        defaultCartShouldNotBeFound("lastactivedate.specified=false");
    }

    @Test
    @Transactional
    public void getAllCartsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where amount equals to DEFAULT_AMOUNT
        defaultCartShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the cartList where amount equals to UPDATED_AMOUNT
        defaultCartShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllCartsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultCartShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the cartList where amount equals to UPDATED_AMOUNT
        defaultCartShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllCartsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where amount is not null
        defaultCartShouldBeFound("amount.specified=true");

        // Get all the cartList where amount is null
        defaultCartShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    public void getAllCartsByCheckoutIsEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where checkout equals to DEFAULT_CHECKOUT
        defaultCartShouldBeFound("checkout.equals=" + DEFAULT_CHECKOUT);

        // Get all the cartList where checkout equals to UPDATED_CHECKOUT
        defaultCartShouldNotBeFound("checkout.equals=" + UPDATED_CHECKOUT);
    }

    @Test
    @Transactional
    public void getAllCartsByCheckoutIsInShouldWork() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where checkout in DEFAULT_CHECKOUT or UPDATED_CHECKOUT
        defaultCartShouldBeFound("checkout.in=" + DEFAULT_CHECKOUT + "," + UPDATED_CHECKOUT);

        // Get all the cartList where checkout equals to UPDATED_CHECKOUT
        defaultCartShouldNotBeFound("checkout.in=" + UPDATED_CHECKOUT);
    }

    @Test
    @Transactional
    public void getAllCartsByCheckoutIsNullOrNotNull() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where checkout is not null
        defaultCartShouldBeFound("checkout.specified=true");

        // Get all the cartList where checkout is null
        defaultCartShouldNotBeFound("checkout.specified=false");
    }

    @Test
    @Transactional
    public void getAllCartsByPointsIsEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where points equals to DEFAULT_POINTS
        defaultCartShouldBeFound("points.equals=" + DEFAULT_POINTS);

        // Get all the cartList where points equals to UPDATED_POINTS
        defaultCartShouldNotBeFound("points.equals=" + UPDATED_POINTS);
    }

    @Test
    @Transactional
    public void getAllCartsByPointsIsInShouldWork() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where points in DEFAULT_POINTS or UPDATED_POINTS
        defaultCartShouldBeFound("points.in=" + DEFAULT_POINTS + "," + UPDATED_POINTS);

        // Get all the cartList where points equals to UPDATED_POINTS
        defaultCartShouldNotBeFound("points.in=" + UPDATED_POINTS);
    }

    @Test
    @Transactional
    public void getAllCartsByPointsIsNullOrNotNull() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where points is not null
        defaultCartShouldBeFound("points.specified=true");

        // Get all the cartList where points is null
        defaultCartShouldNotBeFound("points.specified=false");
    }

    @Test
    @Transactional
    public void getAllCartsByPointsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where points greater than or equals to DEFAULT_POINTS
        defaultCartShouldBeFound("points.greaterOrEqualThan=" + DEFAULT_POINTS);

        // Get all the cartList where points greater than or equals to UPDATED_POINTS
        defaultCartShouldNotBeFound("points.greaterOrEqualThan=" + UPDATED_POINTS);
    }

    @Test
    @Transactional
    public void getAllCartsByPointsIsLessThanSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where points less than or equals to DEFAULT_POINTS
        defaultCartShouldNotBeFound("points.lessThan=" + DEFAULT_POINTS);

        // Get all the cartList where points less than or equals to UPDATED_POINTS
        defaultCartShouldBeFound("points.lessThan=" + UPDATED_POINTS);
    }


    @Test
    @Transactional
    public void getAllCartsByCustomerIsEqualToSomething() throws Exception {
        // Initialize the database
        Customer customer = CustomerResourceIntTest.createEntity(em);
        em.persist(customer);
        em.flush();
        cart.setCustomer(customer);
        cartRepository.saveAndFlush(cart);
        Long customerId = customer.getId();

        // Get all the cartList where customer equals to customerId
        defaultCartShouldBeFound("customerId.equals=" + customerId);

        // Get all the cartList where customer equals to customerId + 1
        defaultCartShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultCartShouldBeFound(String filter) throws Exception {
        restCartMockMvc.perform(get("/api/carts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cart.getId())))
            .andExpect(jsonPath("$.[*].normCart").value(hasItem(DEFAULT_NORM_CART)))
            .andExpect(jsonPath("$.[*].createddate").value(hasItem(DEFAULT_CREATEDDATE.toString())))
            .andExpect(jsonPath("$.[*].lastactivedate").value(hasItem(DEFAULT_LASTACTIVEDATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].checkout").value(hasItem(DEFAULT_CHECKOUT.booleanValue())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultCartShouldNotBeFound(String filter) throws Exception {
        restCartMockMvc.perform(get("/api/carts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingCart() throws Exception {
        // Get the cart
        restCartMockMvc.perform(get("/api/carts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCart() throws Exception {
        // Initialize the database
        cartService.save(cart);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockCartSearchRepository);

        int databaseSizeBeforeUpdate = cartRepository.findAll().size();

        // Update the cart
        Cart updatedCart = cartRepository.findById(cart.getId()).get();
        // Disconnect from session so that the updates on updatedCart are not directly saved in db
        em.detach(updatedCart);
        updatedCart
            .normCart(UPDATED_NORM_CART)
            .createddate(UPDATED_CREATEDDATE)
            .lastactivedate(UPDATED_LASTACTIVEDATE)
            .amount(UPDATED_AMOUNT)
            .checkout(UPDATED_CHECKOUT)
            .points(UPDATED_POINTS);

        restCartMockMvc.perform(put("/api/carts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCart)))
            .andExpect(status().isOk());

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
        Cart testCart = cartList.get(cartList.size() - 1);
        assertThat(testCart.getNormCart()).isEqualTo(UPDATED_NORM_CART);
        assertThat(testCart.getCreateddate()).isEqualTo(UPDATED_CREATEDDATE);
        assertThat(testCart.getLastactivedate()).isEqualTo(UPDATED_LASTACTIVEDATE);
        assertThat(testCart.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testCart.isCheckout()).isEqualTo(UPDATED_CHECKOUT);
        assertThat(testCart.getPoints()).isEqualTo(UPDATED_POINTS);

        // Validate the Cart in Elasticsearch
        verify(mockCartSearchRepository, times(1)).save(testCart);
    }

    @Test
    @Transactional
    public void updateNonExistingCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().size();

        // Create the Cart

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restCartMockMvc.perform(put("/api/carts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cart)))
            .andExpect(status().isBadRequest());

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cart in Elasticsearch
        verify(mockCartSearchRepository, times(0)).save(cart);
    }

    @Test
    @Transactional
    public void deleteCart() throws Exception {
        // Initialize the database
        cartService.save(cart);

        int databaseSizeBeforeDelete = cartRepository.findAll().size();

        // Get the cart
        restCartMockMvc.perform(delete("/api/carts/{id}", cart.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Cart> cartList = cartRepository.findAll();
        assertThat(cartList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Cart in Elasticsearch
        verify(mockCartSearchRepository, times(1)).deleteById(cart.getId());
    }

    @Test
    @Transactional
    public void searchCart() throws Exception {
        // Initialize the database
        cartService.save(cart);
        when(mockCartSearchRepository.search(queryStringQuery("id:" + cart.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(cart), PageRequest.of(0, 1), 1));
        // Search the cart
        restCartMockMvc.perform(get("/api/_search/carts?query=id:" + cart.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cart.getId())))
            .andExpect(jsonPath("$.[*].normCart").value(hasItem(DEFAULT_NORM_CART)))
            .andExpect(jsonPath("$.[*].createddate").value(hasItem(DEFAULT_CREATEDDATE.toString())))
            .andExpect(jsonPath("$.[*].lastactivedate").value(hasItem(DEFAULT_LASTACTIVEDATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].checkout").value(hasItem(DEFAULT_CHECKOUT.booleanValue())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cart.class);
        Cart cart1 = new Cart();
        cart1.setId(1L);
        Cart cart2 = new Cart();
        cart2.setId(cart1.getId());
        assertThat(cart1).isEqualTo(cart2);
        cart2.setId(2L);
        assertThat(cart1).isNotEqualTo(cart2);
        cart1.setId(null);
        assertThat(cart1).isNotEqualTo(cart2);
    }
}
