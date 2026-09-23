package com.recipebox.recipe;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recipebox.common.NotFoundException;
import com.recipebox.cook.Cook;
import com.recipebox.cook.CookRepository;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final CookRepository cookRepository;
    private final RecipeValidator validator;

    public RecipeService( RecipeRepository recipeRepository, CookRepository cookRepository,
            RecipeValidator validator ) {
        this.recipeRepository = recipeRepository;
        this.cookRepository = cookRepository;
        this.validator = validator;
    }

    @Transactional( readOnly = true )
    public List<RecipeView> list( String q ) {
        List<Recipe> recipes = ( q == null || q.trim().isEmpty() )
                ? recipeRepository.findAllByOrderByTitleAsc()
                : recipeRepository.search( q.trim() );
        return recipes.stream().map( RecipeView::of ).toList();
    }

    @Transactional
    public RecipeView add( String title, String cuisine, Long cookId, Integer servings,
            boolean vegetarian ) {
        validator.validate( title, cuisine, cookId, servings );
        Cook cook = cookRepository.findById( cookId )
                .orElseThrow( () -> new NotFoundException( "No cook with id " + cookId ) );
        return RecipeView.of( recipeRepository.save(
                new Recipe( title.trim(), cuisine, servings, vegetarian, cook ) ) );
    }

    @Transactional
    public void delete( Long id ) {
        Recipe recipe = recipeRepository.findById( id )
                .orElseThrow( () -> new NotFoundException( "No recipe with id " + id ) );
        recipeRepository.delete( recipe );
    }
}
