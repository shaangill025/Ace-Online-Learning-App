package io.github.softech.dev.sgill.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Choice.
 */
@Entity
@Table(name = "choice")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "choice")
public class Choice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @Column(name = "text_choice", nullable = false)
    private String textChoice;

    @Column(name = "isanswer")
    private Boolean isanswer;

    @ManyToOne
    @JsonIgnoreProperties("choices")
    private Question question;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextChoice() {
        return textChoice;
    }

    public Choice textChoice(String textChoice) {
        this.textChoice = textChoice;
        return this;
    }

    public void setTextChoice(String textChoice) {
        this.textChoice = textChoice;
    }

    public Boolean isIsanswer() {
        return isanswer;
    }

    public Choice isanswer(Boolean isanswer) {
        this.isanswer = isanswer;
        return this;
    }

    public void setIsanswer(Boolean isanswer) {
        this.isanswer = isanswer;
    }

    public Question getQuestion() {
        return question;
    }

    public Choice question(Question question) {
        this.question = question;
        return this;
    }

    public void setQuestion(Question question) {
        this.question = question;
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
        Choice choice = (Choice) o;
        if (choice.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), choice.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Choice{" +
            "id=" + getId() +
            ", textChoice='" + getTextChoice() + "'" +
            ", isanswer='" + isIsanswer() + "'" +
            "}";
    }
}
