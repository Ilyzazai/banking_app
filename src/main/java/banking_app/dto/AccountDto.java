package banking_app.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

//@Data
//@AllArgsConstructor
//public class AccountDto {
//
//    private Long id;
//    private String accountHolderName;
//    private double balance;
//
//
//}
//they are final
public record AccountDto(
        Long id,
        String accountHolderName,
        double balance) {
}
