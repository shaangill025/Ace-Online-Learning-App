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
 * Criteria class for the QuizHistory entity. This class is used in QuizHistoryResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /quiz-histories?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class QuizHistoryCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private InstantFilter start;

    private BooleanFilter passed;

    private LongFilter customerId;

    private LongFilter quizId;

    public QuizHistoryCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getStart() {
        return start;
    }

    public void setStart(InstantFilter start) {
        this.start = start;
    }

    public BooleanFilter getPassed() {
        return passed;
    }

    public void setPassed(BooleanFilter passed) {
        this.passed = passed;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public LongFilter getQuizId() {
        return quizId;
    }

    public void setQuizId(LongFilter quizId) {
        this.quizId = quizId;
    }

    @Override
    public String toString() {
        return "QuizHistoryCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (start != null ? "start=" + start + ", " : "") +
                (passed != null ? "passed=" + passed + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
                (quizId != null ? "quizId=" + quizId + ", " : "") +
            "}";
    }

}
