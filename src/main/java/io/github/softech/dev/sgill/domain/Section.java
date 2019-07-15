package io.github.softech.dev.sgill.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Section.
 */
@Entity
@Table(name = "section")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "section")
public class Section implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "notes")
    private String notes;

    @Column(name = "norm_section")
    private String normSection;

    
    @Lob
    @Column(name = "content", nullable = true)
    private byte[] content;

    @Column(name = "content_content_type", nullable = true)
    private String contentContentType;

    @Column(name = "video_url")
    private String videoUrl;

    @Lob
    @Column(name = "textcontent")
    private String textcontent;

    @NotNull
    @Column(name = "jhi_type", nullable = false)
    private String type;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "total_pages")
    private Integer totalPages;

    @OneToOne
    @JoinColumn(unique = true)
    private Quiz quiz;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "section_tags",
               joinColumns = @JoinColumn(name = "sections_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "tags_id", referencedColumnName = "id"))
    private Set<Tags> tags = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("")
    private Course course;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Section name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public Section notes(String notes) {
        this.notes = notes;
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNormSection() {
        return normSection;
    }

    public Section normSection(String normSection) {
        this.normSection = normSection;
        return this;
    }

    public void setNormSection(String normSection) {
        this.normSection = normSection;
    }

    public byte[] getContent() {
        return content;
    }

    public Section content(byte[] content) {
        this.content = content;
        return this;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentContentType() {
        return contentContentType;
    }

    public Section contentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
        return this;
    }

    public void setContentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public Section videoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        return this;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTextcontent() {
        return textcontent;
    }

    public Section textcontent(String textcontent) {
        this.textcontent = textcontent;
        return this;
    }

    public void setTextcontent(String textcontent) {
        this.textcontent = textcontent;
    }

    public String getType() {
        return type;
    }

    public Section type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public Section pdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
        return this;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Section totalPages(Integer totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public Section quiz(Quiz quiz) {
        this.quiz = quiz;
        return this;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Set<Tags> getTags() {
        return tags;
    }

    public Section tags(Set<Tags> tags) {
        this.tags = tags;
        return this;
    }

    public Section addTags(Tags tags) {
        this.tags.add(tags);
        return this;
    }

    public Section removeTags(Tags tags) {
        this.tags.remove(tags);
        return this;
    }

    public void setTags(Set<Tags> tags) {
        this.tags = tags;
    }

    public Course getCourse() {
        return course;
    }

    public Section course(Course course) {
        this.course = course;
        return this;
    }

    public void setCourse(Course course) {
        this.course = course;
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
        Section section = (Section) o;
        if (section.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", notes='" + getNotes() + "'" +
            ", normSection='" + getNormSection() + "'" +
            ", content='" + getContent() + "'" +
            ", contentContentType='" + getContentContentType() + "'" +
            ", videoUrl='" + getVideoUrl() + "'" +
            ", textcontent='" + getTextcontent() + "'" +
            ", type='" + getType() + "'" +
            ", pdfUrl='" + getPdfUrl() + "'" +
            ", totalPages=" + getTotalPages() +
            "}";
    }
}
