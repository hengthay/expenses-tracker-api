package com.hengthay.myapp.repository;

import com.hengthay.myapp.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query("select a from Account a where a.id=:id")
    Optional<Account> getAccountById(UUID id);
}
