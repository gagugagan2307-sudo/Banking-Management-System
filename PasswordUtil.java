package edu.college.smartbank.service;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
public final class PasswordUtil {
 private PasswordUtil(){}
 public static String hash(String password){
  try {
   byte[] salt=new byte[16]; new SecureRandom().nextBytes(salt);
   PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),salt,120000,256);
   byte[] key=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
   return Base64.getEncoder().encodeToString(salt)+":"+Base64.getEncoder().encodeToString(key);
  } catch(Exception e){throw new IllegalStateException("Password hashing failed",e);}
 }
 public static boolean verify(String password,String stored){
  try {
   String[] p=stored.split(":"); byte[] salt=Base64.getDecoder().decode(p[0]);
   PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),salt,120000,256);
   byte[] key=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
   return java.security.MessageDigest.isEqual(key,Base64.getDecoder().decode(p[1]));
  } catch(Exception e){return false;}
 }
}
