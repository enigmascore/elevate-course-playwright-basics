package com.recipebox.shopping;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table( name = "shopping_items" )
public class ShoppingItem {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "shopping_items_seq" )
    @SequenceGenerator( name = "shopping_items_seq", sequenceName = "shopping_items_seq", allocationSize = 1 )
    private Long id;

    @Column( nullable = false )
    private String item;

    @Column( nullable = false )
    private String quantity;

    @Column( nullable = false )
    private String forRecipe;

    protected ShoppingItem() {
    }

    public Long getId() { return id; }
    public String getItem() { return item; }
    public String getQuantity() { return quantity; }
    public String getForRecipe() { return forRecipe; }
}
