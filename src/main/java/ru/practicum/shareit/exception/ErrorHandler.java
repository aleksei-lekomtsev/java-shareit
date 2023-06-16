package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException e) {
        log.warn("ValidationException: " + e.getMessage() + " HTTP-code: " + HttpStatus.BAD_REQUEST);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadInputDataException(final BadInputDataException e) {
        log.warn("BadInputDataException: " + e.getMessage() + " HTTP-code: " + HttpStatus.BAD_REQUEST);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(final EntityNotFoundException e) {
        log.warn("EntityNotFoundException: " + e.getMessage() + " HTTP-code: " + HttpStatus.NOT_FOUND);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn("Throwable: " + e.getMessage() + " HTTP-code: " + HttpStatus.INTERNAL_SERVER_ERROR);
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("Throwable: " + e.getMessage() + " HTTP-code: " + HttpStatus.INTERNAL_SERVER_ERROR);
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
    }
}
