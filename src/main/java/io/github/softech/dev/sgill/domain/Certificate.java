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
 * A Certificate.
 */
@Entity
@Table(name = "certificate")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "certificate")
public class Certificate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "jhi_timestamp")
    private Instant timestamp;

    @Column(name = "is_emailed")
    private Boolean isEmailed;

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

    public Instant getTimestamp() {
        return timestamp;
    }

    public Certificate timestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean isIsEmailed() {
        return isEmailed;
    }

    public Certificate isEmailed(Boolean isEmailed) {
        this.isEmailed = isEmailed;
        return this;
    }

    public void setIsEmailed(Boolean isEmailed) {
        this.isEmailed = isEmailed;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Certificate customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CourseHistory getCourseHistory() {
        return courseHistory;
    }

    public Certificate courseHistory(CourseHistory courseHistory) {
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
        Certificate certificate = (Certificate) o;
        if (certificate.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), certificate.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Certificate{" +
            "id=" + getId() +
            ", timestamp='" + getTimestamp() + "'" +
            ", isEmailed='" + isIsEmailed() + "'" +
            "}";
    }
}
