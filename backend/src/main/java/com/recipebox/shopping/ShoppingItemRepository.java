package com.recipebox.shopping;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, Long> {

    List<ShoppingItem> findAllByOrderByItemAsc();
}
