package io.github.softech.dev.sgill.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the Certificate entity. This class is used in CertificateResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /certificates?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CertificateCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter timestamp;

    private BooleanFilter isEmailed;

    private LongFilter customerId;

    private LongFilter courseHistoryId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(InstantFilter timestamp) {
        this.timestamp = timestamp;
    }

    public BooleanFilter getIsEmailed() {
        return isEmailed;
    }

    public void setIsEmailed(BooleanFilter isEmailed) {
        this.isEmailed = isEmailed;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public LongFilter getCourseHistoryId() {
        return courseHistoryId;
    }

    public void setCourseHistoryId(LongFilter courseHistoryId) {
        this.courseHistoryId = courseHistoryId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CertificateCriteria that = (CertificateCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(timestamp, that.timestamp) &&
            Objects.equals(isEmailed, that.isEmailed) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(courseHistoryId, that.courseHistoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        timestamp,
        isEmailed,
        customerId,
        courseHistoryId
        );
    }

    @Override
    public String toString() {
        return "CertificateCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (timestamp != null ? "timestamp=" + timestamp + ", " : "") +
                (isEmailed != null ? "isEmailed=" + isEmailed + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
                (courseHistoryId != null ? "courseHistoryId=" + courseHistoryId + ", " : "") +
            "}";
    }

}
