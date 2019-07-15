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
 * Criteria class for the Section entity. This class is used in SectionResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /sections?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SectionCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter name;

    private StringFilter notes;

    private StringFilter normSection;

    private StringFilter videoUrl;

    private StringFilter type;

    private StringFilter pdfUrl;

    private IntegerFilter totalPages;

    private LongFilter quizId;

    private LongFilter tagsId;

    private LongFilter courseId;

    public SectionCriteria() {
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

    public StringFilter getNotes() {
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public StringFilter getNormSection() {
        return normSection;
    }

    public void setNormSection(StringFilter normSection) {
        this.normSection = normSection;
    }

    public StringFilter getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(StringFilter videoUrl) {
        this.videoUrl = videoUrl;
    }

    public StringFilter getType() {
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public StringFilter getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(StringFilter pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public IntegerFilter getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(IntegerFilter totalPages) {
        this.totalPages = totalPages;
    }

    public LongFilter getQuizId() {
        return quizId;
    }

    public void setQuizId(LongFilter quizId) {
        this.quizId = quizId;
    }

    public LongFilter getTagsId() {
        return tagsId;
    }

    public void setTagsId(LongFilter tagsId) {
        this.tagsId = tagsId;
    }

    public LongFilter getCourseId() {
        return courseId;
    }

    public void setCourseId(LongFilter courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "SectionCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (notes != null ? "notes=" + notes + ", " : "") +
                (normSection != null ? "normSection=" + normSection + ", " : "") +
                (videoUrl != null ? "videoUrl=" + videoUrl + ", " : "") +
                (type != null ? "type=" + type + ", " : "") +
                (pdfUrl != null ? "pdfUrl=" + pdfUrl + ", " : "") +
                (totalPages != null ? "totalPages=" + totalPages + ", " : "") +
                (quizId != null ? "quizId=" + quizId + ", " : "") +
                (tagsId != null ? "tagsId=" + tagsId + ", " : "") +
                (courseId != null ? "courseId=" + courseId + ", " : "") +
            "}";
    }

}
