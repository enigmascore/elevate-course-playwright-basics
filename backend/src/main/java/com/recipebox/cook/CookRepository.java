package com.recipebox.cook;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CookRepository extends JpaRepository<Cook, Long> {

    List<Cook> findAllByOrderByNameAsc();
}
