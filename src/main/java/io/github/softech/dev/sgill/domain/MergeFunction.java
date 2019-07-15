package io.github.softech.dev.sgill.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A MergeFunction.
 */
@Entity
@Table(name = "merge_function")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "mergefunction")
public class MergeFunction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "datetime", nullable = false)
    private LocalDate datetime;

    @NotNull
    @Column(name = "note", nullable = false)
    private String note;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("")
    private Customer tobeRemoved;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("")
    private Customer replacement;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDatetime() {
        return datetime;
    }

    public MergeFunction datetime(LocalDate datetime) {
        this.datetime = datetime;
        return this;
    }

    public void setDatetime(LocalDate datetime) {
        this.datetime = datetime;
    }

    public String getNote() {
        return note;
    }

    public MergeFunction note(String note) {
        this.note = note;
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Customer getTobeRemoved() {
        return tobeRemoved;
    }

    public MergeFunction tobeRemoved(Customer customer) {
        this.tobeRemoved = customer;
        return this;
    }

    public void setTobeRemoved(Customer customer) {
        this.tobeRemoved = customer;
    }

    public Customer getReplacement() {
        return replacement;
    }

    public MergeFunction replacement(Customer customer) {
        this.replacement = customer;
        return this;
    }

    public void setReplacement(Customer customer) {
        this.replacement = customer;
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
        MergeFunction mergeFunction = (MergeFunction) o;
        if (mergeFunction.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), mergeFunction.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MergeFunction{" +
            "id=" + getId() +
            ", datetime='" + getDatetime() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
