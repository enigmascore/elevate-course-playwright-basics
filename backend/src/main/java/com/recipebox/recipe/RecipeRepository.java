package com.recipebox.recipe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findAllByOrderByTitleAsc();

    List<Recipe> findByCookIdOrderByTitleAsc( Long cookId );

    /** server-side search: title OR cuisine contains the query, case-insensitively */
    @Query( "select r from Recipe r where lower( r.title ) like lower( concat( '%', :q, '%' ) ) "
            + "or lower( r.cuisine ) like lower( concat( '%', :q, '%' ) ) order by r.title" )
    List<Recipe> search( @Param( "q" ) String q );
}
