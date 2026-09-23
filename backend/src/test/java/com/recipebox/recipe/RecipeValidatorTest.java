package com.recipebox.recipe;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.recipebox.common.ValidationException;

class RecipeValidatorTest {

    private final RecipeValidator validator = new RecipeValidator();

    @Test
    void aCompleteRecipePasses() {
        assertDoesNotThrow( () -> validator.validate( "Tacos", "Mexican", 1L, 4 ) );
    }

    @Test
    void titleCuisineAndCookAreRequired() {
        ValidationException e = assertThrows( ValidationException.class,
                () -> validator.validate( "", "Martian", null, 4 ) );

        assertEquals( "Title is required", e.getErrors().get( "title" ) );
        assertEquals( "Choose a cuisine", e.getErrors().get( "cuisine" ) );
        assertEquals( "Choose a cook", e.getErrors().get( "cookId" ) );
    }

    @Test
    void servingsMustBeSensible() {
        assertEquals( "Servings must be between 1 and 24", assertThrows( ValidationException.class,
                () -> validator.validate( "Tacos", "Mexican", 1L, 0 ) ).getErrors().get( "servings" ) );
        assertEquals( "Servings must be between 1 and 24", assertThrows( ValidationException.class,
                () -> validator.validate( "Tacos", "Mexican", 1L, null ) ).getErrors().get( "servings" ) );
    }
}
