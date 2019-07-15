package io.github.softech.dev.sgill.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Bookmark entity.
 */
public class BookmarkDTO implements Serializable {

    private Long id;

    @NotNull
    private String text;

    private Integer slide;

    private String timestamp;

    private Long sectionId;

    private String sectionName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getSlide() {
        return slide;
    }

    public void setSlide(Integer slide) {
        this.slide = slide;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BookmarkDTO bookmarkDTO = (BookmarkDTO) o;
        if (bookmarkDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), bookmarkDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BookmarkDTO{" +
            "id=" + getId() +
            ", text='" + getText() + "'" +
            ", slide=" + getSlide() +
            ", timestamp='" + getTimestamp() + "'" +
            ", section=" + getSectionId() +
            ", section='" + getSectionName() + "'" +
            "}";
    }
}
