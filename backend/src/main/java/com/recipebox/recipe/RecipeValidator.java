package com.recipebox.recipe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.recipebox.common.ValidationException;

/** The Add recipe rules. The front end mirrors every one of them. */
@Component
public class RecipeValidator {

    public static final List<String> CUISINES =
            List.of( "Italian", "Japanese", "Indian", "Mexican", "French" );

    public void validate( String title, String cuisine, Long cookId, Integer servings ) {
        Map<String, String> errors = new LinkedHashMap<>();

        if ( isBlank( title ) ) {
            errors.put( "title", "Title is required" );
        }
        if ( isBlank( cuisine ) || !CUISINES.contains( cuisine ) ) {
            errors.put( "cuisine", "Choose a cuisine" );
        }
        if ( cookId == null ) {
            errors.put( "cookId", "Choose a cook" );
        }
        if ( servings == null || servings < 1 || servings > 24 ) {
            errors.put( "servings", "Servings must be between 1 and 24" );
        }

        if ( !errors.isEmpty() ) {
            throw new ValidationException( errors );
        }
    }

    static boolean isBlank( String value ) {
        return value == null || value.trim().isEmpty();
    }
}
