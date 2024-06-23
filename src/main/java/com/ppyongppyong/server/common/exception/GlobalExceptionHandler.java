package com.ppyongppyong.server.common.exception;

import com.ppyongppyong.server.common.dto.StatusResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<StatusResponseDto.Error> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorMsg(), e);
        return ResponseEntity.status(e.getErrorMsg().getHttpStatus())
                .body(new StatusResponseDto.Error(e.getErrorMsg().getHttpStatus().value(), e.getErrorMsg().getMessage()));
    }

    @ExceptionHandler({BindException.class})
    public ResponseEntity<StatusResponseDto.Error> bindException(BindException ex) {
        String errorMessage = ex.getFieldError() != null ? ex.getFieldError().getDefaultMessage() : "Invalid request";
        log.error("BindException: {}", errorMessage, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new StatusResponseDto.Error(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<StatusResponseDto.Error> handleAll(final Exception ex) {
        log.error("Unhandled exception: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StatusResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
    }

    @ExceptionHandler(value = {IOException.class})
    public ResponseEntity<StatusResponseDto.Error> handleIOException(IOException ex) {
        log.error("IOException: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StatusResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "I/O error occurred"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StatusResponseDto.Error> validationException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        String errorMessage = errors.isEmpty() ? "Validation error" : errors.get(0);
        log.error("MethodArgumentNotValidException: {}", errorMessage, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new StatusResponseDto.Error(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }
}