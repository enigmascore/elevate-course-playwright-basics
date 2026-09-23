package com.recipebox.recipe;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/recipes" )
public class RecipeController {

    /** every field nullable, so a missing one reaches the VALIDATOR ( a 422 ), not the JSON parser */
    public record AddRecipeRequest( String title, String cuisine, Long cookId, Integer servings,
            Boolean vegetarian ) {
    }

    private final RecipeService recipeService;

    public RecipeController( RecipeService recipeService ) {
        this.recipeService = recipeService;
    }

    /** GET /api/recipes?q=pasta - a REAL request to intercept */
    @GetMapping
    public List<RecipeView> list( @RequestParam( required = false ) String q ) {
        return recipeService.list( q );
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    public RecipeView add( @RequestBody AddRecipeRequest request ) {
        return recipeService.add( request.title(), request.cuisine(), request.cookId(),
                request.servings(), Boolean.TRUE.equals( request.vegetarian() ) );
    }

    @DeleteMapping( "/{id}" )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void delete( @PathVariable Long id ) {
        recipeService.delete( id );
    }
}
