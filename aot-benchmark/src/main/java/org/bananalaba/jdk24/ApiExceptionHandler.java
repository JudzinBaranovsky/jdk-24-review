package org.bananalaba.jdk24;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorDto> map(DataNotFoundException e) {
        var body = new ErrorDto(e.getMessage());
        return ResponseEntity.status(404).body(body);
    }

}
