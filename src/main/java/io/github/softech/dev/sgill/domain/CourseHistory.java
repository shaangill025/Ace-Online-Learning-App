package io.github.softech.dev.sgill.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A CourseHistory.
 */
@Entity
@Table(name = "course_history")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "coursehistory")
public class CourseHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "startdate")
    private Instant startdate;

    @Column(name = "lastactivedate")
    private Instant lastactivedate;

    @Column(name = "isactive")
    private Boolean isactive;

    @Column(name = "iscompleted")
    private Boolean iscompleted;

    @Column(name = "jhi_access")
    private Boolean access;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Customer customer;

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

    public Instant getStartdate() {
        return startdate;
    }

    public CourseHistory startdate(Instant startdate) {
        this.startdate = startdate;
        return this;
    }

    public void setStartdate(Instant startdate) {
        this.startdate = startdate;
    }

    public Instant getLastactivedate() {
        return lastactivedate;
    }

    public CourseHistory lastactivedate(Instant lastactivedate) {
        this.lastactivedate = lastactivedate;
        return this;
    }

    public void setLastactivedate(Instant lastactivedate) {
        this.lastactivedate = lastactivedate;
    }

    public Boolean isIsactive() {
        return isactive;
    }

    public CourseHistory isactive(Boolean isactive) {
        this.isactive = isactive;
        return this;
    }

    public void setIsactive(Boolean isactive) {
        this.isactive = isactive;
    }

    public Boolean isIscompleted() {
        return iscompleted;
    }

    public CourseHistory iscompleted(Boolean iscompleted) {
        this.iscompleted = iscompleted;
        return this;
    }

    public void setIscompleted(Boolean iscompleted) {
        this.iscompleted = iscompleted;
    }

    public Boolean isAccess() {
        return access;
    }

    public CourseHistory access(Boolean access) {
        this.access = access;
        return this;
    }

    public void setAccess(Boolean access) {
        this.access = access;
    }

    public Customer getCustomer() {
        return customer;
    }

    public CourseHistory customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Course getCourse() {
        return course;
    }

    public CourseHistory course(Course course) {
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
        CourseHistory courseHistory = (CourseHistory) o;
        if (courseHistory.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), courseHistory.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CourseHistory{" +
            "id=" + getId() +
            ", startdate='" + getStartdate() + "'" +
            ", lastactivedate='" + getLastactivedate() + "'" +
            ", isactive='" + isIsactive() + "'" +
            ", iscompleted='" + isIscompleted() + "'" +
            ", access='" + isAccess() + "'" +
            "}";
    }
}
