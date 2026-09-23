package com.recipebox.cook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.recipebox.mail.EmailJobRepository;
import com.recipebox.testsupport.ApiTestSupport;

/** The cooks area - the behaviour the graded Playwright task tests through the UI. */
@SuppressWarnings( { "rawtypes", "unchecked" } )
class CookIT extends ApiTestSupport {

    @Autowired
    private EmailJobRepository emailJobs;

    private HttpHeaders aliceJson() {
        HttpHeaders headers = authHeaders( bearer( "alice@example.com", "bookworm" ) );
        headers.setContentType( MediaType.APPLICATION_JSON );
        return headers;
    }

    @Test
    void theThreeSeededCooksAreListedWithTheirRecipeCounts() {
        List<Map> cooks = rest.exchange( url( "/api/cooks" ), HttpMethod.GET, new HttpEntity<>( aliceJson() ), List.class ).getBody();

        assertTrue( cooks.size() >= 3 );
        Map maria = cooks.stream().filter( c -> c.get( "name" ).equals( "Maria Rossi" ) ).findFirst().orElseThrow();
        assertEquals( 3, maria.get( "recipeCount" ) );
        assertEquals( "PRO", maria.get( "level" ) );
    }

    @Test
    void theDetailListsTheCooksRecipesByTitle() {
        Map ken = rest.exchange( url( "/api/cooks/2" ), HttpMethod.GET, new HttpEntity<>( aliceJson() ), Map.class ).getBody();

        assertEquals( "Ken Tanaka", ken.get( "name" ) );
        assertEquals( List.of( "Chicken Katsu Curry", "Miso Soup" ), ken.get( "recipes" ) );
    }

    @Test
    void addingACookQueuesAWelcomeEmailAndTheNewCookCanBeRemoved() {
        String email = "sam." + System.nanoTime() + "@example.com";
        ResponseEntity<Map> created = rest.postForEntity( url( "/api/cooks" ), new HttpEntity<>(
                Map.of( "name", "Sam Lee", "email", email, "level", "HOME" ), aliceJson() ), Map.class );

        assertEquals( HttpStatus.CREATED, created.getStatusCode() );
        assertEquals( 0, created.getBody().get( "recipeCount" ) );
        Number id = (Number) created.getBody().get( "id" );

        var job = emailJobs.findAll().stream().filter( j -> j.getRecipient().equals( email ) ).findFirst().orElseThrow();
        assertEquals( "Welcome to Recipe Box, Sam Lee", job.getSubject() );

        assertEquals( HttpStatus.NO_CONTENT, rest.exchange( url( "/api/cooks/" + id ), HttpMethod.DELETE,
                new HttpEntity<>( aliceJson() ), Void.class ).getStatusCode() );
        assertEquals( HttpStatus.NOT_FOUND, rest.exchange( url( "/api/cooks/" + id ), HttpMethod.GET,
                new HttpEntity<>( aliceJson() ), Map.class ).getStatusCode() );
    }

    @Test
    void aCookWithRecipesCannotBeRemoved() {
        ResponseEntity<Map> response = rest.exchange( url( "/api/cooks/1" ), HttpMethod.DELETE,
                new HttpEntity<>( aliceJson() ), Map.class );

        assertEquals( HttpStatus.CONFLICT, response.getStatusCode() );
        assertEquals( "Remove the cook's recipes first", response.getBody().get( "message" ) );
    }

    @Test
    void addCookValidationReportsEveryField() {
        ResponseEntity<Map> response = rest.postForEntity( url( "/api/cooks" ), new HttpEntity<>(
                Map.of( "name", "", "email", "sam", "level", "CHEF" ), aliceJson() ), Map.class );

        assertEquals( HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode() );
        Map errors = (Map) response.getBody().get( "errors" );
        assertEquals( "Name is required", errors.get( "name" ) );
        assertEquals( "Email must look like name@example.com", errors.get( "email" ) );
        assertEquals( "Choose a level", errors.get( "level" ) );
    }
}
