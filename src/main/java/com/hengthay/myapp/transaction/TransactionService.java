package com.hengthay.myapp.transaction;

import com.hengthay.myapp.account.AccountRepository;
import com.hengthay.myapp.auth.AuthService;
import com.hengthay.myapp.category.CategoryNotFoundException;
import com.hengthay.myapp.category.CategoryRepository;
import com.hengthay.myapp.user.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AuthService authService;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    public TransactionDto getTransactionById(UUID id) {
        var transaction = transactionRepository.findById(id).orElse(null);

        if(transaction == null)
            throw new TransactionNotFoundException();

        return transactionMapper.toDto(transaction);
    }

    public List<TransactionDto> getMyTransaction() {
        var user = authService.getCurrentUser();

        if(user == null) {
            throw new UserNotFoundException();
        }

        var transaction = transactionRepository.findByUserId(user.getId());

        if(transaction.isEmpty())
            throw new TransactionNotFoundException();

        return transaction.stream().map(transactionMapper::toDto).toList();
    }

    public List<TransactionDto> getTransactionByAccount(UUID accountId) {

        return transactionRepository
                .findAllByAccountId(accountId)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    public TransactionDto createTransaction(TransactionCreateRequest request) {
        var user = authService.getCurrentUser();

        if(user == null)
            throw new UserNotFoundException();


        // find account id
        var account = accountRepository.findById(request.getAccountId()).orElse(null);

        if(!account.getUser().getId().equals(user.getId())) {
            throw new AccountDeniedException();
        }

        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        // Category doesn't exist
        if(category == null)
            throw new CategoryNotFoundException();


        var transactionEntity = transactionMapper.toEntity(request);

        transactionEntity.setUser(user);
        transactionEntity.setAccount(account);
        transactionEntity.setCategory(category);

        var savedTransaction = transactionRepository.save(transactionEntity);

        // update account balance
        if(request.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(request.getAmount()));
        }else {
            account.setBalance(account.getBalance().subtract(request.getAmount()));
        }

        accountRepository.save(account);

        return transactionMapper.toDto(savedTransaction);
    }

    public TransactionDto updateTransaction(UUID id, TransactionUpdateRequest request) {
        var user = authService.getCurrentUser();

        if(user == null)
            throw new UserNotFoundException();


        var transaction = transactionRepository.findById(id).orElse(null);

        if(transaction == null)
            throw new TransactionNotFoundException();

        // ensure the user owns this transaction
        if(!transaction.getUser().getId().equals(user.getId())) {
            throw new AccountDeniedException();
        }

        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null)
            throw new CategoryNotFoundException();


        // get account from transaction
        var account = transaction.getAccount();
        // Revert the old transaction from the account balance
        if(transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        }

        transaction.setCategory(category);
        transactionMapper.update(request, transaction);

        // Update new transaction
        if (transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        }

        accountRepository.save(account);
        var savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toDto(savedTransaction);
    }

    public void deleteTransaction(UUID id) {
        var user = authService.getCurrentUser();

        if(user == null)
            throw new UserNotFoundException();


        var transaction = transactionRepository.findById(id).orElse(null);

        if(transaction == null)
            throw new TransactionNotFoundException();

        // ensure the user owns this transaction
        if(!transaction.getUser().getId().equals(user.getId())) {
            throw new AccountDeniedException();
        }

        // get account from transaction
        var account = transaction.getAccount();
        // Revert the old transaction from the account balance
        if(transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        }
        accountRepository.save(account);
        transactionRepository.delete(transaction);
    }
}
