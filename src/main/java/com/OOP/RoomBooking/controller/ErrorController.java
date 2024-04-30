package com.OOP.RoomBooking.controller;

import com.OOP.RoomBooking.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler({ CustomException.class })
    public ResponseEntity<Object> handleException(CustomException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("Error", ex.getMessage());
        return ResponseEntity.status(500).body(error);
    }
}