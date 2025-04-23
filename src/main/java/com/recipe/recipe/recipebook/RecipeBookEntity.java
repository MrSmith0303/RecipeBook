package com.recipe.recipe.recipebook;

import jakarta.persistence.*;

@Entity
@Table(name = "recipes")
public class RecipeBookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    private String ingredients;

    @Column(length = 2000)
    private String imageUrl;

    @Column(length = 2000)
    private String description;
    private String cook;
    @Column(name = "creator")
    private String creator;

    // Alapértelmezett konstruktor
    public RecipeBookEntity() {
    }

    // Paraméteres konstruktor
    public RecipeBookEntity(String name, String ingredients, String imageUrl, String description, String cook, String creator) {
        this.name = name;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
        this.description = description;
        this.cook = cook;
        this.creator = creator;
    }

    // Getterek és setterek
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

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCook() {
        return cook;
    }

    public void setCook(String cook) {
        this.cook = cook;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}