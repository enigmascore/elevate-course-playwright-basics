package com.recipebox.cook;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.recipebox.common.ValidationException;

/** The Add cook rules. The front end mirrors every one of them. */
@Component
public class CookValidator {

    static final Pattern EMAIL = Pattern.compile( "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$" );

    public void validate( String name, String email, String level ) {
        Map<String, String> errors = new LinkedHashMap<>();

        if ( isBlank( name ) ) {
            errors.put( "name", "Name is required" );
        }
        if ( isBlank( email ) ) {
            errors.put( "email", "Email is required" );
        }
        else if ( !EMAIL.matcher( email.trim() ).matches() ) {
            errors.put( "email", "Email must look like name@example.com" );
        }
        if ( isBlank( level ) || !( level.equals( "HOME" ) || level.equals( "PRO" ) ) ) {
            errors.put( "level", "Choose a level" );
        }

        if ( !errors.isEmpty() ) {
            throw new ValidationException( errors );
        }
    }

    static boolean isBlank( String value ) {
        return value == null || value.trim().isEmpty();
    }
}
