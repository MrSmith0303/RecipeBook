package com.recipe.recipe.recipebook;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeBookRepository extends JpaRepository<RecipeBookEntity, Long> {
}