package com.zipjung.backend.config;

import com.zipjung.backend.exception.DuplicateEmailException;
import com.zipjung.backend.exception.DuplicateUsernameException;
import com.zipjung.backend.exception.InvaildTokenException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {
    // JWT
    @ExceptionHandler(InvaildTokenException.class)
    public void handlerInvailedToken(HttpServletResponse response, InvaildTokenException ex) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
    }


    // Login
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> handleDuplicateEmailException(DuplicateEmailException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // IM_USED는 사용할 수 없음 = 508 : 서버 웹 확장을 위한 코드
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<String> handleDuplicateUsernameException(DuplicateUsernameException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }


}
