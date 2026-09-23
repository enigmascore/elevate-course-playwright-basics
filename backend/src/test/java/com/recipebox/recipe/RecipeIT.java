package com.recipebox.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.recipebox.testsupport.ApiTestSupport;

@SuppressWarnings( { "rawtypes", "unchecked" } )
class RecipeIT extends ApiTestSupport {

    private HttpHeaders aliceJson() {
        HttpHeaders headers = authHeaders( bearer( "alice@example.com", "bookworm" ) );
        headers.setContentType( MediaType.APPLICATION_JSON );
        return headers;
    }

    @Test
    void theSeededRecipesAreListedWithTheirCooks() {
        List<Map> recipes = rest.exchange( url( "/api/recipes" ), HttpMethod.GET,
                new HttpEntity<>( aliceJson() ), List.class ).getBody();

        assertTrue( recipes.size() >= 6 );
        Map carbonara = recipes.stream().filter( r -> r.get( "title" ).equals( "Spaghetti Carbonara" ) ).findFirst().orElseThrow();
        assertEquals( "Maria Rossi", carbonara.get( "cookName" ) );
        assertEquals( 1, carbonara.get( "cookId" ) );
    }

    @Test
    void searchMatchesTitleOrCuisine() {
        List<Map> byTitle = rest.exchange( url( "/api/recipes?q=miso" ), HttpMethod.GET, new HttpEntity<>( aliceJson() ), List.class ).getBody();
        List<Map> byCuisine = rest.exchange( url( "/api/recipes?q=italian" ), HttpMethod.GET, new HttpEntity<>( aliceJson() ), List.class ).getBody();

        assertEquals( 1, byTitle.size() );
        assertEquals( "Miso Soup", byTitle.get( 0 ).get( "title" ) );
        assertEquals( 3, byCuisine.size() );
    }

    @Test
    void addingThenDeletingARecipe() {
        String title = "Tacos al Pastor " + System.nanoTime();
        ResponseEntity<Map> created = rest.postForEntity( url( "/api/recipes" ), new HttpEntity<>(
                Map.of( "title", title, "cuisine", "Mexican", "cookId", 3, "servings", 4, "vegetarian", false ),
                aliceJson() ), Map.class );

        assertEquals( HttpStatus.CREATED, created.getStatusCode() );
        assertEquals( "Priya Nair", created.getBody().get( "cookName" ) );
        Number id = (Number) created.getBody().get( "id" );

        assertEquals( HttpStatus.NO_CONTENT, rest.exchange( url( "/api/recipes/" + id ), HttpMethod.DELETE,
                new HttpEntity<>( aliceJson() ), Void.class ).getStatusCode() );
        assertEquals( HttpStatus.NOT_FOUND, rest.exchange( url( "/api/recipes/" + id ), HttpMethod.DELETE,
                new HttpEntity<>( aliceJson() ), Map.class ).getStatusCode() );
    }

    @Test
    void addRecipeValidationReportsEveryField() {
        ResponseEntity<Map> response = rest.postForEntity( url( "/api/recipes" ), new HttpEntity<>(
                Map.of( "title", "", "cuisine", "Martian", "servings", 0 ), aliceJson() ), Map.class );

        assertEquals( HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode() );
        Map errors = (Map) response.getBody().get( "errors" );
        assertEquals( "Title is required", errors.get( "title" ) );
        assertEquals( "Choose a cuisine", errors.get( "cuisine" ) );
        assertEquals( "Choose a cook", errors.get( "cookId" ) );
        assertEquals( "Servings must be between 1 and 24", errors.get( "servings" ) );
    }
}
