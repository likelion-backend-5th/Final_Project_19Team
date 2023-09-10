package com.likelion.teammatch.exception;

import com.likelion.teammatch.exception.status.Status400Exception;
import com.likelion.teammatch.exception.status.Status403Exception;
import com.likelion.teammatch.exception.status.Status404Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleStatus400Exception(final Status400Exception ex, Model model){
        model.addAttribute("errorMsg", ex.getMessage());
        return "/html/error";
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleStatus403Exception(final Status403Exception ex, Model model){
        model.addAttribute("errorMsg", ex.getMessage());
        return "/html/error";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleStatus404Exception(final Status404Exception ex, Model model){
        model.addAttribute("errorMsg", ex.getMessage());
        return "/html/error";
    }
}


 */
