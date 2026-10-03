package com.hengthay.myapp.repository;

import com.hengthay.myapp.entities.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @EntityGraph(attributePaths = "user")
    Optional<Transaction> findByUserId(Long userId);
}
