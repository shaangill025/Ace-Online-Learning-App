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
 * Criteria class for the Bookmark entity. This class is used in BookmarkResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /bookmarks?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BookmarkCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter text;

    private IntegerFilter slide;

    private StringFilter timestamp;

    private StringFilter module;

    private IntegerFilter seconds;

    private LongFilter sectionId;

    public BookmarkCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getText() {
        return text;
    }

    public void setText(StringFilter text) {
        this.text = text;
    }

    public IntegerFilter getSlide() {
        return slide;
    }

    public void setSlide(IntegerFilter slide) {
        this.slide = slide;
    }

    public StringFilter getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(StringFilter timestamp) {
        this.timestamp = timestamp;
    }

    public StringFilter getModule() {
        return module;
    }

    public void setModule(StringFilter module) {
        this.module = module;
    }

    public IntegerFilter getSeconds() {
        return seconds;
    }

    public void setSeconds(IntegerFilter seconds) {
        this.seconds = seconds;
    }

    public LongFilter getSectionId() {
        return sectionId;
    }

    public void setSectionId(LongFilter sectionId) {
        this.sectionId = sectionId;
    }

    @Override
    public String toString() {
        return "BookmarkCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (text != null ? "text=" + text + ", " : "") +
                (slide != null ? "slide=" + slide + ", " : "") +
                (timestamp != null ? "timestamp=" + timestamp + ", " : "") +
                (module != null ? "module=" + module + ", " : "") +
                (seconds != null ? "seconds=" + seconds + ", " : "") +
                (sectionId != null ? "sectionId=" + sectionId + ", " : "") +
            "}";
    }

}
