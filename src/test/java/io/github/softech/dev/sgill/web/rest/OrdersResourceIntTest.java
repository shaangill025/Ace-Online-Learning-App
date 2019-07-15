package io.github.softech.dev.sgill.web.rest;

import io.github.softech.dev.sgill.SmartCpdApp;

import io.github.softech.dev.sgill.domain.Orders;
import io.github.softech.dev.sgill.domain.Cart;
import io.github.softech.dev.sgill.repository.*;
import io.github.softech.dev.sgill.repository.search.OrdersSearchRepository;
import io.github.softech.dev.sgill.service.*;
import io.github.softech.dev.sgill.web.rest.errors.ExceptionTranslator;
import io.github.softech.dev.sgill.service.dto.OrdersCriteria;

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

import io.github.softech.dev.sgill.domain.enumeration.NOTIFICATIONS;
import io.github.softech.dev.sgill.domain.enumeration.PAYMENT;
/**
 * Test class for the OrdersResource REST controller.
 *
 * @see OrdersResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartCpdApp.class)
public class OrdersResourceIntTest {

    private static final Instant DEFAULT_CREATEDDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATEDDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_AMOUNT = 1D;
    private static final Double UPDATED_AMOUNT = 2D;

    private static final NOTIFICATIONS DEFAULT_STATUS = NOTIFICATIONS.ORDERPROCESSING;
    private static final NOTIFICATIONS UPDATED_STATUS = NOTIFICATIONS.COMPLETE;

    private static final PAYMENT DEFAULT_PAYMENT = PAYMENT.PAYPAL;
    private static final PAYMENT UPDATED_PAYMENT = PAYMENT.STRIPE;

    private static final String DEFAULT_GATEWAY_ID = "AAAAAAAAAA";
    private static final String UPDATED_GATEWAY_ID = "BBBBBBBBBB";

    private static final String DEFAULT_SELLER_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_SELLER_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_NETWORK_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_NETWORK_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_SELLER_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_SELLER_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_GATEWAY_AMT = "AAAAAAAAAA";
    private static final String UPDATED_GATEWAY_AMT = "BBBBBBBBBB";

    private static final String DEFAULT_SELLER_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_SELLER_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_CARD_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CARD_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_4 = "AAAAAAAAAA";
    private static final String UPDATED_LAST_4 = "BBBBBBBBBB";

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersService ordersService;

    /**
     * This repository is mocked in the io.github.softech.dev.sgill.repository.search test package.
     *
     * @see io.github.softech.dev.sgill.repository.search.OrdersSearchRepositoryMockConfiguration
     */
    @Autowired
    private OrdersSearchRepository mockOrdersSearchRepository;

    @Autowired
    private OrdersQueryService ordersQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOrdersMockMvc;

    private Orders orders;

    @Autowired
    private CourseHistoryRepository courseHistoryRepository;

    @Autowired
    private CourseCartBridgeRepository courseCartBridgeRepository;

    @Autowired
    private CourseHistoryService courseHistoryService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CartService cartService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final OrdersResource ordersResource = new OrdersResource(ordersService, ordersQueryService,
            courseHistoryRepository, courseCartBridgeRepository, courseHistoryService, courseRepository,
            customerRepository, customerService, cartService, ordersRepository);
        this.restOrdersMockMvc = MockMvcBuilders.standaloneSetup(ordersResource)
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
    public static Orders createEntity(EntityManager em) {
        Orders orders = new Orders()
            .createddate(DEFAULT_CREATEDDATE)
            .amount(DEFAULT_AMOUNT)
            .status(DEFAULT_STATUS)
            .payment(DEFAULT_PAYMENT)
            .gateway_id(DEFAULT_GATEWAY_ID)
            .seller_message(DEFAULT_SELLER_MESSAGE)
            .network_status(DEFAULT_NETWORK_STATUS)
            .seller_status(DEFAULT_SELLER_STATUS)
            .gateway_amt(DEFAULT_GATEWAY_AMT)
            .seller_type(DEFAULT_SELLER_TYPE)
            .card_type(DEFAULT_CARD_TYPE)
            .last4(DEFAULT_LAST_4);
        return orders;
    }

    @Before
    public void initTest() {
        orders = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrders() throws Exception {
        int databaseSizeBeforeCreate = ordersRepository.findAll().size();

        // Create the Orders
        restOrdersMockMvc.perform(post("/api/orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isCreated());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeCreate + 1);
        Orders testOrders = ordersList.get(ordersList.size() - 1);
        assertThat(testOrders.getCreateddate()).isEqualTo(DEFAULT_CREATEDDATE);
        assertThat(testOrders.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testOrders.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testOrders.getPayment()).isEqualTo(DEFAULT_PAYMENT);
        assertThat(testOrders.getGateway_id()).isEqualTo(DEFAULT_GATEWAY_ID);
        assertThat(testOrders.getSeller_message()).isEqualTo(DEFAULT_SELLER_MESSAGE);
        assertThat(testOrders.getNetwork_status()).isEqualTo(DEFAULT_NETWORK_STATUS);
        assertThat(testOrders.getSeller_status()).isEqualTo(DEFAULT_SELLER_STATUS);
        assertThat(testOrders.getGateway_amt()).isEqualTo(DEFAULT_GATEWAY_AMT);
        assertThat(testOrders.getSeller_type()).isEqualTo(DEFAULT_SELLER_TYPE);
        assertThat(testOrders.getCard_type()).isEqualTo(DEFAULT_CARD_TYPE);
        assertThat(testOrders.getLast4()).isEqualTo(DEFAULT_LAST_4);

        // Validate the Orders in Elasticsearch
        verify(mockOrdersSearchRepository, times(1)).save(testOrders);
    }

    @Test
    @Transactional
    public void createOrdersWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ordersRepository.findAll().size();

        // Create the Orders with an existing ID
        orders.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrdersMockMvc.perform(post("/api/orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeCreate);

        // Validate the Orders in Elasticsearch
        verify(mockOrdersSearchRepository, times(0)).save(orders);
    }

    @Test
    @Transactional
    public void getAllOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList
        restOrdersMockMvc.perform(get("/api/orders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orders.getId())))
            .andExpect(jsonPath("$.[*].createddate").value(hasItem(DEFAULT_CREATEDDATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].payment").value(hasItem(DEFAULT_PAYMENT.toString())))
            .andExpect(jsonPath("$.[*].gateway_id").value(hasItem(DEFAULT_GATEWAY_ID)))
            .andExpect(jsonPath("$.[*].seller_message").value(hasItem(DEFAULT_SELLER_MESSAGE)))
            .andExpect(jsonPath("$.[*].network_status").value(hasItem(DEFAULT_NETWORK_STATUS)))
            .andExpect(jsonPath("$.[*].seller_status").value(hasItem(DEFAULT_SELLER_STATUS)))
            .andExpect(jsonPath("$.[*].gateway_amt").value(hasItem(DEFAULT_GATEWAY_AMT)))
            .andExpect(jsonPath("$.[*].seller_type").value(hasItem(DEFAULT_SELLER_TYPE)))
            .andExpect(jsonPath("$.[*].card_type").value(hasItem(DEFAULT_CARD_TYPE)))
            .andExpect(jsonPath("$.[*].last4").value(hasItem(DEFAULT_LAST_4)));
    }
    

    @Test
    @Transactional
    public void getOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get the orders
        restOrdersMockMvc.perform(get("/api/orders/{id}", orders.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(orders.getId()))
            .andExpect(jsonPath("$.createddate").value(DEFAULT_CREATEDDATE.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.payment").value(DEFAULT_PAYMENT.toString()))
            .andExpect(jsonPath("$.gateway_id").value(DEFAULT_GATEWAY_ID))
            .andExpect(jsonPath("$.seller_message").value(DEFAULT_SELLER_MESSAGE))
            .andExpect(jsonPath("$.network_status").value(DEFAULT_NETWORK_STATUS))
            .andExpect(jsonPath("$.seller_status").value(DEFAULT_SELLER_STATUS))
            .andExpect(jsonPath("$.gateway_amt").value(DEFAULT_GATEWAY_AMT))
            .andExpect(jsonPath("$.seller_type").value(DEFAULT_SELLER_TYPE))
            .andExpect(jsonPath("$.card_type").value(DEFAULT_CARD_TYPE))
            .andExpect(jsonPath("$.last4").value(DEFAULT_LAST_4));
    }

    @Test
    @Transactional
    public void getAllOrdersByCreateddateIsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where createddate equals to DEFAULT_CREATEDDATE
        defaultOrdersShouldBeFound("createddate.equals=" + DEFAULT_CREATEDDATE);

        // Get all the ordersList where createddate equals to UPDATED_CREATEDDATE
        defaultOrdersShouldNotBeFound("createddate.equals=" + UPDATED_CREATEDDATE);
    }

    @Test
    @Transactional
    public void getAllOrdersByCreateddateIsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where createddate in DEFAULT_CREATEDDATE or UPDATED_CREATEDDATE
        defaultOrdersShouldBeFound("createddate.in=" + DEFAULT_CREATEDDATE + "," + UPDATED_CREATEDDATE);

        // Get all the ordersList where createddate equals to UPDATED_CREATEDDATE
        defaultOrdersShouldNotBeFound("createddate.in=" + UPDATED_CREATEDDATE);
    }

    @Test
    @Transactional
    public void getAllOrdersByCreateddateIsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where createddate is not null
        defaultOrdersShouldBeFound("createddate.specified=true");

        // Get all the ordersList where createddate is null
        defaultOrdersShouldNotBeFound("createddate.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where amount equals to DEFAULT_AMOUNT
        defaultOrdersShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the ordersList where amount equals to UPDATED_AMOUNT
        defaultOrdersShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllOrdersByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultOrdersShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the ordersList where amount equals to UPDATED_AMOUNT
        defaultOrdersShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllOrdersByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where amount is not null
        defaultOrdersShouldBeFound("amount.specified=true");

        // Get all the ordersList where amount is null
        defaultOrdersShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where status equals to DEFAULT_STATUS
        defaultOrdersShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the ordersList where status equals to UPDATED_STATUS
        defaultOrdersShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllOrdersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultOrdersShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the ordersList where status equals to UPDATED_STATUS
        defaultOrdersShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllOrdersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where status is not null
        defaultOrdersShouldBeFound("status.specified=true");

        // Get all the ordersList where status is null
        defaultOrdersShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByPaymentIsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where payment equals to DEFAULT_PAYMENT
        defaultOrdersShouldBeFound("payment.equals=" + DEFAULT_PAYMENT);

        // Get all the ordersList where payment equals to UPDATED_PAYMENT
        defaultOrdersShouldNotBeFound("payment.equals=" + UPDATED_PAYMENT);
    }

    @Test
    @Transactional
    public void getAllOrdersByPaymentIsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where payment in DEFAULT_PAYMENT or UPDATED_PAYMENT
        defaultOrdersShouldBeFound("payment.in=" + DEFAULT_PAYMENT + "," + UPDATED_PAYMENT);

        // Get all the ordersList where payment equals to UPDATED_PAYMENT
        defaultOrdersShouldNotBeFound("payment.in=" + UPDATED_PAYMENT);
    }

    @Test
    @Transactional
    public void getAllOrdersByPaymentIsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where payment is not null
        defaultOrdersShouldBeFound("payment.specified=true");

        // Get all the ordersList where payment is null
        defaultOrdersShouldNotBeFound("payment.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByGateway_idIsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where gateway_id equals to DEFAULT_GATEWAY_ID
        defaultOrdersShouldBeFound("gateway_id.equals=" + DEFAULT_GATEWAY_ID);

        // Get all the ordersList where gateway_id equals to UPDATED_GATEWAY_ID
        defaultOrdersShouldNotBeFound("gateway_id.equals=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    public void getAllOrdersByGateway_idIsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where gateway_id in DEFAULT_GATEWAY_ID or UPDATED_GATEWAY_ID
        defaultOrdersShouldBeFound("gateway_id.in=" + DEFAULT_GATEWAY_ID + "," + UPDATED_GATEWAY_ID);

        // Get all the ordersList where gateway_id equals to UPDATED_GATEWAY_ID
        defaultOrdersShouldNotBeFound("gateway_id.in=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    public void getAllOrdersByGateway_idIsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where gateway_id is not null
        defaultOrdersShouldBeFound("gateway_id.specified=true");

        // Get all the ordersList where gateway_id is null
        defaultOrdersShouldNotBeFound("gateway_id.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersBySeller_messageIsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where seller_message equals to DEFAULT_SELLER_MESSAGE
        defaultOrdersShouldBeFound("seller_message.equals=" + DEFAULT_SELLER_MESSAGE);

        // Get all the ordersList where seller_message equals to UPDATED_SELLER_MESSAGE
        defaultOrdersShouldNotBeFound("seller_message.equals=" + UPDATED_SELLER_MESSAGE);
    }

    @Test
    @Transactional
    public void getAllOrdersBySeller_messageIsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where seller_message in DEFAULT_SELLER_MESSAGE or UPDATED_SELLER_MESSAGE
        defaultOrdersShouldBeFound("seller_message.in=" + DEFAULT_SELLER_MESSAGE + "," + UPDATED_SELLER_MESSAGE);

        // Get all the ordersList where seller_message equals to UPDATED_SELLER_MESSAGE
        defaultOrdersShouldNotBeFound("seller_message.in=" + UPDATED_SELLER_MESSAGE);
    }

    @Test
    @Transactional
    public void getAllOrdersBySeller_messageIsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where seller_message is not null
        defaultOrdersShouldBeFound("seller_message.specified=true");

        // Get all the ordersList where seller_message is null
        defaultOrdersShouldNotBeFound("seller_message.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByNetwork_statusIsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where network_status equals to DEFAULT_NETWORK_STATUS
        defaultOrdersShouldBeFound("network_status.equals=" + DEFAULT_NETWORK_STATUS);

        // Get all the ordersList where network_status equals to UPDATED_NETWORK_STATUS
        defaultOrdersShouldNotBeFound("network_status.equals=" + UPDATED_NETWORK_STATUS);
    }

    @Test
    @Transactional
    public void getAllOrdersByNetwork_statusIsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where network_status in DEFAULT_NETWORK_STATUS or UPDATED_NETWORK_STATUS
        defaultOrdersShouldBeFound("network_status.in=" + DEFAULT_NETWORK_STATUS + "," + UPDATED_NETWORK_STATUS);

        // Get all the ordersList where network_status equals to UPDATED_NETWORK_STATUS
        defaultOrdersShouldNotBeFound("network_status.in=" + UPDATED_NETWORK_STATUS);
    }

    @Test
    @Transactional
    public void getAllOrdersByNetwork_statusIsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where network_status is not null
        defaultOrdersShouldBeFound("network_status.specified=true");

        // Get all the ordersList where network_status is null
        defaultOrdersShouldNotBeFound("network_status.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersBySeller_statusIsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where seller_status equals to DEFAULT_SELLER_STATUS
        defaultOrdersShouldBeFound("seller_status.equals=" + DEFAULT_SELLER_STATUS);

        // Get all the ordersList where seller_status equals to UPDATED_SELLER_STATUS
        defaultOrdersShouldNotBeFound("seller_status.equals=" + UPDATED_SELLER_STATUS);
    }

    @Test
    @Transactional
    public void getAllOrdersBySeller_statusIsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where seller_status in DEFAULT_SELLER_STATUS or UPDATED_SELLER_STATUS
        defaultOrdersShouldBeFound("seller_status.in=" + DEFAULT_SELLER_STATUS + "," + UPDATED_SELLER_STATUS);

        // Get all the ordersList where seller_status equals to UPDATED_SELLER_STATUS
        defaultOrdersShouldNotBeFound("seller_status.in=" + UPDATED_SELLER_STATUS);
    }

    @Test
    @Transactional
    public void getAllOrdersBySeller_statusIsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where seller_status is not null
        defaultOrdersShouldBeFound("seller_status.specified=true");

        // Get all the ordersList where seller_status is null
        defaultOrdersShouldNotBeFound("seller_status.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByGateway_amtIsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where gateway_amt equals to DEFAULT_GATEWAY_AMT
        defaultOrdersShouldBeFound("gateway_amt.equals=" + DEFAULT_GATEWAY_AMT);

        // Get all the ordersList where gateway_amt equals to UPDATED_GATEWAY_AMT
        defaultOrdersShouldNotBeFound("gateway_amt.equals=" + UPDATED_GATEWAY_AMT);
    }

    @Test
    @Transactional
    public void getAllOrdersByGateway_amtIsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where gateway_amt in DEFAULT_GATEWAY_AMT or UPDATED_GATEWAY_AMT
        defaultOrdersShouldBeFound("gateway_amt.in=" + DEFAULT_GATEWAY_AMT + "," + UPDATED_GATEWAY_AMT);

        // Get all the ordersList where gateway_amt equals to UPDATED_GATEWAY_AMT
        defaultOrdersShouldNotBeFound("gateway_amt.in=" + UPDATED_GATEWAY_AMT);
    }

    @Test
    @Transactional
    public void getAllOrdersByGateway_amtIsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where gateway_amt is not null
        defaultOrdersShouldBeFound("gateway_amt.specified=true");

        // Get all the ordersList where gateway_amt is null
        defaultOrdersShouldNotBeFound("gateway_amt.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersBySeller_typeIsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where seller_type equals to DEFAULT_SELLER_TYPE
        defaultOrdersShouldBeFound("seller_type.equals=" + DEFAULT_SELLER_TYPE);

        // Get all the ordersList where seller_type equals to UPDATED_SELLER_TYPE
        defaultOrdersShouldNotBeFound("seller_type.equals=" + UPDATED_SELLER_TYPE);
    }

    @Test
    @Transactional
    public void getAllOrdersBySeller_typeIsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where seller_type in DEFAULT_SELLER_TYPE or UPDATED_SELLER_TYPE
        defaultOrdersShouldBeFound("seller_type.in=" + DEFAULT_SELLER_TYPE + "," + UPDATED_SELLER_TYPE);

        // Get all the ordersList where seller_type equals to UPDATED_SELLER_TYPE
        defaultOrdersShouldNotBeFound("seller_type.in=" + UPDATED_SELLER_TYPE);
    }

    @Test
    @Transactional
    public void getAllOrdersBySeller_typeIsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where seller_type is not null
        defaultOrdersShouldBeFound("seller_type.specified=true");

        // Get all the ordersList where seller_type is null
        defaultOrdersShouldNotBeFound("seller_type.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByCard_typeIsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where card_type equals to DEFAULT_CARD_TYPE
        defaultOrdersShouldBeFound("card_type.equals=" + DEFAULT_CARD_TYPE);

        // Get all the ordersList where card_type equals to UPDATED_CARD_TYPE
        defaultOrdersShouldNotBeFound("card_type.equals=" + UPDATED_CARD_TYPE);
    }

    @Test
    @Transactional
    public void getAllOrdersByCard_typeIsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where card_type in DEFAULT_CARD_TYPE or UPDATED_CARD_TYPE
        defaultOrdersShouldBeFound("card_type.in=" + DEFAULT_CARD_TYPE + "," + UPDATED_CARD_TYPE);

        // Get all the ordersList where card_type equals to UPDATED_CARD_TYPE
        defaultOrdersShouldNotBeFound("card_type.in=" + UPDATED_CARD_TYPE);
    }

    @Test
    @Transactional
    public void getAllOrdersByCard_typeIsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where card_type is not null
        defaultOrdersShouldBeFound("card_type.specified=true");

        // Get all the ordersList where card_type is null
        defaultOrdersShouldNotBeFound("card_type.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByLast4IsEqualToSomething() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where last4 equals to DEFAULT_LAST_4
        defaultOrdersShouldBeFound("last4.equals=" + DEFAULT_LAST_4);

        // Get all the ordersList where last4 equals to UPDATED_LAST_4
        defaultOrdersShouldNotBeFound("last4.equals=" + UPDATED_LAST_4);
    }

    @Test
    @Transactional
    public void getAllOrdersByLast4IsInShouldWork() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where last4 in DEFAULT_LAST_4 or UPDATED_LAST_4
        defaultOrdersShouldBeFound("last4.in=" + DEFAULT_LAST_4 + "," + UPDATED_LAST_4);

        // Get all the ordersList where last4 equals to UPDATED_LAST_4
        defaultOrdersShouldNotBeFound("last4.in=" + UPDATED_LAST_4);
    }

    @Test
    @Transactional
    public void getAllOrdersByLast4IsNullOrNotNull() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where last4 is not null
        defaultOrdersShouldBeFound("last4.specified=true");

        // Get all the ordersList where last4 is null
        defaultOrdersShouldNotBeFound("last4.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByCartIsEqualToSomething() throws Exception {
        // Initialize the database
        Cart cart = CartResourceIntTest.createEntity(em);
        em.persist(cart);
        em.flush();
        orders.setCart(cart);
        ordersRepository.saveAndFlush(orders);
        Long cartId = cart.getId();

        // Get all the ordersList where cart equals to cartId
        defaultOrdersShouldBeFound("cartId.equals=" + cartId);

        // Get all the ordersList where cart equals to cartId + 1
        defaultOrdersShouldNotBeFound("cartId.equals=" + (cartId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultOrdersShouldBeFound(String filter) throws Exception {
        restOrdersMockMvc.perform(get("/api/orders?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orders.getId())))
            .andExpect(jsonPath("$.[*].createddate").value(hasItem(DEFAULT_CREATEDDATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].payment").value(hasItem(DEFAULT_PAYMENT.toString())))
            .andExpect(jsonPath("$.[*].gateway_id").value(hasItem(DEFAULT_GATEWAY_ID)))
            .andExpect(jsonPath("$.[*].seller_message").value(hasItem(DEFAULT_SELLER_MESSAGE)))
            .andExpect(jsonPath("$.[*].network_status").value(hasItem(DEFAULT_NETWORK_STATUS)))
            .andExpect(jsonPath("$.[*].seller_status").value(hasItem(DEFAULT_SELLER_STATUS)))
            .andExpect(jsonPath("$.[*].gateway_amt").value(hasItem(DEFAULT_GATEWAY_AMT)))
            .andExpect(jsonPath("$.[*].seller_type").value(hasItem(DEFAULT_SELLER_TYPE)))
            .andExpect(jsonPath("$.[*].card_type").value(hasItem(DEFAULT_CARD_TYPE)))
            .andExpect(jsonPath("$.[*].last4").value(hasItem(DEFAULT_LAST_4)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultOrdersShouldNotBeFound(String filter) throws Exception {
        restOrdersMockMvc.perform(get("/api/orders?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingOrders() throws Exception {
        // Get the orders
        restOrdersMockMvc.perform(get("/api/orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrders() throws Exception {
        // Initialize the database
        ordersService.save(orders);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockOrdersSearchRepository);

        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();

        // Update the orders
        Orders updatedOrders = ordersRepository.findById(orders.getId()).get();
        // Disconnect from session so that the updates on updatedOrders are not directly saved in db
        em.detach(updatedOrders);
        updatedOrders
            .createddate(UPDATED_CREATEDDATE)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS)
            .payment(UPDATED_PAYMENT)
            .gateway_id(UPDATED_GATEWAY_ID)
            .seller_message(UPDATED_SELLER_MESSAGE)
            .network_status(UPDATED_NETWORK_STATUS)
            .seller_status(UPDATED_SELLER_STATUS)
            .gateway_amt(UPDATED_GATEWAY_AMT)
            .seller_type(UPDATED_SELLER_TYPE)
            .card_type(UPDATED_CARD_TYPE)
            .last4(UPDATED_LAST_4);

        restOrdersMockMvc.perform(put("/api/orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOrders)))
            .andExpect(status().isOk());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
        Orders testOrders = ordersList.get(ordersList.size() - 1);
        assertThat(testOrders.getCreateddate()).isEqualTo(UPDATED_CREATEDDATE);
        assertThat(testOrders.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testOrders.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOrders.getPayment()).isEqualTo(UPDATED_PAYMENT);
        assertThat(testOrders.getGateway_id()).isEqualTo(UPDATED_GATEWAY_ID);
        assertThat(testOrders.getSeller_message()).isEqualTo(UPDATED_SELLER_MESSAGE);
        assertThat(testOrders.getNetwork_status()).isEqualTo(UPDATED_NETWORK_STATUS);
        assertThat(testOrders.getSeller_status()).isEqualTo(UPDATED_SELLER_STATUS);
        assertThat(testOrders.getGateway_amt()).isEqualTo(UPDATED_GATEWAY_AMT);
        assertThat(testOrders.getSeller_type()).isEqualTo(UPDATED_SELLER_TYPE);
        assertThat(testOrders.getCard_type()).isEqualTo(UPDATED_CARD_TYPE);
        assertThat(testOrders.getLast4()).isEqualTo(UPDATED_LAST_4);

        // Validate the Orders in Elasticsearch
        verify(mockOrdersSearchRepository, times(1)).save(testOrders);
    }

    @Test
    @Transactional
    public void updateNonExistingOrders() throws Exception {
        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();

        // Create the Orders

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restOrdersMockMvc.perform(put("/api/orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Orders in Elasticsearch
        verify(mockOrdersSearchRepository, times(0)).save(orders);
    }

    @Test
    @Transactional
    public void deleteOrders() throws Exception {
        // Initialize the database
        ordersService.save(orders);

        int databaseSizeBeforeDelete = ordersRepository.findAll().size();

        // Get the orders
        restOrdersMockMvc.perform(delete("/api/orders/{id}", orders.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Orders in Elasticsearch
        verify(mockOrdersSearchRepository, times(1)).deleteById(orders.getId());
    }

    @Test
    @Transactional
    public void searchOrders() throws Exception {
        // Initialize the database
        ordersService.save(orders);
        when(mockOrdersSearchRepository.search(queryStringQuery("id:" + orders.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(orders), PageRequest.of(0, 1), 1));
        // Search the orders
        restOrdersMockMvc.perform(get("/api/_search/orders?query=id:" + orders.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orders.getId())))
            .andExpect(jsonPath("$.[*].createddate").value(hasItem(DEFAULT_CREATEDDATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].payment").value(hasItem(DEFAULT_PAYMENT.toString())))
            .andExpect(jsonPath("$.[*].gateway_id").value(hasItem(DEFAULT_GATEWAY_ID)))
            .andExpect(jsonPath("$.[*].seller_message").value(hasItem(DEFAULT_SELLER_MESSAGE)))
            .andExpect(jsonPath("$.[*].network_status").value(hasItem(DEFAULT_NETWORK_STATUS)))
            .andExpect(jsonPath("$.[*].seller_status").value(hasItem(DEFAULT_SELLER_STATUS)))
            .andExpect(jsonPath("$.[*].gateway_amt").value(hasItem(DEFAULT_GATEWAY_AMT)))
            .andExpect(jsonPath("$.[*].seller_type").value(hasItem(DEFAULT_SELLER_TYPE)))
            .andExpect(jsonPath("$.[*].card_type").value(hasItem(DEFAULT_CARD_TYPE)))
            .andExpect(jsonPath("$.[*].last4").value(hasItem(DEFAULT_LAST_4)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Orders.class);
        Orders orders1 = new Orders();
        orders1.setId(1L);
        Orders orders2 = new Orders();
        orders2.setId(orders1.getId());
        assertThat(orders1).isEqualTo(orders2);
        orders2.setId(2L);
        assertThat(orders1).isNotEqualTo(orders2);
        orders1.setId(null);
        assertThat(orders1).isNotEqualTo(orders2);
    }
}
