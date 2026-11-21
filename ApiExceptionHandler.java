package edu.college.smartbank.controller;
import edu.college.smartbank.dto.ApiDtos.Message;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(IllegalArgumentException.class)
 public ResponseEntity<Message> badRequest(IllegalArgumentException e){return ResponseEntity.badRequest().body(new Message(e.getMessage()));}
 @ExceptionHandler(Exception.class)
 public ResponseEntity<Message> serverError(Exception e){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Message("Something went wrong. Check server logs."));}
}
