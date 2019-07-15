package io.github.softech.dev.sgill.service.dto;

import java.io.Serializable;
import io.github.softech.dev.sgill.domain.enumeration.NOTIFICATIONS;
import io.github.softech.dev.sgill.domain.enumeration.PAYMENT;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import io.github.jhipster.service.filter.InstantFilter;




/**
 * Criteria class for the Orders entity. This class is used in OrdersResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /orders?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OrdersCriteria implements Serializable {
    /**
     * Class for filtering NOTIFICATIONS
     */
    public static class NOTIFICATIONSFilter extends Filter<NOTIFICATIONS> {
    }

    /**
     * Class for filtering PAYMENT
     */
    public static class PAYMENTFilter extends Filter<PAYMENT> {
    }

    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private InstantFilter createddate;

    private DoubleFilter amount;

    private NOTIFICATIONSFilter status;

    private PAYMENTFilter payment;

    private StringFilter gateway_id;

    private StringFilter seller_message;

    private StringFilter network_status;

    private StringFilter seller_status;

    private StringFilter gateway_amt;

    private StringFilter seller_type;

    private StringFilter card_type;

    private StringFilter last4;

    private LongFilter cartId;

    public OrdersCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getCreateddate() {
        return createddate;
    }

    public void setCreateddate(InstantFilter createddate) {
        this.createddate = createddate;
    }

    public DoubleFilter getAmount() {
        return amount;
    }

    public void setAmount(DoubleFilter amount) {
        this.amount = amount;
    }

    public NOTIFICATIONSFilter getStatus() {
        return status;
    }

    public void setStatus(NOTIFICATIONSFilter status) {
        this.status = status;
    }

    public PAYMENTFilter getPayment() {
        return payment;
    }

    public void setPayment(PAYMENTFilter payment) {
        this.payment = payment;
    }

    public StringFilter getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(StringFilter gateway_id) {
        this.gateway_id = gateway_id;
    }

    public StringFilter getSeller_message() {
        return seller_message;
    }

    public void setSeller_message(StringFilter seller_message) {
        this.seller_message = seller_message;
    }

    public StringFilter getNetwork_status() {
        return network_status;
    }

    public void setNetwork_status(StringFilter network_status) {
        this.network_status = network_status;
    }

    public StringFilter getSeller_status() {
        return seller_status;
    }

    public void setSeller_status(StringFilter seller_status) {
        this.seller_status = seller_status;
    }

    public StringFilter getGateway_amt() {
        return gateway_amt;
    }

    public void setGateway_amt(StringFilter gateway_amt) {
        this.gateway_amt = gateway_amt;
    }

    public StringFilter getSeller_type() {
        return seller_type;
    }

    public void setSeller_type(StringFilter seller_type) {
        this.seller_type = seller_type;
    }

    public StringFilter getCard_type() {
        return card_type;
    }

    public void setCard_type(StringFilter card_type) {
        this.card_type = card_type;
    }

    public StringFilter getLast4() {
        return last4;
    }

    public void setLast4(StringFilter last4) {
        this.last4 = last4;
    }

    public LongFilter getCartId() {
        return cartId;
    }

    public void setCartId(LongFilter cartId) {
        this.cartId = cartId;
    }

    @Override
    public String toString() {
        return "OrdersCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (createddate != null ? "createddate=" + createddate + ", " : "") +
                (amount != null ? "amount=" + amount + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (payment != null ? "payment=" + payment + ", " : "") +
                (gateway_id != null ? "gateway_id=" + gateway_id + ", " : "") +
                (seller_message != null ? "seller_message=" + seller_message + ", " : "") +
                (network_status != null ? "network_status=" + network_status + ", " : "") +
                (seller_status != null ? "seller_status=" + seller_status + ", " : "") +
                (gateway_amt != null ? "gateway_amt=" + gateway_amt + ", " : "") +
                (seller_type != null ? "seller_type=" + seller_type + ", " : "") +
                (card_type != null ? "card_type=" + card_type + ", " : "") +
                (last4 != null ? "last4=" + last4 + ", " : "") +
                (cartId != null ? "cartId=" + cartId + ", " : "") +
            "}";
    }

}
