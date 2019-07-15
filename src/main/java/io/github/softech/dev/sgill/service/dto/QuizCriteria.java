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
 * Criteria class for the Quiz entity. This class is used in QuizResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /quizzes?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class QuizCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter name;

    private StringFilter difficulty;

    private IntegerFilter passingscore;

    private LongFilter newSectionId;

    public QuizCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(StringFilter difficulty) {
        this.difficulty = difficulty;
    }

    public IntegerFilter getPassingscore() {
        return passingscore;
    }

    public void setPassingscore(IntegerFilter passingscore) {
        this.passingscore = passingscore;
    }

    public LongFilter getNewSectionId() {
        return newSectionId;
    }

    public void setNewSectionId(LongFilter newSectionId) {
        this.newSectionId = newSectionId;
    }

    @Override
    public String toString() {
        return "QuizCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (difficulty != null ? "difficulty=" + difficulty + ", " : "") +
                (passingscore != null ? "passingscore=" + passingscore + ", " : "") +
                (newSectionId != null ? "newSectionId=" + newSectionId + ", " : "") +
            "}";
    }

}
