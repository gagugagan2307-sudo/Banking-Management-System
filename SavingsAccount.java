package edu.college.smartbank.model;
import java.math.BigDecimal;
public class SavingsAccount extends BankAccount {
 public SavingsAccount(long id,String no,BigDecimal balance){super(id,no,"SAVINGS",balance);}
 @Override public BigDecimal minimumBalance(){return new BigDecimal("0.00");}
}
