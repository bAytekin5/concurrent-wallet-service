package com.berkay.wallet.exception;

import com.berkay.wallet.exception.model.BaseApiError;
import com.berkay.wallet.exception.model.ExceptionDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<BaseApiError<String>> handleBaseException(BaseException ex, WebRequest request) {
        HttpStatus status = ex.getErrorMessage().getMessageType().getHttpStatus();
        String messageDetails = ex.getMessage();

        return createResponse(messageDetails, status, request);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<BaseApiError<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return createResponse(validationErrors, HttpStatus.BAD_REQUEST, request);
    }


    private <E> ResponseEntity<BaseApiError<E>> createResponse(E errorBody, HttpStatus status, WebRequest request) {
        ExceptionDetail<E> exceptionDetail = new ExceptionDetail<>();
        exceptionDetail.setCreateTime(LocalDateTime.now());
        exceptionDetail.setMessage(errorBody);
        // WebRequest.getDescription(false) returns "uri=/api/..." format, substring(4) strips "uri=" prefix
        exceptionDetail.setPath(request.getDescription(false).substring(4));

        try {
            exceptionDetail.setHostName(Inet4Address.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            exceptionDetail.setHostName("unknown-host");
        }
        BaseApiError<E> response = new BaseApiError<>();
        response.setStatus(status.value());
        response.setException(exceptionDetail);

        return new ResponseEntity<>(response, status);
    }

}
