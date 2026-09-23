package com.recipebox.cook;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.recipebox.common.ValidationException;

class CookValidatorTest {

    private final CookValidator validator = new CookValidator();

    @Test
    void aCompleteCookPasses() {
        assertDoesNotThrow( () -> validator.validate( "Sam", "sam@example.com", "HOME" ) );
    }

    @Test
    void everyMissingFieldIsReportedAtOnce() {
        ValidationException e = assertThrows( ValidationException.class,
                () -> validator.validate( " ", "", null ) );

        assertEquals( "Name is required", e.getErrors().get( "name" ) );
        assertEquals( "Email is required", e.getErrors().get( "email" ) );
        assertEquals( "Choose a level", e.getErrors().get( "level" ) );
    }

    @Test
    void emailMustLookLikeAnEmail() {
        ValidationException e = assertThrows( ValidationException.class,
                () -> validator.validate( "Sam", "sam-at-example", "PRO" ) );

        assertEquals( "Email must look like name@example.com", e.getErrors().get( "email" ) );
        assertEquals( 1, e.getErrors().size() );
    }

    @Test
    void levelMustBeHomeOrPro() {
        ValidationException e = assertThrows( ValidationException.class,
                () -> validator.validate( "Sam", "sam@example.com", "CHEF" ) );

        assertEquals( "Choose a level", e.getErrors().get( "level" ) );
    }
}
