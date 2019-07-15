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
 * Criteria class for the SectionHistory entity. This class is used in SectionHistoryResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /section-histories?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SectionHistoryCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private InstantFilter startdate;

    private InstantFilter lastactivedate;

    private BooleanFilter watched;

    private IntegerFilter stamp;

    private LongFilter customerId;

    private LongFilter sectionId;

    public SectionHistoryCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getStartdate() {
        return startdate;
    }

    public void setStartdate(InstantFilter startdate) {
        this.startdate = startdate;
    }

    public InstantFilter getLastactivedate() {
        return lastactivedate;
    }

    public void setLastactivedate(InstantFilter lastactivedate) {
        this.lastactivedate = lastactivedate;
    }

    public BooleanFilter getWatched() {
        return watched;
    }

    public void setWatched(BooleanFilter watched) {
        this.watched = watched;
    }

    public IntegerFilter getStamp() {
        return stamp;
    }

    public void setStamp(IntegerFilter stamp) {
        this.stamp = stamp;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public LongFilter getSectionId() {
        return sectionId;
    }

    public void setSectionId(LongFilter sectionId) {
        this.sectionId = sectionId;
    }

    @Override
    public String toString() {
        return "SectionHistoryCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (startdate != null ? "startdate=" + startdate + ", " : "") +
                (lastactivedate != null ? "lastactivedate=" + lastactivedate + ", " : "") +
                (watched != null ? "watched=" + watched + ", " : "") +
                (stamp != null ? "stamp=" + stamp + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
                (sectionId != null ? "sectionId=" + sectionId + ", " : "") +
            "}";
    }

}
