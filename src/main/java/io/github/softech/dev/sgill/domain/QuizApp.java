package io.github.softech.dev.sgill.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A QuizApp.
 */
@Entity
@Table(name = "quiz_app")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "quizapp")
public class QuizApp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Quiz quiz;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Section currSection;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Section newSection;


    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public QuizApp quiz(Quiz quiz) {
        this.quiz = quiz;
        return this;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Section getCurrSection() {
        return currSection;
    }

    public QuizApp currSection(Section section) {
        this.currSection = section;
        return this;
    }

    public void setCurrSection(Section section) {
        this.currSection = section;
    }

    public Section getNewSection() {
        return newSection;
    }

    public QuizApp newSection(Section section) {
        this.newSection = section;
        return this;
    }

    public void setNewSection(Section section) {
        this.newSection = section;
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
        QuizApp quizApp = (QuizApp) o;
        if (quizApp.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), quizApp.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "QuizApp{" +
            "id=" + getId() +
            "}";
    }
}
