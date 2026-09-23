package com.recipebox.cook;

import java.util.List;

/** A cook as the API returns it; the detail endpoint adds the recipe titles. */
public record CookView( Long id, String name, String email, String level, int recipeCount,
        List<String> recipes ) {
}
