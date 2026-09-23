package com.recipebox.cook;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recipebox.common.ConflictException;
import com.recipebox.common.NotFoundException;
import com.recipebox.mail.MailService;
import com.recipebox.recipe.Recipe;
import com.recipebox.recipe.RecipeRepository;

@Service
public class CookService {

    private final CookRepository cookRepository;
    private final RecipeRepository recipeRepository;
    private final CookValidator validator;
    private final MailService mailService;

    public CookService( CookRepository cookRepository, RecipeRepository recipeRepository,
            CookValidator validator, MailService mailService ) {
        this.cookRepository = cookRepository;
        this.recipeRepository = recipeRepository;
        this.validator = validator;
        this.mailService = mailService;
    }

    @Transactional( readOnly = true )
    public List<CookView> list() {
        return cookRepository.findAllByOrderByNameAsc().stream().map( this::view ).toList();
    }

    @Transactional( readOnly = true )
    public CookView get( Long id ) {
        return view( find( id ) );
    }

    /** adds the cook and queues them a WELCOME email */
    @Transactional
    public CookView add( String name, String email, String level ) {
        validator.validate( name, email, level );
        Cook cook = cookRepository.save(
                new Cook( name.trim(), email.trim().toLowerCase(), Cook.Level.valueOf( level ) ) );
        mailService.sendWelcome( cook.getEmail(), cook.getName() );
        return view( cook );
    }

    /** a cook who still has recipes cannot be removed */
    @Transactional
    public void remove( Long id ) {
        Cook cook = find( id );

        if ( !recipeRepository.findByCookIdOrderByTitleAsc( id ).isEmpty() ) {
            throw new ConflictException( "Remove the cook's recipes first" );
        }

        cookRepository.delete( cook );
    }

    private Cook find( Long id ) {
        return cookRepository.findById( id )
                .orElseThrow( () -> new NotFoundException( "No cook with id " + id ) );
    }

    private CookView view( Cook cook ) {
        List<String> recipes = recipeRepository.findByCookIdOrderByTitleAsc( cook.getId() )
                .stream().map( Recipe::getTitle ).toList();
        return new CookView( cook.getId(), cook.getName(), cook.getEmail(),
                cook.getLevel().name(), recipes.size(), recipes );
    }
}
