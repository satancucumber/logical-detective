package com.example.logicaldetective.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(name = "literal_entity-graph", attributeNodes = @NamedAttributeNode("formulas"))
@Table(name = "literal")
public class Literal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "suspect")
    private Boolean suspect;
    @ManyToMany
    @JoinTable (name="formula_literal",
            joinColumns=@JoinColumn (name="literal_id"),
            inverseJoinColumns=@JoinColumn(name="formula_id"))
    @JsonIgnore
    private List<Formula> formulas;
    @JsonIgnore
    @Transient
    private Boolean negative;

    public Boolean getNegative() {
        return negative;
    }
    public void setNegative(Boolean negative) {
        this.negative = negative;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getSuspect() {
        return suspect;
    }

    public void setSuspect(Boolean suspect) {
        this.suspect = suspect;
    }

    public List<Formula> getFormulas() {
        return formulas;
    }

    public void setFormulas(List<Formula> formulas) {
        this.formulas = formulas;
    }
}
