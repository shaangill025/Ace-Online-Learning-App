package io.github.softech.dev.sgill.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Course.
 */
@Entity
@Table(name = "course")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "course")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "section", nullable = false)
    private String section;

    @Column(name = "norm_courses")
    private String normCourses;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Double amount;

    
    @Lob
    @Column(name = "image", nullable = false)
    private byte[] image;

    @Column(name = "image_content_type", nullable = false)
    private String imageContentType;

    @Column(name = "point")
    private Long point;

    @NotNull
    @Column(name = "credit", nullable = false)
    private String credit;

    @NotNull
    @Column(name = "country", nullable = false)
    private String country;

    @NotNull
    @Column(name = "state", nullable = false)
    private String state;

    @NotNull
    @Column(name = "jhi_show", nullable = false)
    private Boolean show;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Topic topic;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "course_tags",
               joinColumns = @JoinColumn(name = "courses_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "tags_id", referencedColumnName = "id"))
    private Set<Tags> tags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Course title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSection() {
        return section;
    }

    public Course section(String section) {
        this.section = section;
        return this;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getNormCourses() {
        return normCourses;
    }

    public Course normCourses(String normCourses) {
        this.normCourses = normCourses;
        return this;
    }

    public void setNormCourses(String normCourses) {
        this.normCourses = normCourses;
    }

    public String getDescription() {
        return description;
    }

    public Course description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public Course amount(Double amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public byte[] getImage() {
        return image;
    }

    public Course image(byte[] image) {
        this.image = image;
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public Course imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Long getPoint() {
        return point;
    }

    public Course point(Long point) {
        this.point = point;
        return this;
    }

    public void setPoint(Long point) {
        this.point = point;
    }

    public String getCredit() {
        return credit;
    }

    public Course credit(String credit) {
        this.credit = credit;
        return this;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getCountry() {
        return country;
    }

    public Course country(String country) {
        this.country = country;
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public Course state(String state) {
        this.state = state;
        return this;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean isShow() {
        return show;
    }

    public Course show(Boolean show) {
        this.show = show;
        return this;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public Topic getTopic() {
        return topic;
    }

    public Course topic(Topic topic) {
        this.topic = topic;
        return this;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Set<Tags> getTags() {
        return tags;
    }

    public Course tags(Set<Tags> tags) {
        this.tags = tags;
        return this;
    }

    public Course addTags(Tags tags) {
        this.tags.add(tags);
        return this;
    }

    public Course removeTags(Tags tags) {
        this.tags.remove(tags);
        return this;
    }

    public void setTags(Set<Tags> tags) {
        this.tags = tags;
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
        Course course = (Course) o;
        if (course.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), course.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Course{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", section='" + getSection() + "'" +
            ", normCourses='" + getNormCourses() + "'" +
            ", description='" + getDescription() + "'" +
            ", amount=" + getAmount() +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", point=" + getPoint() +
            ", credit='" + getCredit() + "'" +
            ", country='" + getCountry() + "'" +
            ", state='" + getState() + "'" +
            ", show='" + isShow() + "'" +
            "}";
    }
}
