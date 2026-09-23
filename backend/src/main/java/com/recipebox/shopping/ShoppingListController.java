package com.recipebox.shopping;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Answers SLOWLY on purpose ( app.shopping-list.delay-ms ) - something to wait for. */
@RestController
public class ShoppingListController {

    private final ShoppingItemRepository repository;
    private final long delayMs;

    public ShoppingListController( ShoppingItemRepository repository,
            @Value( "${app.shopping-list.delay-ms}" ) long delayMs ) {
        this.repository = repository;
        this.delayMs = delayMs;
    }

    @GetMapping( "/api/shopping-list" )
    public List<ShoppingItem> list() throws InterruptedException {
        Thread.sleep( delayMs );
        return repository.findAllByOrderByItemAsc();
    }
}
