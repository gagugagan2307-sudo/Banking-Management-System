package edu.college.smartbank.model;
public class Customer {
 private final long id; private final String fullName, email, phone;
 public Customer(long id, String fullName, String email, String phone) {
  this.id=id; this.fullName=fullName; this.email=email; this.phone=phone;
 }
 public long getId(){return id;} public String getFullName(){return fullName;}
 public String getEmail(){return email;} public String getPhone(){return phone;}
}
