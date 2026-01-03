package banking_app.service.impl;

import banking_app.dto.AccountDto;
import banking_app.dto.TransactionDto;
import banking_app.dto.TransferFundDto;
import banking_app.entity.Account;
import banking_app.entity.Transaction;
import banking_app.exception.AcccountException;
import banking_app.mapper.AccountMapper;
import banking_app.repository.AccountRepository;
import banking_app.repository.TransactionRepository;
import banking_app.service.AccountService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImplementation implements AccountService {

    private static final String TRANSACTION_TYPE_DEPOSIT="DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAW="WITHDRAW";
    private static final String TRANSACTION_TYPE_TRANSFER="TRANSFER";

    public AccountServiceImplementation(AccountRepository accountRepository,
                                        TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository= transactionRepository;
    }

    //injection to use methods of datajpa
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;




    //mapper class is created for to prevent boilercode  and to exchange data between two classes Account and AccountDto
    //create an Accoun in the database and return to RESTAPI Controller
    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        System.out.println("Accountserviceimplementation method has called when the values are in accountdto" + accountDto.toString());
        Account account = AccountMapper.mapToAccount(accountDto);
        System.out.println("Accountserviceimplementation method values added into Account and vlaues" + account);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    //Get user data by id and return to the RESTAPI Controller
    @Override
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AcccountException("Account does not exist"));
        AccountDto accountDto = AccountMapper.mapToAccountDto(account);
        return accountDto;
    }

    //Deposit amount in the database and return back to the REST API Controller
    @Override
    public AccountDto deposit(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AcccountException("Account does not exist"));
        double total = account.getBalance() + amount;
        account.setBalance(total);
        Account savedAccount= accountRepository.save(account);

        Transaction transaction=new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

       return AccountMapper.mapToAccountDto(savedAccount);
    }

    //withdraw or - money from the database
    @Override
    public AccountDto withdraw(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AcccountException("Account does not exist"));
        if (account.getBalance() < amount){
            throw new RuntimeException("insuficient amount");
        }
        double total=account.getBalance() - amount;
        account.setBalance(total);
        Account savedAccount=accountRepository.save(account);

        Transaction transaction=new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    //get all accounts from the database
    @Override
    public List<AccountDto> getAllAccounts() {
       List<Account> accounts=accountRepository.findAll();
       return accounts.stream().map((account) -> AccountMapper.mapToAccountDto(account))
               .collect(Collectors.toList());
    }
    // delete the method with no return type
    @Override
    public void delete(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AcccountException("Account does not exist"));
       accountRepository.deleteById(id);
    }
    //transfer fund from one account to another account
    @Override
    public void transferFund(TransferFundDto transferFundDto) {
        //retrive the account from which we send the amount
        Account fromAccount = accountRepository
                .findById(transferFundDto.fromAccountId())//getting id from transferfunddto record class and check in the db
                .orElseThrow(() -> new AcccountException("Account does not exist"));

        //retrive the account to which we send the amount
        Account toAccount = accountRepository
                .findById(transferFundDto.toAccountId())
                .orElseThrow(()-> new AcccountException("Account does not exist"));

        //validation
        if(fromAccount.getBalance() < transferFundDto.amount())
        {
            throw new RuntimeException("insufficient amount");
        }

        //debit or minus the amount fromAccount object
       fromAccount.setBalance( fromAccount.getBalance()- transferFundDto.amount() );

       //credit or add the amount toAccount object
        toAccount.setBalance(toAccount.getBalance()+ transferFundDto.amount());

        accountRepository.save(toAccount);
        accountRepository.save(fromAccount);

        Transaction transaction=new Transaction();
        transaction.setAccountId(transferFundDto.fromAccountId());
        transaction.setAmount(transferFundDto.amount());
        transaction.setTransactionType(TRANSACTION_TYPE_TRANSFER);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

    }

    @Override
    public List<TransactionDto> getAccountTransaction(Long accountId) {
        List<Transaction> transactions =transactionRepository
                .findByAccountIdOrderByTimestampDesc(accountId);

        return transactions.stream().map((transaction)->convertEntitytoDto(transaction))
                .collect(Collectors.toList());
    }
    private TransactionDto convertEntitytoDto(Transaction transaction){
        return new TransactionDto(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getTimestamp()
        );
    }
}
