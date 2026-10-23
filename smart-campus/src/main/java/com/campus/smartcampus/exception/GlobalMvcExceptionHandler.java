package com.campus.smartcampus.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice(annotations = Controller.class)
public class GlobalMvcExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ModelAndView handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String viewName = status == HttpStatus.NOT_FOUND ? "error/404" : "error/500";
        ModelAndView mav = new ModelAndView(viewName);
        mav.setStatus(status);
        mav.addObject("status", status.value());
        mav.addObject("error", status.getReasonPhrase());
        mav.addObject("message", ex.getReason() != null ? ex.getReason() : ex.getMessage());
        mav.addObject("path", request.getRequestURI());
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    @ExceptionHandler({NoResourceFoundException.class, NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(Exception ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("status", 404);
        mav.addObject("error", "Not Found");
        mav.addObject("message", ex.getMessage() != null ? ex.getMessage() : "The requested resource could not be found.");
        mav.addObject("path", request.getRequestURI());
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleSecurityException(SecurityException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.setStatus(HttpStatus.FORBIDDEN);
        mav.addObject("status", 403);
        mav.addObject("error", "Access Denied");
        mav.addObject("message", ex.getMessage());
        mav.addObject("path", request.getRequestURI());
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        String msg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
        if (msg.contains("not found") || msg.contains("no admission pass found")) {
            ModelAndView mav = new ModelAndView("error/404");
            mav.setStatus(HttpStatus.NOT_FOUND);
            mav.addObject("status", 404);
            mav.addObject("error", "Not Found");
            mav.addObject("message", ex.getMessage());
            mav.addObject("path", request.getRequestURI());
            mav.addObject("timestamp", LocalDateTime.now());
            return mav;
        }

        ModelAndView mav = new ModelAndView("error/500");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("status", 500);
        mav.addObject("error", "Internal Server Error");
        mav.addObject("message", ex.getMessage());
        mav.addObject("path", request.getRequestURI());
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGeneralException(Exception ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/500");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("status", 500);
        mav.addObject("error", "Internal Server Error");
        mav.addObject("message", ex.getMessage());
        mav.addObject("path", request.getRequestURI());
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }
}
