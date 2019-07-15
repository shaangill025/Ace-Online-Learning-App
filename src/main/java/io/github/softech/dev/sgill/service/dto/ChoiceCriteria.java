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
 * Criteria class for the Choice entity. This class is used in ChoiceResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /choices?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ChoiceCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter textChoice;

    private BooleanFilter isanswer;

    private LongFilter questionId;

    public ChoiceCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTextChoice() {
        return textChoice;
    }

    public void setTextChoice(StringFilter textChoice) {
        this.textChoice = textChoice;
    }

    public BooleanFilter getIsanswer() {
        return isanswer;
    }

    public void setIsanswer(BooleanFilter isanswer) {
        this.isanswer = isanswer;
    }

    public LongFilter getQuestionId() {
        return questionId;
    }

    public void setQuestionId(LongFilter questionId) {
        this.questionId = questionId;
    }

    @Override
    public String toString() {
        return "ChoiceCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (textChoice != null ? "textChoice=" + textChoice + ", " : "") +
                (isanswer != null ? "isanswer=" + isanswer + ", " : "") +
                (questionId != null ? "questionId=" + questionId + ", " : "") +
            "}";
    }

}
