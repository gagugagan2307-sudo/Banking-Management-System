package edu.college.smartbank.model;
import java.math.BigDecimal;
public abstract class BankAccount {
 private final long id; private final String accountNumber; private final String accountType;
 private BigDecimal balance;
 protected BankAccount(long id,String accountNumber,String accountType,BigDecimal balance){
  this.id=id;this.accountNumber=accountNumber;this.accountType=accountType;this.balance=balance;
 }
 public long getId(){return id;} public String getAccountNumber(){return accountNumber;}
 public String getAccountType(){return accountType;} public BigDecimal getBalance(){return balance;}
 public void setBalance(BigDecimal balance){this.balance=balance;}
 public abstract BigDecimal minimumBalance();
}
