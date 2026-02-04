package com.opslife.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException ex) {
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(Map.of("error", ex.getMessage()));
  }

  // IMPORTANT: SSE connections can timeout; don't try to write JSON on a text/event-stream response.
  @ExceptionHandler(AsyncRequestTimeoutException.class)
  public ResponseEntity<Void> asyncTimeout(AsyncRequestTimeoutException ex) {
    // no body (avoids converter errors)
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> serverError(Exception ex) {
    // keep it simple for now
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(Map.of("error", "internal_error"));
  }
}
