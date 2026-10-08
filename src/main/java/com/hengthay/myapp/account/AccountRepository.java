package com.hengthay.myapp.account;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    @EntityGraph(attributePaths = "user")
    List<Account> findAll();

    @EntityGraph(attributePaths = "user")
    @Query("select a from Account a where a.id=:id")
    Optional<Account> getAccountById(UUID id);

    Optional<Account> findByUserId(Long userId);
}
