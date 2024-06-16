package com.dinesh.eorder.handler;

import com.dinesh.eorder.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handle (BusinessException ex) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ex.getMsg());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handle (EntityNotFoundException ex) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handle (MethodArgumentNotValidException ex) {
        var errors = new HashMap<String,String>();
        ex.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var fieldsName = ((FieldError)error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.put(fieldsName,errorMessage);
                });
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new CustomErrorResponse(errors));
    }
}
