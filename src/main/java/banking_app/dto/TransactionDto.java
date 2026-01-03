package banking_app.dto;

import java.time.LocalDateTime;

public record TransactionDto(
                Long id,
                Long accountId,
                double amount,
                String transationType,
                LocalDateTime timestamp){
}
