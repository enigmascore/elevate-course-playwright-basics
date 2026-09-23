package com.recipebox.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.recipebox.testsupport.ApiTestSupport;

@SuppressWarnings( "rawtypes" )
class AuthIT extends ApiTestSupport {

    @Test
    void aliceLogsInAndGetsAToken() {
        ResponseEntity<Map> response = rest.postForEntity( url( "/api/auth/login" ),
                json( Map.of( "email", "alice@example.com", "password", "bookworm" ) ), Map.class );

        assertEquals( HttpStatus.OK, response.getStatusCode() );
        assertEquals( "Alice", response.getBody().get( "name" ) );
        assertNotNull( response.getBody().get( "token" ) );
    }

    @Test
    void aWrongPasswordIs401WithTheWrongCredentialsCode() {
        ResponseEntity<Map> response = rest.postForEntity( url( "/api/auth/login" ),
                json( Map.of( "email", "alice@example.com", "password", "nope" ) ), Map.class );

        assertEquals( HttpStatus.UNAUTHORIZED, response.getStatusCode() );
        assertEquals( "WRONG_CREDENTIALS", response.getBody().get( "code" ) );
    }
}
