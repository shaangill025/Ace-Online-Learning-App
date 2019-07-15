package io.github.softech.dev.sgill.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A SectionHistory.
 */
@Entity
@Table(name = "section_history")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "sectionhistory")
public class SectionHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "startdate")
    private Instant startdate;

    @Column(name = "lastactivedate")
    private Instant lastactivedate;

    @Column(name = "watched")
    private Boolean watched;

    @Column(name = "stamp")
    private Integer stamp;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Customer customer;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Section section;

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

    public SectionHistory startdate(Instant startdate) {
        this.startdate = startdate;
        return this;
    }

    public void setStartdate(Instant startdate) {
        this.startdate = startdate;
    }

    public Instant getLastactivedate() {
        return lastactivedate;
    }

    public SectionHistory lastactivedate(Instant lastactivedate) {
        this.lastactivedate = lastactivedate;
        return this;
    }

    public void setLastactivedate(Instant lastactivedate) {
        this.lastactivedate = lastactivedate;
    }

    public Boolean isWatched() {
        return watched;
    }

    public SectionHistory watched(Boolean watched) {
        this.watched = watched;
        return this;
    }

    public void setWatched(Boolean watched) {
        this.watched = watched;
    }

    public Integer getStamp() {
        return stamp;
    }

    public SectionHistory stamp(Integer stamp) {
        this.stamp = stamp;
        return this;
    }

    public void setStamp(Integer stamp) {
        this.stamp = stamp;
    }

    public Customer getCustomer() {
        return customer;
    }

    public SectionHistory customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Section getSection() {
        return section;
    }

    public SectionHistory section(Section section) {
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
        SectionHistory sectionHistory = (SectionHistory) o;
        if (sectionHistory.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), sectionHistory.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SectionHistory{" +
            "id=" + getId() +
            ", startdate='" + getStartdate() + "'" +
            ", lastactivedate='" + getLastactivedate() + "'" +
            ", watched='" + isWatched() + "'" +
            ", stamp=" + getStamp() +
            "}";
    }
}
