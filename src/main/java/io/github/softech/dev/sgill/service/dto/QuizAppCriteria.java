package io.github.softech.dev.sgill.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;






/**
 * Criteria class for the QuizApp entity. This class is used in QuizAppResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /quiz-apps?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class QuizAppCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private LongFilter quizId;

    private LongFilter currSectionId;

    private LongFilter newSectionId;

    public QuizAppCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getQuizId() {
        return quizId;
    }

    public void setQuizId(LongFilter quizId) {
        this.quizId = quizId;
    }

    public LongFilter getCurrSectionId() {
        return currSectionId;
    }

    public void setCurrSectionId(LongFilter currSectionId) {
        this.currSectionId = currSectionId;
    }

    public LongFilter getNewSectionId() {
        return newSectionId;
    }

    public void setNewSectionId(LongFilter newSectionId) {
        this.newSectionId = newSectionId;
    }


    @Override
    public String toString() {
        return "QuizAppCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (quizId != null ? "quizId=" + quizId + ", " : "") +
                (currSectionId != null ? "currSectionId=" + currSectionId + ", " : "") +
                (newSectionId != null ? "newSectionId=" + newSectionId + ", " : "") +
            "}";
    }

}
