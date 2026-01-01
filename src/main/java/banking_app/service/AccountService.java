package banking_app.service;

import banking_app.dto.AccountDto;

import java.util.List;

public interface AccountService {

    //instead of using direct entity better to use DTO

    AccountDto createAccount(AccountDto accountDto);
    AccountDto getAccountById(Long id);
    AccountDto deposit(Long id , double amount);
    AccountDto withdraw(Long id , double amount);
    List<AccountDto> getAllAccounts();
    void delete(Long id);
}
