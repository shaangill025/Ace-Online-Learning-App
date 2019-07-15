package io.github.softech.dev.sgill.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Servicelist.
 */
@Entity
@Table(name = "servicelist")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "servicelist")
public class Servicelist implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "company")
    private String company;

    @Column(name = "url")
    private String url;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "areas")
    private String areas;

    @Column(name = "speciality")
    private String speciality;

    @Column(name = "trades")
    private String trades;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Customer customer;

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

    public Servicelist name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public Servicelist company(String company) {
        this.company = company;
        return this;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUrl() {
        return url;
    }

    public Servicelist url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhone() {
        return phone;
    }

    public Servicelist phone(String phone) {
        this.phone = phone;
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public Servicelist email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAreas() {
        return areas;
    }

    public Servicelist areas(String areas) {
        this.areas = areas;
        return this;
    }

    public void setAreas(String areas) {
        this.areas = areas;
    }

    public String getSpeciality() {
        return speciality;
    }

    public Servicelist speciality(String speciality) {
        this.speciality = speciality;
        return this;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getTrades() {
        return trades;
    }

    public Servicelist trades(String trades) {
        this.trades = trades;
        return this;
    }

    public void setTrades(String trades) {
        this.trades = trades;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Servicelist customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
        Servicelist servicelist = (Servicelist) o;
        if (servicelist.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), servicelist.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Servicelist{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", company='" + getCompany() + "'" +
            ", url='" + getUrl() + "'" +
            ", phone='" + getPhone() + "'" +
            ", email='" + getEmail() + "'" +
            ", areas='" + getAreas() + "'" +
            ", speciality='" + getSpeciality() + "'" +
            ", trades='" + getTrades() + "'" +
            "}";
    }
}
