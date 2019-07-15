package io.github.softech.dev.sgill.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import io.github.softech.dev.sgill.domain.enumeration.NOTIFICATIONS;

import io.github.softech.dev.sgill.domain.enumeration.PAYMENT;

/**
 * A Orders.
 */
@Entity
@Table(name = "orders")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "orders")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "createddate")
    private Instant createddate;

    @Column(name = "amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NOTIFICATIONS status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment")
    private PAYMENT payment;

    @Column(name = "gateway_id")
    private String gateway_id;

    @Column(name = "seller_message")
    private String seller_message;

    @Column(name = "network_status")
    private String network_status;

    @Column(name = "seller_status")
    private String seller_status;

    @Column(name = "gateway_amt")
    private String gateway_amt;

    @Column(name = "seller_type")
    private String seller_type;

    @Column(name = "card_type")
    private String card_type;

    @Column(name = "last_4")
    private String last4;

    @OneToOne
    @JoinColumn(unique = true)
    private Cart cart;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreateddate() {
        return createddate;
    }

    public Orders createddate(Instant createddate) {
        this.createddate = createddate;
        return this;
    }

    public void setCreateddate(Instant createddate) {
        this.createddate = createddate;
    }

    public Double getAmount() {
        return amount;
    }

    public Orders amount(Double amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public NOTIFICATIONS getStatus() {
        return status;
    }

    public Orders status(NOTIFICATIONS status) {
        this.status = status;
        return this;
    }

    public void setStatus(NOTIFICATIONS status) {
        this.status = status;
    }

    public PAYMENT getPayment() {
        return payment;
    }

    public Orders payment(PAYMENT payment) {
        this.payment = payment;
        return this;
    }

    public void setPayment(PAYMENT payment) {
        this.payment = payment;
    }

    public String getGateway_id() {
        return gateway_id;
    }

    public Orders gateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
        return this;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    public String getSeller_message() {
        return seller_message;
    }

    public Orders seller_message(String seller_message) {
        this.seller_message = seller_message;
        return this;
    }

    public void setSeller_message(String seller_message) {
        this.seller_message = seller_message;
    }

    public String getNetwork_status() {
        return network_status;
    }

    public Orders network_status(String network_status) {
        this.network_status = network_status;
        return this;
    }

    public void setNetwork_status(String network_status) {
        this.network_status = network_status;
    }

    public String getSeller_status() {
        return seller_status;
    }

    public Orders seller_status(String seller_status) {
        this.seller_status = seller_status;
        return this;
    }

    public void setSeller_status(String seller_status) {
        this.seller_status = seller_status;
    }

    public String getGateway_amt() {
        return gateway_amt;
    }

    public Orders gateway_amt(String gateway_amt) {
        this.gateway_amt = gateway_amt;
        return this;
    }

    public void setGateway_amt(String gateway_amt) {
        this.gateway_amt = gateway_amt;
    }

    public String getSeller_type() {
        return seller_type;
    }

    public Orders seller_type(String seller_type) {
        this.seller_type = seller_type;
        return this;
    }

    public void setSeller_type(String seller_type) {
        this.seller_type = seller_type;
    }

    public String getCard_type() {
        return card_type;
    }

    public Orders card_type(String card_type) {
        this.card_type = card_type;
        return this;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getLast4() {
        return last4;
    }

    public Orders last4(String last4) {
        this.last4 = last4;
        return this;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public Cart getCart() {
        return cart;
    }

    public Orders cart(Cart cart) {
        this.cart = cart;
        return this;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Orders orders = (Orders) o;
        if (orders.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), orders.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Orders{" +
            "id=" + getId() +
            ", createddate='" + getCreateddate() + "'" +
            ", amount=" + getAmount() +
            ", status='" + getStatus() + "'" +
            ", payment='" + getPayment() + "'" +
            ", gateway_id='" + getGateway_id() + "'" +
            ", seller_message='" + getSeller_message() + "'" +
            ", network_status='" + getNetwork_status() + "'" +
            ", seller_status='" + getSeller_status() + "'" +
            ", gateway_amt='" + getGateway_amt() + "'" +
            ", seller_type='" + getSeller_type() + "'" +
            ", card_type='" + getCard_type() + "'" +
            ", last4='" + getLast4() + "'" +
            "}";
    }
}
