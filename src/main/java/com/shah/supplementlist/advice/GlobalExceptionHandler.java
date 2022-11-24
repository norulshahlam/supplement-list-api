package com.shah.supplementlist.advice;


import com.shah.supplementlist.exception.SupplementException;
import com.shah.supplementlist.model.SupplementResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.shah.supplementlist.model.ResponseStatus.FAILURE;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({SupplementException.class})
    @ResponseBody
    public SupplementResponse handleSupplementException(HttpServletRequest req, SupplementException e) {
        return SupplementResponse.builder()
                .status(FAILURE)
                .errorMessage(e.getErrorMessage())
                .build();
    }

    /**
     * to handle constraint when validating request body from client input
     * @param req
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public SupplementResponse handleMethodArgumentNotValidException(
            HttpServletRequest req, MethodArgumentNotValidException e) {

        List<String> cause = new ArrayList<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            cause.add(error.getDefaultMessage());
        }
        for (ObjectError error : e.getBindingResult().getGlobalErrors()) {
            cause.add(error.getDefaultMessage());
        }
        String joined = String.join(", ", cause);

        log.error("Error caused by: {}",joined);
        return SupplementResponse.builder()
                .status(FAILURE)
                .errorMessage(joined)
                .build();
    }
    /**
     * to handle constraint when validating request body from client input
     * @param req
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    public SupplementResponse handleHttpMessageNotReadableException(
            HttpServletRequest req, HttpMessageNotReadableException e) {

        log.error("Error caused by: {}",e);
        return SupplementResponse.builder()
                .status(FAILURE)
                .errorMessage(e.getCause().getMessage())
                .build();
    }
}
