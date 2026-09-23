package com.recipebox.recipe;

public record RecipeView( Long id, String title, String cuisine, int servings, boolean vegetarian,
        Long cookId, String cookName ) {

    static RecipeView of( Recipe recipe ) {
        return new RecipeView( recipe.getId(), recipe.getTitle(), recipe.getCuisine(),
                recipe.getServings(), recipe.isVegetarian(), recipe.getCook().getId(),
                recipe.getCook().getName() );
    }
}
