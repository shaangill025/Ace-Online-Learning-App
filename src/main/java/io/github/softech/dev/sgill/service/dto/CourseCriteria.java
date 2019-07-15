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

/**
 * Criteria class for the Course entity. This class is used in CourseResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /courses?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CourseCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter section;

    private StringFilter normCourses;

    private StringFilter description;

    private DoubleFilter amount;

    private LongFilter point;

    private StringFilter credit;

    private StringFilter country;

    private StringFilter state;

    private BooleanFilter show;

    private LongFilter topicId;

    private LongFilter tagsId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getSection() {
        return section;
    }

    public void setSection(StringFilter section) {
        this.section = section;
    }

    public StringFilter getNormCourses() {
        return normCourses;
    }

    public void setNormCourses(StringFilter normCourses) {
        this.normCourses = normCourses;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public DoubleFilter getAmount() {
        return amount;
    }

    public void setAmount(DoubleFilter amount) {
        this.amount = amount;
    }

    public LongFilter getPoint() {
        return point;
    }

    public void setPoint(LongFilter point) {
        this.point = point;
    }

    public StringFilter getCredit() {
        return credit;
    }

    public void setCredit(StringFilter credit) {
        this.credit = credit;
    }

    public StringFilter getCountry() {
        return country;
    }

    public void setCountry(StringFilter country) {
        this.country = country;
    }

    public StringFilter getState() {
        return state;
    }

    public void setState(StringFilter state) {
        this.state = state;
    }

    public BooleanFilter getShow() {
        return show;
    }

    public void setShow(BooleanFilter show) {
        this.show = show;
    }

    public LongFilter getTopicId() {
        return topicId;
    }

    public void setTopicId(LongFilter topicId) {
        this.topicId = topicId;
    }

    public LongFilter getTagsId() {
        return tagsId;
    }

    public void setTagsId(LongFilter tagsId) {
        this.tagsId = tagsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CourseCriteria that = (CourseCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(section, that.section) &&
            Objects.equals(normCourses, that.normCourses) &&
            Objects.equals(description, that.description) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(point, that.point) &&
            Objects.equals(credit, that.credit) &&
            Objects.equals(country, that.country) &&
            Objects.equals(state, that.state) &&
            Objects.equals(show, that.show) &&
            Objects.equals(topicId, that.topicId) &&
            Objects.equals(tagsId, that.tagsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        title,
        section,
        normCourses,
        description,
        amount,
        point,
        credit,
        country,
        state,
        show,
        topicId,
        tagsId
        );
    }

    @Override
    public String toString() {
        return "CourseCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (section != null ? "section=" + section + ", " : "") +
                (normCourses != null ? "normCourses=" + normCourses + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (amount != null ? "amount=" + amount + ", " : "") +
                (point != null ? "point=" + point + ", " : "") +
                (credit != null ? "credit=" + credit + ", " : "") +
                (country != null ? "country=" + country + ", " : "") +
                (state != null ? "state=" + state + ", " : "") +
                (show != null ? "show=" + show + ", " : "") +
                (topicId != null ? "topicId=" + topicId + ", " : "") +
                (tagsId != null ? "tagsId=" + tagsId + ", " : "") +
            "}";
    }

}
