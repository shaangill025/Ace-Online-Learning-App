package io.github.softech.dev.sgill.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import io.github.jhipster.service.filter.InstantFilter;




/**
 * Criteria class for the Cart entity. This class is used in CartResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /carts?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CartCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter normCart;

    private InstantFilter createddate;

    private InstantFilter lastactivedate;

    private DoubleFilter amount;

    private BooleanFilter checkout;

    private IntegerFilter points;

    private LongFilter customerId;

    public CartCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNormCart() {
        return normCart;
    }

    public void setNormCart(StringFilter normCart) {
        this.normCart = normCart;
    }

    public InstantFilter getCreateddate() {
        return createddate;
    }

    public void setCreateddate(InstantFilter createddate) {
        this.createddate = createddate;
    }

    public InstantFilter getLastactivedate() {
        return lastactivedate;
    }

    public void setLastactivedate(InstantFilter lastactivedate) {
        this.lastactivedate = lastactivedate;
    }

    public DoubleFilter getAmount() {
        return amount;
    }

    public void setAmount(DoubleFilter amount) {
        this.amount = amount;
    }

    public BooleanFilter getCheckout() {
        return checkout;
    }

    public void setCheckout(BooleanFilter checkout) {
        this.checkout = checkout;
    }

    public IntegerFilter getPoints() {
        return points;
    }

    public void setPoints(IntegerFilter points) {
        this.points = points;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "CartCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (normCart != null ? "normCart=" + normCart + ", " : "") +
                (createddate != null ? "createddate=" + createddate + ", " : "") +
                (lastactivedate != null ? "lastactivedate=" + lastactivedate + ", " : "") +
                (amount != null ? "amount=" + amount + ", " : "") +
                (checkout != null ? "checkout=" + checkout + ", " : "") +
                (points != null ? "points=" + points + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
            "}";
    }

}
