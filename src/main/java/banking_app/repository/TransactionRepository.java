package banking_app.repository;

import banking_app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    //tells database findby account id and order by timestamp
    List<Transaction> findByAccountIdOrderByTimestampDesc(Long accountId);

}
