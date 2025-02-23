package com.bside.mzoffice.common.exception;

import com.bside.mzoffice.common.domain.ResponseCode;
import com.bside.mzoffice.common.exception.customException.AuthLoginException;
import com.bside.mzoffice.common.exception.customException.ChatException;
import com.bside.mzoffice.common.exception.customException.ClovaAiException;
import com.bside.mzoffice.common.exception.customException.NotFoundException;
import com.bside.mzoffice.common.response.ServerResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.naming.AuthenticationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> resourceNotFoundException(HttpServletRequest request, Exception e) {
        log.error("Resource not found: {}", e.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class,
    })
    public ServerResponse<?> clientExceptionHandler(HttpServletRequest request, Exception e) {
        log.error("Client error: {}", e.getMessage());
        return ServerResponse.errorResponse(ResponseCode.valueOf(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ServerResponse<?> authenticationExceptionHandler(HttpServletRequest request, Exception e) {
        log.error("Authentication error: {}", e.getMessage());
        return ServerResponse.errorResponse(ResponseCode.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ServerResponse<?> accessDeniedExceptionHandler(HttpServletRequest request, Exception e) {
        log.error("Access denied: {}", e.getMessage());
        return ServerResponse.errorResponse(ResponseCode.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ServerResponse<?> methodArgumentNotValidExceptionHandler(
            HttpServletRequest request, MethodArgumentNotValidException e) {
        log.error(
                "Validation error: {}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ServerResponse.errorResponse(
                ResponseCode.valueOf(e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ServerResponse<?> serverExceptionHandler(HttpServletRequest request, Exception e) {
        log.error("Internal server error: {}", e.getMessage());
        return ServerResponse.errorResponse(ResponseCode.SERVER_ERROR);
    }

    // 사용자 정의 예외 클래스 생성
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthLoginException.class)
    public ServerResponse<?> authLoginExceptionHandler(HttpServletRequest request, Exception e) {
        log.error("Auth Login Error: {}", e.getMessage());
        return ServerResponse.errorResponse(ResponseCode.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ServerResponse<?> customerNotFoundExceptionHandler(HttpServletRequest request, NotFoundException e) {
        return ServerResponse.errorResponse(ResponseCode.valueOf(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ChatException.class)
    public ServerResponse<?> chatExceptionHandler(HttpServletRequest request, ChatException e) {
        return ServerResponse.errorResponse(ResponseCode.valueOf(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ClovaAiException.class)
    public ServerResponse<?> clovaAiExceptionHandler(HttpServletRequest request, ClovaAiException e) {
        return ServerResponse.errorResponse(ResponseCode.valueOf(e.getMessage()));
    }
}
