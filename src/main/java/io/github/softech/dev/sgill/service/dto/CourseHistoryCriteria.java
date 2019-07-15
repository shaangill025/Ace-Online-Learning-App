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
 * Criteria class for the CourseHistory entity. This class is used in CourseHistoryResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /course-histories?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CourseHistoryCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private InstantFilter startdate;

    private InstantFilter lastactivedate;

    private BooleanFilter isactive;

    private BooleanFilter iscompleted;

    private BooleanFilter access;

    private LongFilter customerId;

    private LongFilter courseId;

    public CourseHistoryCriteria() {
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

    public BooleanFilter getIsactive() {
        return isactive;
    }

    public void setIsactive(BooleanFilter isactive) {
        this.isactive = isactive;
    }

    public BooleanFilter getIscompleted() {
        return iscompleted;
    }

    public void setIscompleted(BooleanFilter iscompleted) {
        this.iscompleted = iscompleted;
    }

    public BooleanFilter getAccess() {
        return access;
    }

    public void setAccess(BooleanFilter access) {
        this.access = access;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public LongFilter getCourseId() {
        return courseId;
    }

    public void setCourseId(LongFilter courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "CourseHistoryCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (startdate != null ? "startdate=" + startdate + ", " : "") +
                (lastactivedate != null ? "lastactivedate=" + lastactivedate + ", " : "") +
                (isactive != null ? "isactive=" + isactive + ", " : "") +
                (iscompleted != null ? "iscompleted=" + iscompleted + ", " : "") +
                (access != null ? "access=" + access + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
                (courseId != null ? "courseId=" + courseId + ", " : "") +
            "}";
    }

}
