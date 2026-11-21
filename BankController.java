package edu.college.smartbank.controller;
import edu.college.smartbank.dto.ApiDtos.*;
import edu.college.smartbank.service.BankingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BankController {
 private final BankingService service;
 public BankController(BankingService service){this.service=service;}
 private long user(HttpServletRequest req){String h=req.getHeader("Authorization");if(h==null||!h.startsWith("Bearer "))throw new IllegalArgumentException("Please sign in.");return service.customerId(h.substring(7));}
 @PostMapping("/auth/register") public AuthResponse register(@RequestBody RegisterRequest r){return service.register(r);}
 @PostMapping("/auth/login") public AuthResponse login(@RequestBody LoginRequest r){return service.login(r);}
 @PostMapping("/auth/logout") public Message logout(HttpServletRequest req){String h=req.getHeader("Authorization");if(h!=null&&h.startsWith("Bearer "))service.logout(h.substring(7));return new Message("Signed out.");}
 @GetMapping("/dashboard") public DashboardView dashboard(HttpServletRequest req){return service.dashboard(user(req));}
 @PostMapping("/deposit") public Message deposit(@RequestBody MoneyRequest r,HttpServletRequest req){service.deposit(user(req),r);return new Message("Deposit completed.");}
 @PostMapping("/withdraw") public Message withdraw(@RequestBody MoneyRequest r,HttpServletRequest req){service.withdraw(user(req),r);return new Message("Withdrawal completed.");}
 @PostMapping("/transfer") public Message transfer(@RequestBody TransferRequest r,HttpServletRequest req){service.transfer(user(req),r);return new Message("Transfer completed.");}
 @GetMapping("/transactions") public List<TransactionView> transactions(@RequestParam String accountNumber,HttpServletRequest req){return service.transactions(user(req),accountNumber);}
}
