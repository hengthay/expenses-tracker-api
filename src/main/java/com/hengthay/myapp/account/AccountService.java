package com.hengthay.myapp.account;

import com.hengthay.myapp.auth.AuthService;
import com.hengthay.myapp.user.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AuthService authService;

    public List<AccountDto> getAllAccounts() {
        return accountRepository
                .findAll()
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }


    public AccountDto getAccountById(UUID id) {
        var account = accountRepository.getAccountById(id).orElse(null);

        if(account == null)
            throw new UserAccountNotFoundException();

        return accountMapper.toDto(account);
    }

    public AccountDto getMyAccount() {
        var user = authService.getCurrentUser();

        if(user == null)
            throw new UserNotFoundException();

        var account = accountRepository.findByUserId(user.getId()).orElse(null);

        if(account == null)
            throw new UserAccountNotFoundException();

        return accountMapper.toDto(account);
    }

    public AccountDto registerAccount(AccountCreateRequest request) {
        var user = authService.getCurrentUser();

        if(user == null)
            throw new UserNotFoundException();

        var accountEntity = accountMapper.toEntity(request);

        accountEntity.setUser(user);

        var savedAccount = accountRepository.save(accountEntity);

        return accountMapper.toDto(savedAccount);
    }

    public AccountDto updateAccount(UUID id, RequestAccountUpdate request) {
        var account = accountRepository.getAccountById(id).orElse(null);

        if(account == null)
            throw new UserAccountNotFoundException();

        var user = authService.getCurrentUser();

        // to check only owner of this account can be update information
        if(user == null || !account.getUser().getId().equals(user.getId())) {
            throw new UserNotFoundException();
        }

        accountMapper.update(request, account);

        var savedAccount = accountRepository.save(account);

        return accountMapper.toDto(savedAccount);
    }

    public void deleteAccount(UUID id) {
        var account = accountRepository.getAccountById(id).orElse(null);

        if(account == null)
            throw new UserAccountNotFoundException();

        var user = authService.getCurrentUser();
        // to check only owner of this account can be update information
        if(user == null || !account.getUser().getId().equals(user.getId())) {
            throw new UserNotFoundException();
        }

        if(!account.getTransactions().isEmpty()) {
            throw new AccountDeleteException("Cannot delete account because it contains active transactions. Delete the transactions first.");

//                    ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("Cannot delete account because it contains active transactions. Delete the transactions first.");
        }

        accountRepository.delete(account);
    }
}
