package com.recipebox.recipe;

import com.recipebox.cook.Cook;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table( name = "recipes" )
public class Recipe {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "recipes_seq" )
    @SequenceGenerator( name = "recipes_seq", sequenceName = "recipes_seq", allocationSize = 1 )
    private Long id;

    @Column( nullable = false )
    private String title;

    @Column( nullable = false )
    private String cuisine;

    @Column( nullable = false )
    private int servings;

    @Column( nullable = false )
    private boolean vegetarian;

    /** every recipe BELONGS to a cook */
    @ManyToOne( fetch = FetchType.LAZY, optional = false )
    @JoinColumn( name = "cook_id", nullable = false )
    private Cook cook;

    protected Recipe() {
    }

    public Recipe( String title, String cuisine, int servings, boolean vegetarian, Cook cook ) {
        this.title = title;
        this.cuisine = cuisine;
        this.servings = servings;
        this.vegetarian = vegetarian;
        this.cook = cook;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getCuisine() { return cuisine; }
    public int getServings() { return servings; }
    public boolean isVegetarian() { return vegetarian; }
    public Cook getCook() { return cook; }
}
