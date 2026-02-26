package com.berkay.wallet.exception;

import com.fasterxml.jackson.core.JsonParseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class RestApiErrorHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(HttpServletRequest request, Exception ex, Locale locale) {
        log.error("An unexpected error occurred. URL: {}, Method: {}", request.getRequestURL(), request.getMethod(), ex);

        ApiError error = ErrorUtils.createError(ErrorCode.GENERIC_ERROR.getErrMsgKey(),
                        ErrorCode.GENERIC_ALREADY_EXISTS.getErrCode(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setUrl(request.getRequestURL().toString())
                .setTimestamp(Instant.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleHttpMediaTypeNotSupportedException(HttpServletRequest request,
                                                                             HttpMediaTypeNotSupportedException ex,
                                                                             Locale locale) {
        log.error("Unsupported media type. URL: {}, Unexpected Type: {}", request.getRequestURL(), ex.getContentType());
        ApiError error =
                ErrorUtils.createError(ErrorCode.HTTP_MEDIATYPE_NOT_SUPPORTED.getErrMsgKey(),
                                ErrorCode.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE.getErrCode(),
                                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                        .setUrl(request.getRequestURL().toString())
                        .setReqMethod(request.getMethod())
                        .setTimestamp(Instant.now());
        log.info("HttpMediaTypeNotSupportedException :: request.getMethod(): " + request.getMethod());
        return new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotWritableException(HttpServletRequest request,
                                                                          HttpMessageNotWritableException ex,
                                                                          Locale locale) {
        log.error("The response could not be converted to JSON format. URL: {}", request.getRequestURL(), ex);
        ApiError error = ErrorUtils.createError(ErrorCode.HTTP_MESSAGE_NOT_WRITABLE.getErrMsgKey(),
                        ErrorCode.HTTP_MESSAGE_NOT_WRITABLE.getErrCode(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setUrl(request.getRequestURL().toString())
                .setReqMethod(request.getMethod())
                .setTimestamp(Instant.now());
        log.info("HttpMessageNotWritableException :: request.getMethod(): " + request.getMethod());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiError> handleHttpMediaTypeNotAcceptableException(HttpServletRequest request,
                                                                              HttpMediaTypeNotAcceptableException ex,
                                                                              Locale locale) {
        log.error("An unacceptable media type was requested. URL: {}, Requested Types: {}", request.getRequestURL(), ex.getSupportedMediaTypes());

        ApiError error = ErrorUtils.createError(ErrorCode.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE.getErrMsgKey(),
                        ErrorCode.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE.getErrCode(),
                        HttpStatus.NOT_ACCEPTABLE.value())
                .setUrl(request.getRequestURL().toString())
                .setReqMethod(request.getMethod())
                .setTimestamp(Instant.now());

        log.info("HttpMediaTypeNotAcceptableException :: request.getMethod(): " + request.getMethod());
        return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpServletRequest request,
                                                                          HttpMessageNotReadableException ex,
                                                                          Locale locale) {
        log.error("The incoming HTTP request could not be read (corrupted JSON or incorrect type). URL: {}",
                request.getRequestURL(), ex);

        ApiError error = ErrorUtils.createError(ErrorCode.HTTP_MESSAGE_NOT_READABLE.getErrMsgKey(),
                        ErrorCode.HTTP_MESSAGE_NOT_READABLE.getErrCode(),
                        HttpStatus.BAD_REQUEST.value())
                .setUrl(request.getRequestURL().toString())
                .setReqMethod(request.getMethod())
                .setTimestamp(Instant.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ApiError> handleJsonParseException(HttpServletRequest request,
                                                             JsonParseException ex,
                                                             Locale locale) {
        ApiError error = ErrorUtils.createError(ErrorCode.JSON_PARSE_ERROR.getErrMsgKey(),
                        ErrorCode.JSON_PARSE_ERROR.getErrCode(),
                        HttpStatus.BAD_REQUEST.value())
                .setUrl(request.getRequestURL().toString())
                .setReqMethod(request.getMethod())
                .setTimestamp(Instant.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleHttpRequestMethodNotSupportedException(HttpServletRequest request,
                                                                                 HttpRequestMethodNotSupportedException ex,
                                                                                 Locale locale) {
        log.error("Invalid HTTP method attempted. URL: {}, Method Attempted: {}, Supported Methods: {}",
                request.getRequestURL(), request.getMethod(), ex.getSupportedMethods());
        ApiError error = ErrorUtils.createError(ErrorCode.HTTP_REQUEST_METHOD_NOT_SUPPORTED.getErrMsgKey(),
                        ErrorCode.HTTP_REQUEST_METHOD_NOT_SUPPORTED.getErrCode(),
                        HttpStatus.METHOD_NOT_ALLOWED.value())
                .setUrl(request.getRequestURL().toString())
                .setReqMethod(request.getMethod())
                .setTimestamp(Instant.now());
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleHIllegalArgumentException(
            HttpServletRequest request,
            IllegalArgumentException ex,
            Locale locale) {
        ApiError error = ErrorUtils
                .createError(String
                                .format("%s %s", ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getErrMsgKey(), ex.getMessage()),
                        ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getErrCode(),
                        HttpStatus.BAD_REQUEST.value()).setUrl(request.getRequestURL().toString())
                .setReqMethod(request.getMethod())
                .setTimestamp(Instant.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(HttpServletRequest request,
                                                                    ResourceNotFoundException ex, Locale locale) {
        ApiError error = ErrorUtils
                .createError(
                        String.format("%s %s", ErrorCode.RESOURCE_NOT_FOUND.getErrMsgKey(), ex.getMessage()),
                        ex.getErrCode(),
                        HttpStatus.NOT_FOUND.value()).setUrl(request.getRequestURL().toString())
                .setReqMethod(request.getMethod())
                .setTimestamp(Instant.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleCustomerNotFoundException(HttpServletRequest request,
                                                                    UserNotFoundException ex, Locale locale) {
        ApiError error = ErrorUtils
                .createError(
                        String.format("%s %s", ErrorCode.USER_NOT_FOUND.getErrMsgKey(), ex.getMessage()),
                        ex.getErrCode(),
                        HttpStatus.NOT_FOUND.value()).setUrl(request.getRequestURL().toString())
                .setReqMethod(request.getMethod())
                .setTimestamp(Instant.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ApiError> handleWalletNotFoundException(HttpServletRequest request,
                                                                  WalletNotFoundException ex,
                                                                  Locale locale) {
        ApiError error = ErrorUtils.createError(
                        String.format("%s %s", ErrorCode.WALLET_NOT_FOUND.getErrMsgKey(), ex.getMessage()),
                        ex.getErrCode(),
                        HttpStatus.NOT_FOUND.value())
                .setUrl(request.getRequestURL().toString())
                .setReqMethod(request.getMethod())
                .setTimestamp(Instant.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GenericAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleGenericAlreadyExistsException(HttpServletRequest request,
                                                                        GenericAlreadyExistsException ex, Locale locale) {
        ApiError error = ErrorUtils.createError(
                        String.format("%s %s", ErrorCode.GENERIC_ALREADY_EXISTS.getErrMsgKey(), ex.getMessage()),
                        ex.getErrorCode(),
                        HttpStatus.NOT_ACCEPTABLE.value())
                .setUrl(request.getRequestURL().toString())
                .setReqMethod(request.getMethod())
                .setTimestamp(Instant.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
    }
}
