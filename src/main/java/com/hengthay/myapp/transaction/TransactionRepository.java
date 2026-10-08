package com.hengthay.myapp.transaction;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @EntityGraph(attributePaths = "user")
    List<Transaction> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"account", "category", "user"})
    List<Transaction> findAllByAccountId(UUID id);
}
