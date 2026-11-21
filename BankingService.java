package edu.college.smartbank.service;

import edu.college.smartbank.dto.ApiDtos.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BankingService {
 private final JdbcTemplate jdbc;
 private final Map<String,Long> sessions=new ConcurrentHashMap<>();
 private final SecureRandom random=new SecureRandom();
 public BankingService(JdbcTemplate jdbc){this.jdbc=jdbc;}

 public AuthResponse register(RegisterRequest r){
  if(blank(r.fullName())||blank(r.email())||r.password()==null||r.password().length()<8) throw new IllegalArgumentException("Enter name, email and password (minimum 8 characters).");
  if(r.openingBalance()==null||r.openingBalance().compareTo(BigDecimal.ZERO)<0) throw new IllegalArgumentException("Opening balance cannot be negative.");
  String type=(r.accountType()==null?"SAVINGS":r.accountType().toUpperCase(Locale.ROOT));
  if(!type.equals("SAVINGS")&&!type.equals("CURRENT")) throw new IllegalArgumentException("Choose Savings or Current account.");
  jdbc.update("INSERT INTO customers(full_name,email,phone,password_hash) VALUES(?,?,?,?)",
    r.fullName().trim(),r.email().trim().toLowerCase(),r.phone(),PasswordUtil.hash(r.password()));
  Long id=jdbc.queryForObject("SELECT id FROM customers WHERE email=?",Long.class,r.email().trim().toLowerCase());
  String accountNo=accountNumber();
  jdbc.update("INSERT INTO bank_accounts(account_number,customer_id,account_type,balance) VALUES(?,?,?,?)",
    accountNo,id,type,r.openingBalance());
  Long accountId=jdbc.queryForObject("SELECT id FROM bank_accounts WHERE account_number=?",Long.class,accountNo);
  addTransaction(accountId,"OPENING",r.openingBalance(),"Account opened");
  return new AuthResponse(newSession(id),r.fullName(),r.email().toLowerCase());
 }

 public AuthResponse login(LoginRequest r){
  var rows=jdbc.query("SELECT id,full_name,email,password_hash FROM customers WHERE email=?",
   (rs,n)->new Object[]{rs.getLong("id"),rs.getString("full_name"),rs.getString("email"),rs.getString("password_hash")},
   r.email().trim().toLowerCase());
  if(rows.isEmpty()||!PasswordUtil.verify(r.password(),(String)rows.get(0)[3])) throw new IllegalArgumentException("Invalid email or password.");
  Object[] c=rows.get(0); return new AuthResponse(newSession((Long)c[0]),(String)c[1],(String)c[2]);
 }
 public void logout(String token){sessions.remove(token);}
 public long customerId(String token){
  Long id=sessions.get(token); if(id==null) throw new IllegalArgumentException("Session expired. Please sign in again."); return id;
 }
 private String newSession(long id){byte[] b=new byte[32];random.nextBytes(b);String t=Base64.getUrlEncoder().withoutPadding().encodeToString(b);sessions.put(t,id);return t;}
 private String accountNumber(){return "SB"+(1000000000L+Math.abs(random.nextLong()%8999999999L));}
 public DashboardView dashboard(long customerId){
  String[] c=jdbc.queryForObject("SELECT full_name,email FROM customers WHERE id=?", (rs,n)->new String[]{rs.getString(1),rs.getString(2)},customerId);
  var a=jdbc.query("SELECT account_number,account_type,balance,status FROM bank_accounts WHERE customer_id=?",
   (rs,n)->new AccountView(rs.getString(1),rs.getString(2),rs.getBigDecimal(3),rs.getString(4)),customerId);
  return new DashboardView(c[0],c[1],a);
 }
 private long ownedAccount(String number,long customerId){
  var ids=jdbc.query("SELECT id FROM bank_accounts WHERE account_number=? AND customer_id=? AND status='ACTIVE'",
   (rs,n)->rs.getLong(1),number);
  if(ids.isEmpty()) throw new IllegalArgumentException("Account not found."); return ids.get(0);
 }
 @Transactional public void deposit(long customerId,MoneyRequest r){
  validateAmount(r.amount()); long id=ownedAccount(r.accountNumber(),customerId);
  jdbc.update("UPDATE bank_accounts SET balance=balance+? WHERE id=?",r.amount(),id);
  addTransaction(id,"DEPOSIT",r.amount(),safeDescription(r.description(),"Cash deposit"));
 }
 @Transactional public void withdraw(long customerId,MoneyRequest r){
  validateAmount(r.amount()); long id=ownedAccount(r.accountNumber(),customerId);
  int changed=jdbc.update("UPDATE bank_accounts SET balance=balance-? WHERE id=? AND balance>=?",r.amount(),id,r.amount());
  if(changed==0) throw new IllegalArgumentException("Insufficient balance.");
  addTransaction(id,"WITHDRAWAL",r.amount(),safeDescription(r.description(),"Cash withdrawal"));
 }
 @Transactional public void transfer(long customerId,TransferRequest r){
  validateAmount(r.amount());
  if(r.fromAccount().equals(r.toAccount())) throw new IllegalArgumentException("Choose two different accounts.");
  long from=ownedAccount(r.fromAccount(),customerId);
  var targets=jdbc.query("SELECT id FROM bank_accounts WHERE account_number=? AND status='ACTIVE'",(rs,n)->rs.getLong(1),r.toAccount());
  if(targets.isEmpty()) throw new IllegalArgumentException("Recipient account not found.");
  long to=targets.get(0);
  int changed=jdbc.update("UPDATE bank_accounts SET balance=balance-? WHERE id=? AND balance>=?",r.amount(),from,r.amount());
  if(changed==0) throw new IllegalArgumentException("Insufficient balance.");
  jdbc.update("UPDATE bank_accounts SET balance=balance+? WHERE id=?",r.amount(),to);
  String desc=safeDescription(r.description(),"Funds transfer");
  addTransaction(from,"TRANSFER_OUT",r.amount(),desc+" to "+r.toAccount());
  addTransaction(to,"TRANSFER_IN",r.amount(),desc+" from "+r.fromAccount());
 }
 public List<TransactionView> transactions(long customerId,String accountNo){
  long id=ownedAccount(accountNo,customerId);
  return jdbc.query("SELECT reference_no,transaction_type,amount,description,created_at FROM transactions WHERE account_id=? ORDER BY created_at DESC,id DESC",
   (rs,n)->new TransactionView(rs.getString(1),rs.getString(2),rs.getBigDecimal(3),rs.getString(4),rs.getTimestamp(5).toString()),id);
 }
 private void addTransaction(long accountId,String type,BigDecimal amount,String desc){
  jdbc.update("INSERT INTO transactions(reference_no,account_id,transaction_type,amount,description) VALUES(?,?,?,?,?)",
   "TX"+UUID.randomUUID().toString().replace("-","").substring(0,18).toUpperCase(),accountId,type,amount,desc);
 }
 private void validateAmount(BigDecimal a){if(a==null||a.compareTo(BigDecimal.ZERO)<=0)throw new IllegalArgumentException("Amount must be greater than zero.");}
 private boolean blank(String s){return s==null||s.isBlank();}
 private String safeDescription(String s,String fallback){return blank(s)?fallback:s.trim().substring(0,Math.min(200,s.trim().length()));}
}
