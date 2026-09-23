package com.recipebox.cook;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/cooks" )
public class CookController {

    public record AddCookRequest( String name, String email, String level ) {
    }

    private final CookService cookService;

    public CookController( CookService cookService ) {
        this.cookService = cookService;
    }

    @GetMapping
    public List<CookView> list() {
        return cookService.list();
    }

    @GetMapping( "/{id}" )
    public CookView get( @PathVariable Long id ) {
        return cookService.get( id );
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    public CookView add( @RequestBody AddCookRequest request ) {
        return cookService.add( request.name(), request.email(), request.level() );
    }

    @DeleteMapping( "/{id}" )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void remove( @PathVariable Long id ) {
        cookService.remove( id );
    }
}
