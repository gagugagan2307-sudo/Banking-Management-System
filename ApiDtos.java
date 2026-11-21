package edu.college.smartbank.dto;
import java.math.BigDecimal;
public final class ApiDtos {
 private ApiDtos(){}
 public record RegisterRequest(String fullName,String email,String phone,String password,String accountType,BigDecimal openingBalance){}
 public record LoginRequest(String email,String password){}
 public record AuthResponse(String token,String name,String email){}
 public record MoneyRequest(String accountNumber,BigDecimal amount,String description){}
 public record TransferRequest(String fromAccount,String toAccount,BigDecimal amount,String description){}
 public record AccountView(String accountNumber,String accountType,BigDecimal balance,String status){}
 public record TransactionView(String referenceNo,String transactionType,BigDecimal amount,String description,String createdAt){}
 public record DashboardView(String name,String email,java.util.List<AccountView> accounts){}
 public record Message(String message){}
}
