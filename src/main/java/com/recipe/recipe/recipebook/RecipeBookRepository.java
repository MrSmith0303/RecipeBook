package com.recipe.recipe.recipebook;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeBookRepository extends JpaRepository<RecipeBookEntity, Long> {
    List<RecipeBookEntity> findByCreator(String creator);
}