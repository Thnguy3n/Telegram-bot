package com.milktea.bot.repository;

import com.milktea.bot.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByItemId(String itemId);
    List<Product> findByAvailableTrue();
    List<Product> findByCategoryAndAvailableTrue(String category);
}
