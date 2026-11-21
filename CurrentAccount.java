package edu.college.smartbank.model;
import java.math.BigDecimal;
public class CurrentAccount extends BankAccount {
 public CurrentAccount(long id,String no,BigDecimal balance){super(id,no,"CURRENT",balance);}
 @Override public BigDecimal minimumBalance(){return new BigDecimal("0.00");}
}
