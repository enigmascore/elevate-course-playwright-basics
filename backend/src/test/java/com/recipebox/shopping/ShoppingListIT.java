package com.recipebox.shopping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.recipebox.testsupport.ApiTestSupport;

@SuppressWarnings( { "rawtypes", "unchecked" } )
class ShoppingListIT extends ApiTestSupport {

    @Test
    void theFiveSeededItemsComeBackAfterTheDeliberateDelay() {
        HttpEntity<Void> asBob = new HttpEntity<>( authHeaders( bearer( "bob@example.com", "pageturner" ) ) );

        long started = System.currentTimeMillis();
        ResponseEntity<List> response = rest.exchange( url( "/api/shopping-list" ), HttpMethod.GET, asBob, List.class );
        long elapsed = System.currentTimeMillis() - started;

        assertEquals( HttpStatus.OK, response.getStatusCode() );
        List<Map> items = response.getBody();
        assertEquals( 5, items.size() );
        assertEquals( "Chickpeas", items.get( 0 ).get( "item" ) );
        assertTrue( elapsed >= 50, "expected the configured delay, took " + elapsed + "ms" );
    }
}
