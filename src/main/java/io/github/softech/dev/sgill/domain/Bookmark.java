package io.github.softech.dev.sgill.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Bookmark.
 */
@Entity
@Table(name = "bookmark")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "bookmark")
public class Bookmark implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "slide")
    private Integer slide;

    @Column(name = "jhi_timestamp")
    private String timestamp;

    @Column(name = "module")
    private String module;

    @Column(name = "seconds")
    private Integer seconds;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("")
    private Section section;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public Bookmark text(String text) {
        this.text = text;
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getSlide() {
        return slide;
    }

    public Bookmark slide(Integer slide) {
        this.slide = slide;
        return this;
    }

    public void setSlide(Integer slide) {
        this.slide = slide;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Bookmark timestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getModule() {
        return module;
    }

    public Bookmark module(String module) {
        this.module = module;
        return this;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public Bookmark seconds(Integer seconds) {
        this.seconds = seconds;
        return this;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    public Section getSection() {
        return section;
    }

    public Bookmark section(Section section) {
        this.section = section;
        return this;
    }

    public void setSection(Section section) {
        this.section = section;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Bookmark bookmark = (Bookmark) o;
        if (bookmark.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), bookmark.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Bookmark{" +
            "id=" + getId() +
            ", text='" + getText() + "'" +
            ", slide=" + getSlide() +
            ", timestamp='" + getTimestamp() + "'" +
            ", module='" + getModule() + "'" +
            ", seconds=" + getSeconds() +
            "}";
    }
}
