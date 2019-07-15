package io.github.softech.dev.sgill.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A TimeCourseLog.
 */
@Entity
@Table(name = "time_course_log")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "timecourselog")
public class TimeCourseLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timespent")
    private Long timespent;

    @Column(name = "recorddate")
    private Instant recorddate;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Customer customer;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("")
    private CourseHistory courseHistory;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimespent() {
        return timespent;
    }

    public TimeCourseLog timespent(Long timespent) {
        this.timespent = timespent;
        return this;
    }

    public void setTimespent(Long timespent) {
        this.timespent = timespent;
    }

    public Instant getRecorddate() {
        return recorddate;
    }

    public TimeCourseLog recorddate(Instant recorddate) {
        this.recorddate = recorddate;
        return this;
    }

    public void setRecorddate(Instant recorddate) {
        this.recorddate = recorddate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public TimeCourseLog customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CourseHistory getCourseHistory() {
        return courseHistory;
    }

    public TimeCourseLog courseHistory(CourseHistory courseHistory) {
        this.courseHistory = courseHistory;
        return this;
    }

    public void setCourseHistory(CourseHistory courseHistory) {
        this.courseHistory = courseHistory;
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
        TimeCourseLog timeCourseLog = (TimeCourseLog) o;
        if (timeCourseLog.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), timeCourseLog.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TimeCourseLog{" +
            "id=" + getId() +
            ", timespent=" + getTimespent() +
            ", recorddate='" + getRecorddate() + "'" +
            "}";
    }
}
